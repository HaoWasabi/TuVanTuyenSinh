package com.tuyensinh.controllerWeb;

import com.tuyensinh.ModelWeb.NganhTraCuuResponse;
import com.tuyensinh.serviceWeb.NganhServiceWeb;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping({ "/nganh", "/tra-cuu-nganh" })
public class NganhControllerWeb {

    private final NganhServiceWeb nganhServiceWeb = new NganhServiceWeb();

    @GetMapping
    public String getAll(
            @RequestParam(name = "search", required = false) String search,
            Model model) {

        try {
            List<NganhTraCuuResponse> nganhs = nganhServiceWeb.searchTraCuuNganh(search);

            model.addAttribute("nganhs", nganhs);
            model.addAttribute("search", search == null ? "" : search);
            return "tra-cuu-nganh";
        } catch (Exception e) {
            model.addAttribute("nganhs", Collections.emptyList());
            model.addAttribute("search", search == null ? "" : search);
            model.addAttribute("error", true);
            return "tra-cuu-nganh";
        }
    }
}