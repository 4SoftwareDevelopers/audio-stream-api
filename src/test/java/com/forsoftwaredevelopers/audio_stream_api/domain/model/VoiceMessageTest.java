package com.forsoftwaredevelopers.audio_stream_api.domain.model;

import com.forsoftwaredevelopers.audio_stream_api.domain.result.DomainError;
import com.forsoftwaredevelopers.audio_stream_api.domain.result.ErrorType;
import com.forsoftwaredevelopers.audio_stream_api.domain.result.Result;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class VoiceMessageTest {

    @Test
    void create_withValidData_returnsOkResultWithReceivedStatus() {
        String streamId = "stream-123";
        String username = "testuser";
        String email = "test@example.com";

        Result<VoiceMessage> result = VoiceMessage.create(streamId, username, email);

        assertTrue(result.isOk());
        VoiceMessage message = result.getOrThrow();
        assertNotNull(message.getId());
        assertEquals(streamId, message.getStreamId());
        assertEquals(username, message.getUsername());
        assertEquals(email, message.getEmail());
        assertEquals(VoiceMessageStatus.RECEIVED, message.getStatus());
    }

    @Test
    void create_withNullEmail_returnsFailResult() {
        String streamId = "stream-123";
        String username = "testuser";
        String email = null;

        Result<VoiceMessage> result = VoiceMessage.create(streamId, username, email);

        assertTrue(result.isFail());
        DomainError error = result.getErrorOrThrow();
        assertEquals(VoiceMessage.EMAIL_REQUIRED, error.code());
        assertEquals(ErrorType.VALIDATION, error.type());
    }

    @Test
    void create_withBlankUsername_returnsFailResult() {
        String streamId = "stream-123";
        String username = "   ";
        String email = "test@example.com";

        Result<VoiceMessage> result = VoiceMessage.create(streamId, username, email);

        assertTrue(result.isFail());
        DomainError error = result.getErrorOrThrow();
        assertEquals(VoiceMessage.USERNAME_REQUIRED, error.code());
    }

    @Test
    void create_withBlankEmail_returnsFailResult() {
        String streamId = "stream-123";
        String username = "testuser";
        String email = "   ";

        Result<VoiceMessage> result = VoiceMessage.create(streamId, username, email);

        assertTrue(result.isFail());
        DomainError error = result.getErrorOrThrow();
        assertEquals(VoiceMessage.USERNAME_REQUIRED, error.code());
    }

    @Test
    void create_withInvalidEmail_returnsFailResult() {
        String streamId = "stream-123";
        String username = "testuser";
        String email = "invalid-email";

        Result<VoiceMessage> result = VoiceMessage.create(streamId, username, email);

        assertTrue(result.isFail());
        DomainError error = result.getErrorOrThrow();
        assertEquals(VoiceMessage.EMAIL_INVALID, error.code());
    }

    @Test
    void restore_reconstructsEntityWithoutValidation() {
        String id = "test-id";
        String streamId = "stream-123";
        String username = "testuser";
        String email = "test@example.com";
        VoiceMessageStatus status = VoiceMessageStatus.PLAYED;
        Instant createdAt = Instant.now();
        Instant updatedAt = Instant.now();

        VoiceMessage message = VoiceMessage.restore(id, streamId, username, email, status, createdAt, updatedAt);

        assertEquals(id, message.getId());
        assertEquals(streamId, message.getStreamId());
        assertEquals(username, message.getUsername());
        assertEquals(email, message.getEmail());
        assertEquals(status, message.getStatus());
        assertEquals(createdAt, message.getCreatedAt());
        assertEquals(updatedAt, message.getUpdatedAt());
    }

    @Test
    void markAsPlayed_withReceivedStatus_returnsOkAndUpdatesStatus() {
        Result<VoiceMessage> createResult = VoiceMessage.create("stream-123", "testuser", "test@example.com");
        VoiceMessage message = createResult.getOrThrow();

        Result<Void> result = message.markAsPlayed();

        assertTrue(result.isOk());
        assertEquals(VoiceMessageStatus.PLAYED, message.getStatus());
    }

    @Test
    void markAsPlayed_withPlayedStatus_returnsFail() {
        Result<VoiceMessage> createResult = VoiceMessage.create("stream-123", "testuser", "test@example.com");
        VoiceMessage message = createResult.getOrThrow();
        message.markAsPlayed();

        Result<Void> result = message.markAsPlayed();

        assertTrue(result.isFail());
        DomainError error = result.getErrorOrThrow();
        assertEquals(VoiceMessage.MESSAGE_NOT_PENDING, error.code());
        assertEquals(ErrorType.CONFLICT, error.type());
    }

    @Test
    void markAsPlayed_withRejectedStatus_returnsFail() {
        Result<VoiceMessage> createResult = VoiceMessage.create("stream-123", "testuser", "test@example.com");
        VoiceMessage message = createResult.getOrThrow();
        message.markAsRejected();

        Result<Void> result = message.markAsPlayed();

        assertTrue(result.isFail());
        DomainError error = result.getErrorOrThrow();
        assertEquals(VoiceMessage.MESSAGE_NOT_PENDING, error.code());
    }

    @Test
    void markAsRejected_withReceivedStatus_returnsOkAndUpdatesStatus() {
        Result<VoiceMessage> createResult = VoiceMessage.create("stream-123", "testuser", "test@example.com");
        VoiceMessage message = createResult.getOrThrow();

        Result<Void> result = message.markAsRejected();

        assertTrue(result.isOk());
        assertEquals(VoiceMessageStatus.REJECTED, message.getStatus());
    }

    @Test
    void markAsRejected_withRejectedStatus_returnsFail() {
        Result<VoiceMessage> createResult = VoiceMessage.create("stream-123", "testuser", "test@example.com");
        VoiceMessage message = createResult.getOrThrow();
        message.markAsRejected();

        Result<Void> result = message.markAsRejected();

        assertTrue(result.isFail());
        DomainError error = result.getErrorOrThrow();
        assertEquals(VoiceMessage.MESSAGE_NOT_PENDING, error.code());
    }
}
