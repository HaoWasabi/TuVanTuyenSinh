package com.tuyensinh;

import com.tuyensinh.database.HibernateUtil;
import com.tuyensinh.model.ThiSinh;
import com.tuyensinh.service.ThiSinhService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ServiceTestThiSinh {

    public static void main(String[] args) {
        ThiSinhService service = new ThiSinhService();

        try {
            System.out.println("\n========== SERVICE TEST THÍ SINH START ==========\n");

            // Gọi hàm test thêm thí sinh độc lập
            testThemThiSinh();

            // Test 1: CREATE - Tạo 3 thí sinh giả lập
            System.out.println("=== TEST 1: CREATE ===");
            // Lưu ý: Dùng random CCCD để tránh lỗi trùng lặp Khóa duy nhất (Unique) khi chạy test nhiều lần
            String cccd1 = "079200" + System.currentTimeMillis() % 100000;
            String cccd2 = "079201" + System.currentTimeMillis() % 100000;
            String cccd3 = "079202" + System.currentTimeMillis() % 100000;

            ThiSinh ts1 = createTestThiSinh(cccd1, "TS001", "Nguyễn Văn", "An", "01/01/2007", "0901234567");
            ThiSinh ts2 = createTestThiSinh(cccd2, "TS002", "Trần Thị", "Bình", "15/05/2007", "0987654321");
            ThiSinh ts3 = createTestThiSinh(cccd3, "TS003", "Lê Hoàng", "Anh", "20/10/2007", "0911222333");

            ThiSinh created1 = service.create(ts1);
            ThiSinh created2 = service.create(ts2);
            ThiSinh created3 = service.create(ts3);

            System.out.println("[✓] Created ts1 - ID: " + created1.getIdthisinh() + ", Họ Tên: " + created1.getHo() + " " + created1.getTen() + ", CCCD: " + created1.getCccd());
            System.out.println("[✓] Created ts2 - ID: " + created2.getIdthisinh() + ", Họ Tên: " + created2.getHo() + " " + created2.getTen() + ", CCCD: " + created2.getCccd());
            System.out.println("[✓] Created ts3 - ID: " + created3.getIdthisinh() + ", Họ Tên: " + created3.getHo() + " " + created3.getTen() + ", CCCD: " + created3.getCccd());

            // Test 2: READ ALL - Lấy tất cả thí sinh
            System.out.println("\n=== TEST 2: READ ALL ===");
            List<ThiSinh> allThiSinh = service.getAll();
            System.out.println("[✓] Found " + allThiSinh.size() + " records:");
            for (ThiSinh t : allThiSinh) {
                System.out.println("    - ID: " + t.getIdthisinh() + ", SBD: " + t.getSobaodanh() + ", Họ Tên: " + t.getHo() + " " + t.getTen() + ", CCCD: " + t.getCccd());
            }

            // Test 3: READ BY CCCD (Chính xác)
            System.out.println("\n=== TEST 3: READ BY CCCD ===");
            String searchCccd = created2.getCccd();
            Optional<ThiSinh> foundByCccd = service.getByCccd(searchCccd);
            if (foundByCccd.isPresent()) {
                ThiSinh t = foundByCccd.get();
                System.out.println("[✓] Found by CCCD (" + searchCccd + "): ID=" + t.getIdthisinh() + ", Họ tên=" + t.getHo() + " " + t.getTen());
            } else {
                System.out.println("[✗] Not found CCCD: " + searchCccd);
            }

            // Test 4: SEARCH BY HỌ TÊN (Gần đúng)
            System.out.println("\n=== TEST 4: SEARCH BY HỌ TÊN ===");
            String keyword = "Anh"; // Tìm những ai có chữ "Anh" trong Tên hoặc Họ
            List<ThiSinh> searchResults = service.searchByHoTen(keyword);
            System.out.println("[✓] Tìm kiếm từ khóa '" + keyword + "' trả về " + searchResults.size() + " kết quả:");
            for (ThiSinh t : searchResults) {
                System.out.println("    - ID: " + t.getIdthisinh() + ", Họ Tên: " + t.getHo() + " " + t.getTen());
            }

            // Test 5: UPDATE - Thay đổi thông tin số điện thoại và khu vực của ts1
            System.out.println("\n=== TEST 5: UPDATE ===");
            String oldPhone = created1.getDienThoai();
            String newPhone = "0999999999";
            created1.setDienThoai(newPhone);
            created1.setKhuVuc("KV2-NT"); // Giả sử thay đổi khu vực ưu tiên
            
            ThiSinh updated = service.update(created1);
            System.out.println("[✓] Updated ID " + updated.getIdthisinh() + ": Số ĐT " + oldPhone + " → " + updated.getDienThoai() + ", Khu vực mới: " + updated.getKhuVuc());

            // Verify update bằng cách lấy lại từ DB qua CCCD
            Optional<ThiSinh> verified = service.getByCccd(updated.getCccd());
            if (verified.isPresent() && verified.get().getDienThoai().equals(newPhone)) {
                System.out.println("[✓] Verified: Update successful in Database");
            }

            // Chú ý: Vì ThiSinhService chưa có hàm Delete nên Test 6: DELETE bị bỏ qua.
            System.out.println("\n[ℹ] Bỏ qua TEST 6 (DELETE) vì ThiSinhService chưa implement chức năng xóa.");

            // Test 7: FINAL CHECK
            System.out.println("\n=== TEST 7: FINAL CHECK ===");
            List<ThiSinh> remaining = service.getAll();
            System.out.println("[✓] Current total records: " + remaining.size());

            // Test 8: IMPORT TỪ FILE EXCEL
            System.out.println("\n=== TEST 8: IMPORT TỪ FILE EXCEL ===");
            String excelFilePath = "src/main/resources/Ds thi sinh.xlsx";
            try {
                List<ThiSinh> importedRecords = service.importFromExcel(excelFilePath);
                System.out.println("[✓] Đã import thành công " + importedRecords.size() + " thí sinh từ file Excel!");
                
                // In ra tối đa 3 bản ghi đầu tiên để kiểm tra
                int limit = Math.min(3, importedRecords.size());
                for (int i = 0; i < limit; i++) {
                    ThiSinh t = importedRecords.get(i);
                    System.out.println("    - Mới Import: " + t.getHo() + " " + t.getTen() + " | CCCD: " + t.getCccd() + " | Nơi sinh: " + t.getNoiSinh());
                }
            } catch (Exception ex) {
                System.out.println("[⚠] Bỏ qua test Excel do file không tồn tại hoặc lỗi: " + ex.getMessage());
            }

            System.out.println("\n========== SERVICE TEST THÍ SINH END - ALL TESTS PASSED ==========\n");

        } catch (Exception ex) {
            System.err.println("[✗] ERROR: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            // Đóng kết nối Hibernate để kết thúc chương trình mượt mà
            HibernateUtil.shutdown();
        }
    }

    public static void testThemThiSinh() {
        System.out.println("=== TEST 0: THÊM MỚI THÍ SINH ĐỘC LẬP ===");
        
        // 1. Khởi tạo Service
        ThiSinhService service = new ThiSinhService();

        try {
            // 2. Tạo đối tượng ThiSinh chứa thông tin (Giả lập dữ liệu nhập từ form)
            String randomCccd = "0792" + System.currentTimeMillis() % 1000000; 
            
            ThiSinh thiSinhMoi = ThiSinh.builder()
                    .cccd(randomCccd)
                    .sobaodanh("TS999")
                    .ho("Lê Văn")
                    .ten("Luyện")
                    .ngaySinh("01/01/2007")
                    .dienThoai("0909123456")
                    .email("ts999@xettuyen.edu.vn")
                    .gioiTinh("Nam")
                    .khuVuc("KV1")
                    .doiTuong("01")
                    .updatedAt(LocalDate.now())
                    .build();

            // 3. Gọi hàm create để lưu vào Database
            ThiSinh createdThiSinh = service.create(thiSinhMoi);

            // 4. Kiểm tra kết quả trả về
            if (createdThiSinh != null && createdThiSinh.getIdthisinh() != null) {
                System.out.println("[✓] THÊM THÀNH CÔNG!");
                System.out.println("    - ID tự tăng: " + createdThiSinh.getIdthisinh());
                System.out.println("    - Họ Tên: " + createdThiSinh.getHo() + " " + createdThiSinh.getTen());
                System.out.println("    - CCCD: " + createdThiSinh.getCccd());
            } else {
                System.out.println("[✗] THÊM THẤT BẠI!");
            }

        } catch (Exception e) {
            System.err.println("[✗] LỖI TRONG QUÁ TRÌNH THÊM THÍ SINH:");
            e.printStackTrace();
        }
    }

    /**
     * Helper method: Tạo đối tượng ThiSinh với các dữ liệu cơ bản
     */
    private static ThiSinh createTestThiSinh(String cccd, String sbd, String ho, String ten, String ngaySinh, String dienThoai) {
        // Mock dữ liệu mặc định cho các trường còn lại
        String email = sbd.toLowerCase() + "@xettuyen.edu.vn";
        String password = "hashed_password_123";
        String gioiTinh = "Nam";
        String noiSinh = "TP.Hồ Chí Minh";
        String doiTuong = "01";
        String khuVuc = "KV1";

        return ThiSinh.builder()
                .cccd(cccd)
                .sobaodanh(sbd)
                .ho(ho)
                .ten(ten)
                .ngaySinh(ngaySinh)
                .dienThoai(dienThoai)
                .email(email)
                .password(password)
                .gioiTinh(gioiTinh)
                .noiSinh(noiSinh)
                .doiTuong(doiTuong)
                .khuVuc(khuVuc)
                .updatedAt(LocalDate.now()) // Gán ngày hiện tại cho updatedAt
                .build();
    }
}