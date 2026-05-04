package com.tuyensinh.service;

import com.tuyensinh.model.DiemCong;
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

    public boolean add(NguyenVong nv) {
        if (nv.getNvKeys() == null || nv.getNvKeys().isEmpty()) {
            // Format: NV_079123456789_1
            String generatedKey = "NV_" + nv.getNnCccd() + "_" + nv.getNvTt();
            nv.setNvKeys(generatedKey);
        }
        return repository.add(nv);
    }

    public NguyenVong update(NguyenVong nguyenVong) {
        return repository.update(nguyenVong);
    }

    public boolean delete(Integer id) {
        return repository.deleteById(id);
    }

    public List<NguyenVong> importFromExcel(String filePath, int is_hs) throws IOException {
        return repository.importFromExcel(filePath,is_hs);
    }
}
