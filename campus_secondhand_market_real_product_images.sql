-- 将演示商品图片替换为按商品名称匹配的真实封面/实物图。
-- MySQL 5.7 可执行；导入主数据后再执行本文件。

DROP TEMPORARY TABLE IF EXISTS tmp_real_product_images;
CREATE TEMPORARY TABLE tmp_real_product_images (
  title VARCHAR(100) PRIMARY KEY,
  image_url VARCHAR(500) NOT NULL
) ENGINE=MEMORY DEFAULT CHARSET=utf8mb4;

INSERT INTO tmp_real_product_images (title, image_url) VALUES
('数据库系统概论教材', 'https://covers.openlibrary.org/b/isbn/9787040406641-L.jpg'),
('高等数学同济第七版', 'https://covers.openlibrary.org/b/isbn/9787040396638-L.jpg'),
('计算机网络教材', 'https://covers.openlibrary.org/b/isbn/9787121411748-L.jpg'),
('Java程序设计实训书', 'https://covers.openlibrary.org/b/isbn/9787302423287-L.jpg'),
('概率论与数理统计', 'https://covers.openlibrary.org/b/isbn/9787040516609-L.jpg'),
('数据结构课程教材', 'https://covers.openlibrary.org/b/isbn/9787302023685-L.jpg'),
('英语六级词汇书', 'https://covers.openlibrary.org/b/isbn/9787513559034-L.jpg'),
('操作系统考研讲义', 'https://covers.openlibrary.org/b/isbn/9787111604365-L.jpg'),
('二手无线鼠标', 'https://resource.logitech.com/w_900,c_limit,q_auto,f_auto,dpr_1.0/d_transparent.gif/content/dam/logitech/en/products/mice/mx-master-3s/gallery/mx-master-3s-mouse-top-view-graphite.png'),
('降噪蓝牙耳机', 'https://resource.bose.com/content/dam/Bose_DAM/Web/consumer_electronics/global/products/headphones/QCUH-HEADPHONEARN/product_silo_images/AEM_PDP_GALLERY_BLACK-1.png'),
('机械键盘青轴', 'https://resource.logitech.com/w_900,c_limit,q_auto,f_auto,dpr_1.0/d_transparent.gif/content/dam/logitech/en/products/keyboards/mx-mechanical/gallery/mx-mechanical-keyboard-top-view-graphite.png'),
('iPad学习平板', 'https://store.storeimages.cdn-apple.com/1/as-images.apple.com/is/ipad-air-finish-select-gallery-202405-11inch-blue-wifi'),
('轻薄笔记本电脑', 'https://p3-ofp.static.pub/fes/cms/2023/02/10/9tm95wd61rzbotpkjcho8qbm3jntbl026956.png'),
('24寸显示器', 'https://images.samsung.com/is/image/samsung/p6pim/hk_en/ls24c310eacxxk/gallery/hk-en-essential-s3-s31c-ls24c310eacxxk-536726306?$684_547_PNG$'),
('蓝牙音箱', 'https://resource.jbl.com/is/image/JBL/JBL_FLIP_6_HERO_BLACK_29391_x1?wid=900&hei=900&fmt=png-alpha'),
('移动充电宝', 'https://cdn.shopify.com/s/files/1/0493/9834/9974/files/A1289H11_TD01_V1.png?v=1705482546'),
('微单相机', 'https://www.sony.com/image/5d02da5df552836db894cead8a68f5f9?fmt=png-alpha&wid=900'),
('千兆路由器', 'https://static.tp-link.com/upload/product-overview/2023/202309/20230915/Archer_AX55_1.0_large_20230915082113p.png'),
('游戏手柄', 'https://compass-ssl.xbox.com/assets/91/dc/91dca8d7-bd84-4d70-a060-a5a04f2684ad.png?n=10202020-poster-large.jpg'),
('摄影补光灯', 'https://www.elgato.com/sites/default/files/2022-03/key-light-air-gallery-1.png'),
('宿舍护眼台灯', 'https://resource.se.com/is/image/schneider-electric/PH1209307?$pnglarge$'),
('床上折叠书桌', 'https://www.ikea.com/us/en/images/products/klipsk-bed-tray-white__0711267_pe728202_s5.jpg'),
('桌面收纳架', 'https://www.ikea.com/us/en/images/products/dragan-3-piece-bathroom-set-bamboo__0711808_pe728607_s5.jpg'),
('宿舍小风扇', 'https://www.mi.com/global/product/xiaomi-smart-standing-fan-2-pro/img/overview01.png'),
('迷你电饭煲', 'https://www.mi.com/global/product/xiaomi-smart-multifunctional-rice-cooker/img/overview01.png'),
('宿舍小冰箱', 'https://images.samsung.com/is/image/samsung/p6pim/us/rf29bb8600qlaa/gallery/us-bespoke-4-door-flex-rf29bb8600qlaa-533070959?$684_547_PNG$'),
('人体工学椅', 'https://www.ikea.com/us/en/images/products/matchspel-gaming-chair-bomstad-black__0985645_pe816670_s5.jpg'),
('落地穿衣镜', 'https://www.ikea.com/us/en/images/products/knapper-standing-mirror-white__0710721_pe727682_s5.jpg'),
('宿舍地毯', 'https://www.ikea.com/us/en/images/products/vindum-rug-high-pile-white__0605421_pe681735_s5.jpg'),
('校园通勤自行车', 'https://upload.wikimedia.org/wikipedia/commons/thumb/4/4e/Trek_Y-Foil_77.jpg/900px-Trek_Y-Foil_77.jpg'),
('羽毛球拍套装', 'https://www.yonex.com/media/catalog/product/cache/4f4aa1e73e2e5249cf3884a2bcd7a57a/a/s/astrox_77_pro_high_orange.png'),
('瑜伽垫', 'https://contents.mediadecathlon.com/p2400776/k$dfeb4d8b8e6a6af49e9e5bb04840fdd1/yoga-mat-5-mm.jpg'),
('可调节哑铃', 'https://contents.mediadecathlon.com/p2397549/k$e3e6297ed3cb7f7594515ec12fb1fc47/hex-dumbbell-10-kg.jpg'),
('室外篮球', 'https://www.spalding.com/dw/image/v2/BJZV_PRD/on/demandware.static/-/Sites-spalding-master/default/dw56a98dd4/images/hi-res/76874E_FRONT.jpg'),
('网球拍', 'https://www.wilson.com/en-us/media/catalog/product/w/r/wr149811u__blade_98_16x19_v9_grip3_1.png'),
('跳绳计数款', 'https://contents.mediadecathlon.com/p1728774/k$9e19f1e59605f500e5bd47bb79cbbd24/adult-skipping-rope.jpg'),
('双肩电脑包', 'https://www.thenorthface.com/content/publish/caas/v1/media/306242/data/fe2b073759f1b6478c73e5ad54096315/306242_A3J4.png'),
('运动跑鞋', 'https://static.nike.com/a/images/t_PDP_1728_v1/f_auto,q_auto:eco/307533b1-c8d6-4464-a9bb-f84d89f06971/NIKE+PEGASUS+41.png'),
('春秋夹克外套', 'https://image.uniqlo.com/UQ/ST3/WesternCommon/imagesgoods/459591/item/goods_09_459591.jpg'),
('20寸登机箱', 'https://cdn.shopify.com/s/files/1/0041/3270/5091/files/carry-on-flex-coast-blue-front.png?v=1706745324'),
('连帽卫衣', 'https://image.uniqlo.com/UQ/ST3/WesternCommon/imagesgoods/444966/item/goods_69_444966.jpg'),
('帆布托特包', 'https://image.uniqlo.com/UQ/ST3/WesternCommon/imagesgoods/461053/item/goods_30_461053.jpg'),
('棒球帽', 'https://static.nike.com/a/images/t_PDP_1728_v1/f_auto,q_auto:eco/b7f26f2a-033f-4727-b25e-d3c31ff2a73c/U+NK+DF+CLUB+CAP+S+CB+P.png');

DELETE pi
FROM product_image pi
JOIN product p ON p.id = pi.product_id
JOIN tmp_real_product_images t ON t.title = p.title;

INSERT INTO product_image (product_id, image_url, sort_no)
SELECT p.id, t.image_url, 1
FROM product p
JOIN tmp_real_product_images t ON t.title = p.title;
