package com.tuyensinh.service;

import com.tuyensinh.model.Nganh;
import com.tuyensinh.model.NguyenVong;
import com.tuyensinh.model.ThiSinh;
import com.tuyensinh.repository.NganhRepository;
import com.tuyensinh.repository.NguyenVongRepository;
import com.tuyensinh.repository.ThiSinhRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import com.tuyensinh.database.HibernateUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class XetTuyenService {

    private final NguyenVongRepository nguyenVongRepository;
    private final NganhRepository nganhRepository;
    private final ThiSinhRepository thiSinhRepository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private volatile XetTuyenJob currentJob;
    private volatile boolean isLocked = false;
    private volatile LocalDateTime lastRunTime;
    private int currentRound = 0;

    public XetTuyenService() {
        this.nguyenVongRepository = new NguyenVongRepository();
        this.nganhRepository = new NganhRepository();
        this.thiSinhRepository = new ThiSinhRepository();
        loadRoundFromDatabase();
    }

    private void loadRoundFromDatabase() {
        List<Nganh> allNganh = nganhRepository.findAll();
        for (Nganh nganh : allNganh) {
            if (nganh.getNDiemtrungtuyen() != null) {
                currentRound = 1;
                break;
            }
        }
    }

    public boolean isFullQuota() {
        List<Nganh> allNganh = nganhRepository.findAll();
        for (Nganh nganh : allNganh) {
            if (nganh.getNChitieu() == null || nganh.getNChitieu() <= 0) continue;
            int slTrungTuyen = 0;
            List<NguyenVong> list = nguyenVongRepository.findByMaNganh(nganh.getManganh());
            for (NguyenVong nv : list) {
                if ("TRUNG_TUYEN".equals(nv.getNvKetqua())) slTrungTuyen++;
            }
            if (slTrungTuyen < nganh.getNChitieu()) {
                return false;
            }
        }
        return true;
    }

    public List<ValidationResult> validateData() {
        List<ValidationResult> results = new ArrayList<>();
        List<NguyenVong> allNV = nguyenVongRepository.findAll();
        List<Nganh> allNganh = nganhRepository.findAll();
        Map<String, Nganh> nganhMap = allNganh.stream()
                .collect(Collectors.toMap(Nganh::getManganh, n -> n, (a, b) -> a));

        Set<String> cccdSet = new HashSet<>();
        Map<String, List<NguyenVong>> nvByCccd = allNV.stream()
                .collect(Collectors.groupingBy(NguyenVong::getNnCccd));

        for (NguyenVong nv : allNV) {
            if (nv.getNnCccd() == null || nv.getNnCccd().trim().isEmpty()) {
                results.add(new ValidationResult(ValidationLevel.ERROR,
                        "Nguyện vọng ID " + nv.getIdnv() + ": Thiếu CCCD"));
            }
            if (nv.getNvManganh() == null || nv.getNvManganh().trim().isEmpty()) {
                results.add(new ValidationResult(ValidationLevel.ERROR,
                        "Nguyện vọng ID " + nv.getIdnv() + " (CCCD: " + nv.getNnCccd() + "): Thiếu mã ngành"));
            } else if (!nganhMap.containsKey(nv.getNvManganh())) {
                results.add(new ValidationResult(ValidationLevel.WARN,
                        "Nguyện vọng ID " + nv.getIdnv() + " (CCCD: " + nv.getNnCccd() + "): Mã ngành '" + nv.getNvManganh() + "' không tồn tại"));
            }
            if (nv.getDiemXettuyen() == null) {
                results.add(new ValidationResult(ValidationLevel.WARN,
                        "Nguyện vọng ID " + nv.getIdnv() + " (CCCD: " + nv.getNnCccd() + "): Chưa có điểm xét tuyển"));
            }
        }

        for (Map.Entry<String, Nganh> entry : nganhMap.entrySet()) {
            Nganh nganh = entry.getValue();
            if (nganh.getNChitieu() == null || nganh.getNChitieu() <= 0) {
                results.add(new ValidationResult(ValidationLevel.WARN,
                        "Ngành '" + entry.getKey() + "': Chưa có chỉ tiêu hoặc chỉ tiêu = 0"));
            }
        }

        for (Map.Entry<String, List<NguyenVong>> entry : nvByCccd.entrySet()) {
            String cccd = entry.getKey();
            List<NguyenVong> nvList = entry.getValue();
            Set<Integer> ttSet = new HashSet<>();
            for (NguyenVong nv : nvList) {
                if (nv.getNvTt() != null && !ttSet.add(nv.getNvTt())) {
                    results.add(new ValidationResult(ValidationLevel.WARN,
                            "CCCD " + cccd + ": Trùng thứ tự nguyện vọng NV" + nv.getNvTt()));
                }
            }
        }

        long errorCount = results.stream().filter(r -> r.level == ValidationLevel.ERROR).count();
        if (errorCount == 0) {
            results.add(0, new ValidationResult(ValidationLevel.INFO,
                    "Kiểm tra hoàn tất: " + allNV.size() + " nguyện vọng, " + nganhMap.size() + " ngành. Không có lỗi nghiêm trọng."));
        } else {
            results.add(0, new ValidationResult(ValidationLevel.ERROR,
                    "Phát hiện " + errorCount + " lỗi nghiêm trọng. Vui lòng sửa trước khi chạy xét tuyển."));
        }

        return results;
    }

    public CompletableFuture<XetTuyenResult> runXetTuyenAsync(XetTuyenConfig config) {
        currentJob = new XetTuyenJob();
        currentJob.setStatus("Đang chuẩn bị...");
        currentJob.setProgress(0);
        currentJob.setStartTime(LocalDateTime.now());

        return CompletableFuture.supplyAsync(() -> {
            try {
                return executeXetTuyen(config);
            } catch (Exception e) {
                currentJob.setStatus("Lỗi: " + e.getMessage());
                currentJob.addLog(ValidationLevel.ERROR, "Lỗi thực thi: " + e.getMessage());
                return XetTuyenResult.failure(e.getMessage());
            }
        }, executor);
    }

    private XetTuyenResult executeXetTuyen(XetTuyenConfig config) {
        updateProgress("Đang tải dữ liệu...", 5);
        addLog(ValidationLevel.INFO, "Bắt đầu xét tuyển...");

        loadRoundFromDatabase();
        Map<String, BigDecimal> previousCutoffs = new HashMap<>();
        List<Nganh> allNganh = nganhRepository.findAll();
        for (Nganh n : allNganh) {
            if (n.getNDiemtrungtuyen() != null) {
                previousCutoffs.put(n.getManganh(), n.getNDiemtrungtuyen());
            }
        }

        boolean isRound2Plus = !previousCutoffs.isEmpty();
        if (isRound2Plus) {
            currentRound = 2;
            addLog(ValidationLevel.INFO, "Đây là lần xét tuyển thứ 2. Điểm chuẩn lần 1 sẽ được giữ nguyên làm mức sàn.");
        } else {
            currentRound = 1;
            addLog(ValidationLevel.INFO, "Đây là lần xét tuyển thứ 1.");
        }

        List<NguyenVong> allNV = nguyenVongRepository.findAll();
        Map<String, ThiSinh> thiSinhMap = thiSinhRepository.findAll().stream()
                .collect(Collectors.toMap(ThiSinh::getCccd, t -> t, (a, b) -> a));

        Set<String> targetCccdSet = new HashSet<>();
        List<NguyenVong> filteredNV = allNV;

        if (config.getPhamVi() == XetTuyenConfig.PhamVi.THEO_NGANH && config.getNganhApDung() != null) {
            filteredNV = allNV.stream()
                    .filter(nv -> config.getNganhApDung().equals(nv.getNvManganh()))
                    .collect(Collectors.toList());
            addLog(ValidationLevel.INFO, "Phạm vi: Ngành " + config.getNganhApDung());
        } else if (config.getPhamVi() == XetTuyenConfig.PhamVi.THEO_PHUONG_THUC && config.getPhuongThucApDung() != null) {
            filteredNV = allNV.stream()
                    .filter(nv -> config.getPhuongThucApDung().equalsIgnoreCase(nv.getTtPhuongthuc()))
                    .collect(Collectors.toList());
            addLog(ValidationLevel.INFO, "Phạm vi: Phương thức " + config.getPhuongThucApDung());
        } else {
            addLog(ValidationLevel.INFO, "Phạm vi: Toàn trường");
        }

        for (NguyenVong nv : filteredNV) {
            if (nv.getNnCccd() != null) targetCccdSet.add(nv.getNnCccd());
        }

        updateProgress("Đã tải " + filteredNV.size() + " nguyện vọng của " + targetCccdSet.size() + " thí sinh", 15);

        Map<String, Integer> chiTieuMap = new HashMap<>();
        Map<String, Integer> daXetMap = new HashMap<>();
        for (Nganh nganh : allNganh) {
            chiTieuMap.put(nganh.getManganh(), nganh.getNChitieu() != null ? nganh.getNChitieu() : 0);
            int daTrung = 0;
            for (NguyenVong nv : filteredNV) {
                if (nv.getNvManganh() != null && nv.getNvManganh().equals(nganh.getManganh())
                        && "TRUNG_TUYEN".equals(nv.getNvKetqua())) {
                    daTrung++;
                }
            }
            daXetMap.put(nganh.getManganh(), daTrung);
        }

        Set<String> cccdTrungTuyen = new HashSet<>();
        int totalTrungTuyen = 0;
        int totalTruot = 0;
        int totalProcessed = 0;
        int totalNV = filteredNV.size();

        updateProgress("Đang xét tuyển...", 25);

        List<NguyenVong> sortedNV = filteredNV.stream()
                .sorted(Comparator
                        .comparing((NguyenVong nv) -> nv.getNvManganh() != null ? nv.getNvManganh() : "")
                        .thenComparing(nv -> nv.getDiemXettuyen() != null ? nv.getDiemXettuyen() : BigDecimal.ZERO, Comparator.reverseOrder())
                        .thenComparing(nv -> nv.getNvTt() != null ? nv.getNvTt() : 999))
                .collect(Collectors.toList());

        for (NguyenVong nv : sortedNV) {
            if (nv.getNnCccd() != null && cccdTrungTuyen.contains(nv.getNnCccd())) {
                if (!"TRUNG_TUYEN".equals(nv.getNvKetqua())) {
                    nv.setNvKetqua("TRUOT");
                }
                continue;
            }
        }

        List<NguyenVong> toUpdate = new ArrayList<>();

        for (NguyenVong nv : sortedNV) {
            if (currentJob != null && currentJob.isCancelled()) {
                addLog(ValidationLevel.WARN, "Đã hủy xét tuyển");
                return XetTuyenResult.cancelled();
            }

            totalProcessed++;
            String cccd = nv.getNnCccd();
            String manganh = nv.getNvManganh();
            if (manganh == null) continue;

            if (cccd != null && cccdTrungTuyen.contains(cccd)) {
                if (!"TRUNG_TUYEN".equals(nv.getNvKetqua())) {
                    nv.setNvKetqua("TRUOT");
                    toUpdate.add(nv);
                    totalTruot++;
                }
                continue;
            }

            if ("TRUNG_TUYEN".equals(nv.getNvKetqua())) {
                if (cccd != null) cccdTrungTuyen.add(cccd);
                continue;
            }

            if (isRound2Plus && previousCutoffs.containsKey(manganh)) {
                BigDecimal prevCutoff = previousCutoffs.get(manganh);
                BigDecimal diemXT = nv.getDiemXettuyen() != null ? nv.getDiemXettuyen() : BigDecimal.ZERO;
                if (diemXT.compareTo(prevCutoff) < 0) {
                    if (!"TRUNG_TUYEN".equals(nv.getNvKetqua())) {
                        nv.setNvKetqua("TRUOT");
                        toUpdate.add(nv);
                        totalTruot++;
                    }
                    continue;
                }
            }

            int chiTieu = chiTieuMap.getOrDefault(manganh, 0);
            int daXet = daXetMap.getOrDefault(manganh, 0);

            if (daXet < chiTieu) {
                nv.setNvKetqua("TRUNG_TUYEN");
                toUpdate.add(nv);
                if (cccd != null) cccdTrungTuyen.add(cccd);
                daXetMap.put(manganh, daXet + 1);
                totalTrungTuyen++;
            } else {
                if (!"TRUNG_TUYEN".equals(nv.getNvKetqua())) {
                    nv.setNvKetqua("TRUOT");
                    toUpdate.add(nv);
                    totalTruot++;
                }
            }

            if (totalProcessed % 50 == 0) {
                int progress = 25 + (int) ((double) totalProcessed / totalNV * 60);
                updateProgress("Đã xử lý " + totalProcessed + "/" + totalNV, progress);
            }
        }

        updateProgress("Đang lưu kết quả...", 90);

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            for (NguyenVong nv : toUpdate) {
                session.merge(nv);
            }
            tx.commit();
            addLog(ValidationLevel.INFO, "Đã lưu " + toUpdate.size() + " kết quả nguyện vọng");
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            addLog(ValidationLevel.ERROR, "Lỗi lưu kết quả: " + e.getMessage());
            return XetTuyenResult.failure("Lỗi lưu kết quả: " + e.getMessage());
        } finally {
            session.close();
        }

        updateProgress("Đang cập nhật điểm trúng tuyển...", 95);

        Map<String, BigDecimal> diemChuanMap = new HashMap<>();
        Map<String, Integer> slTrungTuyenMap = new HashMap<>();
        for (NguyenVong nv : toUpdate) {
            if (!"TRUNG_TUYEN".equals(nv.getNvKetqua())) continue;
            String manganh = nv.getNvManganh();
            if (manganh == null) continue;
            BigDecimal currentLowest = diemChuanMap.get(manganh);
            BigDecimal diem = nv.getDiemXettuyen() != null ? nv.getDiemXettuyen() : BigDecimal.ZERO;
            if (currentLowest == null || diem.compareTo(currentLowest) < 0) {
                diemChuanMap.put(manganh, diem);
            }
            slTrungTuyenMap.merge(manganh, 1, Integer::sum);
        }

        Session updateSession = HibernateUtil.getSessionFactory().openSession();
        Transaction tx2 = null;
        try {
            tx2 = updateSession.beginTransaction();
            for (Map.Entry<String, BigDecimal> entry : diemChuanMap.entrySet()) {
                String manganh = entry.getKey();
                BigDecimal diemChuan = entry.getValue();
                int slTrungTuyen = slTrungTuyenMap.getOrDefault(manganh, 0);

                if (isRound2Plus && previousCutoffs.containsKey(manganh)) {
                    BigDecimal prevCutoff = previousCutoffs.get(manganh);
                    if (diemChuan.compareTo(prevCutoff) < 0) {
                        diemChuan = prevCutoff;
                    }
                }

                Nganh nganh = nganhRepository.findByMaNganh(manganh).orElse(null);
                if (nganh != null) {
                    boolean updated = false;
                    if (nganh.getNDiemtrungtuyen() == null || diemChuan.compareTo(nganh.getNDiemtrungtuyen()) >= 0) {
                        nganh.setNDiemtrungtuyen(diemChuan);
                        updated = true;
                    }
                    if (nganh.getNChitieu() != null && nganh.getNChitieu() > 0) {
                        double tiLe = (double) slTrungTuyen / nganh.getNChitieu() * 100;
                        addLog(ValidationLevel.INFO, String.format("Ngành %s: điểm chuẩn = %s, trúng tuyển %d/%d (%.0f%%)",
                                manganh, diemChuan.stripTrailingZeros().toPlainString(),
                                slTrungTuyen, nganh.getNChitieu(), tiLe));
                    }
                    updateSession.merge(nganh);
                }
            }
            tx2.commit();
            addLog(ValidationLevel.INFO, "Đã cập nhật điểm trúng tuyển cho " + diemChuanMap.size() + " ngành");
        } catch (Exception e) {
            if (tx2 != null) tx2.rollback();
            addLog(ValidationLevel.WARN, "Lỗi cập nhật điểm trúng tuyển: " + e.getMessage());
        } finally {
            updateSession.close();
        }

        lastRunTime = LocalDateTime.now();
        isLocked = false;

        updateProgress("Hoàn tất!", 100);
        addLog(ValidationLevel.INFO, String.format("Xét tuyển lần %d hoàn tất: %d trúng tuyển, %d trượt, tổng %d nguyện vọng",
                currentRound, totalTrungTuyen, totalTruot, totalProcessed));

        return XetTuyenResult.success(totalProcessed, targetCccdSet.size(), totalTrungTuyen, totalTruot, 0);
    }

    public boolean rollbackXetTuyen() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.createMutationQuery("UPDATE NguyenVong SET nvKetqua = 'CHUA_XET' WHERE status = 'active'").executeUpdate();
            session.createMutationQuery("UPDATE Nganh SET nDiemtrungtuyen = NULL WHERE status = 'active'").executeUpdate();
            tx.commit();
            isLocked = false;
            lastRunTime = null;
            currentRound = 0;
            addLog(ValidationLevel.INFO, "Đã rollback toàn bộ kết quả xét tuyển và xóa điểm chuẩn");
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            addLog(ValidationLevel.ERROR, "Lỗi rollback: " + e.getMessage());
            return false;
        } finally {
            session.close();
        }
    }

    public boolean lockKetQua() {
        isLocked = true;
        addLog(ValidationLevel.INFO, "Đã khóa kết quả xét tuyển");
        return true;
    }

    public XetTuyenJob getCurrentJob() {
        return currentJob;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public LocalDateTime getLastRunTime() {
        return lastRunTime;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public List<XetTuyenLogEntry> getLogs() {
        if (currentJob == null) return new ArrayList<>();
        return currentJob.getLogs();
    }

    private void updateProgress(String status, int progress) {
        if (currentJob != null) {
            currentJob.setStatus(status);
            currentJob.setProgress(progress);
        }
    }

    private void addLog(ValidationLevel level, String message) {
        if (currentJob != null) {
            currentJob.addLog(level, message);
        }
    }

    public Map<String, Map<String, Long>> getThongKePivot() {
        List<NguyenVong> allNV = nguyenVongRepository.findAll();
        Map<String, Map<String, Long>> pivot = new TreeMap<>();

        for (NguyenVong nv : allNV) {
            if (!"TRUNG_TUYEN".equals(nv.getNvKetqua())) continue;
            String nganh = nv.getNvManganh() != null ? nv.getNvManganh() : "UNKNOWN";
            String phuongThuc = nv.getTtPhuongthuc() != null ? nv.getTtPhuongthuc() : "UNKNOWN";

            pivot.computeIfAbsent(nganh, k -> new TreeMap<>());
            Map<String, Long> ptMap = pivot.get(nganh);
            ptMap.put(phuongThuc, ptMap.getOrDefault(phuongThuc, 0L) + 1);
        }

        return pivot;
    }

    public List<NguyenVong> getTrungTuyenByFilter(String manganh, String phuongThuc) {
        return nguyenVongRepository.findAll().stream()
                .filter(nv -> "TRUNG_TUYEN".equals(nv.getNvKetqua()))
                .filter(nv -> manganh == null || manganh.isEmpty() || manganh.equals(nv.getNvManganh()))
                .filter(nv -> phuongThuc == null || phuongThuc.isEmpty() || phuongThuc.equalsIgnoreCase(nv.getTtPhuongthuc()))
                .sorted(Comparator
                        .comparing((NguyenVong nv) -> nv.getNvManganh() != null ? nv.getNvManganh() : "")
                        .thenComparing(nv -> nv.getDiemXettuyen() != null ? nv.getDiemXettuyen() : BigDecimal.ZERO, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    public void shutdown() {
        executor.shutdown();
    }

    public static class XetTuyenConfig {
        public enum PhamVi { TOAN_TRUONG, THEO_NGANH, THEO_PHUONG_THUC }

        private PhamVi phamVi = PhamVi.TOAN_TRUONG;
        private String nganhApDung;
        private String phuongThucApDung;
        private boolean uuTienThuTuNV = true;
        private boolean motNguyenVongDuyNhat = true;

        public PhamVi getPhamVi() { return phamVi; }
        public void setPhamVi(PhamVi phamVi) { this.phamVi = phamVi; }
        public String getNganhApDung() { return nganhApDung; }
        public void setNganhApDung(String nganhApDung) { this.nganhApDung = nganhApDung; }
        public String getPhuongThucApDung() { return phuongThucApDung; }
        public void setPhuongThucApDung(String phuongThucApDung) { this.phuongThucApDung = phuongThucApDung; }
        public boolean isUuTienThuTuNV() { return uuTienThuTuNV; }
        public void setUuTienThuTuNV(boolean uuTienThuTuNV) { this.uuTienThuTuNV = uuTienThuTuNV; }
        public boolean isMotNguyenVongDuyNhat() { return motNguyenVongDuyNhat; }
        public void setMotNguyenVongDuyNhat(boolean motNguyenVongDuyNhat) { this.motNguyenVongDuyNhat = motNguyenVongDuyNhat; }
    }

    public static class XetTuyenResult {
        private boolean success;
        private boolean cancelled;
        private String errorMessage;
        private int totalProcessed;
        private int totalCandidates;
        private int totalTrungTuyen;
        private int totalTruot;
        private int totalDuBi;

        public static XetTuyenResult success(int processed, int candidates, int trungTuyen, int truot, int duBi) {
            XetTuyenResult r = new XetTuyenResult();
            r.success = true;
            r.totalProcessed = processed;
            r.totalCandidates = candidates;
            r.totalTrungTuyen = trungTuyen;
            r.totalTruot = truot;
            r.totalDuBi = duBi;
            return r;
        }

        public static XetTuyenResult failure(String error) {
            XetTuyenResult r = new XetTuyenResult();
            r.success = false;
            r.errorMessage = error;
            return r;
        }

        public static XetTuyenResult cancelled() {
            XetTuyenResult r = new XetTuyenResult();
            r.cancelled = true;
            return r;
        }

        public boolean isSuccess() { return success; }
        public boolean isCancelled() { return cancelled; }
        public String getErrorMessage() { return errorMessage; }
        public int getTotalProcessed() { return totalProcessed; }
        public int getTotalCandidates() { return totalCandidates; }
        public int getTotalTrungTuyen() { return totalTrungTuyen; }
        public int getTotalTruot() { return totalTruot; }
        public int getTotalDuBi() { return totalDuBi; }
    }

    public static class XetTuyenJob {
        private volatile String status = "";
        private volatile int progress = 0;
        private volatile boolean cancelled = false;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private final List<XetTuyenLogEntry> logs = Collections.synchronizedList(new ArrayList<>());

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public int getProgress() { return progress; }
        public void setProgress(int progress) { this.progress = progress; }
        public boolean isCancelled() { return cancelled; }
        public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        public List<XetTuyenLogEntry> getLogs() { return logs; }
        public void addLog(ValidationLevel level, String message) {
            logs.add(new XetTuyenLogEntry(level, message));
        }
    }

    public static class XetTuyenLogEntry {
        private final LocalDateTime timestamp;
        private final ValidationLevel level;
        private final String message;

        public XetTuyenLogEntry(ValidationLevel level, String message) {
            this.timestamp = LocalDateTime.now();
            this.level = level;
            this.message = message;
        }

        public LocalDateTime getTimestamp() { return timestamp; }
        public ValidationLevel getLevel() { return level; }
        public String getMessage() { return message; }
        public String getFormattedTime() {
            return timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }
    }

    public static class ValidationResult {
        public final ValidationLevel level;
        public final String message;

        public ValidationResult(ValidationLevel level, String message) {
            this.level = level;
            this.message = message;
        }

        public ValidationLevel getLevel() { return level; }
        public String getMessage() { return message; }
    }

    public enum ValidationLevel {
        INFO, WARN, ERROR
    }
}
