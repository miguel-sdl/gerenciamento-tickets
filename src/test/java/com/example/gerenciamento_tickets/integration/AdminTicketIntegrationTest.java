package com.example.gerenciamento_tickets.integration;

import com.example.gerenciamento_tickets.dto.AtualizarCategoriaRequestBody;
import com.example.gerenciamento_tickets.model.Categoria;
import com.example.gerenciamento_tickets.model.Usuario;
import com.example.gerenciamento_tickets.repository.CategoriaRepository;
import com.example.gerenciamento_tickets.repository.UsuarioRepository;
import com.example.gerenciamento_tickets.security.TokenJWTService;
import com.example.gerenciamento_tickets.util.CategoriaCreator;
import com.example.gerenciamento_tickets.util.UsuarioCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
public class AdminTicketIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenJWTService tokenJWTService;

    private String adminToken;
    private String userToken;
    private String tecnicoToken;

    @BeforeEach
    void setup() throws Exception {
        Usuario usuario = UsuarioCreator.usuarioParaSalvar(passwordEncoder);
        usuarioRepository.save(usuario);
        Usuario tecnico = UsuarioCreator.tecnicoParaSalvar(passwordEncoder);
        usuarioRepository.save(tecnico);
        Usuario admin = UsuarioCreator.adminParaSalvar(passwordEncoder);
        usuarioRepository.save(admin);


        userToken = tokenJWTService.generateToken(usuario);
        tecnicoToken = tokenJWTService.generateToken(tecnico);
        adminToken = tokenJWTService.generateToken(admin);

    }

    @Test
    void deveCriarCategoria_quandoAdmin() throws Exception {
        var dto = CategoriaCreator.criarCategoriaRequestBody();

        mockMvc.perform(post("/admin/tickets/categoria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value(dto.nome()));
    }

    @Test
    void deveNegarAcesso_quandoUsuarioNaoAdmin() throws Exception {
        var criarCategoraDto = CategoriaCreator.criarCategoriaRequestBody();

        mockMvc.perform(post("/admin/tickets/categoria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarCategoraDto))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/admin/tickets/categoria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarCategoraDto))
                        .header("Authorization", "Bearer " + tecnicoToken))
                .andExpect(status().isForbidden());


        var atualizarCategoriaDto = CategoriaCreator.atualizarCategoriaRequestBody();

        mockMvc.perform(put("/admin/tickets/categoria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizarCategoriaDto))
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/admin/tickets/categoria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizarCategoriaDto))
                        .header("Authorization", "Bearer " + tecnicoToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveNegarAcesso_quandoNaoAutenticado() throws Exception {
        var dto = CategoriaCreator.criarCategoriaRequestBody();


        mockMvc.perform(post("/admin/tickets/categoria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/admin/tickets/categoria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CategoriaCreator.atualizarCategoriaRequestBody())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveAtualizarSomenteCamposInformados_QuandoAdmin() throws Exception {
        var criarCategoriDto = CategoriaCreator.criarCategoriaRequestBody();

        String content = mockMvc.perform(post("/admin/tickets/categoria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarCategoriDto))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();


        long id = objectMapper.readTree(content).get("id").asLong();
        Categoria antesDeAtualizar = categoriaRepository.findById(id).orElseThrow();
        String nomeAntesDeAtualizar = antesDeAtualizar.getNome();
        int prazoDefaultEmHorasAntesDeAtualizar = antesDeAtualizar.getPrazoDefaultEmHoras();
        List<Usuario> usuariosResponsaveisAntesDeAtualizar = antesDeAtualizar.getUsuariosResponsaveis();


        var atualizarCategoriaDto = new AtualizarCategoriaRequestBody(id, "Outro Nome", null, null);

        mockMvc.perform(put("/admin/tickets/categoria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizarCategoriaDto))
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        Categoria depoisDeAtualizar = categoriaRepository.findById(id).orElseThrow();

        Assertions.assertEquals(prazoDefaultEmHorasAntesDeAtualizar, depoisDeAtualizar.getPrazoDefaultEmHoras());
        Assertions.assertEquals(usuariosResponsaveisAntesDeAtualizar, depoisDeAtualizar.getUsuariosResponsaveis());
        Assertions.assertNotEquals(nomeAntesDeAtualizar, depoisDeAtualizar.getNome());
        Assertions.assertEquals("Outro Nome", depoisDeAtualizar.getNome());

    }
}
