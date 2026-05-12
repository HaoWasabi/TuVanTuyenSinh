package com.tuyensinh.controllerWeb;

import com.tuyensinh.model.User;
import com.tuyensinh.model.ThiSinh;
import com.tuyensinh.service.AuthService;
import com.tuyensinh.service.ThiSinhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class AuthController {

    private final AuthService authService = new AuthService();

    @Autowired
    private ThiSinhService thiSinhService;

    @GetMapping("login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("login")
    public String login(
            @RequestParam("cccd") String cccd,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        try {
            User authenticatedUser = authService.login(cccd.trim(), password);

            Optional<ThiSinh> thiSinhOpt = thiSinhService.getByCccd(authenticatedUser.getUsername().trim());

            if (thiSinhOpt.isPresent()) {
                ThiSinh thiSinh = thiSinhOpt.get();
                session.setAttribute("user", thiSinh);
                session.setAttribute("authUser", authenticatedUser);
                return "redirect:/diem-ca-nhan";
            } else {
                model.addAttribute("errorMessage", "Tài khoản đã đăng nhập nhưng chưa có hồ sơ thí sinh tương ứng.");
                model.addAttribute("cccd", cccd);
                return "login";
            }
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("cccd", cccd);
            return "login";
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Không thể đăng nhập lúc này. Vui lòng thử lại.");
            model.addAttribute("cccd", cccd);
            return "login";
        }
    }

    @GetMapping("logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
