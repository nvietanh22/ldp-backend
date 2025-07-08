package vn.lottefinance.landingpage.config;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.salt.ZeroSaltGenerator;
import org.jasypt.util.text.BasicTextEncryptor;
import org.jasypt.util.text.TextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptConfig {
    @Value("${jasypt.encryptor.password}")
    private String encryptDecryptKey;

    @Bean
    public TextEncryptor createTextEncryptor() {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(encryptDecryptKey);

        return textEncryptor;
    }

    @Bean
    public StandardPBEStringEncryptor standardPBEStringEncryptor() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(encryptDecryptKey);
        encryptor.setAlgorithm("PBEWithMD5AndDES");
        encryptor.setSaltGenerator(new ZeroSaltGenerator());
        return encryptor;
    }
}
