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

    default boolean isOk() {
        return this instanceof Ok;
    }

    default boolean isFail() {
        return this instanceof Fail;
    }

    default T getOrThrow() {
        if (this instanceof Ok<T>(T value)) {
            return value;
        }
        throw new IllegalStateException("Cannot get value from a Fail result");
    }

    default DomainError getErrorOrThrow() {
        if (this instanceof Fail<T>(DomainError error)) {
            return error;
        }
        throw new IllegalStateException("Cannot get error from an Ok result");
    }

    @SuppressWarnings("unchecked")
    default <R> Result<R> propagate() {
        if (this instanceof Fail<T> fail) {
            return (Result<R>) fail;
        }
        throw new IllegalStateException("Cannot propagate an Ok result as a Fail");
    }
}
