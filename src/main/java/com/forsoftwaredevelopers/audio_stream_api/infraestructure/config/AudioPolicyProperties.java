package com.forsoftwaredevelopers.audio_stream_api.domain.port;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "audio.policy")
public record AudioPolicyProperties(
        String maxDurationSeconds
) {
}
