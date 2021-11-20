package com.jiguem.demo.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiguem.demo.dto.UserDTO;
import com.jiguem.demo.entity.Room;
import com.jiguem.demo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository{

    private RedisTemplate<String, Object> redisTemplate;
    private ObjectMapper objectMapper;

    public UserRepositoryImpl(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    private final String USER_KEY = "USER_KEY";
    private final String ROOM_KEY = "ROOM_KEY";

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

    @Override
    public void setRandomColor(User user) {
        user.setRandomColor();
    }

    @Override
    public UserDTO findByRoom(String id, Room room) {
        User user = room.getUsers().get(id);
        if (user == null) {
            throw new IllegalArgumentException(String.format("User %s does not exist in the room", id));
        }
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getUsername(room.getId()))
                .isBanned(user.getIsBanned())
                .isTooLate(user.getIsTooLate())
                .lastLongitude(user.getLastLongitude())
                .lastLatitude(user.getLastLatitude())
                .color(user.getColor())
                .build();
    }

    @Override
    public void setDefaultStatus(User user) {
        user.setDefaultStatus();
    }

    @Override
    public void updateUser(User user) {
        redisTemplate.opsForHash().put(USER_KEY, user.getId(), user);
    }
}
