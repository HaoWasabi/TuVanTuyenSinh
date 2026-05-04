package com.tuyensinh.serviceWeb;

import com.tuyensinh.ModelWeb.ThiSinhWithNguyenVongResponse;
import com.tuyensinh.model.ThiSinh;
import com.tuyensinh.model.NguyenVong;
import com.tuyensinh.repository.ThiSinhRepository;
import com.tuyensinh.repository.NguyenVongRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Web: Xử lý API GET /api/thisinh/{cccd}/{ngaySinh}
 * Join dữ liệu từ bảng xt_thisinhxettuyen25 và xt_nguyenvongxettuyen
 */
public class ThiSinhWithNguyenVongServiceWeb {
    
    private final ThiSinhRepository thiSinhRepository;
    private final NguyenVongRepository nguyenVongRepository;
    
    public ThiSinhWithNguyenVongServiceWeb() {
        this.thiSinhRepository = new ThiSinhRepository();
        this.nguyenVongRepository = new NguyenVongRepository();
    }
    
    public ThiSinhWithNguyenVongServiceWeb(ThiSinhRepository thiSinhRepository, 
                                          NguyenVongRepository nguyenVongRepository) {
        this.thiSinhRepository = thiSinhRepository;
        this.nguyenVongRepository = nguyenVongRepository;
    }
    
    /**
     * Lấy thông tin thí sinh + danh sách nguyên vọng xét tuyển theo CCCD và ngày sinh
     * 
     * @param cccd Số CCCD của thí sinh
     * @param ngaySinh Ngày sinh (định dạng: ddMMyyyy hoặc dd/MM/yyyy)
     * @return Optional chứa ThiSinhWithNguyenVongResponse hoặc empty nếu không tìm thấy
     */
    public Optional<ThiSinhWithNguyenVongResponse> getThiSinhWithNguyenVong(String cccd, String ngaySinh) {
        // Bước 1: Tìm thí sinh theo CCCD và ngày sinh
        Optional<ThiSinh> thiSinhOpt = thiSinhRepository.findByCccdAndNgaySinh(cccd, ngaySinh);
        
        if (thiSinhOpt.isEmpty()) {
            return Optional.empty();
        }
        
        ThiSinh thiSinh = thiSinhOpt.get();
        
        // Bước 2: Lấy danh sách nguyên vọng theo CCCD
        List<NguyenVong> nguyenVongList = nguyenVongRepository.findByCccd(cccd);
        
        // Bước 3: Chuyển đổi sang DTO response
        return Optional.of(buildThiSinhWithNguyenVongResponse(thiSinh, nguyenVongList));
    }
    
    /**
     * Lấy thông tin thí sinh + danh sách nguyên vọng theo CCCD (tìm chính xác)
     * 
     * @param cccd Số CCCD của thí sinh
     * @return Optional chứa ThiSinhWithNguyenVongResponse hoặc empty nếu không tìm thấy
     */
    public Optional<ThiSinhWithNguyenVongResponse> getThiSinhWithNguyenVongByCccd(String cccd) {
        // Bước 1: Tìm thí sinh theo CCCD
        Optional<ThiSinh> thiSinhOpt = thiSinhRepository.findByCccd(cccd);
        
        if (thiSinhOpt.isEmpty()) {
            return Optional.empty();
        }
        
        ThiSinh thiSinh = thiSinhOpt.get();
        
        // Bước 2: Lấy danh sách nguyên vọng theo CCCD
        List<NguyenVong> nguyenVongList = nguyenVongRepository.findByCccd(cccd);
        
        // Bước 3: Chuyển đổi sang DTO response
        return Optional.of(buildThiSinhWithNguyenVongResponse(thiSinh, nguyenVongList));
    }
    
    /**
     * Hỗ trợ: Xây dựng Response DTO từ entity model
     */
    private ThiSinhWithNguyenVongResponse buildThiSinhWithNguyenVongResponse(
            ThiSinh thiSinh, 
            List<NguyenVong> nguyenVongList) {
        
        // Chuyển đổi danh sách NguyenVong sang NguyenVongInfo DTO
        List<ThiSinhWithNguyenVongResponse.NguyenVongInfo> nguyenVongInfoList = 
            nguyenVongList.stream()
                .map(nv -> ThiSinhWithNguyenVongResponse.NguyenVongInfo.builder()
                    .idnv(nv.getIdnv())
                    .nv_manganh(nv.getNvManganh())
                    .nv_tt(nv.getNvTt())
                    .diem_thxt(nv.getDiemThxt())
                    .diem_utqd(nv.getDiemUtqd())
                    .diem_cong(nv.getDiemCong())
                    .diem_xettuyen(nv.getDiemXettuyen())
                    .nv_ketqua(nv.getNvKetqua())
                    .nv_keys(nv.getNvKeys())
                    .tt_phuongthuc(nv.getTtPhuongthuc())
                    .tt_thm(nv.getTtThm())
                    .build())
                .collect(Collectors.toList());
        
        // Xây dựng response DTO
        return ThiSinhWithNguyenVongResponse.builder()
            .cccd(thiSinh.getCccd())
            .sobaodanh(thiSinh.getSobaodanh())
            .ho(thiSinh.getHo())
            .ten(thiSinh.getTen())
            .ngaySinh(thiSinh.getNgaySinh())
            .dienThoai(thiSinh.getDienThoai())
            .gioiTinh(thiSinh.getGioiTinh())
            .email(thiSinh.getEmail())
            .noiSinh(thiSinh.getNoiSinh())
            .doiTuong(thiSinh.getDoiTuong())
            .khuVuc(thiSinh.getKhuVuc())
            .nguyenVongList(nguyenVongInfoList)
            .build();
    }
}
