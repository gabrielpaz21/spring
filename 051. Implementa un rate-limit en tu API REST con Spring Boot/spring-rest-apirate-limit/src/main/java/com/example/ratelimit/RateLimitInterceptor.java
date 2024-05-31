package com.example.ratelimit;

import com.example.service.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final SubscriptionService service;
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public RateLimitInterceptor(SubscriptionService service) {
        this.service = service;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {

        String username = Optional.ofNullable(
                        SecurityContextHolder.getContext().getAuthentication()
                ).map(Authentication::getPrincipal)
                .map(String.class::cast)
                .orElseThrow();

        Bucket bucket = cache.computeIfAbsent(username, this::createBucket);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-Api-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            return true;
        } else {
            long remaining = TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill());
            response.addHeader("X-Api-Rate-Limit-Remaining-Wait-Seconds", String.valueOf(remaining));
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().append("Api rate limit consumed. You have to wait for refill.");
            return false;
        }
    }

    private Bucket createBucket(String username) {
        var subscription = service.findByUserName(username);
        Long capacity = subscription.getCapacity();
        Long minutes = subscription.getRefillMinutes();
        return Bucket.builder()
                        .addLimit(limit -> limit.capacity(capacity)
                        .refillIntervally(capacity, Duration.ofMinutes(minutes)))
                        .build();
    }
}
