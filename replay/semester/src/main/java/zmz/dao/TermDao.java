package zmz.dao;

import zmz.entity.Term;
import zmz.util.JdbcUtil;

import java.sql.*;
import java.util.*;

public class TermDao {
    public boolean save(Term term) {
        String sql = "INSERT INTO term (term_id, term_name, term_start_date, term_end_date, is_current_term, deleted, grade_type, create_by, create_time, update_by, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, term.getTermId());
            ps.setString(2, term.getTermName());
            ps.setDate(3, new java.sql.Date(term.getTermStartDate().getTime()));
            ps.setDate(4, new java.sql.Date(term.getTermEndDate().getTime()));
            ps.setBoolean(5, term.getIsCurrentTerm());
            ps.setBoolean(6, term.getDeleted());
            ps.setString(7, term.getGradeType());
            ps.setString(8, term.getCreateBy());
            ps.setTimestamp(9, new Timestamp(term.getCreateTime().getTime()));
            ps.setString(10, term.getUpdateBy());
            ps.setTimestamp(11, new Timestamp(term.getUpdateTime().getTime()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Term getById(Long termId) {
        String sql = "SELECT * FROM term WHERE term_id = ? AND deleted = 0";
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, termId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToTerm(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(Term term) {
        String sql = "UPDATE term SET term_name=?, term_start_date=?, term_end_date=?, is_current_term=?, deleted=?, grade_type=?, update_by=?, update_time=? WHERE term_id=?";
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, term.getTermName());
            ps.setDate(2, new java.sql.Date(term.getTermStartDate().getTime()));
            ps.setDate(3, new java.sql.Date(term.getTermEndDate().getTime()));
            ps.setBoolean(4, term.getIsCurrentTerm());
            ps.setBoolean(5, term.getDeleted());
            ps.setString(6, term.getGradeType());
            ps.setString(7, term.getUpdateBy());
            ps.setTimestamp(8, new Timestamp(term.getUpdateTime().getTime()));
            ps.setLong(9, term.getTermId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long termId) {
        String sql = "UPDATE term SET deleted = 1, update_time = NOW() WHERE term_id = ?";
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, termId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Term> list(String termName) {
        String sql = "SELECT * FROM term WHERE deleted = 0";
        if (termName != null && !termName.trim().isEmpty()) {
            sql += " AND term_name LIKE ?";
        }
        List<Term> terms = new ArrayList<>();
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (termName != null && !termName.trim().isEmpty()) {
                ps.setString(1, "%" + termName + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                terms.add(mapResultSetToTerm(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return terms;
    }

    private Term mapResultSetToTerm(ResultSet rs) throws SQLException {
        Term term = new Term();
        term.setTermId(rs.getLong("term_id"));
        term.setTermName(rs.getString("term_name"));
        term.setTermStartDate(rs.getDate("term_start_date"));
        term.setTermEndDate(rs.getDate("term_end_date"));
        term.setIsCurrentTerm(rs.getBoolean("is_current_term"));
        term.setDeleted(rs.getBoolean("deleted"));
        term.setGradeType(rs.getString("grade_type"));
        term.setCreateBy(rs.getString("create_by"));
        term.setCreateTime(rs.getTimestamp("create_time"));
        term.setUpdateBy(rs.getString("update_by"));
        term.setUpdateTime(rs.getTimestamp("update_time"));
        return term;
    }
} 