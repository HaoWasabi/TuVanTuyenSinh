package com.tuyensinh.service;

import com.tuyensinh.model.DiemThi;
import com.tuyensinh.repository.DiemThiRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class DiemThiService {

    private final DiemThiRepository repository;

    public DiemThiService() {
        this.repository = new DiemThiRepository();
    }

    public DiemThiService(DiemThiRepository repository) {
        this.repository = repository;
    }

    public void create(DiemThi diemThi) {
        repository.save(diemThi);
    }

    public Optional<DiemThi> getById(Integer id) {
        return repository.findById(id);
    }

    public List<DiemThi> getByCccd(String cccd) {
        return repository.findByCccd(cccd);
    }

    public List<DiemThi> getAll() {
        return repository.findAll();
    }

    public boolean add(DiemThi dt) {
        return repository.add(dt);
    }

    public DiemThi update(DiemThi diemThi) {
        return repository.update(diemThi);
    }

    public boolean delete(Integer id) {
        return repository.deleteById(id);
    }

    public List<DiemThi> importFromExcel(String filePath) throws IOException {
        return repository.importFromExcel(filePath);
    }

    // ================= THỐNG KÊ =================

    /**
     * Thống kê điểm theo môn (trung bình, min, max, số lượng)
     * @param mon Tên môn: toan, vatLi, hoaHoc, sinhHoc, lichSu, diaLi, nguVan, tinHoc, ktpl, n1Thi, n1Cc, cncn, cnnn, nl1, nk1, nk2
     * @return Object[] {avg, min, max, count}
     */
    public Object[] thongKeDiemTheoMon(String mon) {
        return repository.thongKeDiemTheoMon(mon);
    }

    /**
     * Đếm số thí sinh có điểm trong khoảng
     */
    public Long demTheoKhoangDiem(String mon, BigDecimal tuDiem, BigDecimal denDiem) {
        return repository.demTheoKhoangDiem(mon, tuDiem, denDiem);
    }

    /**
     * Thống kê số lượng theo phương thức xét tuyển
     * @return List của Object[] {phuongThuc, soLuong}
     */
    public List<Object[]> thongKeSoLuongTheoPhuongThuc() {
        return repository.thongKeSoLuongTheoPhuongThuc();
    }

    /**
     * Thống kê điểm trung bình tất cả các môn
     * @return Object[] {avgToan, avgLy, avgHoa, avgSinh, avgSu, avgDia, avgVan, avgTin, avgKtpl}
     */
    public Object[] thongKeDiemTrungBinhTatCaMon() {
        return repository.thongKeDiemTrungBinhTatCaMon();
    }

    /**
     * Thống kê phân bố điểm theo môn (0-2, 2-4, 4-6, 6-8, 8-10)
     * @param mon Tên môn
     * @return List của Object[] {khoangDiem, soLuong}
     */
    public List<Object[]> thongKePhanBoTheoMon(String mon) {
        return repository.thongKePhanBoTheoMon(mon);
    }
}
