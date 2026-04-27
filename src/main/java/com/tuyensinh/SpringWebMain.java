package com.tuyensinh;

import com.tuyensinh.AppConfig;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

public class SpringWebMain {

    public static void main(String[] args) throws Exception {
        // 1. Cấu hình Embedded Tomcat
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8081);
        tomcat.getConnector(); // Khởi tạo connector lắng nghe ở port 8081

        // Tạo Context mặc định
        Context context = tomcat.addContext("", new File(".").getAbsolutePath());

        // 2. Khởi tạo Spring Framework
        AnnotationConfigWebApplicationContext springContext = new AnnotationConfigWebApplicationContext();
        springContext.register(AppConfig.class);

        // 3. Đăng ký DispatcherServlet (cốt lõi của Spring Web MVC) vào Tomcat
        DispatcherServlet dispatcherServlet = new DispatcherServlet(springContext);
        Tomcat.addServlet(context, "dispatcherServlet", dispatcherServlet).setLoadOnStartup(1);
        context.addServletMappingDecoded("/", "dispatcherServlet");

        System.out.println("\n========== BẮT ĐẦU CHẠY SPRING WEB API TRÊN CỔNG 8081 ==========");
        tomcat.start();
        tomcat.getServer().await();
    }
}