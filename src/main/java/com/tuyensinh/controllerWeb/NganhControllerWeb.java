package com.tuyensinh.controllerWeb;

import com.tuyensinh.model.Nganh;
import com.tuyensinh.serviceWeb.NganhServiceWeb;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
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
    public ResponseEntity<?> getByTenNganh(@PathVariable("tennganh") String tennganh) {
        Optional<Nganh> nganh = nganhServiceWeb.getByTenNganh(tennganh);
        
        if (nganh.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("errorMessage", "Không tìm thấy ngành: " + tennganh);
            return ResponseEntity.status(404).body(errorResponse);
        }
        
        return ResponseEntity.ok(nganh.get());
    }
}