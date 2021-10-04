package com.jiguem.demo.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiguem.demo.dto.UserDTO;
import com.jiguem.demo.entity.Room;
import com.jiguem.demo.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository{

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private final String USER_KEY = "USER_KEY";

    private <T> T getResultObject(String key, String id, Class<T> classType) {
        T obj = objectMapper.convertValue(redisTemplate.opsForHash().get(key, id), classType);
        return obj;
    }

    @Override
    public UserDTO fetchOrGenerate(String id) {

        User user = null;

        if (id != null && !id.isBlank()) {
            user = objectMapper.convertValue(redisTemplate.opsForHash().get(USER_KEY, id), User.class);
            log.info("UserRepositoryImpl - fetchOrGenerate resultUser: {}", user);
        }

        if (user == null) {
            user = User.builder().id(UUID.randomUUID().toString()).build();
            redisTemplate.opsForHash().put(USER_KEY, user.getId(), user);

            User temp = objectMapper.convertValue(redisTemplate.opsForHash().get(USER_KEY, user.getId()), User.class);
            log.info("new User info: {}", temp);
        }

        return UserDTO.builder().id(user.getId()).build();
    }

    @Override
    public void addRoom(User user, String id) {
        user.addRoomId(id);
        redisTemplate.opsForHash().put(USER_KEY, user.getId(), user);
    }

    @Override
    public void setRoleToHost(User user) {
        user.setRoleToHost();
    }

    @Override
    public void setHostName(Room room, User user) {
        user.updateHostName(room, room.getHostName());
    }

    @Override
    public void setNameInRoom(String roomId, String name, User user) {
        user.setNameInRoom(roomId, name);
        redisTemplate.opsForHash().put(USER_KEY, user.getId(), user);
    }

    @Override
    public User updateUserName(Room room, User user) {
        return user.updateUsername(room.getId(), user);
    }

    @Override
    public User findById(String userId) {
        return getResultObject(USER_KEY, userId, User.class);
    }
}
