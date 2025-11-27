package com.example.captcha.config;

import com.example.captcha.service.DefaultImageGenerator;
import com.example.captcha.service.ImageGenerator;
import org.infinispan.Cache;
import org.infinispan.commons.api.CacheContainerAdmin;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.EmbeddedCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class CaptchaConfig {

    @Bean
    public ImageGenerator simpleImageGenerator() {
        return new DefaultImageGenerator();
    }

    @Bean
    public Cache<String, Boolean> captchaReplayCache(EmbeddedCacheManager cacheManager,
                                                    CaptchaProperties properties) {
        var cacheConfig = new ConfigurationBuilder()
                .expiration()
                .lifespan(properties.getTTL().getSeconds(), TimeUnit.SECONDS)
                .memory().maxCount(1000)
                .build();
        return cacheManager.administration()
                .withFlags(CacheContainerAdmin.AdminFlag.VOLATILE)
                .getOrCreateCache("captcha-replay-cache", cacheConfig);
    }
}
