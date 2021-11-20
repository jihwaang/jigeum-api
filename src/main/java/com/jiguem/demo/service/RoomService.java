package com.jiguem.demo.service;

import com.jiguem.demo.dto.RoomDTO;
import com.jiguem.demo.dto.UserDTO;
import com.jiguem.demo.entity.Room;

public interface RoomService {
    RoomDTO create(RoomDTO roomDTO);

    RoomDTO update(String id, RoomDTO roomDTO);

    String delete(String id);

    RoomDTO findByIdThenAddUser(String id, UserDTO userDTO);

    RoomDTO updateUser(String roomId, String userId, UserDTO userDTO);

    UserDTO findUser(String id, String userId);

    RoomDTO findById(String id);
}
