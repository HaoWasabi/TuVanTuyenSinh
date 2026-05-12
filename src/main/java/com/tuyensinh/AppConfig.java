package com.tuyensinh;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import com.tuyensinh.model.ThiSinh;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import javax.sql.DataSource;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.tuyensinh")
public class AppConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor());
    }

    @Bean
    public SessionInterceptor sessionInterceptor() {
        return new SessionInterceptor();
    }

    @Bean
    public ClassLoaderTemplateResolver templateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(true);
        return resolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver());
        // Thêm Layout Dialect để hỗ trợ layout:decorate, layout:fragment
        engine.addDialect(new LayoutDialect());
        return engine;
    }

    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine());
        resolver.setCharacterEncoding("UTF-8");
        resolver.setContentType("text/html;charset=UTF-8");
        return resolver;
    }

    @Bean
    public com.tuyensinh.service.ThiSinhService thiSinhService() {
        return new com.tuyensinh.service.ThiSinhService();
    }

    @Bean
    public com.tuyensinh.service.DiemThiService diemThiService() {
        return new com.tuyensinh.service.DiemThiService();
    }

    @Bean
    public com.tuyensinh.service.NguyenVongService nguyenVongService() {
        return new com.tuyensinh.service.NguyenVongService();
    }

    @Bean
    public com.tuyensinh.service.BQDService bqdService() {
        return new com.tuyensinh.service.BQDService();
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/xettuyen2026?useUnicode=true&characterEncoding=UTF-8");
        dataSource.setUsername("root"); // Thay bằng username DB của bạn
        dataSource.setPassword("root");     // Thay bằng password DB của bạn
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    // Inner class for session interceptor
    public static class SessionInterceptor implements HandlerInterceptor {
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                                Object handler) throws Exception {
            return true;
        }

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

        public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                   Object handler, Exception ex) throws Exception {
            // Not implemented
        }
    }
}