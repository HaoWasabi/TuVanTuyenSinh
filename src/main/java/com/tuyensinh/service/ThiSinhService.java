package com.tuyensinh.service;

import com.tuyensinh.model.ThiSinh;
import com.tuyensinh.repository.ThiSinhRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ThiSinhService {

    private final ThiSinhRepository thiSinhRepository;

    public ThiSinhService() {
        this.thiSinhRepository = new ThiSinhRepository();
    }

    public ThiSinhService(ThiSinhRepository thiSinhRepository) {
        this.thiSinhRepository = thiSinhRepository;
    }

    public List<ThiSinh> getAll() {
        return thiSinhRepository.findAll();
    }

    public Optional<ThiSinh> getByCccd(String cccd) {
        return thiSinhRepository.findByCccd(cccd);
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

    public List<ThiSinh> importFromExcel(String filePath) throws IOException {
        return thiSinhRepository.importFromExcel(filePath);
    }
}