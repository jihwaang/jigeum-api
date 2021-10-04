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
}
