package com.miu.alumnimanagementportal.controllers;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.SearchDto;
import com.miu.alumnimanagementportal.dtos.UserActivationDto;
import com.miu.alumnimanagementportal.dtos.UserDto;
import com.miu.alumnimanagementportal.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final Converter converter;

    @GetMapping("/metrics")
    public ResponseEntity<?> getMetrics() {
        try {
            // This is a simple implementation - you might want to create a proper metrics service
            long totalUsers = userService.findAll().size();
            // For now, return dummy data for other metrics
            Map<String, Object> metrics = Map.of(
                "totalUsers", totalUsers,
                "newUsers", 5, // This would need proper implementation
                "activeUsers", 25, // This would need proper implementation
                "contentCounts", "10/5/3" // Events/Jobs/News count
            );
            return converter.buildResponseEntity(Map.of("success", true, "data", metrics), HttpStatus.OK);
        } catch (Exception e) {
            return converter.buildResponseEntity(Map.of("success", false, "message", "Error loading metrics"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@RequestParam(required = false) Integer year,
                                     @RequestParam(required = false) String department,
                                     @RequestParam(required = false) String company) {
        try {
            if (year != null || department != null || company != null) {
                // Create search DTO for filtering
                SearchDto searchDto = new SearchDto();
                if (year != null) searchDto.setGraduationYear(String.valueOf(year));
                searchDto.setCourse(department);
                searchDto.setIndustry(company);

                return converter.buildResponseEntity(Map.of("success", true, "data", userService.searchBy(searchDto)), HttpStatus.OK);
            } else {
                return converter.buildResponseEntity(Map.of("success", true, "data", userService.findAll()), HttpStatus.OK);
            }
        } catch (Exception e) {
            return converter.buildResponseEntity(Map.of("success", false, "message", "Error loading users"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/users/{id}/approve")
    public ResponseEntity<?> approveUser(@PathVariable Long id) {
        try {
            userService.update(new UserActivationDto(id, true), id);
            return converter.buildResponseEntity(Map.of("success", true, "message", "User approved successfully"), HttpStatus.OK);
        } catch (Exception e) {
            return converter.buildResponseEntity(Map.of("success", false, "message", "Error approving user"), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        try {
            userService.update(new UserActivationDto(id, false), id);
            return converter.buildResponseEntity(Map.of("success", true, "message", "User deactivated successfully"), HttpStatus.OK);
        } catch (Exception e) {
            return converter.buildResponseEntity(Map.of("success", false, "message", "Error deactivating user"), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/users/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Long id) {
        try {
            String newPassword = userService.adminResetPassword(id);
            return converter.buildResponseEntity(Map.of("success", true, "message", "Password reset successfully", "newPassword", newPassword), HttpStatus.OK);
        } catch (Exception e) {
            return converter.buildResponseEntity(Map.of("success", false, "message", "Error resetting password"), HttpStatus.BAD_REQUEST);
        }
    }
}
