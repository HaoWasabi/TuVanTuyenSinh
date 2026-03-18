package com.tuyensinh.service;

import com.tuyensinh.model.BangQuyDoi;
import com.tuyensinh.repository.BQDRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class BQDService {

    private final BQDRepository bangQuyDoiRepository;

    public BQDService() {
        this.bangQuyDoiRepository = new BQDRepository();
    }

    public BQDService(BQDRepository bangQuyDoiRepository) {
        this.bangQuyDoiRepository = bangQuyDoiRepository;
    }

    public BangQuyDoi create(BangQuyDoi bangQuyDoi) {
        return bangQuyDoiRepository.save(bangQuyDoi);
    }

    public Optional<BangQuyDoi> getById(Integer idqd) {
        return bangQuyDoiRepository.findById(idqd);
    }

    public Optional<BangQuyDoi> getByMaquydoi(String dMaquydoi) {
        return bangQuyDoiRepository.findByMaquydoi(dMaquydoi);
    }

    public Optional<BangQuyDoi> getByTohopAndMon(String dTohop, String dMon) {
        return bangQuyDoiRepository.findByTohopAndMon(dTohop, dMon);
    }

    public List<BangQuyDoi> getAll() {
        return bangQuyDoiRepository.findAll();
    }

    public BangQuyDoi update(BangQuyDoi bangQuyDoi) {
        return bangQuyDoiRepository.update(bangQuyDoi);
    }

    public boolean deleteById(Integer idqd) {
        return bangQuyDoiRepository.deleteById(idqd);
    }

    /**
     * Import danh sách BangQuyDoi từ file Excel
     * Định dạng Excel: 
     *   - Dòng 1: Header
     *   - Từ dòng 2 trở đi: dữ liệu với cột phuongThuc, toHop, mon, dieA, dieB, dieC, dieD, maQuyDoi, phanvi
     * 
     * @param filePath Đường dẫn file Excel
     * @return Danh sách BangQuyDoi đã import thành công
     * @throws IOException nếu không đọc được file
     */
    public List<BangQuyDoi> importFromExcel(String filePath) throws IOException {
        return bangQuyDoiRepository.importFromExcel(filePath);
    }
}