package com.jiguem.demo.repository;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiguem.demo.dto.RoomDTO;
import com.jiguem.demo.entity.Message;
import com.jiguem.demo.entity.Room;
import com.jiguem.demo.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Repository
public class RoomRepositoryImpl implements RoomRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper mapper;
    private final UserRepository userRepository;
    private final String ROOM_KEY = "ROOM_KEY";
    private final String USER_KEY = "USER_KEY";
    private final String ROOM_NOT_FOUND_EXCEPTION = "%s NOT FOUND";
    private final String USER_NOT_FOUND_EXCEPTION = "%s user not found";

    private <T> T getResultObject(String key, String id, Class<T> classType) {
        T obj = mapper.convertValue(redisTemplate.opsForHash().get(key, id), classType);
        return obj;
    }

    @Override
    public RoomDTO create(Room room) {
        // add user to userList
        var user = getResultObject(USER_KEY, room.getHost(), User.class);
        userRepository.addRoom(user, room.getId());
        userRepository.setRoleToHost(user);
        userRepository.setHostName(room, user);
        userRepository.setRandomColor(user);

        // save room
        redisTemplate.opsForHash().put(ROOM_KEY, room.getId(), room.addParticipate(user));

        // result room
        var createdRoom = getResultObject(ROOM_KEY, room.getId(), Room.class);
        log.info("RoomRepositoryImpl-createdRoom: {}", createdRoom);

        return Room.convertToDTO(room);
    }

    @Override
    public RoomDTO update(String roomId, Room room) {
        // check if existing
        var fetchedRoom = getResultObject(ROOM_KEY, roomId, Room.class);
        if (fetchedRoom == null) {
            throw new IllegalArgumentException(String.format("%s room not found", roomId));
        }
        // check if host is the same
        if (!fetchedRoom.getHost().equals(room.getHost())) {
            throw new IllegalArgumentException(String.format("%s host id is not invalid", room.getHost()));
        }

        // update room
        var updatedRoom = fetchedRoom.updateRoom(roomId, room);

        redisTemplate.opsForHash().put(ROOM_KEY, roomId, updatedRoom);

        // check if updated right
        fetchedRoom = getResultObject(ROOM_KEY, updatedRoom.getId(), Room.class);
        log.info("RoomRepositoryImpl-update: {}", fetchedRoom);

        return Room.convertToDTO(fetchedRoom);
    }

    @Override
    public String delete(String id) {
        // check if existing
        var fetchedRoom = getResultObject(ROOM_KEY, id, Room.class);

        if (fetchedRoom == null) {
            throw new IllegalArgumentException(String.format("%s room not found", id));
        }

        // delete room
        redisTemplate.opsForHash().delete(ROOM_KEY, id);

        // check if deleted right
        fetchedRoom = getResultObject(ROOM_KEY, id, Room.class);
        if (fetchedRoom != null) {
            throw new IllegalArgumentException(String.format("%s room delete failed", id));
        }

        return id;
    }

    @Override
    public Room findById(String id) {
        // fetch the room;
        var room = getResultObject(ROOM_KEY, id, Room.class);
        log.info("RoomRepositoryImpl-findById: {}", room);
        if (room == null) {
            throw new IllegalArgumentException(String.format(ROOM_NOT_FOUND_EXCEPTION, id));
        }
        return room;
    }

    @Override
    public Room addUser(String username, User userToAdd, Room room) {
        // find user by userId
        var user = userRepository.findById(userToAdd.getId());
        // if user does not exist
        if (user == null) throw new IllegalArgumentException(String.format(USER_NOT_FOUND_EXCEPTION, userToAdd.getId()));

        // if the user already exists in the room, return just the room
        if (room.getUsers().containsKey(user.getId())) return room;

        // set room and username in user object
        userRepository.addRoom(user, room.getId());
        userRepository.setNameInRoom(room.getId(), username, user);

        // put user into the room
        room = room.addParticipate(user.setBeforeJoiningRoom(room, username));

        // save room info
        redisTemplate.opsForHash().put(ROOM_KEY, room.getId(), room);
        log.info("RoomRepositoryImpl-addUser: added user to room is {}", room);
        return room;
    }

    @Override
    public Room applyUpdatedUser(User user, Room room) {
        redisTemplate.opsForHash().put(ROOM_KEY, room.getId(), room.applyUpdatedUser(user));
        return room;
    }

    @Override
    public void addConversation(Message message, Room room) {
        room.addConversation(message);
        log.info("room after addConversation is {}", room);
        redisTemplate.opsForHash().put(ROOM_KEY, room.getId(), room);
    }

    @Override
    public Room updateUserStatus(Room room, String userId, User userToUpdate) {
        var user = room.getUsers().get(userId);
        if (user == null) throw new IllegalArgumentException(String.format(USER_NOT_FOUND_EXCEPTION, userId));
        user = user.updateUser(room, userToUpdate);
        room = room.applyUpdatedUser(user);
        redisTemplate.opsForHash().put(ROOM_KEY, room.getId(), room);
        return room;
    }

    @Override
    public Room updateUserName(String name, String userId, Room room) {
        var user = room.getUsers().get(userId);

        if (user == null) throw new IllegalArgumentException(String.format(USER_NOT_FOUND_EXCEPTION, userId));

        room = room.applyUpdatedUser(user.updateName(room, name));

        redisTemplate.opsForHash().put(ROOM_KEY, room.getId(), room);

        return room;
    }
}
