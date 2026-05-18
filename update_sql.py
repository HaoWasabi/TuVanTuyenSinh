import pandas as pd
import re

sql_file = 'src/main/java/com/tuyensinh/database/xettuyen2026.sql'
with open(sql_file, 'r', encoding='utf-8') as f:
    content = f.read()

# Load Excel data
df_ut = pd.read_excel('src/main/java/com/tuyensinh/database/Uu tien xet tuyen.xlsx')
df_ta = pd.read_excel('src/main/java/com/tuyensinh/database/Ds quy doi tieng Anh.xlsx')

# 'Điểm cộng cho môn đạt giải' and 'Điểm cộng'
diem_ut_list = df_ut['Điểm cộng cho môn đạt giải'].fillna(0).tolist()
diem_ta_list = df_ta['Điểm cộng'].fillna(0).tolist()

# Find the block of INSERTS into xt_diemcongxetuyen
insert_match = re.search(r"INSERT INTO `xt_diemcongxetuyen`\n\(`ts_cccd`.*?\)\nVALUES\n(.*?);", content, re.DOTALL)
if insert_match:
    lines = insert_match.group(1).split('\n')
    new_lines = []
    
    for i, line in enumerate(lines):
        if not line.strip(): continue
        
        # A line looks like:
        #        ('...', '...', '...', '...', diemCC, diemUtxt, diemTong, '...', '...', 'active')
        parts = line.split(',')
        if len(parts) >= 10:
            diem_cc = diem_ta_list[i] if i < len(diem_ta_list) else 0.0
            diem_ut = diem_ut_list[i] if i < len(diem_ut_list) else 0.0
            diem_tong = round(float(diem_cc) + float(diem_ut), 2)
            
            # parts[4] is diemCC, parts[5] is diemUtxt, parts[6] is diemTong
            parts[4] = f" {diem_cc}"
            parts[5] = f" {diem_ut}"
            parts[6] = f" {diem_tong}"
            
            # Need to fix up any single quotes that were split if they had commas? 
            # Our ghichu does not have commas, so it should be fine. Wait, let's check ghichu.
            # "mock data" -> no commas.
        new_lines.append(','.join(parts))
        
    new_block = "INSERT INTO `xt_diemcongxetuyen`\n(`ts_cccd`, `manganh`, `matohop`, `phuongthuc`, `diemCC`, `diemUtxt`, `diemTong`, `ghichu`, `dc_keys`, `status`)\nVALUES\n"
    new_block += '\n'.join(new_lines) + ";"
    
    content = content[:insert_match.start()] + new_block + content[insert_match.end():]
    
    with open(sql_file, 'w', encoding='utf-8') as f:
        f.write(content)
    print("Done updating diemUtxt and diemCC!")
else:
    print("Could not find the INSERT block.")
