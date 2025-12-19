package com.forsoftwaredevelopers.audio_stream_api.infraestructure.persistense.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.forsoftwaredevelopers.audio_stream_api.infraestructure.persistense.entity.VoiceMessageJPAEntity;

public interface VoiceMessageJPARepository extends JpaRepository<VoiceMessageJPAEntity, String> {
    List<VoiceMessageJPAEntity> findByStreamIdAndStatus(String streamId, String status);
    List<VoiceMessageJPAEntity> findByStatusOrderByCreatedAtDesc(String status);
}
