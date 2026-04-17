package com.example.integradora5d.security;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Pattern BCRYPT_PATTERN = Pattern.compile("^\\$2([aby])?\\$\\d{2}\\$[./A-Za-z0-9]{53}$");

    @Bean
    public PasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder delegate = new BCryptPasswordEncoder();
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return delegate.encode(rawPassword);
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                if (rawPassword == null || encodedPassword == null || encodedPassword.isBlank()) {
                    return false;
                }
                if (BCRYPT_PATTERN.matcher(encodedPassword).matches()) {
                    return delegate.matches(rawPassword, encodedPassword);
                }
                return encodedPassword.equals(rawPassword.toString());
            }

            @Override
            public boolean upgradeEncoding(String encodedPassword) {
                return encodedPassword == null || !BCRYPT_PATTERN.matcher(encodedPassword).matches();
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            ApplicationUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name()));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of(HttpHeaders.AUTHORIZATION));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService) {
        return new JwtAuthenticationFilter(jwtService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // Públicos
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/evidencia/**").permitAll()

                        // Activos - lectura para todos los autenticados, escritura solo ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/activo").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/activo/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/activo").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/activo/**").hasRole("ADMINISTRADOR")

                        // Resguardo - operaciones móvil para autenticados
                        .requestMatchers(HttpMethod.GET, "/api/resguardo").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/resguardo/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/resguardo/confirmar").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/resguardo/devolver").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/resguardo/*/solicitar-devolucion").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/resguardo").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/resguardo/*/cancelar-baja").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/resguardo/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/resguardo/**").hasRole("ADMINISTRADOR")

                        // Confirmación QR legacy
                        .requestMatchers(HttpMethod.POST, "/api/resguardos/confirmar").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/auth/dispositivo-token").authenticated()

                        // Mantenimiento
                        .requestMatchers(HttpMethod.GET, "/api/mantenimiento/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/mantenimiento/atender").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/mantenimiento/cerrar").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/mantenimiento/solicitar-baja").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/mantenimiento").hasRole("ADMINISTRADOR")

                        // Reporte
                        .requestMatchers(HttpMethod.POST, "/api/reporte").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/reporte/**").authenticated()

                        // Historial - autenticados (admin y usuario pueden ver su propio historial)
                        .requestMatchers(HttpMethod.GET, "/api/historial/**").authenticated()

                        // Dashboard
                        .requestMatchers("/api/dashboard/**").hasRole("ADMINISTRADOR")

                        // QR
                        .requestMatchers(HttpMethod.GET, "/api/qr/**").authenticated()

                        // Usuario
                        .requestMatchers(HttpMethod.GET, "/api/usuario/perfil").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/usuario/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/usuario/tecnicos").authenticated()
                        // PUT propio usuario (cambio contraseña, FCM token) - autenticado
                        .requestMatchers(HttpMethod.PUT, "/api/usuario/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/usuario").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/usuario/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/usuario/**").hasRole("ADMINISTRADOR")

                        .anyRequest().authenticated());
        return http.build();
    }
}
