// Package: com.tuyensinh.daoWeb
package com.tuyensinh.ModelWeb;

import com.tuyensinh.ModelWeb.DiemThi;
import com.tuyensinh.ModelWeb.ThiSinh;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class ThiSinhDAOImpl implements ThiSinhDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public ThiSinh findByCccdAndPassword(String cccd, String password) {
        String sql = "SELECT cccd, sobaodanh, ho, ten, ngay_sinh, dien_thoai, " +
                     "gioi_tinh, email, noi_sinh, doi_tuong, khu_vuc " +
                     "FROM xt_thisinhxettuyen25 " +
                     "WHERE cccd = ? AND password = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new RowMapper<ThiSinh>() {
                @Override
                public ThiSinh mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ThiSinh ts = new ThiSinh();
                    ts.setCccd(rs.getString("cccd"));
                    ts.setSobaodanh(rs.getString("sobaodanh"));
                    ts.setHo(rs.getString("ho"));
                    ts.setTen(rs.getString("ten"));
                    ts.setNgaySinh(rs.getString("ngay_sinh"));
                    ts.setDienThoai(rs.getString("dien_thoai"));
                    ts.setGioiTinh(rs.getString("gioi_tinh"));
                    ts.setEmail(rs.getString("email"));
                    ts.setNoiSinh(rs.getString("noi_sinh"));
                    ts.setDoiTuong(rs.getString("doi_tuong"));
                    ts.setKhuVuc(rs.getString("khu_vuc"));
                    return ts;
                }
            }, cccd, password);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public DiemThi findDiemThiBySobaodanh(String sobaodanh) {
        String sql = "SELECT sobaodanh, d_phuongthuc, " +
                     "`TO`, LI, HO, SI, SU, DI, VA, " +
                     "N1_THI, N1_CC, CNCN, CNNN, TI, KTPL, NL1, NK1, NK2 " +
                     "FROM xt_diemthixettuyen " +
                     "WHERE sobaodanh = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new RowMapper<DiemThi>() {
                @Override
                public DiemThi mapRow(ResultSet rs, int rowNum) throws SQLException {
                    DiemThi dt = new DiemThi();
                    dt.setSobaodanh(rs.getString("sobaodanh"));
                    dt.setDPhuongthuc(rs.getString("d_phuongthuc"));
                    dt.setTo(rs.getBigDecimal("TO"));
                    dt.setLi(rs.getBigDecimal("LI"));
                    dt.setHo(rs.getBigDecimal("HO"));
                    dt.setSi(rs.getBigDecimal("SI"));
                    dt.setSu(rs.getBigDecimal("SU"));
                    dt.setDi(rs.getBigDecimal("DI"));
                    dt.setVa(rs.getBigDecimal("VA"));
                    dt.setN1Thi(rs.getBigDecimal("N1_THI"));
                    dt.setN1Cc(rs.getBigDecimal("N1_CC"));
                    dt.setCncn(rs.getBigDecimal("CNCN"));
                    dt.setCnnn(rs.getBigDecimal("CNNN"));
                    dt.setTi(rs.getBigDecimal("TI"));
                    dt.setKtpl(rs.getBigDecimal("KTPL"));
                    dt.setNl1(rs.getBigDecimal("NL1"));
                    dt.setNk1(rs.getBigDecimal("NK1"));
                    dt.setNk2(rs.getBigDecimal("NK2"));
                    return dt;
                }
            }, sobaodanh);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}