package com.tuyensinh.serviceWeb;

import com.tuyensinh.model.Nganh;
import com.tuyensinh.repository.NganhRepository;

import java.util.Optional;

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
}