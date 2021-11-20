package com.jiguem.demo.dto;

import com.jiguem.demo.MessageType;
import com.jiguem.demo.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageDTO {

    private MessageType type;
    private String content;
    private String sender;
    private String receiver;
    private String roomId;
    private String senderId;
    private String latitude;
    private String longitude;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public static Message toEntity(MessageDTO messageDTO) {
        return Message.builder()
                .type(messageDTO.getType())
                .content(messageDTO.getContent())
                .sender(messageDTO.getSender())
                .receiver(messageDTO.getReceiver())
                .roomId(messageDTO.getRoomId())
                .senderId(messageDTO.getSenderId())
                .latitude((messageDTO.getLatitude()))
                .longitude((messageDTO.getLongitude()))
                .timestamp(messageDTO.getTimestamp())
                .build();
    }
}
