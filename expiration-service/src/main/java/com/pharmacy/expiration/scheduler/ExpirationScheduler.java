package com.pharmacy.expiration.scheduler;

import com.pharmacy.expiration.service.ExpirationCheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExpirationScheduler {

    private static final Logger log = LoggerFactory.getLogger(ExpirationScheduler.class);

    private final ExpirationCheckService expirationCheckService;

    public ExpirationScheduler(ExpirationCheckService expirationCheckService) {
        this.expirationCheckService = expirationCheckService;
    }

    @Scheduled(fixedRate = 60000)
    public void runExpirationCheck() {
        int expiredCount = expirationCheckService.processExpiredProducts();
        if (expiredCount > 0) {
            log.info("Expiration check completed. {} product(s) marked as EXPIRED.", expiredCount);
        }
    }
}
