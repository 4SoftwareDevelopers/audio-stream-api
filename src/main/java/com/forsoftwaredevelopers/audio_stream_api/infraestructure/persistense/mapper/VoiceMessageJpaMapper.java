package com.forsoftwaredevelopers.audio_stream_api.infraestructure.persistense.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.forsoftwaredevelopers.audio_stream_api.domain.model.VoiceMessage;
import com.forsoftwaredevelopers.audio_stream_api.domain.model.VoiceMessageStatus;
import com.forsoftwaredevelopers.audio_stream_api.domain.result.Result;
import com.forsoftwaredevelopers.audio_stream_api.infraestructure.persistense.entity.VoiceMessageJPAEntity;

@Mapper(componentModel = "spring")
public interface VoiceMessageJpaMapper {
    @Mapping(target = "status", expression = "java(voiceMessage.getStatus().name())")
    VoiceMessageJPAEntity toJpaEntity(VoiceMessage voiceMessage);


    default VoiceMessage toDomainModel(VoiceMessageJPAEntity voiceMessageJPAEntity) {
        var result = VoiceMessage.restore(
            voiceMessageJPAEntity.getId(),
            voiceMessageJPAEntity.getStreamId(),
            voiceMessageJPAEntity.getUsername(),
            voiceMessageJPAEntity.getEmail(),
            VoiceMessageStatus.fromString(voiceMessageJPAEntity.getStatus()),
            voiceMessageJPAEntity.getCreatedAt(),
            voiceMessageJPAEntity.getUpdatedAt()
        );
        return ((Result.Ok<VoiceMessage>) result).value();
    }
}
