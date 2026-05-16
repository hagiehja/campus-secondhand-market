-- 校园二手交易系统数据库脚本
-- 学号: 2415304249
-- 题目9: 校园二手交易系统设计与实现
-- 技术栈: Java + Spring Boot + MySQL 5.7 + Redis + Vue
-- 中间件部署: MySQL 运行在 192.168.24.129:3307，Redis 运行在 192.168.24.129:6379

DROP DATABASE IF EXISTS campus_secondhand_market;
CREATE DATABASE campus_secondhand_market
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE campus_secondhand_market;

CREATE USER IF NOT EXISTS 'campus_user'@'%' IDENTIFIED BY 'Campus@2415304249';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, TRIGGER, CREATE VIEW
ON campus_secondhand_market.* TO 'campus_user'@'%';
FLUSH PRIVILEGES;

CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(50) NOT NULL COMMENT '登录账号',
  password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
  real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
  student_no VARCHAR(30) NOT NULL COMMENT '学号',
  phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  role ENUM('ADMIN', 'SELLER', 'BUYER') NOT NULL DEFAULT 'BUYER' COMMENT '角色',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1正常 0禁用',
  avatar_url VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
  last_login_at DATETIME DEFAULT NULL COMMENT '最后登录时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_user_username (username),
  UNIQUE KEY uk_user_student_no (student_no),
  KEY idx_user_role_status (role, status)
) ENGINE=InnoDB COMMENT='用户表';

CREATE TABLE category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
  name VARCHAR(50) NOT NULL COMMENT '分类名称',
  parent_id BIGINT DEFAULT NULL COMMENT '父分类ID',
  sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1启用 0停用',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_category_name_parent (name, parent_id),
  KEY idx_category_parent (parent_id),
  CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES category(id)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='商品分类表';

