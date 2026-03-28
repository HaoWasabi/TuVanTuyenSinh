package com.tuyensinh.service;

import com.tuyensinh.model.NguyenVong;
import com.tuyensinh.repository.NguyenVongRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class NguyenVongService {

    private final NguyenVongRepository repository;

    public NguyenVongService() {
        this.repository = new NguyenVongRepository();
    }

    public NguyenVongService(NguyenVongRepository repository) {
        this.repository = repository;
    }

    public void create(NguyenVong nguyenVong) {
        repository.save(nguyenVong);
    }

    public Optional<NguyenVong> getById(Integer id) {
        return repository.findById(id);
    }

    public List<NguyenVong> getByCccd(String cccd) {
        return repository.findByCccd(cccd);
    }

    public List<NguyenVong> getByMaNganh(String maNganh) {
        return repository.findByMaNganh(maNganh);
    }

    public List<NguyenVong> getAll() {
        return repository.findAll();
    }

    public NguyenVong update(NguyenVong nguyenVong) {
        return repository.update(nguyenVong);
    }

    public boolean delete(Integer id) {
        return repository.deleteById(id);
    }

    public List<NguyenVong> importFromExcel(String filePath) throws IOException {
        return repository.importFromExcel(filePath);
    }
}
