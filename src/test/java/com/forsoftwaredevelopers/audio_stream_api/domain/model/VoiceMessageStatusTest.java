package com.forsoftwaredevelopers.audio_stream_api.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VoiceMessageStatusTest {

    @Test
    void fromString_withLowercase_returnsCorrectStatus() {
        assertEquals(VoiceMessageStatus.RECEIVED, VoiceMessageStatus.fromString("received"));
        assertEquals(VoiceMessageStatus.PLAYED, VoiceMessageStatus.fromString("played"));
        assertEquals(VoiceMessageStatus.REJECTED, VoiceMessageStatus.fromString("rejected"));
    }

    @Test
    void fromString_withUppercase_returnsCorrectStatus() {
        assertEquals(VoiceMessageStatus.RECEIVED, VoiceMessageStatus.fromString("RECEIVED"));
        assertEquals(VoiceMessageStatus.PLAYED, VoiceMessageStatus.fromString("PLAYED"));
        assertEquals(VoiceMessageStatus.REJECTED, VoiceMessageStatus.fromString("REJECTED"));
    }

    @Test
    void fromString_withMixedCase_returnsCorrectStatus() {
        assertEquals(VoiceMessageStatus.RECEIVED, VoiceMessageStatus.fromString("Received"));
        assertEquals(VoiceMessageStatus.PLAYED, VoiceMessageStatus.fromString("Played"));
        assertEquals(VoiceMessageStatus.REJECTED, VoiceMessageStatus.fromString("Rejected"));
    }
}
