package com.forsoftwaredevelopers.audio_stream_api.domain.result;

public sealed interface Result<T> {
    record Ok<T>(T value) implements Result<T> {
        
    }
    record Fail<T>(DomainError error) implements Result<T> {
    }

    static <T> Result<T> ok(T value) {
        return new Ok<>(value);
    }
    static <T> Result<T> fail(DomainError error) {
        return new Fail<>(error);
    }
}
