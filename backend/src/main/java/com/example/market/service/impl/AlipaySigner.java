package com.example.market.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

final class AlipaySigner {

    private AlipaySigner() {
    }

    static String sign(Map<String, String> params, String privateKey) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(loadPrivateKey(privateKey));
            signature.update(contentToSign(params).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception ex) {
            throw new IllegalStateException("支付宝签名失败，请检查应用私钥", ex);
        }
    }

    static boolean verify(Map<String, String> params, String alipayPublicKey) {
        try {
            String sign = params.get("sign");
            if (sign == null || sign.isBlank()) {
                return false;
            }
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(loadPublicKey(alipayPublicKey));
            signature.update(contentToSign(params).getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.getDecoder().decode(sign));
        } catch (Exception ex) {
            return false;
        }
    }

    private static String contentToSign(Map<String, String> params) {
        return params.entrySet().stream()
            .filter(entry -> entry.getValue() != null && !entry.getValue().isBlank())
            .filter(entry -> !"sign".equals(entry.getKey()) && !"sign_type".equals(entry.getKey()))
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> entry.getKey() + "=" + entry.getValue())
            .collect(Collectors.joining("&"));
    }

    private static PrivateKey loadPrivateKey(String privateKey) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(cleanKey(privateKey));
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(bytes));
    }

    private static PublicKey loadPublicKey(String publicKey) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(cleanKey(publicKey));
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
    }

    private static String cleanKey(String key) {
        return key
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replaceAll("\\s", "");
    }
}
