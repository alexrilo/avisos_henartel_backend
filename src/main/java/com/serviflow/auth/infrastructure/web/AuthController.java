package com.serviflow.auth.infrastructure.web;

import com.serviflow.auth.application.GetCurrentUserUseCase;
import com.serviflow.auth.application.LoginUseCase;
import com.serviflow.auth.application.RefreshTokenUseCase;
import com.serviflow.auth.application.input.LoginInput;
import com.serviflow.auth.application.output.LoginOutput;
import com.serviflow.auth.application.output.UserOutput;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Web controller for authentication operations.
 * Thin controller that delegates all business logic to use cases.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    public AuthController(LoginUseCase loginUseCase,
                          RefreshTokenUseCase refreshTokenUseCase,
                          GetCurrentUserUseCase getCurrentUserUseCase) {
        this.loginUseCase = loginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginOutput> login(@Valid @RequestBody LoginRequest request) {
        LoginInput input = new LoginInput(request.email(), request.password());
        LoginOutput output = loginUseCase.execute(input);
        return ResponseEntity.ok(output);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginOutput> refresh(@RequestHeader("X-Refresh-Token") String refreshToken) {
        LoginOutput output = refreshTokenUseCase.execute(refreshToken);
        return ResponseEntity.ok(output);
    }

    @GetMapping("/me")
    public ResponseEntity<UserOutput> me(Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        UserOutput output = getCurrentUserUseCase.execute(username);
        return ResponseEntity.ok(output);
    }

    /**
     * Request DTO for login endpoint.
     * Contains validation annotations for HTTP layer concerns.
     */
    public record LoginRequest(
        String email,
        String password
    ) {}
}