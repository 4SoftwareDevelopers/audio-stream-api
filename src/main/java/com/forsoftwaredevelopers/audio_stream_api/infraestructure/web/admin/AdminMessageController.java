package com.forsoftwaredevelopers.audio_stream_api.infraestructure.web.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.forsoftwaredevelopers.audio_stream_api.application.command.ListPendingVoiceMessageCommand;
import com.forsoftwaredevelopers.audio_stream_api.application.command.PlayVoiceMessageCommand;
import com.forsoftwaredevelopers.audio_stream_api.application.command.ReceiveVoiceMessageCommand;
import com.forsoftwaredevelopers.audio_stream_api.application.command.RejectVoiceMessageCommand;
import com.forsoftwaredevelopers.audio_stream_api.application.service.VoiceMessageService;
import com.forsoftwaredevelopers.audio_stream_api.domain.model.VoiceMessage;
import com.forsoftwaredevelopers.audio_stream_api.infraestructure.web.result.ResultHttpMapper;

@RestController
@RequestMapping("api/admin/messages")
public class AdminMessageController {

    private final VoiceMessageService voiceMessageService;
    private final ResultHttpMapper resultHttpMapper;

    public AdminMessageController(VoiceMessageService voiceMessageService, ResultHttpMapper resultHttpMapper) {
        this.voiceMessageService = voiceMessageService;
        this.resultHttpMapper = resultHttpMapper;
    }

    @GetMapping("/{status}")
    public ResponseEntity<List<VoiceMessage>> listByStatus(@PathVariable String status) {
        return ResponseEntity.ok(voiceMessageService.list(new ListPendingVoiceMessageCommand(status)));
    }

    @PostMapping("/play/{id}")
    public ResponseEntity<?> play(@PathVariable String id) {
        var result = voiceMessageService.play(new PlayVoiceMessageCommand(id));
        return resultHttpMapper.toResponse(result);
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<?> reject(@PathVariable String id) {
        var result = voiceMessageService.reject(new RejectVoiceMessageCommand(id));
        return resultHttpMapper.toResponse(result);
    }

    @PostMapping("/receive")
    public ResponseEntity<?> receive(@RequestBody ReceiveVoiceMessageCommand command) {
        var result = voiceMessageService.recieve(command);
        
        return resultHttpMapper.toResponse(result);
    }

}
