package zmz.dao;

import zmz.entity.LessonPlan;
import zmz.util.JdbcUtil;

import java.sql.*;
import java.util.*;

public class LessonPlanDao {
    public boolean save(LessonPlan plan) {
        String sql = "INSERT INTO lesson_plan (subject_id, textbook_version_label_id, year_label_id, textbook_id, chapter_id, table_of_contents, batch_id, page_view, version, weeks, topic_name, lesson_plan_mode, teaching_plan, teaching_plan_annex, analysis, analysis_annex, tools, tools_annex, resources, design_description, design_description_annex, goal, goal_annex, key_point, key_point_annex, way, way_annex, process, process_annex, teach_reflection, teach_reflection_annex, purview, user_id, deleted, grade_type, create_by, create_time, update_by, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, plan.getSubjectId());
            ps.setLong(2, plan.getTextbookVersionLabelId());
            ps.setLong(3, plan.getYearLabelId());
            ps.setLong(4, plan.getTextbookId());
            ps.setLong(5, plan.getChapterId());
            ps.setString(6, plan.getTableOfContents());
            ps.setObject(7, plan.getBatchId());
            ps.setObject(8, plan.getPageView());
            ps.setInt(9, plan.getVersion());
            ps.setObject(10, plan.getWeeks());
            ps.setString(11, plan.getTopicName());
            ps.setObject(12, plan.getLessonPlanMode());
            ps.setString(13, plan.getTeachingPlan());
            ps.setString(14, plan.getTeachingPlanAnnex());
            ps.setString(15, plan.getAnalysis());
            ps.setString(16, plan.getAnalysisAnnex());
            ps.setString(17, plan.getTools());
            ps.setString(18, plan.getToolsAnnex());
            ps.setString(19, plan.getResources());
            ps.setString(20, plan.getDesignDescription());
            ps.setString(21, plan.getDesignDescriptionAnnex());
            ps.setString(22, plan.getGoal());
            ps.setString(23, plan.getGoalAnnex());
            ps.setString(24, plan.getKeyPoint());
            ps.setString(25, plan.getKeyPointAnnex());
            ps.setString(26, plan.getWay());
            ps.setString(27, plan.getWayAnnex());
            ps.setString(28, plan.getProcess());
            ps.setString(29, plan.getProcessAnnex());
            ps.setString(30, plan.getTeachReflection());
            ps.setString(31, plan.getTeachReflectionAnnex());
            ps.setInt(32, plan.getPurview());
            ps.setObject(33, plan.getUserId());
            ps.setInt(34, plan.getDeleted());
            ps.setString(35, plan.getGradeType());
            ps.setString(36, plan.getCreateBy());
            ps.setTimestamp(37, new Timestamp(plan.getCreateTime().getTime()));
            ps.setString(38, plan.getUpdateBy());
            ps.setTimestamp(39, new Timestamp(plan.getUpdateTime().getTime()));
            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    plan.setLessonPlanId(rs.getLong(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public LessonPlan getById(Long id) {
        String sql = "SELECT * FROM lesson_plan WHERE lesson_plan_id = ? AND deleted = 0";
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToLessonPlan(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(LessonPlan plan) {
        String sql = "UPDATE lesson_plan SET subject_id=?, textbook_version_label_id=?, year_label_id=?, textbook_id=?, chapter_id=?, table_of_contents=?, batch_id=?, page_view=?, version=?, weeks=?, topic_name=?, lesson_plan_mode=?, teaching_plan=?, teaching_plan_annex=?, analysis=?, analysis_annex=?, tools=?, tools_annex=?, resources=?, design_description=?, design_description_annex=?, goal=?, goal_annex=?, key_point=?, key_point_annex=?, way=?, way_annex=?, process=?, process_annex=?, teach_reflection=?, teach_reflection_annex=?, purview=?, user_id=?, deleted=?, grade_type=?, update_by=?, update_time=? WHERE lesson_plan_id=?";
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, plan.getSubjectId());
            ps.setLong(2, plan.getTextbookVersionLabelId());
            ps.setLong(3, plan.getYearLabelId());
            ps.setLong(4, plan.getTextbookId());
            ps.setLong(5, plan.getChapterId());
            ps.setString(6, plan.getTableOfContents());
            ps.setObject(7, plan.getBatchId());
            ps.setObject(8, plan.getPageView());
            ps.setInt(9, plan.getVersion());
            ps.setObject(10, plan.getWeeks());
            ps.setString(11, plan.getTopicName());
            ps.setObject(12, plan.getLessonPlanMode());
            ps.setString(13, plan.getTeachingPlan());
            ps.setString(14, plan.getTeachingPlanAnnex());
            ps.setString(15, plan.getAnalysis());
            ps.setString(16, plan.getAnalysisAnnex());
            ps.setString(17, plan.getTools());
            ps.setString(18, plan.getToolsAnnex());
            ps.setString(19, plan.getResources());
            ps.setString(20, plan.getDesignDescription());
            ps.setString(21, plan.getDesignDescriptionAnnex());
            ps.setString(22, plan.getGoal());
            ps.setString(23, plan.getGoalAnnex());
            ps.setString(24, plan.getKeyPoint());
            ps.setString(25, plan.getKeyPointAnnex());
            ps.setString(26, plan.getWay());
            ps.setString(27, plan.getWayAnnex());
            ps.setString(28, plan.getProcess());
            ps.setString(29, plan.getProcessAnnex());
            ps.setString(30, plan.getTeachReflection());
            ps.setString(31, plan.getTeachReflectionAnnex());
            ps.setInt(32, plan.getPurview());
            ps.setObject(33, plan.getUserId());
            ps.setInt(34, plan.getDeleted());
            ps.setString(35, plan.getGradeType());
            ps.setString(36, plan.getUpdateBy());
            ps.setTimestamp(37, new Timestamp(plan.getUpdateTime().getTime()));
            ps.setLong(38, plan.getLessonPlanId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Long id) {
        String sql = "UPDATE lesson_plan SET deleted = 1, update_time = NOW() WHERE lesson_plan_id = ?";
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<LessonPlan> list(String topicName) {
        String sql = "SELECT * FROM lesson_plan WHERE deleted = 0";
        if (topicName != null && !topicName.trim().isEmpty()) {
            sql += " AND topic_name LIKE ?";
        }
        List<LessonPlan> list = new ArrayList<>();
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (topicName != null && !topicName.trim().isEmpty()) {
                ps.setString(1, "%" + topicName + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToLessonPlan(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private LessonPlan mapResultSetToLessonPlan(ResultSet rs) throws SQLException {
        LessonPlan plan = new LessonPlan();
        plan.setLessonPlanId(rs.getLong("lesson_plan_id"));
        plan.setSubjectId(rs.getLong("subject_id"));
        plan.setTextbookVersionLabelId(rs.getLong("textbook_version_label_id"));
        plan.setYearLabelId(rs.getLong("year_label_id"));
        plan.setTextbookId(rs.getLong("textbook_id"));
        plan.setChapterId(rs.getLong("chapter_id"));
        plan.setTableOfContents(rs.getString("table_of_contents"));
        plan.setBatchId((Long)rs.getObject("batch_id"));
        plan.setPageView((Integer)rs.getObject("page_view"));
        plan.setVersion(rs.getInt("version"));
        plan.setWeeks((Integer)rs.getObject("weeks"));
        plan.setTopicName(rs.getString("topic_name"));
        plan.setLessonPlanMode((Integer)rs.getObject("lesson_plan_mode"));
        plan.setTeachingPlan(rs.getString("teaching_plan"));
        plan.setTeachingPlanAnnex(rs.getString("teaching_plan_annex"));
        plan.setAnalysis(rs.getString("analysis"));
        plan.setAnalysisAnnex(rs.getString("analysis_annex"));
        plan.setTools(rs.getString("tools"));
        plan.setToolsAnnex(rs.getString("tools_annex"));
        plan.setResources(rs.getString("resources"));
        plan.setDesignDescription(rs.getString("design_description"));
        plan.setDesignDescriptionAnnex(rs.getString("design_description_annex"));
        plan.setGoal(rs.getString("goal"));
        plan.setGoalAnnex(rs.getString("goal_annex"));
        plan.setKeyPoint(rs.getString("key_point"));
        plan.setKeyPointAnnex(rs.getString("key_point_annex"));
        plan.setWay(rs.getString("way"));
        plan.setWayAnnex(rs.getString("way_annex"));
        plan.setProcess(rs.getString("process"));
        plan.setProcessAnnex(rs.getString("process_annex"));
        plan.setTeachReflection(rs.getString("teach_reflection"));
        plan.setTeachReflectionAnnex(rs.getString("teach_reflection_annex"));
        plan.setPurview(rs.getInt("purview"));
        plan.setUserId((Long)rs.getObject("user_id"));
        plan.setDeleted(rs.getInt("deleted"));
        plan.setGradeType(rs.getString("grade_type"));
        plan.setCreateBy(rs.getString("create_by"));
        plan.setCreateTime(rs.getTimestamp("create_time"));
        plan.setUpdateBy(rs.getString("update_by"));
        plan.setUpdateTime(rs.getTimestamp("update_time"));
        return plan;
    }
} 