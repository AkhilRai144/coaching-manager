package com.akhilesh.coachingManager.audit;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuditAspect {

    @Around("@annotation(com.akhilesh.coachingManager.audit.Audited)")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {

        String user = currentUser();
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            log.info(
                    "AUDIT user={} action={} result=SUCCESS ({} ms)",
                    user,
                    joinPoint.getSignature().toShortString(),
                    System.currentTimeMillis() - start
            );

            return result;

        } catch (Throwable ex) {

            log.warn(
                    "AUDIT user={} action={} result=FAILED reason={}: {}",
                    user,
                    joinPoint.getSignature().toShortString(),
                    ex.getClass().getSimpleName(),
                    ex.getMessage()
            );

            throw ex;
        }
    }

    private String currentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "anonymous";
        }

        return authentication.getName();
    }
}