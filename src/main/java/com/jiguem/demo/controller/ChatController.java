package com.jiguem.demo.controller;

import com.jiguem.demo.dto.MessageDTO;
import com.jiguem.demo.entity.Message;
import com.jiguem.demo.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/send")
    public void sendMessage(MessageDTO messageDTO) {
        log.info("ChatController-sendMessage: message is {}", messageDTO);
        String destination = chatService.save(messageDTO);
        log.info("destination is {}", destination);
        messagingTemplate.convertAndSend(destination, messageDTO);
    }

    @MessageMapping("/locate")
    public void sendLocation(MessageDTO messageDTO) {
        log.info("ChatController-sendLocation: message is {}", messageDTO);
        String destination = chatService.updateUserLocation(messageDTO);
        log.info("destination is {}", destination);
        messagingTemplate.convertAndSend(destination, messageDTO);
    }
}
