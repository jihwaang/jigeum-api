package com.jiguem.demo.controller;

import com.jiguem.demo.dto.RoomDTO;
import com.jiguem.demo.dto.UserDTO;
import com.jiguem.demo.entity.Room;
import com.jiguem.demo.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/rooms")
@RestController
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDTO> save(@RequestBody RoomDTO roomDTO) {
        log.info("RoomController-save: {}", roomDTO);
        RoomDTO room = roomService.create(roomDTO);
        return ResponseEntity.ok(room);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomDTO> update(@PathVariable String id, @RequestBody RoomDTO roomDTO) {
        log.info("RoomController-update: id {}, dto {}", id, roomDTO);
        RoomDTO room = roomService.update(id, roomDTO);
        return ResponseEntity.ok(room);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        log.info("RoomController-delete: {}", id);
        String result = roomService.delete(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> find(@PathVariable String id/*, @RequestBody UserDTO userDTO*/) {
        log.info("RoomController-find: id is {}", id);
        RoomDTO roomDTO = roomService.findById(id);
        log.info("RoomController-find: room is {}", roomDTO);
        return ResponseEntity.ok(roomDTO);
    }

    @GetMapping("/{id}/users/{userId}")
    public ResponseEntity<UserDTO> findUser(@PathVariable String id, @PathVariable String userId) {
        log.info("RoomController-findUser : id is {}, userId is {}", id, userId);
        UserDTO user = roomService.findUser(id, userId);
        log.info("user in the room is: {}", user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{roomId}")
    public ResponseEntity<RoomDTO> enter(@PathVariable String roomId, @RequestBody UserDTO userDTO) {
        log.info("RoomController-find: id is {}, user is {}", roomId, userDTO);
        RoomDTO room = roomService.findByIdThenAddUser(roomId, userDTO);
        return ResponseEntity.ok(room);
    }

    @PutMapping("/{roomId}/users/{userId}")
    public ResponseEntity<RoomDTO> updateUser(@PathVariable String roomId, @PathVariable String userId, @RequestBody UserDTO userDTO) {
        log.info("RoomController-updateUser: roomId is {}, userId is {}, userDTO is {}", roomId, userId, userDTO);
        RoomDTO room = roomService.updateUser(roomId, userId, userDTO);
        return ResponseEntity.ok(room);
    }

}
