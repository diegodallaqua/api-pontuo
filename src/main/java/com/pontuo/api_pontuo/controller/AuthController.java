package com.pontuo.api_pontuo.controller;

import com.pontuo.api_pontuo.dto.LoginRequestDTO;
import com.pontuo.api_pontuo.dto.LoginResponseDTO;
import com.pontuo.api_pontuo.dto.RegisterRequestDTO;
import com.pontuo.api_pontuo.dto.UserResponseDTO;
import com.pontuo.api_pontuo.dto.UserRoleResponseDTO;
import com.pontuo.api_pontuo.entity.User;
import com.pontuo.api_pontuo.entity.UserRole;
import com.pontuo.api_pontuo.security.AppUserDetails;
import com.pontuo.api_pontuo.security.JwtService;
import com.pontuo.api_pontuo.security.TokenRevocationService;
import com.pontuo.api_pontuo.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String TOKEN_TYPE = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRevocationService revocationService;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          TokenRevocationService revocationService,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.revocationService = revocationService;
        this.userService = userService;
    }

    /**
     * Valida as credenciais e devolve o access token. O campo username aceita
     * tanto o username quanto o email do usuário.
     */
    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        requestDTO.username(), requestDTO.password()));

        JwtService.IssuedToken token = jwtService.issue(authentication);
        User user = ((AppUserDetails) authentication.getPrincipal()).user();

        return new LoginResponseDTO(
                TOKEN_TYPE,
                token.value(),
                token.expiresInSeconds(),
                toResponseDTO(user));
    }

    /**
     * Invalida o token usado na requisição. Como o JWT é stateless, o logout
     * consiste em registrar o jti do token na lista de revogados até que ele
     * expire — o cliente também deve descartar o token que guardou.
     */
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@AuthenticationPrincipal Jwt jwt) {
        revocationService.revoke(jwt);
        SecurityContextHolder.clearContext();
    }

    /** Dados do usuário dono do token, sem expor a senha. */
    @GetMapping("/me")
    public UserResponseDTO me(@AuthenticationPrincipal Jwt jwt) {
        return userService.findByUsername(jwt.getSubject())
                .map(this::toResponseDTO)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User não encontrado: username=" + jwt.getSubject()));
    }

    /** Cadastro público; a role de estudante é aplicada pelo serviço. */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO register(@Valid @RequestBody RegisterRequestDTO requestDTO) {
        User created = userService.register(new User(
                requestDTO.username(),
                requestDTO.email(),
                requestDTO.birthDate(),
                requestDTO.password()));
        return toResponseDTO(created);
    }

    private UserResponseDTO toResponseDTO(User entity) {
        return new UserResponseDTO(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getBirthDate(),
                entity.getCreatedAt(),
                toUserRoleResponseDTO(entity.getUserRole()));
    }

    private UserRoleResponseDTO toUserRoleResponseDTO(UserRole entity) {
        if (entity == null) {
            return null;
        }
        return new UserRoleResponseDTO(entity.getId(), entity.getDescription());
    }
}
