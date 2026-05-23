package com.java.ploud.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class Encryptor {

    private final String SECRET;

    private final byte[] keyBytes;

    public Encryptor(@Value("${encrypt.secret}") String secret) {
        SECRET = secret;
        keyBytes = SECRET.getBytes(StandardCharsets.UTF_8);
    }

    public String encrypt(String plainText) throws Exception {

        //12byte인 이유는 없음 GCM 스펙상 96bit = 12바이트에서 가장 잘 돌아가기 때문
        byte[] iv = new byte[12];
        //iv에 랜덤 백터 생성
        new SecureRandom().nextBytes(iv);

        //자바 암호화 클래스, 대칭키알고리즘/암호 생성방식/데이터 길이 맞추기
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");


        SecretKey key = new SecretKeySpec(keyBytes, "AES");

        GCMParameterSpec spec = new GCMParameterSpec(128, iv);

        //미리 설정한 값으로 cipher 초기화
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] encrypted =
                cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));


        byte[] combined = new byte[iv.length + encrypted.length];

        //iv 벡터와 암호화된 데이터 적재
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        //문자열로 변경
        return Base64.getEncoder().encodeToString(combined);
    }

    public String decrypt(String cipherText) throws Exception {

        byte[] decoded = Base64.getDecoder().decode(cipherText);

        byte[] iv = new byte[12];
        byte[] encrypted = new byte[decoded.length - 12];

        System.arraycopy(decoded, 0, iv, 0, iv.length);
        System.arraycopy(decoded, iv.length, encrypted, 0, encrypted.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        SecretKey key = new SecretKeySpec(keyBytes, "AES");

        GCMParameterSpec spec = new GCMParameterSpec(128, iv);

        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] decrypted = cipher.doFinal(encrypted);

        return new String(decrypted, StandardCharsets.UTF_8);
    }

}