CREATE TABLE product (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
  seller_id BIGINT NOT NULL COMMENT '卖家ID',
  category_id BIGINT NOT NULL COMMENT '分类ID',
  title VARCHAR(100) NOT NULL COMMENT '商品标题',
  description TEXT COMMENT '商品描述',
  price DECIMAL(10,2) NOT NULL COMMENT '售价',
  original_price DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
  condition_level ENUM('NEW', 'LIKE_NEW', 'GOOD', 'FAIR') NOT NULL DEFAULT 'GOOD' COMMENT '成色',
  trade_place VARCHAR(120) DEFAULT NULL COMMENT '交易地点',
  status ENUM('DRAFT', 'ON_SALE', 'LOCKED', 'SOLD', 'OFF_SHELF') NOT NULL DEFAULT 'ON_SALE' COMMENT '商品状态',
  view_count INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  CONSTRAINT fk_product_seller FOREIGN KEY (seller_id) REFERENCES sys_user(id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category(id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='商品表';

CREATE TABLE product_image (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '图片ID',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  image_url VARCHAR(255) NOT NULL COMMENT '图片地址',
  sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_product_image_product (product_id, sort_no),
  CONSTRAINT fk_product_image_product FOREIGN KEY (product_id) REFERENCES product(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='商品图片表';

CREATE TABLE trade_order (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
  order_no VARCHAR(40) NOT NULL COMMENT '订单编号',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  buyer_id BIGINT NOT NULL COMMENT '买家ID',
  seller_id BIGINT NOT NULL COMMENT '卖家ID',
  amount DECIMAL(10,2) NOT NULL COMMENT '订单金额',
  status ENUM('PENDING', 'PAID', 'FINISHED', 'CANCELLED') NOT NULL DEFAULT 'PENDING' COMMENT '订单状态',
  buyer_remark VARCHAR(255) DEFAULT NULL COMMENT '买家备注',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  paid_at DATETIME DEFAULT NULL COMMENT '支付时间',
  finished_at DATETIME DEFAULT NULL COMMENT '完成时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_trade_order_no (order_no),
  KEY idx_trade_order_buyer (buyer_id, created_at),
  KEY idx_trade_order_seller (seller_id, created_at),
  KEY idx_trade_order_product_status (product_id, status),
  CONSTRAINT fk_trade_order_product FOREIGN KEY (product_id) REFERENCES product(id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_trade_order_buyer FOREIGN KEY (buyer_id) REFERENCES sys_user(id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_trade_order_seller FOREIGN KEY (seller_id) REFERENCES sys_user(id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='交易订单表';

CREATE TABLE favorite (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  UNIQUE KEY uk_favorite_user_product (user_id, product_id),
  KEY idx_favorite_product (product_id),
  CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES sys_user(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_favorite_product FOREIGN KEY (product_id) REFERENCES product(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='商品收藏表';

CREATE TABLE message (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
  product_id BIGINT NOT NULL COMMENT '关联商品ID',
  sender_id BIGINT NOT NULL COMMENT '发送人ID',
  receiver_id BIGINT NOT NULL COMMENT '接收人ID',
  content VARCHAR(500) NOT NULL COMMENT '消息内容',
  is_read TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读: 1是 0否',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  KEY idx_message_receiver_read (receiver_id, is_read, created_at),
  KEY idx_message_product_created (product_id, created_at),
  CONSTRAINT fk_message_product FOREIGN KEY (product_id) REFERENCES product(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_message_sender FOREIGN KEY (sender_id) REFERENCES sys_user(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_message_receiver FOREIGN KEY (receiver_id) REFERENCES sys_user(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='商品咨询消息表';

CREATE TABLE review (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
  order_id BIGINT NOT NULL COMMENT '订单ID',
  reviewer_id BIGINT NOT NULL COMMENT '评价人ID',
  target_user_id BIGINT NOT NULL COMMENT '被评价人ID',
  score TINYINT NOT NULL COMMENT '评分1-5',
  content VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评价时间',
  UNIQUE KEY uk_review_order_reviewer (order_id, reviewer_id),
  KEY idx_review_target_user (target_user_id, created_at),
  CONSTRAINT fk_review_order FOREIGN KEY (order_id) REFERENCES trade_order(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_review_reviewer FOREIGN KEY (reviewer_id) REFERENCES sys_user(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_review_target_user FOREIGN KEY (target_user_id) REFERENCES sys_user(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='交易评价表';

CREATE TABLE operation_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
  biz_type VARCHAR(50) NOT NULL COMMENT '业务类型',
  biz_id BIGINT NOT NULL COMMENT '业务ID',
  action VARCHAR(50) NOT NULL COMMENT '操作动作',
  detail VARCHAR(500) DEFAULT NULL COMMENT '详情',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_operation_log_biz (biz_type, biz_id, created_at)
) ENGINE=InnoDB COMMENT='操作日志表';

-- 课程要求: 至少一个索引。这里单独创建商品查询组合索引，支持 Vue 首页搜索和分类筛选。
CREATE INDEX idx_product_search ON product (status, category_id, price, created_at);

-- 课程要求: 至少一个视图。前端商品列表可直接查询该视图。
CREATE OR REPLACE VIEW v_on_sale_product AS
SELECT
  p.id AS product_id,
  p.title,
  p.price,
  p.condition_level,
  p.trade_place,
  p.view_count,
  p.created_at,
  c.name AS category_name,
  u.id AS seller_id,
  u.username AS seller_username,
  u.real_name AS seller_real_name,
  (
    SELECT pi.image_url
    FROM product_image pi
    WHERE pi.product_id = p.id
    ORDER BY pi.sort_no ASC, pi.id ASC
    LIMIT 1
  ) AS cover_image
FROM product p
JOIN category c ON c.id = p.category_id
JOIN sys_user u ON u.id = p.seller_id
WHERE p.status = 'ON_SALE'
  AND c.status = 1
  AND u.status = 1;

DELIMITER //

-- 下单前校验商品状态，并以数据库中的商品价格和卖家为准。
CREATE TRIGGER trg_order_before_insert_validate_order
BEFORE INSERT ON trade_order
FOR EACH ROW
BEGIN
  DECLARE v_product_status VARCHAR(20);
  DECLARE v_seller_id BIGINT;
  DECLARE v_price DECIMAL(10,2);

  SELECT status, seller_id, price
  INTO v_product_status, v_seller_id, v_price
  FROM product
  WHERE id = NEW.product_id;

  IF v_product_status <> 'ON_SALE' THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '商品不是在售状态，不能下单';
  END IF;

  IF NEW.buyer_id = v_seller_id THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '买家不能购买自己发布的商品';
  END IF;

  SET NEW.seller_id = v_seller_id;
  SET NEW.amount = v_price;
END//

-- 课程要求: 至少一个触发器。订单创建后锁定商品，避免同一商品被多人同时下单。
CREATE TRIGGER trg_order_after_insert_lock_product
AFTER INSERT ON trade_order
FOR EACH ROW
BEGIN
  UPDATE product
  SET status = 'LOCKED'
  WHERE id = NEW.product_id
    AND status = 'ON_SALE';

  INSERT INTO operation_log (biz_type, biz_id, action, detail)
  VALUES (
    'ORDER',
    NEW.id,
    'CREATE',
    CONCAT('订单创建，订单号: ', NEW.order_no, '，商品ID: ', NEW.product_id)
  );
END//

-- 订单完成后自动把商品改为已售出。
CREATE TRIGGER trg_order_after_update_finish_product
AFTER UPDATE ON trade_order
FOR EACH ROW
BEGIN
  IF OLD.status <> NEW.status AND NEW.status = 'FINISHED' THEN
    UPDATE product
    SET status = 'SOLD'
    WHERE id = NEW.product_id;

    INSERT INTO operation_log (biz_type, biz_id, action, detail)
    VALUES ('ORDER', NEW.id, 'FINISH', CONCAT('订单完成，商品ID: ', NEW.product_id));
  END IF;

  IF OLD.status <> NEW.status AND NEW.status = 'CANCELLED' THEN
    UPDATE product
    SET status = 'ON_SALE'
    WHERE id = NEW.product_id
      AND status = 'LOCKED';

    INSERT INTO operation_log (biz_type, biz_id, action, detail)
    VALUES ('ORDER', NEW.id, 'CANCEL', CONCAT('订单取消，商品恢复上架，商品ID: ', NEW.product_id));
  END IF;
END//

DELIMITER ;

INSERT INTO sys_user (username, password_hash, real_name, student_no, phone, email, role)
VALUES
  ('admin', '$2a$10$exampleAdminPasswordHash', '系统管理员', '2415304249', '13800000000', 'admin@example.com', 'ADMIN'),
  ('seller01', '$2a$10$exampleSellerPasswordHash', '张三', '2415304001', '13800000001', 'seller01@example.com', 'SELLER'),
  ('buyer01', '$2a$10$exampleBuyerPasswordHash', '李四', '2415304002', '13800000002', 'buyer01@example.com', 'BUYER');

INSERT INTO category (name, parent_id, sort_no)
VALUES
  ('教材资料', NULL, 1),
  ('数码产品', NULL, 2),
  ('生活用品', NULL, 3),
  ('运动器材', NULL, 4),
  ('服饰鞋包', NULL, 5);

INSERT INTO product (seller_id, category_id, title, description, price, original_price, condition_level, trade_place, status)
VALUES
  (2, 1, '数据库系统概论教材', '课程设计可用教材，附少量笔记。', 25.00, 59.00, 'GOOD', '图书馆门口', 'ON_SALE'),
  (2, 2, '二手无线鼠标', '功能正常，适合宿舍和实验室使用。', 35.00, 89.00, 'LIKE_NEW', '教学楼A区', 'ON_SALE');

INSERT INTO product_image (product_id, image_url, sort_no)
VALUES
  (1, '/uploads/products/db-book.jpg', 1),
  (2, '/uploads/products/mouse.jpg', 1);

INSERT INTO favorite (user_id, product_id)
VALUES
  (3, 1);

INSERT INTO message (product_id, sender_id, receiver_id, content)
VALUES
  (1, 3, 2, '你好，这本教材还在吗？');
