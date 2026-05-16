USE campus_secondhand_market;

SET @idx_exists := (
  SELECT COUNT(1)
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'sys_user'
    AND index_name = 'uk_user_phone'
);

SET @sql := IF(
  @idx_exists = 0,
  'ALTER TABLE sys_user ADD UNIQUE KEY uk_user_phone (phone)',
  'SELECT ''uk_user_phone already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
