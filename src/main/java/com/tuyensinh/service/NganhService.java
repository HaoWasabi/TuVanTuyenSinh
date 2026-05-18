package com.tuyensinh.service;

import com.tuyensinh.model.Nganh;
import com.tuyensinh.repository.NganhRepository;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NganhService {

    private final NganhRepository repository;

    public NganhService() {
        this.repository = new NganhRepository();
    }

    public NganhService(NganhRepository repository) {
        this.repository = repository;
    }

    public void create(Nganh nganh) {
         repository.save(nganh);
    }

    public Optional<Nganh> getById(Integer id) {
        return repository.findById(id);
    }

    public Optional<Nganh> getByMaNganh(String manganh) {
        return repository.findByMaNganh(manganh);
    }

    public List<Nganh> getAll() {
        return repository.findAll();
    }

    public Map<String, Long> getNguyenVongCountByMaNganh() {
        return repository.countNguyenVongByMaNganh();
    }

    public Nganh update(Nganh nganh) {
        return repository.update(nganh);
    }

    public boolean delete(Integer id) {
        return repository.deleteById(id);
    }

    public List<Nganh> importFromExcel(String filePath) throws IOException {
        return repository.importFromExcel(filePath);
    }
}
