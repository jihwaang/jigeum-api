package com.jiguem.demo.dto;

import com.jiguem.demo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {

    private String id;
    private String name;
    private Boolean isTooLate;
    private Boolean yetToArrive;
    private Boolean isBanned;
    private String lastLongitude;
    private String lastLatitude;
    private String color;
    private Boolean isSharing;

    public static User toEntity(String roomId, UserDTO userDTO) {
        if (userDTO.getName() != null) {
            return User.builder()
                    .id(userDTO.getId())
                    .name(Map.of(roomId, userDTO.getName()))
                    .isTooLate(userDTO.getIsTooLate())
                    .yetToArrive(userDTO.getYetToArrive())
                    .isBanned(userDTO.getIsBanned())
                    .color(userDTO.getColor())
                    .isSharing(userDTO.getIsSharing())
                    .build();
        }
        return User.builder()
                .id(userDTO.getId())
                .isTooLate(userDTO.getIsTooLate() != null && userDTO.getIsTooLate())
                .yetToArrive((userDTO.getYetToArrive() != null && userDTO.getYetToArrive()))
                .isBanned(userDTO.getIsBanned() != null && userDTO.getIsBanned())
                .isSharing(userDTO.getIsSharing() != null &&  userDTO.getIsSharing())
                .build();
    }

}
