package com.forsoftwaredevelopers.audio_stream_api.domain.model;

public enum VoiceMessageStatus {
    RECEIVED, PLAYED, REJECTED; 

    public static VoiceMessageStatus fromString(String status) {
        return VoiceMessageStatus.valueOf(status.toUpperCase());
    }
}
