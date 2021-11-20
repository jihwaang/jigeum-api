package com.jiguem.demo.repository;

import com.jiguem.demo.entity.Message;

public interface ChatRepository {
    String save(Message message);

    String findRoomID(Message message);

    String updateUserLocation(Message message);
}
