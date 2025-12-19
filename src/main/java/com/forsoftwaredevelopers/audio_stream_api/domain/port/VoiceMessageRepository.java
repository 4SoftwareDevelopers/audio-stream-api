package com.forsoftwaredevelopers.audio_stream_api.domain.port;

import java.util.List;

import com.forsoftwaredevelopers.audio_stream_api.domain.model.VoiceMessage;

public interface VoiceMessageRepository {
    VoiceMessage save(VoiceMessage voiceMessage);
    List<VoiceMessage> findByStatus(String status);
    VoiceMessage findById(String id);
    
}
