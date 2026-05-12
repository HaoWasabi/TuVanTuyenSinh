package com.tuyensinh.controllerWeb;

import com.tuyensinh.model.NguyenVong;
import com.tuyensinh.model.Nganh;
import com.tuyensinh.model.ThiSinh;
import com.tuyensinh.service.NguyenVongService;
import com.tuyensinh.serviceWeb.NganhServiceWeb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping({ "/nguyen-vong", "/nguyenvong" })
public class NguyenVongController {

    @Autowired
    private NguyenVongService nguyenVongService;

    private final NganhServiceWeb nganhServiceWeb = new NganhServiceWeb();

    @GetMapping
    public String getNguyenVong(HttpSession session, Model model) {
        ThiSinh user = (ThiSinh) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        try {
            List<NguyenVong> nguyenVongs = nguyenVongService.getByCccd(user.getCccd());
            nguyenVongs.forEach(nv -> nv.setTenNganh(resolveTenNganh(nv.getNvManganh())));

            // Get list of majors for dropdown
            List<String> allNames = nganhServiceWeb.getAllTenNganh();
            List<Nganh> nganhs = allNames.stream()
                    .map(name -> nganhServiceWeb.getByTenNganh(name).orElse(null))
                    .filter(n -> n != null)
                    .toList();

            model.addAttribute("nguyenVongs", nguyenVongs);
            model.addAttribute("danhSachNganh", nganhs);
        } catch (Exception e) {
            model.addAttribute("error", true);
        }

        return "nguyen-vong";
    }

    @PostMapping("add")
    public String addNguyenVong(
            @RequestParam("nganhId") long nganhId,
            @RequestParam("priority") int priority,
            HttpSession session) {

        ThiSinh user = (ThiSinh) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        try {
            NguyenVong nv = new NguyenVong();
            nv.setNnCccd(user.getCccd());
            nv.setNvManganh(resolveMaNganh(nganhId));
            nv.setNvTt(priority);

            nguyenVongService.add(nv);
        } catch (Exception e) {
            // Log error
        }

        return "redirect:/nguyen-vong";
    }

    @PostMapping("delete")
    public String deleteNguyenVong(
            @RequestParam("nvId") long nvId,
            HttpSession session) {

        ThiSinh user = (ThiSinh) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        try {
            nguyenVongService.delete((int) nvId);
        } catch (Exception e) {
            // Log error
        }

        return "redirect:/nguyen-vong";
    }

    @PostMapping("move-up")
    public String moveUp(
            @RequestParam("nvId") long nvId,
            HttpSession session) {

        ThiSinh user = (ThiSinh) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        try {
            movePreference(nvId, -1, user.getCccd());
        } catch (Exception e) {
            // Log error
        }

        return "redirect:/nguyen-vong";
    }

    @PostMapping("move-down")
    public String moveDown(
            @RequestParam("nvId") long nvId,
            HttpSession session) {

        ThiSinh user = (ThiSinh) session.getAttribute("user");
        if (user == null)
            return "redirect:/login";

        try {
            movePreference(nvId, 1, user.getCccd());
        } catch (Exception e) {
            // Log error
        }

        return "redirect:/nguyen-vong";
    }

    private void movePreference(long nvId, int direction, String cccd) {
        List<NguyenVong> nguyenVongs = nguyenVongService.getByCccd(cccd).stream()
                .sorted(Comparator.comparing(NguyenVong::getNvTt, Comparator.nullsLast(Integer::compareTo)))
                .toList();

        int currentIndex = -1;
        for (int i = 0; i < nguyenVongs.size(); i++) {
            NguyenVong current = nguyenVongs.get(i);
            if (current.getIdnv() != null && current.getIdnv().longValue() == nvId) {
                currentIndex = i;
                break;
            }
        }

        int targetIndex = currentIndex + direction;
        if (currentIndex < 0 || targetIndex < 0 || targetIndex >= nguyenVongs.size()) {
            return;
        }

        NguyenVong current = nguyenVongs.get(currentIndex);
        NguyenVong target = nguyenVongs.get(targetIndex);

        Integer currentOrder = current.getNvTt();
        current.setNvTt(target.getNvTt());
        target.setNvTt(currentOrder);

        nguyenVongService.update(current);
        nguyenVongService.update(target);
    }

    private String resolveMaNganh(long nganhId) {
        String idString = String.valueOf(nganhId);
        return nganhServiceWeb.getAllTenNganh().stream()
                .map(name -> nganhServiceWeb.getByTenNganh(name).orElse(null))
                .filter(n -> n != null && n.getIdnganh() != null)
                .filter(n -> String.valueOf(n.getIdnganh()).equals(idString))
                .map(Nganh::getManganh)
                .findFirst()
                .orElse(idString);
    }

    private String resolveTenNganh(String maNganh) {
        return nganhServiceWeb.getAllTenNganh().stream()
                .map(name -> nganhServiceWeb.getByTenNganh(name).orElse(null))
                .filter(n -> n != null && maNganh != null && maNganh.equals(n.getManganh()))
                .map(Nganh::getTenNganh)
                .findFirst()
                .orElse(maNganh);
    }
}
