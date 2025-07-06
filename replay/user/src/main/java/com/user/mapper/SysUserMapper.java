package com.user.mapper;

import com.saturday.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserMapper {
    SysUser selectByAccountNo(@Param("accountNo") String accountNo);
    
    SysUser selectByPhone(@Param("phone") String phone);
    
    int insert(SysUser user);
    
    int updateById(SysUser user);
} 