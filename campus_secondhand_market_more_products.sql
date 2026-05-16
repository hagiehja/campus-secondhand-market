USE campus_secondhand_market;
SET NAMES utf8mb4;

UPDATE product SET status = 'ON_SALE' WHERE status IN ('LOCKED', 'OFF_SHELF');

INSERT INTO product
  (seller_id, category_id, title, description, price, original_price, condition_level, trade_place, status, view_count, created_at)
SELECT
  seed.seller_id, seed.category_id, seed.title, seed.description, seed.price, seed.original_price,
  seed.condition_level, seed.trade_place, 'ON_SALE', seed.view_count, seed.created_at
FROM (
  SELECT 2 AS seller_id, 1 AS category_id, '数据库系统概论教材' AS title, '课程设计和数据库期末复习都能用，内页干净。' AS description, 25.00 AS price, 59.00 AS original_price, 'GOOD' AS condition_level, '图书馆门口' AS trade_place, 128 AS view_count, DATE_SUB(NOW(), INTERVAL 1 HOUR) AS created_at
  UNION ALL SELECT 2, 1, '高等数学同济第七版', '同济版高数教材，重点页有少量笔记。', 32.00, 68.00, 'LIKE_NEW', '一教大厅', 156, DATE_SUB(NOW(), INTERVAL 2 HOUR)
  UNION ALL SELECT 2, 1, '计算机网络教材', '网络协议章节标注完整，适合复习。', 30.00, 75.00, 'GOOD', '图书馆二楼', 119, DATE_SUB(NOW(), INTERVAL 3 HOUR)
  UNION ALL SELECT 2, 1, 'Java程序设计实训书', 'Java 入门和 Spring Boot 前置练习可用。', 28.00, 69.00, 'FAIR', '软件楼门口', 88, DATE_SUB(NOW(), INTERVAL 4 HOUR)
  UNION ALL SELECT 2, 1, '概率论与数理统计', '公共课教材，封面有轻微折痕。', 22.00, 52.00, 'GOOD', '三教门口', 101, DATE_SUB(NOW(), INTERVAL 5 HOUR)
  UNION ALL SELECT 2, 1, '数据结构课程教材', '算法题部分有少量荧光笔标记。', 36.00, 86.00, 'LIKE_NEW', '实验楼A座', 133, DATE_SUB(NOW(), INTERVAL 6 HOUR)
  UNION ALL SELECT 2, 1, '英语六级词汇书', '六级备考词汇书，附赠听力资料链接。', 18.00, 45.00, 'FAIR', '四教自习室', 74, DATE_SUB(NOW(), INTERVAL 7 HOUR)
  UNION ALL SELECT 2, 1, '操作系统考研讲义', '操作系统重点知识整理，适合考研和期末。', 34.00, 89.00, 'GOOD', '图书馆南门', 94, DATE_SUB(NOW(), INTERVAL 8 HOUR)
  UNION ALL SELECT 2, 2, '二手无线鼠标', '无线鼠标，接收器正常，办公学习够用。', 35.00, 89.00, 'LIKE_NEW', '教学楼A区', 96, DATE_SUB(NOW(), INTERVAL 9 HOUR)
  UNION ALL SELECT 2, 2, '降噪蓝牙耳机', '降噪功能正常，适合自习室使用。', 118.00, 299.00, 'GOOD', '宿舍区3栋', 214, DATE_SUB(NOW(), INTERVAL 10 HOUR)
  UNION ALL SELECT 2, 2, '机械键盘青轴', '青轴机械键盘，按键灵敏。', 139.00, 329.00, 'GOOD', '实验楼大厅', 142, DATE_SUB(NOW(), INTERVAL 11 HOUR)
  UNION ALL SELECT 2, 2, 'iPad学习平板', '平板成色新，适合看课件和记笔记。', 1480.00, 2699.00, 'LIKE_NEW', '图书馆南门', 432, DATE_SUB(NOW(), INTERVAL 12 HOUR)
  UNION ALL SELECT 2, 2, '轻薄笔记本电脑', '轻薄办公本，写代码和做课设够用。', 2680.00, 5299.00, 'GOOD', '创新楼一层', 386, DATE_SUB(NOW(), INTERVAL 13 HOUR)
  UNION ALL SELECT 2, 2, '24寸显示器', '宿舍外接显示器，无坏点。', 420.00, 899.00, 'GOOD', '宿舍区8栋', 277, DATE_SUB(NOW(), INTERVAL 14 HOUR)
  UNION ALL SELECT 2, 2, '蓝牙音箱', '小音箱，音质清楚，适合宿舍桌面。', 89.00, 219.00, 'LIKE_NEW', '操场看台', 166, DATE_SUB(NOW(), INTERVAL 15 HOUR)
  UNION ALL SELECT 2, 2, '移动充电宝', '容量够用，接口正常。', 45.00, 119.00, 'GOOD', '二教大厅', 144, DATE_SUB(NOW(), INTERVAL 16 HOUR)
  UNION ALL SELECT 2, 2, '微单相机', '摄影课练习用，相机功能正常。', 2180.00, 4299.00, 'FAIR', '艺术楼门口', 325, DATE_SUB(NOW(), INTERVAL 17 HOUR)
  UNION ALL SELECT 2, 2, '千兆路由器', '宿舍网络覆盖稳定，重置后可用。', 65.00, 169.00, 'GOOD', '宿舍区11栋', 97, DATE_SUB(NOW(), INTERVAL 18 HOUR)
  UNION ALL SELECT 2, 2, '游戏手柄', '按键回弹正常，支持电脑连接。', 95.00, 239.00, 'LIKE_NEW', '东门快递站', 182, DATE_SUB(NOW(), INTERVAL 19 HOUR)
  UNION ALL SELECT 2, 2, '摄影补光灯', '直播和拍作业展示都能用。', 126.00, 299.00, 'GOOD', '传媒楼门口', 189, DATE_SUB(NOW(), INTERVAL 20 HOUR)
  UNION ALL SELECT 2, 3, '宿舍护眼台灯', '亮度可调，宿舍学习夜用。', 48.00, 129.00, 'LIKE_NEW', '二食堂门口', 83, DATE_SUB(NOW(), INTERVAL 21 HOUR)
  UNION ALL SELECT 2, 3, '床上折叠书桌', '床上桌稳固，适合宿舍学习。', 39.00, 99.00, 'GOOD', '宿舍区2栋', 123, DATE_SUB(NOW(), INTERVAL 22 HOUR)
  UNION ALL SELECT 2, 3, '桌面收纳架', '书桌收纳架，能放教材和键盘。', 26.00, 69.00, 'LIKE_NEW', '三食堂门口', 86, DATE_SUB(NOW(), INTERVAL 23 HOUR)
  UNION ALL SELECT 2, 3, '宿舍小风扇', '小风扇三档可调，夏天自习好用。', 35.00, 88.00, 'GOOD', '宿舍区6栋', 108, DATE_SUB(NOW(), INTERVAL 24 HOUR)
  UNION ALL SELECT 2, 3, '迷你电饭煲', '小容量电饭煲，功能正常。', 79.00, 199.00, 'FAIR', '西门生活区', 149, DATE_SUB(NOW(), INTERVAL 25 HOUR)
  UNION ALL SELECT 2, 3, '宿舍小冰箱', '小冰箱制冷正常，毕业转让。', 260.00, 599.00, 'GOOD', '宿舍区10栋', 238, DATE_SUB(NOW(), INTERVAL 26 HOUR)
  UNION ALL SELECT 2, 3, '人体工学椅', '坐垫舒适，适合长时间写课设。', 180.00, 469.00, 'LIKE_NEW', '研究生公寓', 221, DATE_SUB(NOW(), INTERVAL 27 HOUR)
  UNION ALL SELECT 2, 3, '落地穿衣镜', '镜面完好，宿舍自提。', 55.00, 139.00, 'GOOD', '宿舍区4栋', 93, DATE_SUB(NOW(), INTERVAL 28 HOUR)
  UNION ALL SELECT 2, 3, '宿舍地毯', '浅色地毯，已清洁。', 42.00, 118.00, 'LIKE_NEW', '宿舍区12栋', 91, DATE_SUB(NOW(), INTERVAL 29 HOUR)
  UNION ALL SELECT 2, 4, '校园通勤自行车', '车况稳定，适合校内短途通勤。', 260.00, 699.00, 'FAIR', '北门车棚', 171, DATE_SUB(NOW(), INTERVAL 30 HOUR)
  UNION ALL SELECT 2, 4, '羽毛球拍套装', '两支拍加拍套，线还比较新。', 75.00, 199.00, 'LIKE_NEW', '体育馆门口', 169, DATE_SUB(NOW(), INTERVAL 31 HOUR)
  UNION ALL SELECT 2, 4, '瑜伽垫', '厚垫，适合宿舍运动。', 38.00, 109.00, 'GOOD', '操场入口', 82, DATE_SUB(NOW(), INTERVAL 32 HOUR)
  UNION ALL SELECT 2, 4, '可调节哑铃', '成对哑铃，重量可调。', 120.00, 299.00, 'FAIR', '体育馆负一层', 197, DATE_SUB(NOW(), INTERVAL 33 HOUR)
  UNION ALL SELECT 2, 4, '室外篮球', '篮球弹性好，适合室外场。', 45.00, 129.00, 'GOOD', '篮球场边', 141, DATE_SUB(NOW(), INTERVAL 34 HOUR)
  UNION ALL SELECT 2, 4, '网球拍', '拍面完整，手胶新换。', 96.00, 239.00, 'LIKE_NEW', '网球场入口', 117, DATE_SUB(NOW(), INTERVAL 35 HOUR)
  UNION ALL SELECT 2, 4, '跳绳计数款', '电子计数跳绳，健身打卡用。', 18.00, 49.00, 'LIKE_NEW', '操场主席台', 66, DATE_SUB(NOW(), INTERVAL 36 HOUR)
  UNION ALL SELECT 2, 5, '双肩电脑包', '电脑隔层完好，容量大。', 68.00, 189.00, 'GOOD', '东门快递站', 151, DATE_SUB(NOW(), INTERVAL 37 HOUR)
  UNION ALL SELECT 2, 5, '运动跑鞋', '跑步鞋，鞋底磨损轻。', 168.00, 499.00, 'LIKE_NEW', '体育馆门口', 247, DATE_SUB(NOW(), INTERVAL 38 HOUR)
  UNION ALL SELECT 2, 5, '春秋夹克外套', '薄外套，春秋穿合适。', 88.00, 269.00, 'GOOD', '宿舍区9栋', 134, DATE_SUB(NOW(), INTERVAL 39 HOUR)
  UNION ALL SELECT 2, 5, '20寸登机箱', '箱体有轻微划痕，轮子顺滑。', 110.00, 299.00, 'FAIR', '北门快递柜', 178, DATE_SUB(NOW(), INTERVAL 40 HOUR)
  UNION ALL SELECT 2, 5, '连帽卫衣', '卫衣成色新，版型宽松。', 58.00, 159.00, 'LIKE_NEW', '宿舍区1栋', 103, DATE_SUB(NOW(), INTERVAL 41 HOUR)
  UNION ALL SELECT 2, 5, '帆布托特包', '日常上课通勤可用。', 29.00, 79.00, 'GOOD', '图书馆一楼', 73, DATE_SUB(NOW(), INTERVAL 42 HOUR)
  UNION ALL SELECT 2, 5, '棒球帽', '帽型完整，几乎全新。', 24.00, 69.00, 'LIKE_NEW', '西门操场', 58, DATE_SUB(NOW(), INTERVAL 43 HOUR)
) AS seed
WHERE NOT EXISTS (
  SELECT 1 FROM product p WHERE p.title = seed.title
);

