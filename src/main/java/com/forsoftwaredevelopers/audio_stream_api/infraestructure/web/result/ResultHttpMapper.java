package com.forsoftwaredevelopers.audio_stream_api.infraestructure.web.result;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.forsoftwaredevelopers.audio_stream_api.domain.result.Result;

@Component
public class ResultHttpMapper {
    public <T> ResponseEntity<?> toResponse(Result<T> result) {
        if (result instanceof Result.Ok<T>(T value)) {
            return ResponseEntity.ok(value);
        }

        var status = mapErrorToHttpStatus(((Result.Fail<T>) result).error().code());
        return ResponseEntity.status(status).body(((Result.Fail<T>) result).error());
    }

    private HttpStatus mapErrorToHttpStatus(String code) {
        if (code.endsWith("_REQUIRED") || code.endsWith("_INVALID")) {
            return HttpStatus.BAD_REQUEST;
        }

        if (code.endsWith("_NOT_FOUND")) {
            return HttpStatus.NOT_FOUND;
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
