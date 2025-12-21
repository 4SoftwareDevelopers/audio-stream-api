package com.forsoftwaredevelopers.audio_stream_api.domain.model;

import java.time.Instant;
import java.util.UUID;

import com.forsoftwaredevelopers.audio_stream_api.domain.result.DomainError;
import com.forsoftwaredevelopers.audio_stream_api.domain.result.ErrorType;
import com.forsoftwaredevelopers.audio_stream_api.domain.result.Result;

public class VoiceMessage {

    public static final String EMAIL_REQUIRED = "EMAIL_REQUIRED";
    public static final String USERNAME_REQUIRED = "USERNAME_REQUIRED";
    public static final String EMAIL_INVALID = "EMAIL_INVALID";
    public static final String MESSAGE_NOT_PENDING = "MESSAGE_NOT_PENDING";

    private String id;
    private String streamId;
    private String username;
    private String email;
    private VoiceMessageStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    private VoiceMessage(String id, String streamId, String username, String email, VoiceMessageStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.streamId = streamId;
        this.username = username;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private static Result<VoiceMessage> validate(String streamId, String username, String email) {
        if (email == null) {
            return Result.fail(new DomainError(EMAIL_REQUIRED, "Email is required", ErrorType.VALIDATION));
        }

        if (username.trim().isBlank() || email.trim().isBlank()) {
            return Result.fail(new DomainError(USERNAME_REQUIRED, "Username and email cannot be empty", ErrorType.VALIDATION));
        }

        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            return Result.fail(new DomainError(EMAIL_INVALID, "Invalid email address", ErrorType.VALIDATION));
        }

        return Result.ok(null);
    }

    public static Result<VoiceMessage> create(String streamId, String username, String email) {
        
        var validationResult = validate(streamId, username, email);
        if (validationResult.isFail()) {
            return validationResult.propagate();
        }

        return Result.ok(new VoiceMessage(
            UUID.randomUUID().toString(), 
            streamId, 
            username, 
            email,
            VoiceMessageStatus.RECEIVED, 
            Instant.now(), 
            Instant.now()));
    }

    public static VoiceMessage restore(String id, String streamId, String username, String email, VoiceMessageStatus status, Instant createdAt, Instant updatedAt) {
        return new VoiceMessage(id, streamId, username, email, status, createdAt, updatedAt);
    }

    public Result<Void> markAsPlayed() {

        if (this.status != VoiceMessageStatus.RECEIVED) {
            return Result.fail(new DomainError(MESSAGE_NOT_PENDING, "Message must be pending to be marked as played", ErrorType.CONFLICT));
        }

        this.status = VoiceMessageStatus.PLAYED;
        this.updatedAt = Instant.now();

        return Result.ok(null);
    }

    public Result<Void> markAsRejected() {
        if (this.status != VoiceMessageStatus.RECEIVED) {
            return Result.fail(new DomainError(MESSAGE_NOT_PENDING, "Message must be pending to be marked as rejected", ErrorType.CONFLICT));
        }

        this.status = VoiceMessageStatus.REJECTED;
        this.updatedAt = Instant.now();

        return Result.ok(null);
    }

    public String getId() {
        return id;
    }

    public String getStreamId() {
        return streamId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public VoiceMessageStatus getStatus() {
        return status;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
