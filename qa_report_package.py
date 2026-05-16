from pathlib import Path
from zipfile import ZipFile

p = Path(r"C:\Users\Zz\Desktop\生产实习\output\report_final.docx")
with ZipFile(p) as z:
    names = z.namelist()
    print("valid zip", "[Content_Types].xml" in names, "word/document.xml" in names)
    media = [n for n in names if n.startswith("word/media/")]
    print("media count", len(media), media)
    xml = z.read("word/document.xml").decode("utf-8", errors="ignore")
    for bad in ["蓝色字体全部删除", "简要描述选题背景", "统一按以下格式"]:
        print(bad, bad in xml)
