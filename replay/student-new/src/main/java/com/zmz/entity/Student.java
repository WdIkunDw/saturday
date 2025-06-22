package com.zmz.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

/**
 * 学生实体类
 */
@Data
@TableName("student")
public class Student {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 学籍号
     */
    private String studentCode;
    
    /**
     * 学号
     */
    private String code;
    
    /**
     * 年级类型：初中、高中
     */
    private String gradeType;
    
    /**
     * 年级id
     */
    private Long gradeId;
    
    /**
     * 班级id
     */
    private Long clazzId;
    
    /**
     * 姓名
     */
    private String name;
    
    /**
     * 昵称
     */
    private String nickName;
    
    /**
     * 昵称审核状态
     */
    private String nickNameApprovalStatus;
    
    /**
     * 昵称设置时间
     */
    private Date nickNameSetTime;
    
    /**
     * 昵称审核意见
     */
    private String nickNameApprovalOpinion;
    
    /**
     * 昵称审核时间
     */
    private Date nickNameApprovalTime;
    
    /**
     * 学生密码
     */
    private String password;
    
    /**
     * 性别 0未知 1男 2女
     */
    private Integer sex;
    
    /**
     * 出生年月
     */
    private Date birthday;
    
    /**
     * 身份证
     */
    private String idCard;
    
    /**
     * 身份证后6位(独立使用)
     */
    private String idCardAfter;
    
    /**
     * 其它信息
     */
    private String other;
    
    /**
     * 是否删除 0正常 1删除
     */
    @TableLogic
    private Boolean deleted;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
} 