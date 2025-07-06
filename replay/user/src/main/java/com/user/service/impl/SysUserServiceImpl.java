package com.user.service.impl;

import com.user.dto.LoginDTO;
import com.user.dto.RegisterDTO;
import com.user.entity.SysUser;
import com.user.mapper.SysUserMapper;
import com.user.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public SysUser login(LoginDTO loginDTO) {
        SysUser user = sysUserMapper.selectByAccountNo(loginDTO.getAccountNo());
        if (user == null) {
            throw new RuntimeException("账号不存在");
        }
        
        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }
        
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 清除敏感信息
        user.setPassword(null);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDTO) {
        // 检查账号是否已存在
        SysUser existUser = sysUserMapper.selectByAccountNo(registerDTO.getAccountNo());
        if (existUser != null) {
            throw new RuntimeException("账号已存在");
        }
        
        // 检查手机号是否已存在
        existUser = sysUserMapper.selectByPhone(registerDTO.getPhone());
        if (existUser != null) {
            throw new RuntimeException("手机号已被使用");
        }
        
        // 创建用户
        SysUser user = new SysUser();
        user.setId(generateId()); // 需要实现ID生成方法
        user.setType(registerDTO.getType());
        user.setAccountNo(registerDTO.getAccountNo());
        user.setUserName(registerDTO.getUserName());
        user.setPhone(registerDTO.getPhone());
        user.setIdCard(registerDTO.getIdCard());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setStatus(1);
        user.setHasFixInitPsw(1);
        user.setDeleted(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        sysUserMapper.insert(user);
    }
    
    // 生成ID的方法，可以使用雪花算法或其他ID生成策略
    private Long generateId() {
        return System.currentTimeMillis();
    }
} 