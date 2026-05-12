package com.tuyensinh.controllerWeb;

import com.tuyensinh.model.ThiSinh;
import com.tuyensinh.model.DiemThi;
import com.tuyensinh.model.NguyenVong;
import com.tuyensinh.service.DiemThiService;
import com.tuyensinh.service.NguyenVongService;
import com.tuyensinh.serviceWeb.NganhServiceWeb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping({ "/profile", "/diem-ca-nhan" })
public class ProfileController {

    @Autowired
    private DiemThiService diemThiService;

    @Autowired
    private NguyenVongService nguyenVongService;

    private final NganhServiceWeb nganhServiceWeb = new NganhServiceWeb();

    @GetMapping
    public String profile(HttpSession session, Model model) {
        ThiSinh user = (ThiSinh) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        // Fetch scores
        List<DiemThi> diemThiList = diemThiService.getByCccd(user.getCccd());
        DiemThi diem = diemThiList.isEmpty() ? null : diemThiList.get(0);
        model.addAttribute("diem", diem);
        model.addAttribute("tongDiem", tinhTongDiem(diem));
        model.addAttribute("khuvuc", user.getKhuVuc());
        model.addAttribute("doiTuong", user.getDoiTuong());
        model.addAttribute("diemKhuvuc", BigDecimal.ZERO);
        model.addAttribute("diemDoiTuong", BigDecimal.ZERO);

        List<NguyenVong> nguyenVongs = nguyenVongService.getByCccd(user.getCccd());
        nguyenVongs.forEach(nv -> nv.setTenNganh(resolveTenNganh(nv.getNvManganh())));
        model.addAttribute("nguyenVong", nguyenVongs);

        return "diemthicanhan";
    }

    @PostMapping("update")
    public String updateProfile(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "gender", required = false) String gender,
            HttpSession session) {

        ThiSinh user = (ThiSinh) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        // Update user object
        if (name != null && !name.isEmpty())
            user.setTen(name);
        if (phone != null && !phone.isEmpty())
            user.setDienThoai(phone);
        if (email != null && !email.isEmpty())
            user.setEmail(email);
        if (gender != null && !gender.isEmpty())
            user.setGioiTinh(gender);

        // Save back to session
        session.setAttribute("user", user);

        return "redirect:/profile";
    }

    @PostMapping("change-password")
    public String changePassword(
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            HttpSession session,
            Model model) {

        ThiSinh user = (ThiSinh) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        // Verify old password (simplified check)
        if (!oldPassword.equals(user.getPassword())) {
            model.addAttribute("error", "Mật khẩu cũ không chính xác");
            return "profile";
        }

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu mới không khớp");
            return "profile";
        }

        // Update password
        user.setPassword(newPassword);
        session.setAttribute("user", user);

        model.addAttribute("success", "Đổi mật khẩu thành công");
        return "profile";
    }

    private BigDecimal tinhTongDiem(DiemThi diem) {
        if (diem == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal tong = BigDecimal.ZERO;
        if (diem.getToan() != null)
            tong = tong.add(diem.getToan());
        if (diem.getNguVan() != null)
            tong = tong.add(diem.getNguVan());
        if (diem.getVatLi() != null)
            tong = tong.add(diem.getVatLi());
        if (diem.getHoaHoc() != null)
            tong = tong.add(diem.getHoaHoc());
        if (diem.getSinhHoc() != null)
            tong = tong.add(diem.getSinhHoc());
        if (diem.getLichSu() != null)
            tong = tong.add(diem.getLichSu());
        if (diem.getDiaLi() != null)
            tong = tong.add(diem.getDiaLi());
        if (diem.getTinHoc() != null)
            tong = tong.add(diem.getTinHoc());
        if (diem.getKtpl() != null)
            tong = tong.add(diem.getKtpl());
        return tong;
    }

    private String resolveTenNganh(String maNganh) {
        return nganhServiceWeb.getAllTenNganh().stream()
                .map(name -> nganhServiceWeb.getByTenNganh(name).orElse(null))
                .filter(n -> n != null && maNganh != null && maNganh.equals(n.getManganh()))
                .map(com.tuyensinh.model.Nganh::getTenNganh)
                .findFirst()
                .orElse(maNganh);
    }
}
