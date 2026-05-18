import re

sql_file = 'src/main/java/com/tuyensinh/database/xettuyen2026.sql'
with open(sql_file, 'r', encoding='utf-8') as f:
    content = f.read()

# count total in xt_nguyenvongxettuyen
nv_match = re.search(r"INSERT INTO `xt_nguyenvongxettuyen`\nVALUES\n(.*?);", content, re.DOTALL)
if nv_match:
    lines = nv_match.group(1).split('\n')
    count_0 = 0
    count_non_0 = 0
    total = 0
    
    missing_keys = []
    
    for line in lines:
        if not line.strip(): continue
        parts = line.split(',')
        if len(parts) >= 13:
            nv_keys = parts[9].strip().strip("'")
            diem_cong = float(parts[6].strip())
            if diem_cong == 0.0:
                count_0 += 1
                missing_keys.append(nv_keys)
            else:
                count_non_0 += 1
            total += 1
    print(f"Total Nv: {total}")
    print(f"Nv with diem_cong == 0: {count_0}")
    print(f"Nv with diem_cong != 0: {count_non_0}")
    
    # Let's check how many dc_keys are in xt_diemcongxetuyen
    dc_match = re.search(r"INSERT INTO `xt_diemcongxetuyen`.*?\nVALUES\n(.*?);", content, re.DOTALL)
    dc_keys_set = set()
    if dc_match:
        dc_lines = dc_match.group(1).strip().split('\n')
        for dc_line in dc_lines:
            if not dc_line.strip(): continue
            dc_parts = dc_line.split(',')
            if len(dc_parts) >= 10:
                dc_key = dc_parts[8].strip().strip("'")
                dc_keys_set.add(dc_key)
    
    print(f"\nTotal DC keys in xt_diemcongxetuyen: {len(dc_keys_set)}")
    
    # See intersection
    intersect = set(missing_keys).intersection(dc_keys_set)
    print(f"Missing keys that actually exist in DC keys: {len(intersect)}")
