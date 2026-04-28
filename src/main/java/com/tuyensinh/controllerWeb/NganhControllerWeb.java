package com.tuyensinh.controllerWeb;

import com.tuyensinh.model.Nganh;
import com.tuyensinh.serviceWeb.NganhServiceWeb;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/nganh")
public class NganhControllerWeb {

    private final NganhServiceWeb nganhServiceWeb = new NganhServiceWeb();

    // Lấy danh sách tất cả tennganh
    @GetMapping
    public ResponseEntity<List<String>> getAllTenNganh() {
        List<String> list = nganhServiceWeb.getAllTenNganh();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{tennganh}")
    public ResponseEntity<Nganh> getByTenNganh(@PathVariable("tennganh") String tennganh) {
        Optional<Nganh> nganh = nganhServiceWeb.getByTenNganh(tennganh);
        return nganh.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}