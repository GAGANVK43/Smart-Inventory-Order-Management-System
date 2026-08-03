package com.inventory.service;

import com.inventory.dto.request.LoginRequestDto;
import com.inventory.dto.request.RegisterRequestDto;
import com.inventory.dto.response.AuthResponseDto;
import com.inventory.entity.RefreshToken;
import com.inventory.entity.Role;
import com.inventory.entity.User;
import com.inventory.enums.RoleName;
import com.inventory.exception.ResourceAlreadyExistsException;
import com.inventory.repository.RefreshTokenRepository;
import com.inventory.repository.RoleRepository;
import com.inventory.repository.UserRepository;
import com.inventory.security.JwtTokenProvider;
import com.inventory.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private Role staffRole;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        staffRole = new Role(RoleName.ROLE_STAFF);
        user = new User("johndoe", "john@example.com", "encoded_password");
        user.getRoles().add(staffRole);

        refreshToken = new RefreshToken();
        refreshToken.setToken("sample-uuid-refresh-token");
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(3600));
    }

    @Test
    @DisplayName("User Registration - Success")
    void testRegister_Success() {
        RegisterRequestDto dto = new RegisterRequestDto("johndoe", "john@example.com", "secret123", Collections.singleton("ROLE_STAFF"));

        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded_password");
        when(roleRepository.findByName(RoleName.ROLE_STAFF)).thenReturn(Optional.of(staffRole));
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenProvider.generateToken(any())).thenReturn("sample-jwt-access-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        AuthResponseDto response = authService.register(dto);

        assertNotNull(response);
        assertEquals("sample-jwt-access-token", response.getAccessToken());
        assertEquals("johndoe", response.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("User Registration - Existing Username Throws Exception")
    void testRegister_ExistingUsername_ThrowsException() {
        RegisterRequestDto dto = new RegisterRequestDto("johndoe", "john@example.com", "secret123", null);

        when(userRepository.existsByUsername("johndoe")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class, () -> authService.register(dto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("User Login - Success")
    void testLogin_Success() {
        LoginRequestDto dto = new LoginRequestDto("johndoe", "secret123");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userRepository.findByUsernameOrEmail("johndoe", "johndoe")).thenReturn(Optional.of(user));
        when(tokenProvider.generateToken(authentication)).thenReturn("sample-jwt-access-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        AuthResponseDto response = authService.login(dto);

        assertNotNull(response);
        assertEquals("sample-jwt-access-token", response.getAccessToken());
        assertEquals("johndoe", response.getUsername());
    }
}
