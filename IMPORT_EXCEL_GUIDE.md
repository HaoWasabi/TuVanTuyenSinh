# Hướng dẫn Import BangQuyDoi từ Excel

## Cấu trúc file Excel

File Excel (`BangQuyDoi_import.xlsx`) cần có định dạng sau:

### Header (Dòng 1)
| Cột A | Cột B | Cột C | Cột D | Cột E | Cột F | Cột G | Cột H | Cột I |
|-------|-------|-------|-------|-------|-------|-------|-------|-------|
| phuongThuc | toHop | mon | dieA | dieB | dieC | dieD | maQuyDoi | phanvi |

### Dữ liệu (Từ dòng 2 trở đi)

**Ví dụ 1:**
| V-SAT | A00 | Toan | 90 | 105 | 6 | 7 | QD_VSAT_TOAN_90_105 | Vùng 90-105 |
| ------ | ------ | ------ | ------ | ------ | ------ | ------ | ------ | ------ |

**Ví dụ 2:**
| V-SAT | A00 | Ly | 105 | 120 | 7 | 8 | QD_VSAT_LY_105_120 | Vùng 105-120 |
| ------ | ------ | ------ | ------ | ------ | ------ | ------ | ------ | ------ |

**Ví dụ 3:**
| DGNL | D01 | Tong | 600 | 700 | 20 | 25 | QD_DGNL_TONG_600_700 | Vùng 600-700 |
| ------ | ------ | ------ | ------ | ------ | ------ | ------ | ------ | ------ |

## Chi tiết các cột

- **phuongThuc (Cột A)**: Phương thức tuyển sinh (V-SAT, DGNL, THPT, v.v.)
- **toHop (Cột B)**: Tổ hợp môn (A00, A01, D01, v.v.)
- **mon (Cột C)**: Môn học (Toan, Ly, Hoa, v.v.)
- **dieA (Cột D)**: Điểm A - Giới hạn dưới vùng nguồn (số thập phân)
- **dieB (Cột E)**: Điểm B - Giới hạn trên vùng nguồn (số thập phân)
- **dieC (Cột F)**: Điểm C - Giới hạn dưới vùng đích (số thập phân)
- **dieD (Cột G)**: Điểm D - Giới hạn trên vùng đích (số thập phân)
- **maQuyDoi (Cột H)**: Mã duy nhất của quy đổi (không được để trống)
- **phanvi (Cột I)**: Nhãn vùng (mục đích truy vết/debug)

## Các bước tạo file Excel

1. Mở Microsoft Excel hoặc LibreOffice Calc
2. Tạo sheet mới với header ở dòng 1
3. Điền dữ liệu từ dòng 2 trở đi theo cấu trúc trên
4. **Lưu file với tên**: `BangQuyDoi_import.xlsx`
5. **Lưu tại**: `src/resources/` (tạo folder nếu chưa có)

## Lưu ý quan trọng

- ⚠️ **Dòng 1 là header** - sẽ tự động bỏ qua
- ⚠️ **Số cột**: Phải đúng 9 cột (A-I)
- ⚠️ **Kiểu dữ liệu**:
  - Cột A-C, H-I: Text/String
  - Cột D-G: Number/Decimal
- ⚠️ **Dòng trống**: Tự động bỏ qua
- ✓ **Lỗi dòng**: Nếu một dòng lỗi, import tiếp tục các dòng khác
- ✓ **Format tệp**: Phải là `.xlsx` (Excel 2007+), không phải `.xls`

## Chạy Import

```bash
# Biên dịch project
mvn clean compile

# Chạy test (sẽ tự động import từ Excel nếu file tồn tại)
mvn test
# hoặc
java -cp target/classes com.tuyensinh.ServiceTestBQD
```

## Xem kết quả

- Kiểm tra output console - sẽ hiển thị số bản ghi import và dữ liệu
- Kiểm tra database (MySQL) - các dòng mới sẽ được thêm vào bảng `xt_bangquydoi`

## In lỗi thường gặp

| Lỗi | Nguyên nhân | Giải pháp |
|-----|-----------|----------|
| FileNotFoundException | File không tìm thấy | Kiểm tra đường dẫn và tên file |
| Lỗi dòng N | Dữ liệu không hợp lệ (VD: dieA không phải số) | Sửa dữ liệu ở dòng đó |
| Import 0 records | File Excel trống hoặc không đúng format | Kiểm tra header và dữ liệu |

