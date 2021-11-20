package com.jiguem.demo.repository;

import com.jiguem.demo.dto.RoomDTO;
import com.jiguem.demo.dto.UserDTO;
import com.jiguem.demo.entity.Message;
import com.jiguem.demo.entity.Room;
import com.jiguem.demo.entity.User;

public interface RoomRepository {
    RoomDTO create(Room room);

    RoomDTO update(String id, Room room);

    String delete(String id);

    Room findById(String id);

    Room addUser(String username, User user, Room room);

    Room applyUpdatedUser(User user, Room room);

    void addConversation(Message message, Room room);

    Room updateUserStatus(Room room, String userId, User user);

    Room updateUserName(String name, String userId, Room room);

    Room updateUserLocation(User user, Room room);

    User findUser(Room room, String userId);
}
