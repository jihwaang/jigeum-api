package com.jiguem.demo.controller;

import com.jiguem.demo.dto.UserDTO;
import com.jiguem.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping("/users")
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> fetchOrGenerate(@PathVariable String id) {
        log.info("UserController.fetchOrGenerate - id: {}", id);
        UserDTO userDTO = userService.fetchOrGenerate(id);
        log.info("ResponseEntity<UserDTO>: {}", userDTO);
        return ResponseEntity.ok(userDTO);
    }

}
