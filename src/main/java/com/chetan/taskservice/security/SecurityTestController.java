package com.chetan.taskservice.security;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityTestController {

    @GetMapping("/api/security-test")
    public String test(Authentication authentication) {

        return "Authenticated userId = "
                + authentication.getPrincipal()
                + ", role = "
                + authentication.getAuthorities();
    }
}