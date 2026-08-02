package com.inventory.service;

import com.inventory.dto.request.LoginRequestDto;
import com.inventory.dto.request.RefreshTokenRequestDto;
import com.inventory.dto.request.RegisterRequestDto;
import com.inventory.dto.response.AuthResponseDto;
import com.inventory.dto.response.UserProfileDto;

public interface AuthService {
    AuthResponseDto register(RegisterRequestDto registerRequestDto);
    AuthResponseDto login(LoginRequestDto loginRequestDto);
    AuthResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequestDto);
    UserProfileDto getCurrentUserProfile(String username);
}
