package com.inventory.service.impl;

import com.inventory.dto.request.LoginRequestDto;
import com.inventory.dto.request.RefreshTokenRequestDto;
import com.inventory.dto.request.RegisterRequestDto;
import com.inventory.dto.response.AuthResponseDto;
import com.inventory.dto.response.UserProfileDto;
import com.inventory.entity.RefreshToken;
import com.inventory.entity.Role;
import com.inventory.entity.User;
import com.inventory.enums.RoleName;
import com.inventory.exception.ResourceAlreadyExistsException;
import com.inventory.repository.RefreshTokenRepository;
import com.inventory.repository.RoleRepository;
import com.inventory.repository.UserRepository;
import com.inventory.security.JwtTokenProvider;
import com.inventory.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           RefreshTokenRepository refreshTokenRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Override
    @Transactional
    public AuthResponseDto register(RegisterRequestDto registerRequestDto) {
        if (userRepository.existsByUsername(registerRequestDto.getUsername())) {
            throw new ResourceAlreadyExistsException("Username is already taken: " + registerRequestDto.getUsername());
        }

        if (userRepository.existsByEmail(registerRequestDto.getEmail())) {
            throw new ResourceAlreadyExistsException("Email is already in use: " + registerRequestDto.getEmail());
        }

        User user = new User();
        user.setUsername(registerRequestDto.getUsername());
        user.setEmail(registerRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));

        Set<Role> roles = new HashSet<>();
        if (registerRequestDto.getRoles() == null || registerRequestDto.getRoles().isEmpty()) {
            Role staffRole = roleRepository.findByName(RoleName.ROLE_STAFF)
                    .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_STAFF)));
            roles.add(staffRole);
        } else {
            registerRequestDto.getRoles().forEach(roleStr -> {
                String normalized = roleStr.startsWith("ROLE_") ? roleStr : "ROLE_" + roleStr.toUpperCase();
                RoleName roleName = RoleName.valueOf(normalized);
                Role role = roleRepository.findByName(roleName)
                        .orElseGet(() -> roleRepository.save(new Role(roleName)));
                roles.add(role);
            });
        }
        user.setRoles(roles);
        userRepository.save(user);

        // Authenticate & generate JWT
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(registerRequestDto.getUsername(), registerRequestDto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.generateToken(authentication);
        RefreshToken refreshToken = createRefreshToken(user);

        Set<String> roleNames = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());

        return new AuthResponseDto(accessToken, refreshToken.getToken(), user.getUsername(), user.getEmail(), roleNames);
    }

    @Override
    @Transactional
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsernameOrEmail(), loginRequestDto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsernameOrEmail(loginRequestDto.getUsernameOrEmail(), loginRequestDto.getUsernameOrEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String accessToken = tokenProvider.generateToken(authentication);
        RefreshToken refreshToken = createRefreshToken(user);

        Set<String> roleNames = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());

        return new AuthResponseDto(accessToken, refreshToken.getToken(), user.getUsername(), user.getEmail(), roleNames);
    }

    @Override
    @Transactional
    public AuthResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequestDto) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenRequestDto.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token has expired. Please log in again.");
        }

        User user = refreshToken.getUser();
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null, null);
        String newAccessToken = tokenProvider.generateToken(authentication);

        Set<String> roleNames = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());

        return new AuthResponseDto(newAccessToken, refreshToken.getToken(), user.getUsername(), user.getEmail(), roleNames);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getCurrentUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Set<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());

        return new UserProfileDto(user.getId(), user.getUsername(), user.getEmail(), user.isEnabled(), roles, user.getCreatedAt());
    }

    private RefreshToken createRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60)); // 7 Days
        return refreshTokenRepository.save(refreshToken);
    }
}
