USE campus_secondhand_market;
SET NAMES utf8mb4;

INSERT INTO product (seller_id, category_id, title, description, price, original_price, condition_level, trade_place, status)
SELECT 2, 2, '降噪蓝牙耳机', '降噪功能正常，适合自习室使用。', 118.00, 299.00, 'GOOD', '宿舍区3栋', 'ON_SALE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM product WHERE title = '降噪蓝牙耳机');
SET @p_headset = (SELECT id FROM product WHERE title = '降噪蓝牙耳机' ORDER BY id LIMIT 1);
INSERT INTO product_image (product_id, image_url, sort_no)
SELECT @p_headset, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=80', 1
FROM DUAL
WHERE @p_headset IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM product_image WHERE product_id = @p_headset);

INSERT INTO product (seller_id, category_id, title, description, price, original_price, condition_level, trade_place, status)
SELECT 2, 4, '校园通勤自行车', '车况稳定，适合校内短途通勤。', 260.00, 699.00, 'FAIR', '北门车棚', 'ON_SALE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM product WHERE title = '校园通勤自行车');
SET @p_bike = (SELECT id FROM product WHERE title = '校园通勤自行车' ORDER BY id LIMIT 1);
INSERT INTO product_image (product_id, image_url, sort_no)
SELECT @p_bike, 'https://images.unsplash.com/photo-1485965120184-e220f721d03e?auto=format&fit=crop&w=900&q=80', 1
FROM DUAL
WHERE @p_bike IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM product_image WHERE product_id = @p_bike);

INSERT INTO product (seller_id, category_id, title, description, price, original_price, condition_level, trade_place, status)
SELECT 2, 3, '宿舍护眼台灯', '亮度可调，宿舍学习够用。', 48.00, 129.00, 'LIKE_NEW', '二食堂门口', 'ON_SALE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM product WHERE title = '宿舍护眼台灯');
SET @p_lamp = (SELECT id FROM product WHERE title = '宿舍护眼台灯' ORDER BY id LIMIT 1);
INSERT INTO product_image (product_id, image_url, sort_no)
SELECT @p_lamp, 'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=900&q=80', 1
FROM DUAL
WHERE @p_lamp IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM product_image WHERE product_id = @p_lamp);

INSERT INTO product (seller_id, category_id, title, description, price, original_price, condition_level, trade_place, status)
SELECT 2, 2, '机械键盘青轴', '机械键盘青轴，按键正常。', 139.00, 329.00, 'GOOD', '实验楼大厅', 'ON_SALE'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM product WHERE title = '机械键盘青轴');
SET @p_keyboard = (SELECT id FROM product WHERE title = '机械键盘青轴' ORDER BY id LIMIT 1);
INSERT INTO product_image (product_id, image_url, sort_no)
SELECT @p_keyboard, 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=900&q=80', 1
FROM DUAL
WHERE @p_keyboard IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM product_image WHERE product_id = @p_keyboard);
