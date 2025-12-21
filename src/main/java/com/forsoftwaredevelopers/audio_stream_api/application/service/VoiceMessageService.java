package com.forsoftwaredevelopers.audio_stream_api.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.forsoftwaredevelopers.audio_stream_api.application.command.ListPendingVoiceMessageCommand;
import com.forsoftwaredevelopers.audio_stream_api.application.command.PlayVoiceMessageCommand;
import com.forsoftwaredevelopers.audio_stream_api.application.command.ReceiveVoiceMessageCommand;
import com.forsoftwaredevelopers.audio_stream_api.application.command.RejectVoiceMessageCommand;
import com.forsoftwaredevelopers.audio_stream_api.application.usecase.ListPendingVoiceMessageUseCase;
import com.forsoftwaredevelopers.audio_stream_api.application.usecase.PlayVoiceMessageUseCase;
import com.forsoftwaredevelopers.audio_stream_api.application.usecase.ReceiveVoiceMessageUseCase;
import com.forsoftwaredevelopers.audio_stream_api.application.usecase.RejectVoiceMessageUseCase;
import com.forsoftwaredevelopers.audio_stream_api.domain.model.VoiceMessage;
import com.forsoftwaredevelopers.audio_stream_api.domain.port.VoiceMessageRepository;
import com.forsoftwaredevelopers.audio_stream_api.domain.result.DomainError;
import com.forsoftwaredevelopers.audio_stream_api.domain.result.Result;

@Service
public class VoiceMessageService implements ReceiveVoiceMessageUseCase, RejectVoiceMessageUseCase, 
ListPendingVoiceMessageUseCase, PlayVoiceMessageUseCase {

    private final VoiceMessageRepository voiceMessageRepository;

    public VoiceMessageService(VoiceMessageRepository voiceMessageRepository) {
        this.voiceMessageRepository = voiceMessageRepository;
    }

    @Override
    public Result<Void> play(PlayVoiceMessageCommand command) {
        VoiceMessage voiceMessage = voiceMessageRepository.findById(command.voiceMessageId());

        var result = voiceMessage.markAsPlayed();
        if (result.isFail()) {
            return result.propagate();
        }
        voiceMessageRepository.save(voiceMessage);

        return Result.ok(null);
    }

    @Override
    public List<VoiceMessage> list(ListPendingVoiceMessageCommand command) {
        return voiceMessageRepository.findByStatus(command.status());
    }

    @Override
    public Result<Void> reject(RejectVoiceMessageCommand command) {
        VoiceMessage voiceMessage = voiceMessageRepository.findById(command.voiceMessageId());
        var result = voiceMessage.markAsRejected();
        if (result.isFail()) {
            return result.propagate();
        }
        voiceMessageRepository.save(voiceMessage);
        return Result.ok(null);
    }

    @Override
    public Result<String> recieve(ReceiveVoiceMessageCommand command) {
        var result = VoiceMessage.create(command.streamId(), command.username(), command.email());
        if (result.isFail()) {
            return result.propagate();
        }

        VoiceMessage voiceMessage = result.getOrThrow();
        voiceMessageRepository.save(voiceMessage);
        return Result.ok(voiceMessage.getId());
    }
    
}
