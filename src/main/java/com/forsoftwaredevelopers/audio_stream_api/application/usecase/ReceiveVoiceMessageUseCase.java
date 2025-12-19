package com.forsoftwaredevelopers.audio_stream_api.application.usecase;

import com.forsoftwaredevelopers.audio_stream_api.application.command.ReceiveVoiceMessageCommand;
import com.forsoftwaredevelopers.audio_stream_api.domain.result.Result;

public interface ReceiveVoiceMessageUseCase {
    Result<String> recieve(ReceiveVoiceMessageCommand command);
}
