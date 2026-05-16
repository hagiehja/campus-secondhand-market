package com.example.market.service.impl;

import com.example.market.config.SmsProperties;
import com.example.market.service.SmsCodeService;
import com.example.market.service.SmsSender;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisSmsCodeService implements SmsCodeService {

    private static final String CODE_PREFIX = "sms:login:";
    private final StringRedisTemplate redisTemplate;
    private final SmsSender smsSender;
    private final SmsProperties smsProperties;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, LocalCode> localCodes = new ConcurrentHashMap<>();

    public RedisSmsCodeService(StringRedisTemplate redisTemplate, SmsSender smsSender, SmsProperties smsProperties) {
        this.redisTemplate = redisTemplate;
        this.smsSender = smsSender;
        this.smsProperties = smsProperties;
    }

    @Override
    public void sendLoginCode(String phone) {
        String code = String.format("%06d", secureRandom.nextInt(1_000_000));
        smsSender.sendCode(phone, code);
        Duration ttl = Duration.ofMinutes(Math.max(1, smsProperties.getExpireMinutes()));
        try {
            redisTemplate.opsForValue().set(CODE_PREFIX + phone, code, ttl);
        } catch (RuntimeException ignored) {
            localCodes.put(phone, new LocalCode(code, Instant.now().plus(ttl)));
        }
    }

    @Override
    public void verifyLoginCode(String phone, String code) {
        String storedCode = null;
        try {
            storedCode = redisTemplate.opsForValue().get(CODE_PREFIX + phone);
        } catch (RuntimeException ignored) {
            // Redis 未启动时走本地兜底，方便课程演示。
        }

        if (storedCode == null) {
            LocalCode localCode = localCodes.get(phone);
            if (localCode != null && localCode.expiresAt().isAfter(Instant.now())) {
                storedCode = localCode.code();
            }
        }

        if (!code.equals(storedCode)) {
            throw new IllegalArgumentException("验证码错误或已过期");
        }
        try {
            redisTemplate.delete(CODE_PREFIX + phone);
        } catch (RuntimeException ignored) {
            localCodes.remove(phone);
        }
        localCodes.remove(phone);
    }

    private record LocalCode(String code, Instant expiresAt) {
    }
}
