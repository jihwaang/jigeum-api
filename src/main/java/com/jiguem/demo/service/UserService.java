package com.jiguem.demo.service;

import com.jiguem.demo.dto.UserDTO;
import com.jiguem.demo.entity.Room;
import com.jiguem.demo.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UserDTO fetchOrGenerate(String id);

    UserDTO findByRoom(String id, Room room);
}
