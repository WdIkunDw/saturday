package zmz.entity;

import lombok.Data;

import java.util.Date;
@Data
public class Term {
    private Long termId;
    private String termName;
    private Date termStartDate;
    private Date termEndDate;
    private Boolean isCurrentTerm;
    private Boolean deleted;
    private String gradeType;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
} 