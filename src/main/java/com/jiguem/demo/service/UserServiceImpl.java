package com.jiguem.demo.service;

import com.jiguem.demo.dto.UserDTO;
import com.jiguem.demo.entity.Room;
import com.jiguem.demo.entity.User;
import com.jiguem.demo.repository.RoomRepository;
import com.jiguem.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Override
    public UserDTO fetchOrGenerate(String id) {
        UserDTO UserDTO = userRepository.fetchOrGenerate(id);
        return UserDTO;
    }

    @Override
    public UserDTO findByRoom(String id, Room room) {
        UserDTO userDTO = userRepository.findByRoom(id, room);
        return userDTO;
    }
}
