package com.user.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class LoginDTO {
    @NotBlank(message = "账号不能为空")
    private String accountNo;
    
    @NotBlank(message = "密码不能为空")
    private String password;
} 