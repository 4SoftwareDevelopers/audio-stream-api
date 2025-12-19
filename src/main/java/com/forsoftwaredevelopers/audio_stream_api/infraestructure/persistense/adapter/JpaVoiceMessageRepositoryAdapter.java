package com.forsoftwaredevelopers.audio_stream_api.infraestructure.persistense.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.forsoftwaredevelopers.audio_stream_api.domain.model.VoiceMessage;
import com.forsoftwaredevelopers.audio_stream_api.domain.port.VoiceMessageRepository;
import com.forsoftwaredevelopers.audio_stream_api.infraestructure.persistense.mapper.VoiceMessageJpaMapper;
import com.forsoftwaredevelopers.audio_stream_api.infraestructure.persistense.repository.VoiceMessageJPARepository;

@Repository
public class JpaVoiceMessageRepositoryAdapter implements VoiceMessageRepository {

    private final VoiceMessageJPARepository voiceMessageJPARepository;
    private final VoiceMessageJpaMapper voiceMessageJpaMapper;

    public JpaVoiceMessageRepositoryAdapter(VoiceMessageJPARepository voiceMessageJPARepository, VoiceMessageJpaMapper voiceMessageJpaMapper) {
        this.voiceMessageJPARepository = voiceMessageJPARepository;
        this.voiceMessageJpaMapper = voiceMessageJpaMapper;
    }

    @Override
    public VoiceMessage save(VoiceMessage voiceMessage) {
        var entity = voiceMessageJpaMapper.toJpaEntity(voiceMessage);
        var savedEntity = voiceMessageJPARepository.save(entity);
        return voiceMessageJpaMapper.toDomainModel(savedEntity);
    }

    @Override
    public List<VoiceMessage> findByStatus(String status) {
        var entities = voiceMessageJPARepository.findByStatusOrderByCreatedAtDesc(status);
        return entities.stream().map(voiceMessageJpaMapper::toDomainModel).collect(Collectors.toList());
    }

    @Override
    public VoiceMessage findById(String id) {
        var entity = voiceMessageJPARepository.findById(id);
        return entity.map(voiceMessageJpaMapper::toDomainModel).orElse(null);
    }
    
}
