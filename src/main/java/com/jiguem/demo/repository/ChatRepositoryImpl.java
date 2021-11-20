package com.jiguem.demo.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiguem.demo.entity.Message;
import com.jiguem.demo.entity.Room;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Slf4j
@Repository
public class ChatRepositoryImpl implements ChatRepository{

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    private final String CHAT_KEY = "CHAT_KEY";
    private final String ROOM_KEY = "ROOM_KEY";
    private final String USER_KEY = "USER_KEY";

    private final String ROOM_NOT_FOUND_EXCEPTION = "%s room not found";
    private final String USER_NOT_FOUND_EXCEPTION = "%s user not found";

    private <T> T getObjectResult(String key, String id, Class<T> classType) {
        return objectMapper.convertValue(redisTemplate.opsForHash().get(key, id), classType);
    }

    @Override
    public String save(Message message) {

        String roomId = message.getRoomId();
        Room room = roomRepository.findById(roomId);

        if (room == null)
            throw new IllegalArgumentException(String.format(ROOM_NOT_FOUND_EXCEPTION, roomId));
        else
            roomRepository.addConversation(message.createTimestramp(), room);

        return "/room/"+message.getRoomId();
    }

    @Override
    public String findRoomID(Message message) {
        String roomId = message.getRoomId();
        Room room = roomRepository.findById(roomId);
        if (room == null)
            throw new IllegalArgumentException(String.format(ROOM_NOT_FOUND_EXCEPTION, roomId));
        return "/room/"+message.getRoomId();
    }

    @Override
    public String updateUserLocation(Message message) {
        String roomId = message.getRoomId();
        String userId = message.getSenderId();

        var room = roomRepository.findById(roomId);
        if (room == null)
            throw new IllegalArgumentException(String.format(ROOM_NOT_FOUND_EXCEPTION, roomId));

        var user = roomRepository.findUser(room, userId);
        log.info("updateUserLocation-user is: {}", user);
        if (user == null)
            throw new IllegalArgumentException(String.format(USER_NOT_FOUND_EXCEPTION, userId));

        // update user's location here
        user = user.updateLocation(message);
        userRepository.updateUser(user);
        log.info("updated User is: {}", user);
        // apply room to update here
        room = roomRepository.updateUserLocation(user, room);
        log.info("updated Room is: {}", room);

        return "/room/"+room.getId();
    }
}
