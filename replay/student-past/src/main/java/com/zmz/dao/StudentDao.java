package com.zmz.dao;



import com.zmz.entity.Student;
import com.zmz.util.JdbcUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 学生数据访问层
 */
public class StudentDao {
    
    /**
     * 新增学生
     * @param student 学生信息
     * @return 是否成功
     */
    public boolean save(Student student) {
        String sql = "INSERT INTO student (avatar, student_code, code, grade_type, grade_id, clazz_id, name, " +
                "nick_name, nick_name_approval_status, nick_name_set_time, nick_name_approval_opinion, " +
                "nick_name_approval_time, password, sex, birthday, id_card, id_card_after, other, " +
                "deleted, update_time, create_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, student.getAvatar());
            ps.setString(2, student.getStudentCode());
            ps.setString(3, student.getCode());
            ps.setString(4, student.getGradeType());
            ps.setObject(5, student.getGradeId());
            ps.setObject(6, student.getClazzId());
            ps.setString(7, student.getName());
            ps.setString(8, student.getNickName());
            ps.setString(9, student.getNickNameApprovalStatus());
            ps.setTimestamp(10, student.getNickNameSetTime() != null ? new Timestamp(student.getNickNameSetTime().getTime()) : null);
            ps.setString(11, student.getNickNameApprovalOpinion());
            ps.setTimestamp(12, student.getNickNameApprovalTime() != null ? new Timestamp(student.getNickNameApprovalTime().getTime()) : null);
            ps.setString(13, student.getPassword());
            ps.setInt(14, student.getSex());
            ps.setDate(15, student.getBirthday() != null ? new Date(student.getBirthday().getTime()) : null);
            ps.setString(16, student.getIdCard());
            ps.setString(17, student.getIdCardAfter());
            ps.setString(18, student.getOther());
            ps.setBoolean(19, student.getDeleted());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 根据ID查询学生
     * @param id 学生ID
     * @return 学生信息
     */
    public Student getById(Long id) {
        String sql = "SELECT * FROM student WHERE id = ? AND deleted = 0";
        
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToStudent(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 更新学生信息
     * @param student 学生信息
     * @return 是否成功
     */
    public boolean update(Student student) {
        String sql = "UPDATE student SET avatar = ?, student_code = ?, code = ?, grade_type = ?, " +
                "grade_id = ?, clazz_id = ?, name = ?, nick_name = ?, nick_name_approval_status = ?, " +
                "nick_name_set_time = ?, nick_name_approval_opinion = ?, nick_name_approval_time = ?, " +
                "password = ?, sex = ?, birthday = ?, id_card = ?, id_card_after = ?, other = ?, " +
                "update_time = NOW() WHERE id = ? AND deleted = 0";
        
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, student.getAvatar());
            ps.setString(2, student.getStudentCode());
            ps.setString(3, student.getCode());
            ps.setString(4, student.getGradeType());
            ps.setObject(5, student.getGradeId());
            ps.setObject(6, student.getClazzId());
            ps.setString(7, student.getName());
            ps.setString(8, student.getNickName());
            ps.setString(9, student.getNickNameApprovalStatus());
            ps.setTimestamp(10, student.getNickNameSetTime() != null ? new Timestamp(student.getNickNameSetTime().getTime()) : null);
            ps.setString(11, student.getNickNameApprovalOpinion());
            ps.setTimestamp(12, student.getNickNameApprovalTime() != null ? new Timestamp(student.getNickNameApprovalTime().getTime()) : null);
            ps.setString(13, student.getPassword());
            ps.setInt(14, student.getSex());
            ps.setDate(15, student.getBirthday() != null ? new Date(student.getBirthday().getTime()) : null);
            ps.setString(16, student.getIdCard());
            ps.setString(17, student.getIdCardAfter());
            ps.setString(18, student.getOther());
            ps.setLong(19, student.getId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 删除学生（软删除）
     * @param id 学生ID
     * @return 是否成功
     */
    public boolean delete(Long id) {
        String sql = "UPDATE student SET deleted = 1, update_time = NOW() WHERE id = ?";
        
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 查询学生列表
     * @param name 学生姓名（可选）
     * @return 学生列表
     */
    public List<Student> list(String name) {
        String sql = "SELECT * FROM student WHERE deleted = 0";
        if (name != null && !name.trim().isEmpty()) {
            sql += " AND name LIKE ?";
        }
        
        List<Student> students = new ArrayList<>();
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            if (name != null && !name.trim().isEmpty()) {
                ps.setString(1, "%" + name + "%");
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }
    
    /**
     * 将结果集映射为学生对象
     * @param rs 结果集
     * @return 学生对象
     */
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getLong("id"));
        student.setAvatar(rs.getString("avatar"));
        student.setStudentCode(rs.getString("student_code"));
        student.setCode(rs.getString("code"));
        student.setGradeType(rs.getString("grade_type"));
        student.setGradeId(rs.getObject("grade_id", Long.class));
        student.setClazzId(rs.getObject("clazz_id", Long.class));
        student.setName(rs.getString("name"));
        student.setNickName(rs.getString("nick_name"));
        student.setNickNameApprovalStatus(rs.getString("nick_name_approval_status"));
        student.setNickNameSetTime(rs.getTimestamp("nick_name_set_time"));
        student.setNickNameApprovalOpinion(rs.getString("nick_name_approval_opinion"));
        student.setNickNameApprovalTime(rs.getTimestamp("nick_name_approval_time"));
        student.setPassword(rs.getString("password"));
        student.setSex(rs.getInt("sex"));
        student.setBirthday(rs.getDate("birthday"));
        student.setIdCard(rs.getString("id_card"));
        student.setIdCardAfter(rs.getString("id_card_after"));
        student.setOther(rs.getString("other"));
        student.setDeleted(rs.getBoolean("deleted"));
        student.setUpdateTime(rs.getTimestamp("update_time"));
        student.setCreateTime(rs.getTimestamp("create_time"));
        return student;
    }
} 