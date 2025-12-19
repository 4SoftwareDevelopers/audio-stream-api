package com.forsoftwaredevelopers.audio_stream_api.application.command;

public record ReceiveVoiceMessageCommand(String streamId, String username, String email, byte[] audio) {
    
}
