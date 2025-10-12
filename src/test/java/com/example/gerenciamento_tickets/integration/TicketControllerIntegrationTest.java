package com.example.gerenciamento_tickets.integration;

import com.example.gerenciamento_tickets.model.Categoria;
import com.example.gerenciamento_tickets.model.TicketStatus;
import com.example.gerenciamento_tickets.model.Usuario;
import com.example.gerenciamento_tickets.repository.CategoriaRepository;
import com.example.gerenciamento_tickets.repository.UsuarioRepository;
import com.example.gerenciamento_tickets.security.TokenJWTService;
import com.example.gerenciamento_tickets.util.TicketCreator;
import com.example.gerenciamento_tickets.util.UsuarioCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
public class TicketControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenJWTService tokenJWTService;

    private String token;
    private String adminToken;
    private String tecnicoToken;

    @BeforeEach
    void setup() throws Exception {
        Usuario usuario = UsuarioCreator.usuarioParaSalvar(passwordEncoder);
        usuarioRepository.save(usuario);
        Usuario tecnico = UsuarioCreator.tecnicoParaSalvar(passwordEncoder);
        usuarioRepository.save(tecnico);
        Usuario admin = UsuarioCreator.adminParaSalvar(passwordEncoder);
        usuarioRepository.save(admin);

        token = tokenJWTService.generateToken(usuario);
        adminToken = tokenJWTService.generateToken(admin);
        tecnicoToken = tokenJWTService.generateToken(tecnico);


        Categoria categoria = new Categoria();
        categoria.setNome("Suporte");
        categoria.setUsuariosResponsaveis(new ArrayList<>(List.of(tecnico)));
        categoriaRepository.save(categoria);
    }

    @Test
    void deveCriarEBuscarTicketPorId() throws Exception {
        String criarResponse = mockMvc.perform(post("/tickets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TicketCreator.criarTicketRequestBody())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long ticketId = objectMapper.readTree(criarResponse).get("id").asLong();

        mockMvc.perform(get("/tickets/" + ticketId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ticketId))
                .andExpect(jsonPath("$.titulo").value("Erro de login"));
    }

    @Test
    void deveListarTicketsDoUsuario() throws Exception {
        mockMvc.perform(get("/tickets")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }


    @Test
    void deveAdicionarComentario() throws Exception {
        String criarResponse = mockMvc.perform(post("/tickets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TicketCreator.criarTicketRequestBody())))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long ticketId = objectMapper.readTree(criarResponse).get("id").asLong();

        mockMvc.perform(post("/tickets/comentario")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "ticketId": %d, "texto": "Já tentei resetar a senha" }
                                """.formatted(ticketId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comentarios").isArray())
                .andExpect(jsonPath("$.comentarios[0].texto").exists());
    }

    @Test
    void deveResolverTicketQuandoRoleTecnico() throws Exception {
        String criarResponse = mockMvc.perform(post("/tickets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TicketCreator.criarTicketRequestBody())))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long ticketId = objectMapper.readTree(criarResponse).get("id").asLong();

        mockMvc.perform(post("/tickets/%d/resolver".formatted(ticketId))
                        .header("Authorization", tecnicoToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(TicketStatus.RESOLVIDO.name()));
    }

    @Test
    void deveResolverTicketQuandoAdmin() throws Exception {
        String criarResponse = mockMvc.perform(post("/tickets")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TicketCreator.criarTicketRequestBody())))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long ticketId = objectMapper.readTree(criarResponse).get("id").asLong();

        mockMvc.perform(post("/tickets/%d/resolver".formatted(ticketId))
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(TicketStatus.RESOLVIDO.name()));
    }

    @Test
    void resolverTicketDeveNegarAcessoParaRoleUser() throws Exception {

        mockMvc.perform(post("/tickets/1/resolver")
                        .header("Authorization", token))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveNegarAcessoSemToken() throws Exception {
        mockMvc.perform(get("/tickets"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/tickets/1"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/tickets"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/tickets/comentario"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/tickets/1/resolver"))
                .andExpect(status().isUnauthorized());
    }
}

