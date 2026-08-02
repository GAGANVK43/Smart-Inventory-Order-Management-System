package com.inventory.controller;

import com.inventory.dto.request.LoginRequestDto;
import com.inventory.dto.request.RefreshTokenRequestDto;
import com.inventory.dto.request.RegisterRequestDto;
import com.inventory.dto.response.ApiResponse;
import com.inventory.dto.response.AuthResponseDto;
import com.inventory.dto.response.UserProfileDto;
import com.inventory.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin
@Tag(name = "Authentication & User Management", description = "Endpoints for user registration, login, JWT token refresh, and user profiles")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register User Account", description = "Create a new user account with assigned roles (ROLE_ADMIN, ROLE_MANAGER, ROLE_STAFF)")
    public ResponseEntity<ApiResponse<AuthResponseDto>> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        AuthResponseDto authResponse = authService.register(registerRequestDto);
        return new ResponseEntity<>(ApiResponse.success(authResponse, "User registered successfully"), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate User", description = "Login with username/email and password to obtain JWT access token and refresh token")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        AuthResponseDto authResponse = authService.login(loginRequestDto);
        return ResponseEntity.ok(ApiResponse.success(authResponse, "Login successful"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT Access Token", description = "Obtain a new JWT access token using a valid refresh token")
    public ResponseEntity<ApiResponse<AuthResponseDto>> refreshToken(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        AuthResponseDto authResponse = authService.refreshToken(refreshTokenRequestDto);
        return ResponseEntity.ok(ApiResponse.success(authResponse, "Access token refreshed successfully"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get Current User Profile", description = "Retrieve profile details and permissions for the currently authenticated user")
    public ResponseEntity<ApiResponse<UserProfileDto>> getCurrentUser(Authentication authentication) {
        UserProfileDto userProfile = authService.getCurrentUserProfile(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(userProfile));
    }
}
