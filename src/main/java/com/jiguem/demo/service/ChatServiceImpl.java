package com.jiguem.demo.service;

import com.jiguem.demo.dto.MessageDTO;
import com.jiguem.demo.entity.Message;
import com.jiguem.demo.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatServiceImpl implements ChatService{

    private final ChatRepository chatRepository;

    @Override
    public String save(MessageDTO messageDTO) {
        Message message = MessageDTO.toEntity(messageDTO);
        return chatRepository.save(message);
    }
}
