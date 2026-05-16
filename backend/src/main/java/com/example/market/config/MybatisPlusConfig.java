package com.example.market.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@MapperScan("com.example.market.persistence.mapper")
public class MybatisPlusConfig {
}
