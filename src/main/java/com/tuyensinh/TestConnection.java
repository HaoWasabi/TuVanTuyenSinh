package com.tuyensinh;

import com.tuyensinh.model.NguyenVong;
import com.tuyensinh.repository.NguyenVongRepository;

import java.util.List;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("Đang khởi tạo Hibernate và kết nối Database...");

        NguyenVongRepository repo = new NguyenVongRepository();

        try {
            // Test thử hàm lấy toàn bộ dữ liệu
            List<NguyenVong> dsNguyenVong = repo.findAll();

            System.out.println("✅ THÀNH CÔNG! Đã kết nối được Database.");
            System.out.println("👉 Số lượng dòng trong bảng xt_nguyenvongxettuyen: " + dsNguyenVong.size());

            // Nếu có dữ liệu, in thử dòng đầu tiên ra xem
            if (!dsNguyenVong.isEmpty()) {
                System.out.println("Dữ liệu mẫu: " + dsNguyenVong.get(0).toString());
            }

        } catch (Exception e) {
            System.err.println("❌ THẤT BẠI! Có lỗi xảy ra:");
            e.printStackTrace();
        } finally {
            // Đóng session factory để kết thúc chương trình gọn gàng
            com.tuyensinh.database.HibernateUtil.shutdown();
        }
    }
}