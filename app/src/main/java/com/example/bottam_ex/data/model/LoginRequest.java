// LoginRequest.java
package com.example.bottam_ex.data.model;

public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getter & Setter가 필요할 수도 있음
}