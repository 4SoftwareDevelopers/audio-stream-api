package com.forsoftwaredevelopers.audio_stream_api.domain.result;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void ok_createsOkResult() {
        Result<String> result = Result.ok("test-value");

        assertTrue(result.isOk());
        assertFalse(result.isFail());
        assertEquals("test-value", result.getOrThrow());
    }

    @Test
    void fail_createsFailResult() {
        DomainError error = new DomainError("CODE", "message", ErrorType.VALIDATION);
        Result<String> result = Result.fail(error);

        assertTrue(result.isFail());
        assertFalse(result.isOk());
        assertEquals(error, result.getErrorOrThrow());
    }

    @Test
    void getOrThrow_onFailResult_throwsException() {
        DomainError error = new DomainError("CODE", "message", ErrorType.VALIDATION);
        Result<String> result = Result.fail(error);

        assertThrows(IllegalStateException.class, result::getOrThrow);
    }

    @Test
    void getErrorOrThrow_onOkResult_throwsException() {
        Result<String> result = Result.ok("test-value");

        assertThrows(IllegalStateException.class, result::getErrorOrThrow);
    }

    @Test
    void propagate_onFailResult_returnsFail() {
        DomainError error = new DomainError("CODE", "message", ErrorType.VALIDATION);
        Result<String> result = Result.fail(error);

        Result<Integer> propagated = result.propagate();

        assertTrue(propagated.isFail());
        assertEquals(error, propagated.getErrorOrThrow());
    }

    @Test
    void propagate_onOkResult_throwsException() {
        Result<String> result = Result.ok("test-value");

        assertThrows(IllegalStateException.class, () -> result.propagate());
    }

    @Test
    void ok_withNullValue_isValid() {
        Result<String> result = Result.ok(null);

        assertTrue(result.isOk());
        assertNull(result.getOrThrow());
    }

    @Test
    void ok_withComplexObject_isValid() {
        DomainError error = new DomainError("CODE", "message", ErrorType.VALIDATION);
        Result<DomainError> result = Result.ok(error);

        assertTrue(result.isOk());
        assertEquals(error, result.getOrThrow());
    }
}
