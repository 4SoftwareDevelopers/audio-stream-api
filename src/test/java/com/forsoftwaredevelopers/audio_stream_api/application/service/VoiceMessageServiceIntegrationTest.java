package com.forsoftwaredevelopers.audio_stream_api.application.service;

import com.forsoftwaredevelopers.audio_stream_api.application.command.PlayVoiceMessageCommand;
import com.forsoftwaredevelopers.audio_stream_api.application.command.ReceiveVoiceMessageCommand;
import com.forsoftwaredevelopers.audio_stream_api.application.command.RejectVoiceMessageCommand;
import com.forsoftwaredevelopers.audio_stream_api.domain.model.VoiceMessage;
import com.forsoftwaredevelopers.audio_stream_api.domain.model.VoiceMessageStatus;
import com.forsoftwaredevelopers.audio_stream_api.domain.port.VoiceMessageRepository;
import com.forsoftwaredevelopers.audio_stream_api.domain.result.Result;
import com.forsoftwaredevelopers.audio_stream_api.infraestructure.persistense.repository.VoiceMessageJPARepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class VoiceMessageServiceIntegrationTest {

    @Autowired
    private VoiceMessageService voiceMessageService;

    @Autowired
    private VoiceMessageRepository voiceMessageRepository;

    @Autowired
    private VoiceMessageJPARepository voiceMessageJPARepository;

    @BeforeEach
    void setUp() {
        voiceMessageJPARepository.deleteAll();
    }

    @Test
    void receive_withValidCommand_createsMessageWithReceivedStatus() {
        ReceiveVoiceMessageCommand command = new ReceiveVoiceMessageCommand(
                "stream-123", "testuser", "test@example.com", new byte[0]);

        Result<String> result = voiceMessageService.recieve(command);

        assertTrue(result.isOk());
        String messageId = result.getOrThrow();
        assertNotNull(messageId);

        VoiceMessage savedMessage = voiceMessageRepository.findById(messageId);
        assertNotNull(savedMessage);
        assertEquals("stream-123", savedMessage.getStreamId());
        assertEquals("testuser", savedMessage.getUsername());
        assertEquals("test@example.com", savedMessage.getEmail());
        assertEquals(VoiceMessageStatus.RECEIVED, savedMessage.getStatus());
    }

    @Test
    void receive_withInvalidEmail_returnsFail() {
        ReceiveVoiceMessageCommand command = new ReceiveVoiceMessageCommand(
                "stream-123", "testuser", "invalid-email", new byte[0]);

        Result<String> result = voiceMessageService.recieve(command);

        assertTrue(result.isFail());
    }

    @Test
    void receive_withBlankUsername_returnsFail() {
        ReceiveVoiceMessageCommand command = new ReceiveVoiceMessageCommand(
                "stream-123", "   ", "test@example.com", new byte[0]);

        Result<String> result = voiceMessageService.recieve(command);

        assertTrue(result.isFail());
    }

    @Test
    void list_withReceivedStatus_returnsPendingMessages() {
        voiceMessageService.recieve(new ReceiveVoiceMessageCommand("stream-1", "user1", "user1@test.com", new byte[0]));
        voiceMessageService.recieve(new ReceiveVoiceMessageCommand("stream-2", "user2", "user2@test.com", new byte[0]));

        List<VoiceMessage> result = voiceMessageService.list(
                new com.forsoftwaredevelopers.audio_stream_api.application.command.ListPendingVoiceMessageCommand(
                        VoiceMessageStatus.RECEIVED.name()));

        assertEquals(2, result.size());
    }

    @Test
    void play_withValidMessageId_updatesStatusToPlayed() {
        Result<String> createResult = voiceMessageService.recieve(
                new ReceiveVoiceMessageCommand("stream-123", "testuser", "test@example.com", new byte[0]));
        String messageId = createResult.getOrThrow();

        Result<Void> result = voiceMessageService.play(new PlayVoiceMessageCommand(messageId));

        assertTrue(result.isOk());
        VoiceMessage updatedMessage = voiceMessageRepository.findById(messageId);
        assertEquals(VoiceMessageStatus.PLAYED, updatedMessage.getStatus());
    }

    @Test
    void play_withAlreadyPlayedMessage_returnsFail() {
        Result<String> createResult = voiceMessageService.recieve(
                new ReceiveVoiceMessageCommand("stream-123", "testuser", "test@example.com", new byte[0]));
        String messageId = createResult.getOrThrow();
        voiceMessageService.play(new PlayVoiceMessageCommand(messageId));

        Result<Void> result = voiceMessageService.play(new PlayVoiceMessageCommand(messageId));

        assertTrue(result.isFail());
    }

    @Test
    void reject_withValidMessageId_updatesStatusToRejected() {
        Result<String> createResult = voiceMessageService.recieve(
                new ReceiveVoiceMessageCommand("stream-123", "testuser", "test@example.com", new byte[0]));
        String messageId = createResult.getOrThrow();

        Result<Void> result = voiceMessageService.reject(new RejectVoiceMessageCommand(messageId));

        assertTrue(result.isOk());
        VoiceMessage updatedMessage = voiceMessageRepository.findById(messageId);
        assertEquals(VoiceMessageStatus.REJECTED, updatedMessage.getStatus());
    }

    @Test
    void reject_withAlreadyRejectedMessage_returnsFail() {
        Result<String> createResult = voiceMessageService.recieve(
                new ReceiveVoiceMessageCommand("stream-123", "testuser", "test@example.com", new byte[0]));
        String messageId = createResult.getOrThrow();
        voiceMessageService.reject(new RejectVoiceMessageCommand(messageId));

        Result<Void> result = voiceMessageService.reject(new RejectVoiceMessageCommand(messageId));

        assertTrue(result.isFail());
    }
}
