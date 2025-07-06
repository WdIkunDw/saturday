package com.user.entity;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SysUser {
    private Long id;
    private Integer type;
    private String accountNo;
    private String userName;
    private String idCard;
    private String avatar;
    private String phone;
    private String password;
    private Integer status;
    private Integer hasFixInitPsw;
    private String wxOpenId;
    private String unionId;
    private JsonNode gradeTypes;
    private Integer encrypt;
    private Integer deleted;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
} 