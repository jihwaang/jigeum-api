package com.jiguem.demo.repository;


import com.jiguem.demo.dto.UserDTO;
import com.jiguem.demo.entity.Room;
import com.jiguem.demo.entity.User;

import java.util.UUID;

public interface UserRepository {
    UserDTO fetchOrGenerate(String id);

    void addRoom(User user, String id);

    void setRoleToHost(User user);

    void setHostName(Room room, User user);

    void setNameInRoom(String roomId, String name, User user);

    User updateUserName(Room room, User user);

    User findById(String userId);

    void setRandomColor(User user);
}
