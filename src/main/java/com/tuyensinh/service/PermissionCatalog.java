package com.tuyensinh.service;

import java.util.List;

public final class PermissionCatalog {
    public record PermissionItem(String code, String label) { }

    public record PermissionGroup(String title, List<PermissionItem> items) { }

    private static final List<PermissionGroup> GROUPS = List.of(
            new PermissionGroup("Quản lý người dùng", List.of(
                    new PermissionItem("USER_VIEW", "Xem danh sách người dùng"),
                    new PermissionItem("USER_CREATE", "Thêm người dùng mới"),
                    new PermissionItem("USER_EDIT", "Sửa thông tin, đổi password"),
                    new PermissionItem("USER_DELETE", "Xóa người dùng"),
                    new PermissionItem("USER_CHANGE_ROLE", "Đổi quyền user ↔ admin"),
                    new PermissionItem("USER_TOGGLE", "Enable / disable người dùng")
            )),
            new PermissionGroup("Quản lý thí sinh", List.of(
                    new PermissionItem("THISINH_IMPORT", "Import danh sách thí sinh"),
                    new PermissionItem("THISINH_VIEW", "Xem danh sách, tìm kiếm"),
                    new PermissionItem("THISINH_VIEW_BY_CCCD", "Xem thí sinh theo CCCD từ username đăng nhập"),
                    new PermissionItem("THISINH_CREATE", "Thêm thí sinh mới"),
                    new PermissionItem("THISINH_EDIT", "Sửa thông tin thí sinh"),
                    new PermissionItem("THISINH_DELETE", "Xóa thí sinh")
            )),
            new PermissionGroup("Quản lý điểm", List.of(
                    new PermissionItem("DIEM_IMPORT", "Import điểm"),
                    new PermissionItem("DIEM_VIEW", "Xem danh sách điểm"),
                    new PermissionItem("DIEM_VIEW_BY_CCCD", "Xem điểm thi theo CCCD từ username đăng nhập"),
                    new PermissionItem("DIEM_CREATE", "Thêm điểm"),
                    new PermissionItem("DIEM_EDIT", "Sửa điểm"),
                    new PermissionItem("DIEM_DELETE", "Xóa điểm"),
                    new PermissionItem("DIEM_THONGKE", "Thống kê điểm")
            )),
            new PermissionGroup("Quản lý điểm cộng", List.of(
                    new PermissionItem("DIEMCONG_IMPORT", "Import điểm cộng"),
                    new PermissionItem("DIEMCONG_VIEW", "Xem điểm cộng"),
                    new PermissionItem("DIEMCONG_VIEW_BY_CCCD", "Xem điểm cộng theo CCCD từ username đăng nhập"),
                    new PermissionItem("DIEMCONG_CREATE", "Thêm điểm cộng"),
                    new PermissionItem("DIEMCONG_EDIT", "Sửa điểm cộng"),
                    new PermissionItem("DIEMCONG_DELETE", "Xóa điểm cộng")
            )),
            new PermissionGroup("Quản lý ngành & tổ hợp", List.of(
                    new PermissionItem("NGANH_IMPORT", "Import ngành"),
                    new PermissionItem("NGANH_VIEW", "Xem danh sách ngành"),
                    new PermissionItem("NGANH_CREATE", "Thêm ngành"),
                    new PermissionItem("NGANH_EDIT", "Sửa ngành"),
                    new PermissionItem("NGANH_DELETE", "Xóa ngành"),
                    new PermissionItem("NGANH_TOHOP_VIEW", "Xem ngành - tổ hợp"),
                    new PermissionItem("NGANH_TOHOP_MANAGE", "Quản lý ngành - tổ hợp")
            )),
            new PermissionGroup("Quản lý nguyện vọng", List.of(
                    new PermissionItem("NGUYENVONG_VIEW", "Xem nguyện vọng"),
                    new PermissionItem("NGUYENVONG_VIEW_BY_CCCD", "Xem nguyện vọng theo CCCD từ username đăng nhập"),
                    new PermissionItem("NGUYENVONG_MANAGE", "Quản lý xét nguyện vọng")
            )),
            new PermissionGroup("Quản lý tổ hợp môn", List.of(
                    new PermissionItem("TOHOP_IMPORT", "Import tổ hợp môn"),
                    new PermissionItem("TOHOP_VIEW", "Xem tổ hợp môn"),
                    new PermissionItem("TOHOP_CREATE", "Thêm tổ hợp môn"),
                    new PermissionItem("TOHOP_EDIT", "Sửa tổ hợp môn"),
                    new PermissionItem("TOHOP_DELETE", "Xóa tổ hợp môn")
            )),
            new PermissionGroup("Quản lý bảng quy đổi", List.of(
                    new PermissionItem("QUYDOI_IMPORT", "Import bảng quy đổi"),
                    new PermissionItem("QUYDOI_VIEW", "Xem bảng quy đổi"),
                    new PermissionItem("QUYDOI_CREATE", "Thêm quy đổi"),
                    new PermissionItem("QUYDOI_EDIT", "Sửa quy đổi"),
                    new PermissionItem("QUYDOI_DELETE", "Xóa quy đổi")
            ))
    );

    private PermissionCatalog() {
    }

    public static List<PermissionGroup> groups() {
        return GROUPS;
    }

    public static List<String> allCodes() {
        return GROUPS.stream()
                .flatMap(group -> group.items().stream())
                .map(PermissionItem::code)
                .toList();
    }
}