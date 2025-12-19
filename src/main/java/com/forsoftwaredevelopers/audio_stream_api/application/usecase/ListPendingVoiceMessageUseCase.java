package com.forsoftwaredevelopers.audio_stream_api.application.usecase;

import java.util.List;

import com.forsoftwaredevelopers.audio_stream_api.application.command.ListPendingVoiceMessageCommand;
import com.forsoftwaredevelopers.audio_stream_api.domain.model.VoiceMessage;

public interface ListPendingVoiceMessageUseCase {
    List<VoiceMessage> list(ListPendingVoiceMessageCommand command);

}
