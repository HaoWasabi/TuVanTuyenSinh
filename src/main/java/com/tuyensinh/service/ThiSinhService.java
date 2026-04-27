package com.tuyensinh.service;

import com.tuyensinh.model.ThiSinh;
import com.tuyensinh.model.DiemThi;
import com.tuyensinh.repository.ThiSinhRepository;
import com.tuyensinh.repository.DiemThiRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ThiSinhService {

    private final ThiSinhRepository thiSinhRepository;
    private final DiemThiRepository diemThiRepository;

    public ThiSinhService() {
        this.thiSinhRepository = new ThiSinhRepository();
        this.diemThiRepository = new DiemThiRepository();
    }

    public ThiSinhService(ThiSinhRepository thiSinhRepository, DiemThiRepository diemThiRepository) {
        this.thiSinhRepository = thiSinhRepository;
        this.diemThiRepository = diemThiRepository;
    }

    public List<ThiSinh> getAll() {
        return thiSinhRepository.findAll();
    }

    public Optional<ThiSinh> getByCccd(String cccd) {
        return thiSinhRepository.findByCccd(cccd);
    }

    public Optional<ThiSinh> getByCccdAndNgaySinh(String cccd, String ngaySinh) {
        return thiSinhRepository.findByCccdAndNgaySinh(cccd, ngaySinh);
    }

    // Lấy thông tin thí sinh + điểm thi theo CCCD và ngày sinh
    public Optional<ThiSinhWithDiemThi> getThiSinhWithDiemThi(String cccd, String ngaySinh) {
        Optional<ThiSinh> thiSinhOpt = thiSinhRepository.findByCccdAndNgaySinh(cccd, ngaySinh);
        
        if (thiSinhOpt.isEmpty()) {
            return Optional.empty();
        }
        
        ThiSinh thiSinh = thiSinhOpt.get();
        String sobaodanh = thiSinh.getSobaodanh();
        
        // Lấy điểm thi theo số báo danh
        List<DiemThi> diemThiList = diemThiRepository.findBySoBaoDanh(sobaodanh);
        
        return Optional.of(new ThiSinhWithDiemThi(thiSinh, diemThiList));
    }

    public List<ThiSinh> searchByHoTen(String keyword) {
        return thiSinhRepository.findByHoTen(keyword);
    }

    public ThiSinh update(ThiSinh thiSinh) {
        return thiSinhRepository.update(thiSinh);
    }

    // Thêm mới thí sinh
    public ThiSinh create(ThiSinh thiSinh) {
        return thiSinhRepository.save(thiSinh);
    }

    public boolean deleteByCccd(String cccd) {
        return thiSinhRepository.deleteByCccd(cccd);
    }

    public List<ThiSinh> importFromExcel(String filePath) throws IOException {
        return thiSinhRepository.importFromExcel(filePath);
    }

    // Inner class để chứa cả thông tin thí sinh và điểm thi
    public static class ThiSinhWithDiemThi {
        private final ThiSinh thiSinh;
        private final List<DiemThi> diemThiList;

        public ThiSinhWithDiemThi(ThiSinh thiSinh, List<DiemThi> diemThiList) {
            this.thiSinh = thiSinh;
            this.diemThiList = diemThiList;
        }

        public ThiSinh getThiSinh() {
            return thiSinh;
        }

        public List<DiemThi> getDiemThiList() {
            return diemThiList;
        }
    }
}