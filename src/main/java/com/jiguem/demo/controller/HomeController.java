package com.jiguem.demo.controller;

import com.jiguem.demo.dto.UserDTO;
import com.jiguem.demo.entity.User;
import com.jiguem.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Controller
public class HomeController {

    @RequestMapping({"/", ""})
    public String index() {
        return "index";
    }

    @RequestMapping("/room/{id}")
    public String connectRoom(@PathVariable String id, Model model) {
        log.info("HomeController-connectRoom: id is {}", id);
        model.addAttribute("roomId", id);
        return "room/chat";
    }
}