INSERT INTO product_image (product_id, image_url, sort_no)
SELECT p.id, seed.image_url, 1
FROM product p
JOIN (
  SELECT '数据库系统概论教材' AS title, 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=900&q=80' AS image_url
  UNION ALL SELECT '高等数学同济第七版', 'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '计算机网络教材', 'https://images.unsplash.com/photo-1495446815901-a7297e633e8d?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT 'Java程序设计实训书', 'https://images.unsplash.com/photo-1532012197267-da84d127e765?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '概率论与数理统计', 'https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '数据结构课程教材', 'https://images.unsplash.com/photo-1481627834876-b7833e8f5570?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '英语六级词汇书', 'https://images.unsplash.com/photo-1519682337058-a94d519337bc?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '操作系统考研讲义', 'https://images.unsplash.com/photo-1521587760476-6c12a4b040da?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '二手无线鼠标', 'https://images.unsplash.com/photo-1527814050087-3793815479db?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '降噪蓝牙耳机', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '机械键盘青轴', 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT 'iPad学习平板', 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '轻薄笔记本电脑', 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '24寸显示器', 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '蓝牙音箱', 'https://images.unsplash.com/photo-1545454675-3531b543be5d?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '移动充电宝', 'https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '微单相机', 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '千兆路由器', 'https://images.unsplash.com/photo-1606904825846-647eb07f5be2?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '游戏手柄', 'https://images.unsplash.com/photo-1606144042614-b2417e99c4e3?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '摄影补光灯', 'https://images.unsplash.com/photo-1494438639946-1ebd1d20bf85?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '宿舍护眼台灯', 'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '床上折叠书桌', 'https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '桌面收纳架', 'https://images.unsplash.com/photo-1595526114035-0d45ed16cfbf?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '宿舍小风扇', 'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '迷你电饭煲', 'https://images.unsplash.com/photo-1585515320310-259814833e62?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '宿舍小冰箱', 'https://images.unsplash.com/photo-1571175443880-49e1d25b2bc5?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '人体工学椅', 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '落地穿衣镜', 'https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '宿舍地毯', 'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '校园通勤自行车', 'https://images.unsplash.com/photo-1485965120184-e220f721d03e?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '羽毛球拍套装', 'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '瑜伽垫', 'https://images.unsplash.com/photo-1599447421416-3414500d18a5?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '可调节哑铃', 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '室外篮球', 'https://images.unsplash.com/photo-1546519638-68e109498ffc?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '网球拍', 'https://images.unsplash.com/photo-1622279457486-62dcc4a431d6?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '跳绳计数款', 'https://images.unsplash.com/photo-1599058917212-d750089bc07e?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '双肩电脑包', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '运动跑鞋', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '春秋夹克外套', 'https://images.unsplash.com/photo-1591047139829-d91aecb6caea?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '20寸登机箱', 'https://images.unsplash.com/photo-1565026057447-bc90a3dceb87?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '连帽卫衣', 'https://images.unsplash.com/photo-1523381210434-271e8be1f52b?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '帆布托特包', 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT '棒球帽', 'https://images.unsplash.com/photo-1521369909029-2afed882baee?auto=format&fit=crop&w=900&q=80'
) AS seed ON seed.title = p.title
WHERE NOT EXISTS (
  SELECT 1 FROM product_image pi WHERE pi.product_id = p.id AND pi.image_url = seed.image_url
);

SELECT COUNT(*) AS on_sale_product_count FROM product WHERE status = 'ON_SALE';
