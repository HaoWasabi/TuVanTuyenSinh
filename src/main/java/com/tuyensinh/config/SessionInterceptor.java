package com.tuyensinh.config;

import com.tuyensinh.model.ThiSinh;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    public void postHandle(HttpServletRequest request, HttpServletResponse response, 
                          Object handler, ModelAndView modelAndView) throws Exception {
        
        if (modelAndView != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                ThiSinh user = (ThiSinh) session.getAttribute("user");
                if (user != null) {
                    modelAndView.addObject("user", user);
                }
            }
        }
    }
}
