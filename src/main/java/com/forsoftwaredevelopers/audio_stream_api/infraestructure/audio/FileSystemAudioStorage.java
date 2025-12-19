package com.forsoftwaredevelopers.audio_stream_api.infraestructure.audio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.forsoftwaredevelopers.audio_stream_api.domain.port.AudioStoragePort;

@Component
public class FileSystemAudioStorage implements AudioStoragePort {
    
    @Override
    public String store(byte[] audio) {
        String path = System.getenv("user.home") + "/audio/" + UUID.randomUUID().toString() + ".wav";
        try {
            Files.write(Path.of(path), audio);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store audio", e);
        }
        return path;
    }

    @Override
    public byte[] retrieve(String audioPath) {
        try {
            return Files.readAllBytes(Path.of(audioPath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to retrieve audio", e);
        }
    }
}
