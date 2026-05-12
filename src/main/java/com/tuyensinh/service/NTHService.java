package com.tuyensinh.service;

import com.tuyensinh.model.NganhToHop;
import com.tuyensinh.repository.NTHRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class NTHService {

    private final NTHRepository nganhToHopRepository;

    public NTHService() {
        this.nganhToHopRepository = new NTHRepository();
    }

    public NTHService(NTHRepository nganhToHopRepository) {
        this.nganhToHopRepository = nganhToHopRepository;
    }

    public NganhToHop create(NganhToHop majors) {
        return nganhToHopRepository.save(majors);
    }

    public Optional<NganhToHop> getById(Integer id) {
        return nganhToHopRepository.findById(id);
    }

    public Optional<NganhToHop> getByTbKeys(String tbKeys) {
        return nganhToHopRepository.findByTbKeys(tbKeys);
    }

    public List<NganhToHop> getAll() {
        return nganhToHopRepository.findAll();
    }

    public List<NganhToHop> getByMaNganh(String maNganh) {
        return nganhToHopRepository.findByMaNganh(maNganh);
    }

    public NganhToHop update(NganhToHop majors) {
        return nganhToHopRepository.update(majors);
    }

    public boolean deleteById(Integer id) {
        return nganhToHopRepository.deleteById(id);
    }

    public List<NganhToHop> importFromExcel(String filePath) throws IOException {
        return nganhToHopRepository.importFromExcel(filePath);
    }
}
