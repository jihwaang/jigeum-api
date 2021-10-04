package com.jiguem.demo.dto;

import com.jiguem.demo.entity.Message;
import com.jiguem.demo.entity.Room;
import com.jiguem.demo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoomDTO {
    private String id;
    private String host;
    private String hostName;
    private String title;
    private String isPrivate;
    private String password;
    private String placeIn;
    private String peopleNumber;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime placeAt;
    private String beforeStart;
    private String afterEnd;
    private String longitude;
    private String latitude;

    private List<Message> conversation;
    private Map<String, User> users;

    public static Room toEntity(RoomDTO dto) {
        return Room.builder()
                .host(dto.getHost())
                .hostName(dto.getHostName())
                .title(dto.getTitle())
                .isPrivate(dto.getIsPrivate())
                .password(dto.getPassword())
                .placeIn(dto.getPlaceIn())
                .peopleNumber(dto.getPeopleNumber())
                .placeAt(dto.getPlaceAt())
                .beforeStart(dto.getBeforeStart())
                .afterEnd(dto.getAfterEnd())
                .longitude(dto.getLongitude())
                .latitude(dto.getLatitude())
                .conversation(dto.getConversation())
                .users(dto.getUsers())
                .build();
    }
}
