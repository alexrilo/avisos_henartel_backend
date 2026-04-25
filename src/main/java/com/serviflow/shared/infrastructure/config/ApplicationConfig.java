package com.serviflow.shared.infrastructure.config;

import com.serviflow.auth.application.GetCurrentUserUseCase;
import com.serviflow.auth.application.LoginUseCase;
import com.serviflow.auth.application.RefreshTokenUseCase;
import com.serviflow.auth.domain.port.TokenProvider;
import com.serviflow.aviso.application.AssignTecnicoUseCase;
import com.serviflow.aviso.application.CancelarAvisoUseCase;
import com.serviflow.aviso.application.ChangeEstadoUseCase;
import com.serviflow.aviso.application.CreateAvisoUseCase;
import com.serviflow.aviso.application.GetAvisoUseCase;
import com.serviflow.aviso.application.GetMisTrabajosUseCase;
import com.serviflow.aviso.application.ListAvisosUseCase;
import com.serviflow.aviso.application.ReprogramarAvisoUseCase;
import com.serviflow.aviso.application.UpdateAvisoUseCase;
import com.serviflow.aviso.domain.port.AvisoRepository;
import com.serviflow.aviso.domain.port.CorrelativoRepository;
import com.serviflow.cliente.application.CreateClienteUseCase;
import com.serviflow.cliente.application.GetClienteUseCase;
import com.serviflow.cliente.application.ListClientesUseCase;
import com.serviflow.cliente.application.ToggleClienteStatusUseCase;
import com.serviflow.cliente.application.UpdateClienteUseCase;
import com.serviflow.cliente.domain.port.ClienteRepository;
import com.serviflow.dashboard.application.GetDashboardMetricsUseCase;
import com.serviflow.dashboard.domain.port.DashboardRepository;
import com.serviflow.user.application.CreateUserUseCase;
import com.serviflow.user.application.GetUserUseCase;
import com.serviflow.user.application.ListUsersUseCase;
import com.serviflow.user.application.ToggleUserActiveUseCase;
import com.serviflow.user.application.UpdateUserUseCase;
import com.serviflow.user.domain.port.PasswordEncoder;
import com.serviflow.user.domain.port.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Application configuration that wires use cases as Spring beans.
 * This configuration acts as the composition root, connecting use cases to their dependencies.
 */
@Configuration
public class ApplicationConfig {

    @Bean
    public CreateUserUseCase createUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return new CreateUserUseCase(userRepository, passwordEncoder);
    }

    @Bean
    public UpdateUserUseCase updateUserUseCase(UserRepository userRepository) {
        return new UpdateUserUseCase(userRepository);
    }

    @Bean
    public GetUserUseCase getUserUseCase(UserRepository userRepository) {
        return new GetUserUseCase(userRepository);
    }

    @Bean
    public ListUsersUseCase listUsersUseCase(UserRepository userRepository) {
        return new ListUsersUseCase(userRepository);
    }

    @Bean
    public ToggleUserActiveUseCase toggleUserActiveUseCase(UserRepository userRepository) {
        return new ToggleUserActiveUseCase(userRepository);
    }

    @Bean
    public LoginUseCase loginUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
        return new LoginUseCase(userRepository, passwordEncoder, tokenProvider);
    }

    @Bean
    public RefreshTokenUseCase refreshTokenUseCase(TokenProvider tokenProvider) {
        return new RefreshTokenUseCase(tokenProvider);
    }

    @Bean
    public GetCurrentUserUseCase getCurrentUserUseCase(UserRepository userRepository) {
        return new GetCurrentUserUseCase(userRepository);
    }

    // Cliente Use Cases (uses com.serviflow.cliente.domain.port.ClienteRepository)

    @Bean
    public CreateClienteUseCase createClienteUseCase(ClienteRepository clienteRepository) {
        return new CreateClienteUseCase(clienteRepository);
    }

    @Bean
    public UpdateClienteUseCase updateClienteUseCase(ClienteRepository clienteRepository) {
        return new UpdateClienteUseCase(clienteRepository);
    }

    @Bean
    public GetClienteUseCase getClienteUseCase(ClienteRepository clienteRepository) {
        return new GetClienteUseCase(clienteRepository);
    }

    @Bean
    public ListClientesUseCase listClientesUseCase(ClienteRepository clienteRepository) {
        return new ListClientesUseCase(clienteRepository);
    }

    @Bean
    public ToggleClienteStatusUseCase toggleClienteStatusUseCase(ClienteRepository clienteRepository) {
        return new ToggleClienteStatusUseCase(clienteRepository);
    }

    // Aviso Use Cases (uses com.serviflow.cliente.domain.port.ClienteRepository for cliente validation)

    @Bean
    public CreateAvisoUseCase createAvisoUseCase(
            AvisoRepository avisoRepository,
            ClienteRepository clienteRepository,
            CorrelativoRepository correlativoRepository) {
        return new CreateAvisoUseCase(avisoRepository, clienteRepository, correlativoRepository);
    }

    @Bean
    public UpdateAvisoUseCase updateAvisoUseCase(AvisoRepository avisoRepository) {
        return new UpdateAvisoUseCase(avisoRepository);
    }

    @Bean
    public GetAvisoUseCase getAvisoUseCase(AvisoRepository avisoRepository, ClienteRepository clienteRepository) {
        return new GetAvisoUseCase(avisoRepository, clienteRepository);
    }

    @Bean
    public ListAvisosUseCase listAvisosUseCase(AvisoRepository avisoRepository) {
        return new ListAvisosUseCase(avisoRepository);
    }

    @Bean
    public AssignTecnicoUseCase assignTecnicoUseCase(AvisoRepository avisoRepository) {
        return new AssignTecnicoUseCase(avisoRepository);
    }

    @Bean
    public ChangeEstadoUseCase changeEstadoUseCase(AvisoRepository avisoRepository) {
        return new ChangeEstadoUseCase(avisoRepository);
    }

    @Bean
    public ReprogramarAvisoUseCase reprogramarAvisoUseCase(AvisoRepository avisoRepository) {
        return new ReprogramarAvisoUseCase(avisoRepository);
    }

    @Bean
    public CancelarAvisoUseCase cancelarAvisoUseCase(AvisoRepository avisoRepository) {
        return new CancelarAvisoUseCase(avisoRepository);
    }

    @Bean
    public GetMisTrabajosUseCase getMisTrabajosUseCase(AvisoRepository avisoRepository, ClienteRepository clienteRepository) {
        return new GetMisTrabajosUseCase(avisoRepository, clienteRepository);
    }

    // Dashboard Use Cases

    @Bean
    public GetDashboardMetricsUseCase getDashboardMetricsUseCase(DashboardRepository dashboardRepository) {
        return new GetDashboardMetricsUseCase(dashboardRepository);
    }
}