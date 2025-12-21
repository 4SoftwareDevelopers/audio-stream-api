package com.forsoftwaredevelopers.audio_stream_api.domain.result;

public record DomainError(String code, String message, ErrorType type) {
    public DomainError(String code, String message) {
        this(code, message, ErrorType.INTERNAL);
    }
}
