package com.tuyensinh.service;

import com.tuyensinh.model.DiemCong;
import com.tuyensinh.repository.DiemCongRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class DiemCongService {

    private final DiemCongRepository repository;

    public DiemCongService() {
        this.repository = new DiemCongRepository();
    }

    public DiemCongService(DiemCongRepository repository) {
        this.repository = repository;
    }

    public void create(DiemCong diemCong) {
        repository.save(diemCong);
    }

    public Optional<DiemCong> getById(Integer id) {
        return repository.findById(id);
    }

    public List<DiemCong> getByCccd(String cccd) {
        return repository.findByCccd(cccd);
    }

    public List<DiemCong> getAll() {
        return repository.findAll();
    }

    public DiemCong update(DiemCong diemCong) {
        return repository.update(diemCong);
    }

    public boolean delete(Integer id) {
        return repository.deleteById(id);
    }

    public List<DiemCong> importFromExcel(String filePath) throws IOException {
        return repository.importFromExcel(filePath);
    }
}
