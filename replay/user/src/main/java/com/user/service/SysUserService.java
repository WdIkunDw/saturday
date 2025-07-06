package com.user.service;


public interface SysUserService {
    /**
     * 用户登录
     */
    SysUser login(LoginDTO loginDTO);
    
    /**
     * 用户注册
     */
    void register(RegisterDTO registerDTO);
} 