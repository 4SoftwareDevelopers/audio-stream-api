package com.forsoftwaredevelopers.audio_stream_api.application.usecase;

import com.forsoftwaredevelopers.audio_stream_api.application.command.RejectVoiceMessageCommand;
import com.forsoftwaredevelopers.audio_stream_api.domain.result.Result;

public interface RejectVoiceMessageUseCase {
    Result<Void> reject(RejectVoiceMessageCommand command);
}
