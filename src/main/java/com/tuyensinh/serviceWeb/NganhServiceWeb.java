package com.tuyensinh.serviceWeb;

import com.tuyensinh.ModelWeb.NganhTraCuuResponse;
import com.tuyensinh.model.Nganh;
import com.tuyensinh.model.NganhToHop;
import com.tuyensinh.repository.NTHRepository;
import com.tuyensinh.repository.NganhRepository;
import com.tuyensinh.service.TohopMonthiService;
import com.tuyensinh.utilWeb.CongThucUtil;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class NganhServiceWeb {

    private final NganhRepository nganhRepository;
    private final NTHRepository nthRepository;
    private final TohopMonthiService tohopMonthiService;

    public NganhServiceWeb() {
        this.nganhRepository = new NganhRepository();
        this.nthRepository = new NTHRepository();
        this.tohopMonthiService = new TohopMonthiService();
    }

    public NganhServiceWeb(NganhRepository nganhRepository) {
        this.nganhRepository = nganhRepository;
        this.nthRepository = new NTHRepository();
        this.tohopMonthiService = new TohopMonthiService();
    }

    public Optional<Nganh> getByMaNganh(String manganh) {
        return nganhRepository.findByMaNganh(manganh);
    }

    public Optional<Nganh> getByTenNganh(String tennganh) {
        return nganhRepository.findByTenNganh(tennganh);
    }

    public List<Nganh> getAllNganh() {
        return nganhRepository.findAll();
    }

    public List<NganhTraCuuResponse> searchTraCuuNganh(String keyword) {
        Map<String, String> tenToHopByMa = buildTenToHopMap();
        return searchByKeyword(keyword).stream()
                .map(nganh -> buildTraCuuResponse(nganh, tenToHopByMa))
                .collect(Collectors.toList());
    }

    public List<Nganh> searchByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllNganh();
        }

        String normalizedKeyword = keyword.trim().toLowerCase();

        return nganhRepository.findAll().stream()
                .filter(nganh -> matchesKeyword(nganh, normalizedKeyword))
                .collect(Collectors.toList());
    }

    public Optional<Nganh> findByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return Optional.empty();
        }

        String normalizedKeyword = keyword.trim();

        Optional<Nganh> byMaNganh = getByMaNganh(normalizedKeyword);
        if (byMaNganh.isPresent()) {
            return byMaNganh;
        }

        Optional<Nganh> byTenNganh = getByTenNganh(normalizedKeyword);
        if (byTenNganh.isPresent()) {
            return byTenNganh;
        }

        String lowerKeyword = normalizedKeyword.toLowerCase();
        return nganhRepository.findAll().stream()
                .filter(nganh -> matchesKeyword(nganh, lowerKeyword))
                .findFirst();
    }

    public List<String> getAllMaNganh() {
        return nganhRepository.findAll().stream()
                .map(Nganh::getManganh)
                .collect(Collectors.toList());
    }

    public List<String> getAllTenNganh() {
        return nganhRepository.findAll().stream()
                .map(Nganh::getTenNganh)
                .collect(Collectors.toList());
    }

    public List<String> getAllTenNganhWithMaNganh() {
        return nganhRepository.findAll().stream()
                .map(n -> n.getTenNganh() + " (" + n.getManganh() + ")")
                .collect(Collectors.toList());
    }

    private boolean matchesKeyword(Nganh nganh, String keyword) {
        String tenNganh = nganh.getTenNganh() == null ? "" : nganh.getTenNganh().toLowerCase();
        String maNganh = nganh.getManganh() == null ? "" : nganh.getManganh().toLowerCase();

        return tenNganh.contains(keyword) || maNganh.contains(keyword);
    }

    private NganhTraCuuResponse buildTraCuuResponse(Nganh nganh, Map<String, String> tenToHopByMa) {
        String maToHopGoc = normalizeCode(nganh.getnTohopgoc());
        List<NganhToHop> danhSachToHop = nganh.getManganh() == null
                ? List.of()
                : nthRepository.findByMaNganh(nganh.getManganh());

        List<NganhTraCuuResponse.ToHopDiemInfo> toHopKhacList = danhSachToHop.stream()
                .filter(item -> item.getMaToHop() != null)
                .filter(item -> maToHopGoc == null || !maToHopGoc.equalsIgnoreCase(item.getMaToHop().trim()))
                .map(item -> buildToHopInfo(nganh, maToHopGoc, item, tenToHopByMa))
                .sorted(Comparator.comparing(info -> info.getMaToHop() == null ? "" : info.getMaToHop()))
                .collect(Collectors.toList());

        return NganhTraCuuResponse.builder()
                .maNganh(nganh.getManganh())
                .tenNganh(nganh.getTenNganh())
                .maToHopGoc(maToHopGoc)
                .tenToHopGoc(resolveTenToHop(maToHopGoc, tenToHopByMa))
                .chiTieu(nganh.getnChitieu())
                .diemSan(nganh.getnDiemsan())
                .diemTrungTuyen(nganh.getnDiemtrungtuyen())
                .slXtt(nganh.getSlXtt())
                .slThpt(nganh.getSlThpt())
                .slVsat(nganh.getSlVsat())
                .slDgnl(nganh.getSlDgnl())
                .toHopKhacList(toHopKhacList)
                .build();
    }

    private NganhTraCuuResponse.ToHopDiemInfo buildToHopInfo(Nganh nganh, String maToHopGoc,
            NganhToHop toHopItem, Map<String, String> tenToHopByMa) {
        String maToHop = normalizeCode(toHopItem.getMaToHop());
        String tenToHop = resolveTenToHop(maToHop, tenToHopByMa);
        boolean laToHopGoc = maToHopGoc != null && maToHopGoc.equalsIgnoreCase(maToHop);

        BigDecimal diemSanQuyDoi = null;
        BigDecimal diemTrungTuyenQuyDoi = null;
        if (!laToHopGoc && maToHopGoc != null) {
            diemSanQuyDoi = CongThucUtil.quyDoiVeToHopGoc(nganh.getnDiemsan(), maToHop, maToHopGoc);
            diemTrungTuyenQuyDoi = CongThucUtil.quyDoiVeToHopGoc(nganh.getnDiemtrungtuyen(), maToHop, maToHopGoc);
        }

        return NganhTraCuuResponse.ToHopDiemInfo.builder()
                .maToHop(maToHop)
                .tenToHop(tenToHop)
                .doLech(toHopItem.getDoLech())
                .diemSanQuyDoi(diemSanQuyDoi)
                .diemTrungTuyenQuyDoi(diemTrungTuyenQuyDoi)
                .laToHopGoc(laToHopGoc)
                .build();
    }

    private Map<String, String> buildTenToHopMap() {
        Map<String, String> tenToHopByMa = new HashMap<>();
        tohopMonthiService.getAll().stream()
                .filter(item -> item.getMatohop() != null)
                .forEach(item -> tenToHopByMa.put(normalizeCode(item.getMatohop()), item.getTentohop()));
        return tenToHopByMa;
    }

    private String resolveTenToHop(String maToHop, Map<String, String> tenToHopByMa) {
        if (maToHop == null) {
            return null;
        }
        String tenToHop = tenToHopByMa.get(maToHop.toUpperCase(Locale.ROOT));
        return tenToHop == null || tenToHop.isBlank() ? maToHop : tenToHop;
    }

    private String normalizeCode(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }
}
