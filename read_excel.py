import pandas as pd

def read_excel(file_path):
    print(f"--- {file_path} ---")
    try:
        df = pd.read_excel(file_path, nrows=5)
        print("Columns:", list(df.columns))
        print("Head:")
        print(df.head())
    except Exception as e:
        print(f"Error: {e}")

read_excel('src/main/java/com/tuyensinh/database/Uu tien xet tuyen.xlsx')
read_excel('src/main/java/com/tuyensinh/database/Ds quy doi tieng Anh.xlsx')
