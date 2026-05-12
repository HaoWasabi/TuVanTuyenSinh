package com.tuyensinh.controllerWeb;

import com.tuyensinh.model.BangQuyDoi;
import com.tuyensinh.service.BQDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping({ "/bang-quy-doi", "/quy-doi-diem" })
public class BangQuyDoiController {

    @Autowired
    private BQDService bqdService;

    @GetMapping
    public String getBangQuyDoi(
            @RequestParam(name = "filter", required = false) String filter,
            Model model) {

        try {
            List<BangQuyDoi> bangQuyDoi = bqdService.getAll();

            if (filter != null && !filter.isEmpty()) {
                final String finalFilter = filter.toLowerCase();
                // Filter by method or other fields if available
                // Note: Need to check BangQuyDoi class for actual filterable fields
                // For now, just return all
            }

            model.addAttribute("bangQuyDoi", bangQuyDoi);
            model.addAttribute("filter", filter);
        } catch (Exception e) {
            model.addAttribute("error", true);
        }

        return "bang-quy-doi";
    }
}
