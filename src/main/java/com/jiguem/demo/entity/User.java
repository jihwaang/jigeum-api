package com.jiguem.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    public enum ROLE {
        HOST, PARTICIPANT
    }

    private String id;
    private Map<String, String> name;
    private Boolean isBanned;
    private String lastLongitude;
    private String lastLatitude;
    private Boolean isTooLate;
    private Boolean yetToArrive;
    private ROLE role;
    private String color;
    private Boolean isSharing;

    private Set<String> rooms;

    public void setRoleToHost() {
        this.role = ROLE.HOST;
    }

    public void setRoleToParticipate() {
        this.role = ROLE.PARTICIPANT;
    }

    public User updateName(Room room, String username) {
        return replaceName(room, username);
    }

    private User replaceName(Room room, String username) {
        if (this.name == null) {
            this.name = new HashMap<>();
            this.name.put(room.getId(), username);
        } else {
            this.name.replace(room.getId(), this.name.get(room.getId()), username);
        }
        return this;
    }

    public User updateHostName(Room room, String hostname) {
        return replaceName(room, hostname);
    }

    public void addRoomId(String roomId) {
        if (this.rooms == null) {
            rooms = new HashSet<>();
        }
        this.rooms.add(roomId);
    }

    public User setBeforeJoiningRoom(Room room, String username) {
        this.addRoomId(room.getId());
        this.role = this.id.equals(room.getHost()) ? ROLE.HOST : ROLE.PARTICIPANT;
        this.setRandomColor();
        this.setDefaultStatus();
        return updateName(room, username);
    }

    public String getUsername(String roomId) {
        return this.name.get(roomId);
    }

    public User updateUsername(String roomId, User user) {
        if(!this.name.replace(roomId, this.name.get(roomId), user.getUsername(roomId))) {
            throw new IllegalStateException(String.format("updateUsername failed with roomId %s, user %s",roomId, user));
        }
        return user;
    }

    public User updateUser(Room room, User userToUpdate) {
        String roomId = room.getId();
        String name = this.name.get(roomId);

        this.isTooLate = (userToUpdate.getIsTooLate() != null && userToUpdate.getIsTooLate());
        this.yetToArrive = (userToUpdate.getYetToArrive() != null && userToUpdate.getYetToArrive());
        this.isBanned = (userToUpdate.getIsBanned() != null && userToUpdate.getIsBanned());
        this.isSharing = (userToUpdate.getIsSharing() != null && userToUpdate.getIsSharing());

        return this;
    }

    public void setNameInRoom(String roomId, String name) {
        if (this.name == null) {
            this.name = new HashMap<>();
            this.name.put(roomId, name);
        } else {
            if (this.name.get(roomId) == null) {
                this.name.put(roomId, name);
            } else {
                this.name.replace(roomId, this.name.get(roomId), name);
            }

        }
    }

    public void setRandomColor() {
        Random rand = new Random();
        int color = rand.nextInt(0xFFFFFF+1);
        this.color = String.format("#%06x", color);
    }

    public void setDefaultStatus() {
        this.setIsBanned(false);
        this.setIsTooLate(false);
        this.setYetToArrive(true);
        this.setIsSharing(false);
    }

    public User updateLocation(Message message) {
        this.setLastLatitude(message.getLatitude());
        this.setLastLongitude(message.getLongitude());
        return this;
    }

}
