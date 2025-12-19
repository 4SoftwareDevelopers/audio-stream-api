package com.forsoftwaredevelopers.audio_stream_api.domain.port;

public interface AudioStoragePort {
    String store(byte[] audio);
    byte[] retrieve(String audioPath);
}
