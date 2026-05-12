package com.tuyensinh.controllerWeb;

import com.tuyensinh.model.ThiSinh;
import com.tuyensinh.model.DiemThi;
import com.tuyensinh.model.NguyenVong;
import com.tuyensinh.service.ThiSinhService;
import com.tuyensinh.service.DiemThiService;
import com.tuyensinh.service.NguyenVongService;
import com.tuyensinh.serviceWeb.ThiSinhWithNguyenVongServiceWeb;
import com.tuyensinh.ModelWeb.ThiSinhWithNguyenVongResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping({ "/tra-cuu-diem", "/diem" })
public class ThiSinhController {

    @Autowired
    private ThiSinhService thiSinhService;

    @Autowired
    private DiemThiService diemThiService;

    @Autowired
    private NguyenVongService nguyenVongService;

    private final ThiSinhWithNguyenVongServiceWeb thiSinhWithNguyenVongService = new ThiSinhWithNguyenVongServiceWeb();

    @GetMapping
    public String search(
            @RequestParam(name = "cccd", required = false) String cccd,
            @RequestParam(name = "dob", required = false) String dob,
            Model model) {

        if (cccd != null && !cccd.isEmpty() && dob != null && !dob.isEmpty()) {
            try {
                System.out.println("=== Search Trace ===");
                System.out.println("Input CCCD: " + cccd);
                System.out.println("Input DOB: " + dob);

                // Parse date: thymeleaf gửi định dạng YYYY-MM-DD
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate ngaySinh = LocalDate.parse(dob, formatter);
                String dobString = ngaySinh.format(DateTimeFormatter.ofPattern("ddMMyyyy"));

                System.out.println("Formatted DOB (ddMMyyyy): " + dobString);

                Optional<ThiSinhWithNguyenVongResponse> result = thiSinhWithNguyenVongService
                        .getThiSinhWithNguyenVong(cccd, dobString);

                System.out.println("Search result found: " + result.isPresent());

                if (result.isPresent()) {
                    ThiSinhWithNguyenVongResponse data = result.get();
                    System.out.println("Found candidate: " + data.getHo() + " " + data.getTen());
                    // Create a ThiSinh object from response data
                    ThiSinh thiSinh = new ThiSinh();
                    thiSinh.setCccd(data.getCccd());
                    thiSinh.setHo(data.getHo());
                    thiSinh.setTen(data.getTen());
                    thiSinh.setNgaySinh(data.getNgaySinh());
                    thiSinh.setEmail(data.getEmail());
                    thiSinh.setDienThoai(data.getDienThoai());
                    thiSinh.setKhuVuc(data.getKhuVuc());
                    thiSinh.setDoiTuong(data.getDoiTuong());

                    model.addAttribute("thiSinh", thiSinh);
                    List<DiemThi> diemThiList = diemThiService.getByCccd(data.getCccd());
                    DiemThi diemThi = diemThiList.isEmpty() ? null : diemThiList.get(0);
                    model.addAttribute("diem", diemThi);
                    model.addAttribute("preferences", data.getNguyenVongList());
                    model.addAttribute("tongDiem", data.getNguyenVongList().isEmpty() ? null : data.getNguyenVongList().get(0).getDiem_xettuyen());
                } else {
                    System.out.println("No candidate found");
                    model.addAttribute("error", true);
                }
            } catch (Exception e) {
                System.err.println("Search error for CCCD=" + cccd + ", DOB=" + dob + ": " + e.getMessage());
                e.printStackTrace();
                model.addAttribute("error", true);
            }
        }

        model.addAttribute("cccd", cccd);
        model.addAttribute("dob", dob);
        return "tra-cuu-diem";
    }
}