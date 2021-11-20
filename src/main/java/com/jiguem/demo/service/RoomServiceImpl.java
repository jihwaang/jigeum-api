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
            // change name here
            room = roomRepository.updateUserName(userDTO.getName(), userId, room);
        }
        // change only status
        room = roomRepository.updateUserStatus(room, userId, user);

        return Room.convertToDTO(room);
    }

    @Override
    public UserDTO findUser(String id, String userId) {
        Room room = roomRepository.findById(id);
        if (room == null) {
            throw new IllegalArgumentException(String.format("room id {} not found", id));
        }
        User user = roomRepository.findUser(room, userId);
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getUsername(room.getId()))
                .color(user.getColor())
                .isTooLate(user.getIsTooLate())
                .isBanned(user.getIsBanned())
                .lastLongitude(user.getLastLongitude())
                .lastLongitude(user.getLastLongitude())
                .build();
    }

    @Override
    public RoomDTO findById(String id) {
        Room room = roomRepository.findById(id);
        return Room.convertToDTO(room);
    }

}
