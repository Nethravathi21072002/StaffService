package com.flmhospitals.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.flmhospitals.dto.EmailRequestDto;

@FeignClient(name = "notification-service")
public interface NotificationFeignClient {
    @PostMapping("/api/notifications/email")
    String sendEmail(@RequestBody EmailRequestDto request);
}
