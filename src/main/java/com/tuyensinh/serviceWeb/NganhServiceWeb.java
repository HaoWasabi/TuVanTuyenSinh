package com.tuyensinh.serviceWeb;

import com.tuyensinh.model.Nganh;
import com.tuyensinh.repository.NganhRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NganhServiceWeb {

    private final NganhRepository nganhRepository;

    public NganhServiceWeb() {
        this.nganhRepository = new NganhRepository();
    }

    public NganhServiceWeb(NganhRepository nganhRepository) {
        this.nganhRepository = nganhRepository;
    }

    public Optional<Nganh> getByTenNganh(String tennganh) {
        return nganhRepository.findByTenNganh(tennganh);
    }

    // Lấy danh sách tất cả tennganh
    public List<String> getAllTenNganh() {
        return nganhRepository.findAll().stream()
                .map(Nganh::getTennganh)
                .collect(Collectors.toList());
    }
}