package com.forsoftwaredevelopers.audio_stream_api.application.usecase;


import com.forsoftwaredevelopers.audio_stream_api.application.command.PlayVoiceMessageCommand;
import com.forsoftwaredevelopers.audio_stream_api.domain.result.Result;

public interface PlayVoiceMessageUseCase {
    Result<Void> play(PlayVoiceMessageCommand command);
    
}
