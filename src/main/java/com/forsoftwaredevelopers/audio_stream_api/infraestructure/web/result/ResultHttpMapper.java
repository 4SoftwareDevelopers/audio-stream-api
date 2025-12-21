package com.forsoftwaredevelopers.audio_stream_api.infraestructure.web.result;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.forsoftwaredevelopers.audio_stream_api.domain.result.Result;

@Component
public class ResultHttpMapper {
    public <T> ResponseEntity<?> toResponse(Result<T> result) {
        if (result.isOk()) {
            return ResponseEntity.ok(result.getOrThrow());
        }

        var status = mapErrorToHttpStatus(result.getErrorOrThrow().type());
        return ResponseEntity.status(status).body(result.getErrorOrThrow());
    }

    private HttpStatus mapErrorToHttpStatus(com.forsoftwaredevelopers.audio_stream_api.domain.result.ErrorType type) {
        return switch (type) {
            case VALIDATION -> HttpStatus.BAD_REQUEST;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT -> HttpStatus.CONFLICT;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case INTERNAL -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
