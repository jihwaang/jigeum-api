package com.jiguem.demo.entity;

import com.jiguem.demo.dto.RoomDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Room {

    private String id;
    private String host;
    private String hostName;
    private String title;
    private String isPrivate;
    private String password;
    private String placeIn;
    private String peopleNumber;
    private LocalDateTime placeAt;
    private String beforeStart;
    private String afterEnd;
    private String longitude;
    private String latitude;

    private List<Message> conversation;
    private Map<String, User> users;

    public void addConversation(Message message) {
        if (conversation == null) {
            conversation = new ArrayList<>();
        }
        this.conversation.add(message);
        log.info("conversations are: {}", conversation);
    }

    public Room addParticipate(User user) {
        if (users == null) {
            users = new HashMap<>();
        }
        this.users.put(user.getId(), user);
        return this;
    }

    public Room updateRoom(String roomId, Room room) {

        this.id = roomId;
        this.host = room.getHost();

        if (room.getHostName() == null || !room.getHostName().equals(this.hostName)) {
            if (room.getHostName() == null) {
                this.hostName = "방장";
            } else {
                this.hostName = updateHostName(roomId, room.getHostName());
            }
        }

        this.title = room.getTitle();
        this.isPrivate = room.getIsPrivate();
        this.password = room.getPassword();
        this.placeIn = room.getPlaceIn();
        this.peopleNumber = room.getPeopleNumber();
        this.placeAt = room.getPlaceAt();
        this.beforeStart = room.getBeforeStart();
        this.afterEnd = room.getAfterEnd();
        this.longitude = room.getLongitude();
        this.latitude = room.getLatitude();

        return this;
    }

    private String updateHostName(String roomId, String name) {
        this.users.replace(this.users.get(host).getId(), this.users.get(host), this.users.get(host).updateName(this, name));
        return name;
    }

    public static RoomDTO convertToDTO(Room room) {
        return RoomDTO.builder()
                .id(room.getId())
                .host(room.getHost())
                .hostName(room.getHostName())
                .title(room.getTitle())
                .isPrivate(room.getIsPrivate())
                .password(room.getPassword())
                .placeIn(room.getPlaceIn())
                .peopleNumber(room.getPeopleNumber())
                .placeAt(room.getPlaceAt())
                .beforeStart(room.getBeforeStart())
                .afterEnd(room.getAfterEnd())
                .longitude(room.getLongitude())
                .latitude(room.getLatitude())
                .conversation(room.getConversation())
                .users(room.getUsers())
                .build();
    }

    public Room applyUpdatedUser(User user) {
        // check if name changed
        boolean nameChanged = this.users.get(user.getId()).getName().equals(user.getName());
        // check if user is host
        boolean isHost = this.host.equals(user.getId());
        // if host then update host name
        if(isHost) this.hostName = user.getUsername(this.id);
        // update user nickname of the instance
        this.users.replace(user.getId(), this.users.get(user.getId()), user);
        // update user nickname of messages
        if (nameChanged && this.conversation != null) {
            this.conversation.stream()
                    .filter(message -> Objects.equals(message.getSenderId(), user.getId()))
                    .forEach(message -> message.setSender(user.getName().get(this.getId())));
        }
        return this;
    }
}
