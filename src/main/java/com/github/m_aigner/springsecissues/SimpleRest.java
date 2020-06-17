package com.github.m_aigner.springsecissues;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleRest {
    @PostMapping("/loginsuccessful")
    public String loginSuccess() {
        return "Login successful!";
    }
}
