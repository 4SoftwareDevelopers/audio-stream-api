package com.forsoftwaredevelopers.audio_stream_api.infraestructure.web.admin;

import com.forsoftwaredevelopers.audio_stream_api.infraestructure.persistense.repository.VoiceMessageJPARepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminMessageControllerE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VoiceMessageJPARepository voiceMessageJPARepository;

    @BeforeEach
    void setUp() {
        voiceMessageJPARepository.deleteAll();
    }

    @Test
    void receive_withValidPayload_returnsCreatedWithMessageId() throws Exception {
        String payload = """
                {
                    "streamId": "stream-123",
                    "username": "testuser",
                    "email": "test@example.com",
                    "audio": "dGVzdA=="
                }
                """;

        mockMvc.perform(post("/api/admin/messages/receive")
                        .with(csrf())
                        .with(user("admin").password("password").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()));
    }

    @Test
    void receive_withInvalidEmail_returnsBadRequest() throws Exception {
        String payload = """
                {
                    "streamId": "stream-123",
                    "username": "testuser",
                    "email": "invalid-email",
                    "audio": "dGVzdA=="
                }
                """;

        mockMvc.perform(post("/api/admin/messages/receive")
                        .with(csrf())
                        .with(user("admin").password("password").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("EMAIL_INVALID")));
    }

    @Test
    void receive_withBlankUsername_returnsBadRequest() throws Exception {
        String payload = """
                {
                    "streamId": "stream-123",
                    "username": "   ",
                    "email": "test@example.com",
                    "audio": "dGVzdA=="
                }
                """;

        mockMvc.perform(post("/api/admin/messages/receive")
                        .with(csrf())
                        .with(user("admin").password("password").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("USERNAME_REQUIRED")));
    }

    @Test
    void list_withReceivedStatus_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/admin/messages/RECEIVED")
                        .with(user("admin").password("password").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void list_afterReceivingMessages_returnsMessagesList() throws Exception {
        String payload = """
                {
                    "streamId": "stream-123",
                    "username": "testuser",
                    "email": "test@example.com",
                    "audio": "dGVzdA=="
                }
                """;
        mockMvc.perform(post("/api/admin/messages/receive")
                .with(csrf())
                .with(user("admin").password("password").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload));

        mockMvc.perform(get("/api/admin/messages/RECEIVED")
                        .with(user("admin").password("password").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].streamId", is("stream-123")))
                .andExpect(jsonPath("$[0].username", is("testuser")))
                .andExpect(jsonPath("$[0].status", is("RECEIVED")));
    }

    @Test
    void play_withValidMessageId_returnsOk() throws Exception {
        String payload = """
                {
                    "streamId": "stream-123",
                    "username": "testuser",
                    "email": "test@example.com",
                    "audio": "dGVzdA=="
                }
                """;
        String response = mockMvc.perform(post("/api/admin/messages/receive")
                        .with(csrf())
                        .with(user("admin").password("password").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String messageId = response.replace("\"", "");

        mockMvc.perform(post("/api/admin/messages/play/" + messageId)
                        .with(csrf())
                        .with(user("admin").password("password").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void play_withAlreadyPlayedMessage_returnsConflict() throws Exception {
        String payload = """
                {
                    "streamId": "stream-123",
                    "username": "testuser",
                    "email": "test@example.com",
                    "audio": "dGVzdA=="
                }
                """;
        String messageId = mockMvc.perform(post("/api/admin/messages/receive")
                        .with(csrf())
                        .with(user("admin").password("password").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString().replace("\"", "");

        mockMvc.perform(post("/api/admin/messages/play/" + messageId)
                        .with(csrf())
                        .with(user("admin").password("password").roles("ADMIN")));
        
        mockMvc.perform(post("/api/admin/messages/play/" + messageId)
                        .with(csrf())
                        .with(user("admin").password("password").roles("ADMIN")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", is("MESSAGE_NOT_PENDING")));
    }

    @Test
    void reject_withValidMessageId_returnsOk() throws Exception {
        String payload = """
                {
                    "streamId": "stream-123",
                    "username": "testuser",
                    "email": "test@example.com",
                    "audio": "dGVzdA=="
                }
                """;
        String messageId = mockMvc.perform(post("/api/admin/messages/receive")
                        .with(csrf())
                        .with(user("admin").password("password").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString().replace("\"", "");

        mockMvc.perform(post("/api/admin/messages/reject/" + messageId)
                        .with(csrf())
                        .with(user("admin").password("password").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void reject_withAlreadyRejectedMessage_returnsConflict() throws Exception {
        String payload = """
                {
                    "streamId": "stream-123",
                    "username": "testuser",
                    "email": "test@example.com",
                    "audio": "dGVzdA=="
                }
                """;
        String messageId = mockMvc.perform(post("/api/admin/messages/receive")
                        .with(csrf())
                        .with(user("admin").password("password").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString().replace("\"", "");

        mockMvc.perform(post("/api/admin/messages/reject/" + messageId)
                        .with(csrf())
                        .with(user("admin").password("password").roles("ADMIN")));
        
        mockMvc.perform(post("/api/admin/messages/reject/" + messageId)
                        .with(csrf())
                        .with(user("admin").password("password").roles("ADMIN")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code", is("MESSAGE_NOT_PENDING")));
    }
}
