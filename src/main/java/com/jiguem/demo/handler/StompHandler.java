package com.jiguem.demo.handler;

import com.jiguem.demo.MessageType;
import com.jiguem.demo.dto.MessageDTO;
import com.jiguem.demo.repository.RoomRepository;
import com.jiguem.demo.repository.UserRepository;
import com.jiguem.demo.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Component
public class StompHandler implements ChannelInterceptor {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;

    private String subId = null;
    private String clientId = null;
    private String destination = null;

    private Map<String, String> usernames = new HashMap<>();
    private Map<String, String> userIds = new HashMap<>();

    @Autowired
    public StompHandler(
            @Lazy SimpMessagingTemplate messagingTemplate
            , UserRepository userRepository
            , RoomRepository roomRepository
            , ChatService chatService) {
        this.messagingTemplate = messagingTemplate;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.chatService = chatService;

    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        switch (accessor.getCommand()) {
            case CONNECT:
                log.info("========CONNECTED========");
                break;
            case SUBSCRIBE:
                log.info("========SUBSCRIBE========");
                destination = accessor.getDestination();

                MultiValueMap<String, String> headers = message.getHeaders().get("nativeHeaders", MultiValueMap.class);
                clientId = headers.get("clientId").get(0);
                subId = headers.get("roomId").get(0);
                String username = roomRepository.findById(subId).getUsers().get(clientId).getUsername(subId);
                String userId = roomRepository.findById(subId).getUsers().get(clientId).getId();
                usernames.put(accessor.getSessionId(), username);
                userIds.put(accessor.getSessionId(), userId);

                break;
            case DISCONNECT:
//                log.info("========DISCONNECT========");
//                log.info("accessor is [}, message is {}, channel is {}", accessor, message, channel);
//                log.info("destination is {}", destination);
//                log.info("user name is {}", this.usernames.get(accessor.getSessionId()));
//                String sender = this.usernames.get(accessor.getSessionId());
//                String senderId = this.userIds.get(accessor.getSessionId());
//                if (destination != null) {
//                    //String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser")).map(Principal::getName).orElse("UnknownUser");
//                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
//                    log.info("timestamp is {}", timestamp);
//                    MessageDTO messageDTO = MessageDTO.builder().type(MessageType.LEAVE).roomId(subId).senderId(senderId).sender(sender).content("앱을 종료하셨습니다.").timestamp(LocalDateTime.now()).build();
//                    chatService.save(messageDTO);
//                    messagingTemplate.convertAndSend(destination, messageDTO);
//                }
                break;
            default:
                break;
        }
        return message;
    }
}
