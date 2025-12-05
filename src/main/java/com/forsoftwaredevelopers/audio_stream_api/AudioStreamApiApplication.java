package com.forsoftwaredevelopers.audio_stream_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class AudioStreamApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AudioStreamApiApplication.class, args);
	}

	@GetMapping("/hello")
	public String hello() {
		return "Hello, World from stream api 4sd!";
	}

}
