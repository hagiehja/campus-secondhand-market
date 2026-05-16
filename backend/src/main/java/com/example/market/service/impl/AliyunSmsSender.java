package com.example.market.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.example.market.config.SmsProperties;
import com.example.market.service.SmsSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AliyunSmsSender implements SmsSender {

    private final SmsProperties smsProperties;

    public AliyunSmsSender(SmsProperties smsProperties) {
        this.smsProperties = smsProperties;
    }

    @Override
    public void sendCode(String phone, String code) {
        if (!smsProperties.isEnabled()) {
            throw new IllegalArgumentException("短信服务未启用，请先配置阿里云短信参数");
        }
        String accessKeyId = firstText(smsProperties.getAccessKeyId(), System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID"));
        String accessKeySecret = firstText(smsProperties.getAccessKeySecret(), System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET"));
        if (!StringUtils.hasText(accessKeyId)
            || !StringUtils.hasText(accessKeySecret)
            || !StringUtils.hasText(smsProperties.getSignName())
            || !StringUtils.hasText(smsProperties.getTemplateCode())) {
            throw new IllegalArgumentException("短信服务未配置完整：需要 AccessKey、短信签名和模板 Code");
        }

        try {
            Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
            config.endpoint = smsProperties.getEndpoint();

            Client client = new Client(config);
            SendSmsRequest request = new SendSmsRequest()
                .setPhoneNumbers(phone)
                .setSignName(smsProperties.getSignName())
                .setTemplateCode(smsProperties.getTemplateCode())
                .setTemplateParam("{\"code\":\"" + code + "\"}");

            SendSmsResponse response = client.sendSms(request);
            if (response == null || response.body == null || !"OK".equalsIgnoreCase(response.body.code)) {
                String message = response == null || response.body == null ? "无返回" : response.body.message;
                throw new IllegalArgumentException("短信发送失败：" + message);
            }
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalArgumentException("短信发送失败：" + ex.getMessage(), ex);
        }
    }

    private String firstText(String first, String second) {
        return StringUtils.hasText(first) ? first : second;
    }
}
