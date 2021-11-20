package com.jiguem.demo.service;

import com.jiguem.demo.dto.MessageDTO;
import com.jiguem.demo.entity.Message;

public interface ChatService {
    String save(MessageDTO messageDTO);

    String findRoomID(MessageDTO messageDTO);

    String updateUserLocation(MessageDTO messageDTO);
}
