package zmz.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class LessonPlan {
    private Long lessonPlanId;
    private Long subjectId;
    private Long textbookVersionLabelId;
    private Long yearLabelId;
    private Long textbookId;
    private Long chapterId;
    private String tableOfContents; // JSON
    private Long batchId;
    private Integer pageView;
    private Integer version;
    private Integer weeks;
    private String topicName;
    private Integer lessonPlanMode;
    private String teachingPlan;
    private String teachingPlanAnnex; // JSON
    private String analysis;
    private String analysisAnnex; // JSON
    private String tools;
    private String toolsAnnex; // JSON
    private String resources; // JSON
    private String designDescription;
    private String designDescriptionAnnex; // JSON
    private String goal;
    private String goalAnnex; // JSON
    private String keyPoint;
    private String keyPointAnnex; // JSON
    private String way;
    private String wayAnnex; // JSON
    private String process;
    private String processAnnex; // JSON
    private String teachReflection;
    private String teachReflectionAnnex; // JSON
    private Integer purview;
    private Long userId;
    private Integer deleted;
    private String gradeType;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;

    // getter and setter methods
    public Long getLessonPlanId() { return lessonPlanId; }
    public void setLessonPlanId(Long lessonPlanId) { this.lessonPlanId = lessonPlanId; }
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public Long getTextbookVersionLabelId() { return textbookVersionLabelId; }
    public void setTextbookVersionLabelId(Long textbookVersionLabelId) { this.textbookVersionLabelId = textbookVersionLabelId; }
    public Long getYearLabelId() { return yearLabelId; }
    public void setYearLabelId(Long yearLabelId) { this.yearLabelId = yearLabelId; }
    public Long getTextbookId() { return textbookId; }
    public void setTextbookId(Long textbookId) { this.textbookId = textbookId; }
    public Long getChapterId() { return chapterId; }
    public void setChapterId(Long chapterId) { this.chapterId = chapterId; }
    public String getTableOfContents() { return tableOfContents; }
    public void setTableOfContents(String tableOfContents) { this.tableOfContents = tableOfContents; }
    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    public Integer getPageView() { return pageView; }
    public void setPageView(Integer pageView) { this.pageView = pageView; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public Integer getWeeks() { return weeks; }
    public void setWeeks(Integer weeks) { this.weeks = weeks; }
    public String getTopicName() { return topicName; }
    public void setTopicName(String topicName) { this.topicName = topicName; }
    public Integer getLessonPlanMode() { return lessonPlanMode; }
    public void setLessonPlanMode(Integer lessonPlanMode) { this.lessonPlanMode = lessonPlanMode; }
    public String getTeachingPlan() { return teachingPlan; }
    public void setTeachingPlan(String teachingPlan) { this.teachingPlan = teachingPlan; }
    public String getTeachingPlanAnnex() { return teachingPlanAnnex; }
    public void setTeachingPlanAnnex(String teachingPlanAnnex) { this.teachingPlanAnnex = teachingPlanAnnex; }
    public String getAnalysis() { return analysis; }
    public void setAnalysis(String analysis) { this.analysis = analysis; }
    public String getAnalysisAnnex() { return analysisAnnex; }
    public void setAnalysisAnnex(String analysisAnnex) { this.analysisAnnex = analysisAnnex; }
    public String getTools() { return tools; }
    public void setTools(String tools) { this.tools = tools; }
    public String getToolsAnnex() { return toolsAnnex; }
    public void setToolsAnnex(String toolsAnnex) { this.toolsAnnex = toolsAnnex; }
    public String getResources() { return resources; }
    public void setResources(String resources) { this.resources = resources; }
    public String getDesignDescription() { return designDescription; }
    public void setDesignDescription(String designDescription) { this.designDescription = designDescription; }
    public String getDesignDescriptionAnnex() { return designDescriptionAnnex; }
    public void setDesignDescriptionAnnex(String designDescriptionAnnex) { this.designDescriptionAnnex = designDescriptionAnnex; }
    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }
    public String getGoalAnnex() { return goalAnnex; }
    public void setGoalAnnex(String goalAnnex) { this.goalAnnex = goalAnnex; }
    public String getKeyPoint() { return keyPoint; }
    public void setKeyPoint(String keyPoint) { this.keyPoint = keyPoint; }
    public String getKeyPointAnnex() { return keyPointAnnex; }
    public void setKeyPointAnnex(String keyPointAnnex) { this.keyPointAnnex = keyPointAnnex; }
    public String getWay() { return way; }
    public void setWay(String way) { this.way = way; }
    public String getWayAnnex() { return wayAnnex; }
    public void setWayAnnex(String wayAnnex) { this.wayAnnex = wayAnnex; }
    public String getProcess() { return process; }
    public void setProcess(String process) { this.process = process; }
    public String getProcessAnnex() { return processAnnex; }
    public void setProcessAnnex(String processAnnex) { this.processAnnex = processAnnex; }
    public String getTeachReflection() { return teachReflection; }
    public void setTeachReflection(String teachReflection) { this.teachReflection = teachReflection; }
    public String getTeachReflectionAnnex() { return teachReflectionAnnex; }
    public void setTeachReflectionAnnex(String teachReflectionAnnex) { this.teachReflectionAnnex = teachReflectionAnnex; }
    public Integer getPurview() { return purview; }
    public void setPurview(Integer purview) { this.purview = purview; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
    public String getGradeType() { return gradeType; }
    public void setGradeType(String gradeType) { this.gradeType = gradeType; }
    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
} 