package com.jiguem.demo.service;

import com.jiguem.demo.dto.RoomDTO;
import com.jiguem.demo.dto.UserDTO;
import com.jiguem.demo.entity.Room;
import com.jiguem.demo.entity.User;
import com.jiguem.demo.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoomServiceImpl implements RoomService{

    private final RoomRepository roomRepository;

    @Override
    public RoomDTO create(RoomDTO roomDTO) {
        Room room = RoomDTO.toEntity(roomDTO);
        room = room.updateRoom(UUID.randomUUID().toString(), room);
        return roomRepository.create(room);
    }

    @Override
    public RoomDTO update(String id, RoomDTO roomDTO) {
        Room room = RoomDTO.toEntity(roomDTO);
        return roomRepository.update(id, room);
    }

    @Override
    public String delete(String id) {
        return roomRepository.delete(id);
    }

    @Override
    public RoomDTO findByIdThenAddUser(String id, UserDTO userDTO) {
        log.info("RoomServiceImpl-findByIdThenAddUser: id is {}, userDTO is {}", id, userDTO);

        Room room = roomRepository.findById(id);

        User user = UserDTO.toEntity(id, userDTO);

        String username = userDTO.getName();

        room = roomRepository.addUser(username, user, room);

        return Room.convertToDTO(room);
    }

    @Override
    public RoomDTO updateUser(String roomId, String userId, UserDTO userDTO) {

        Room room = roomRepository.findById(roomId);

        User user = UserDTO.toEntity(roomId, userDTO);

        if (userDTO.getName() != null) {
            room = roomRepository.updateUserName(userDTO.getName(), userId, room);
        }
        else {
            room = roomRepository.updateUserStatus(room, userId, user);
        }

        return Room.convertToDTO(room);
    }

}
