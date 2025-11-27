package com.example.captcha.service;

import lombok.RequiredArgsConstructor;
import org.infinispan.Cache;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CaptchaReplayProtectionService {

    private final Cache<String, Boolean> usedTokenCache;

    public boolean isUsed(String tokenHash) {
        return usedTokenCache.containsKey(tokenHash);
    }

    public void markUsed(String tokenHash) {
        usedTokenCache.put(tokenHash, true);
    }
}
