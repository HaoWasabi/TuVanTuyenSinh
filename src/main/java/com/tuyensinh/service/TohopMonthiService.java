package com.tuyensinh.service;

import com.tuyensinh.model.TohopMonthi;
import com.tuyensinh.repository.TohopMonthiRepository;

import java.util.List;
import java.util.Optional;

public class TohopMonthiService {

    private final TohopMonthiRepository repository;

    public TohopMonthiService() {
        repository = new TohopMonthiRepository();
    }

    public void create(TohopMonthi entity) {
        repository.save(entity);
    }

    public Optional<TohopMonthi> getById(Integer id) {
        return repository.findById(id);
    }

    public Optional<TohopMonthi> getByMaTohop(String matohop) {
        return repository.findByMaTohop(matohop);
    }

    public List<TohopMonthi> getAll() {
        return repository.findAll();
    }

    public TohopMonthi update(TohopMonthi entity) {
        return repository.update(entity);
    }

    public boolean delete(Integer id) {
        return repository.deleteById(id);
    }
    public void exportToExcel(String filePath) throws Exception {
    repository.exportToExcel(filePath);
    }
    public List<TohopMonthi> importFromExcel(String filePath) throws Exception {
    return repository.importFromExcel(filePath);
}
}
