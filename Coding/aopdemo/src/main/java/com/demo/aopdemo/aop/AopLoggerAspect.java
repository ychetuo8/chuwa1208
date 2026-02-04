package com.demo.aopdemo.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Optional;

@Aspect
@Component
public class AopLoggerAspect {

    private static final Logger log = LoggerFactory.getLogger(AopLoggerAspect.class);

    private Optional<HttpServletRequest> currentRequest() {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra) {
            return Optional.ofNullable(sra.getRequest());
        }
        return Optional.empty();
    }

    // ---- REST API: request + response + time (bind pointcut directly, no empty method) ----
    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logApi(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.nanoTime();
        String sig = pjp.getSignature().toShortString();

        currentRequest().ifPresent(req ->
                log.info("[API][REQ] {} {} | handler={} | query={}",
                        req.getMethod(), req.getRequestURI(), sig, req.getQueryString())
        );
        log.info("[API][ARGS] {} | args={}", sig, Arrays.toString(pjp.getArgs()));

        try {
            Object res = pjp.proceed();
            long costMs = (System.nanoTime() - start) / 1_000_000;

            if (res instanceof ResponseEntity<?> re) {
                log.info("[API][RES] {} | status={} | costMs={}", sig, re.getStatusCode(), costMs);
            } else {
                log.info("[API][RES] {} | costMs={}", sig, costMs);
            }
            return res;
        } catch (Throwable ex) {
            long costMs = (System.nanoTime() - start) / 1_000_000;
            log.error("[API][EX] {} | costMs={} | exType={} | msg={}",
                    sig, costMs, ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        }
    }

    // ---- Service/Repo join points: before/after/return/throw + time ----
    @Before("within(@org.springframework.stereotype.Service *) || within(@org.springframework.stereotype.Repository *)")
    public void beforeSrv(JoinPoint jp) {
        log.info("[SRV][BEFORE] {} | args={}", jp.getSignature().toShortString(), Arrays.toString(jp.getArgs()));
    }

    @After("within(@org.springframework.stereotype.Service *) || within(@org.springframework.stereotype.Repository *)")
    public void afterSrv(JoinPoint jp) {
        log.info("[SRV][AFTER] {}", jp.getSignature().toShortString());
    }

    @AfterReturning(
            pointcut = "within(@org.springframework.stereotype.Service *) || within(@org.springframework.stereotype.Repository *)",
            returning = "ret"
    )
    public void afterReturningSrv(JoinPoint jp, Object ret) {
        log.info("[SRV][RETURN] {}", jp.getSignature().toShortString());
    }

    @AfterThrowing(
            pointcut = "within(@org.springframework.stereotype.Service *) || within(@org.springframework.stereotype.Repository *)",
            throwing = "ex"
    )
    public void afterThrowingSrv(JoinPoint jp, Throwable ex) {
        log.error("[SRV][THROW] {} | exType={} | msg={}",
                jp.getSignature().toShortString(), ex.getClass().getSimpleName(), ex.getMessage());
    }

    @Around("within(@org.springframework.stereotype.Service *) || within(@org.springframework.stereotype.Repository *)")
    public Object timeSrv(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.nanoTime();
        try {
            Object res = pjp.proceed();
            long costMs = (System.nanoTime() - start) / 1_000_000;
            log.info("[SRV][TIME] {} | costMs={}", pjp.getSignature().toShortString(), costMs);
            return res;
        } catch (Throwable ex) {
            long costMs = (System.nanoTime() - start) / 1_000_000;
            log.error("[SRV][TIME-EX] {} | costMs={}", pjp.getSignature().toShortString(), costMs);
            throw ex;
        }
    }

    // ---- External code: RestTemplate calls (Spring-managed 3rd-party bean) ----
    @Around("bean(restTemplate) && execution(* org.springframework.web.client.RestTemplate.*(..))")
    public Object logExternal(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.nanoTime();
        String sig = pjp.getSignature().toShortString();
        log.info("[EXT][HTTP][REQ] {} | args={}", sig, Arrays.toString(pjp.getArgs()));

        try {
            Object res = pjp.proceed();
            long costMs = (System.nanoTime() - start) / 1_000_000;
            log.info("[EXT][HTTP][RES] {} | costMs={}", sig, costMs);
            return res;
        } catch (Throwable ex) {
            long costMs = (System.nanoTime() - start) / 1_000_000;
            log.error("[EXT][HTTP][EX] {} | costMs={} | msg={}", sig, costMs, ex.getMessage());
            throw ex;
        }
    }
}
