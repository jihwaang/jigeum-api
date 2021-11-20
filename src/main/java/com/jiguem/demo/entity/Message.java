package com.jiguem.demo.entity;

import com.jiguem.demo.MessageType;
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
public class Message {

    private MessageType type;
    private String content;
    private String sender;
    private String receiver;
    private String roomId;
    private String senderId;
    private String latitude;
    private String longitude;
    private LocalDateTime timestamp;

    public Message createTimestramp() {
        this.timestamp = LocalDateTime.now();
        return this;
    }
}
