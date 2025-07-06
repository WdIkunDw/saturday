package com.user.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RegisterDTO {
    @NotNull(message = "账号类型不能为空")
    private Integer type;
    
    @NotBlank(message = "账号不能为空")
    private String accountNo;
    
    @NotBlank(message = "用户名不能为空")
    private String userName;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @NotBlank(message = "手机号不能为空")
    private String phone;
    
    private String idCard;
} 