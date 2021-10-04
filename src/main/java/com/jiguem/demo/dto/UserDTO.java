package com.jiguem.demo.dto;

import com.jiguem.demo.entity.Room;
import com.jiguem.demo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {

    private String id;
    private String name;
    private Boolean isToBeLate;
    private Boolean isBanned;
    private String lastLongitude;
    private String lastLatitude;

    public static User toEntity(String roomId, UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .isToBeLate(userDTO.getIsToBeLate() != null && userDTO.getIsToBeLate())
                .isBanned(userDTO.getIsBanned() != null && userDTO.getIsBanned())
                .build();
    }

}
