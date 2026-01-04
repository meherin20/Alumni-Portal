package com.miu.alumnimanagementportal.aspects;

import com.miu.alumnimanagementportal.services.ActivityLogService;
import com.miu.alumnimanagementportal.dtos.ActivityLogDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Aspect
@Component
public class LoggerAspect {
    private final HttpServletRequest request;
    private final ActivityLogService activityLogService;

    @Before("execution(* com.miu.alumnimanagementportal.controllers.*.*(..))")
    public void logExecutionTime(JoinPoint joinPoint) throws Throwable {
        String ipAddress = request.getRemoteAddr();
        activityLogService.createActivityLog(ActivityLogDto.builder().accessTime(LocalDateTime.now())
                .operation(joinPoint.getSignature().getName()).ipAddress(ipAddress).build());
    }

}
