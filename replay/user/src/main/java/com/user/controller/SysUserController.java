package com.user.controller;


import com.user.dto.LoginDTO;
import com.user.dto.RegisterDTO;
import com.user.entity.SysUser;
import com.user.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Validated @RequestBody LoginDTO loginDTO) {
        SysUser user = sysUserService.login(loginDTO);
        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        // 这里可以生成token等登录凭证
        return ResponseEntity.ok(result);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Validated @RequestBody RegisterDTO registerDTO) {
        sysUserService.register(registerDTO);
        return ResponseEntity.ok().build();
    }
} 