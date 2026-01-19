package com.miu.alumnimanagementportal.aspects;

import com.miu.alumnimanagementportal.services.ActivityLogService;
import com.miu.alumnimanagementportal.dtos.ActivityLogDto;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
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
    private final ActivityLogService activityLogService;

    @Before("execution(* com.miu.alumnimanagementportal.controllers.*.*(..))")
    public void logExecutionTime(JoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return;
        }
        String ipAddress = attrs.getRequest().getRemoteAddr();
        activityLogService.createActivityLog(ActivityLogDto.builder().accessTime(LocalDateTime.now())
                .operation(joinPoint.getSignature().getName()).ipAddress(ipAddress).build());
    }

}
