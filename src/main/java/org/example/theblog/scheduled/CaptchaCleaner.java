package org.example.theblog.scheduled;

import lombok.RequiredArgsConstructor;
import org.example.theblog.model.repository.CaptchaCodeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class CaptchaCleaner {

    private final CaptchaCodeRepository codeRepository;
    @Value("${blog.captchaCodeLifecycle}")
    private long time;

    @Scheduled(fixedDelayString = "${blog.intervalForClearingCaptchaCodesInMilliseconds}")
    public void captchaClean() {
        codeRepository.deleteAllByTimeIsLessThanEqual(LocalDateTime.now().minusMinutes(time));
    }
}
