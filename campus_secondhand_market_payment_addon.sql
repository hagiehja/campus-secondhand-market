USE campus_secondhand_market;
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS payment_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '支付记录ID',
  order_id BIGINT NOT NULL COMMENT '订单ID',
  payment_no VARCHAR(40) NOT NULL COMMENT '支付流水号',
  channel ENUM('ALIPAY', 'MOCK') NOT NULL COMMENT '支付渠道',
  amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
  status ENUM('INIT', 'SUCCESS', 'FAILED') NOT NULL DEFAULT 'INIT' COMMENT '支付状态',
  gateway_trade_no VARCHAR(80) DEFAULT NULL COMMENT '第三方交易号',
  paid_at DATETIME DEFAULT NULL COMMENT '支付完成时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_payment_no (payment_no),
  KEY idx_payment_order (order_id, status),
  CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES trade_order(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';
