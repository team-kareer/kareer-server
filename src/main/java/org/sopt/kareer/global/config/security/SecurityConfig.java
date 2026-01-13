package org.sopt.kareer.global.config.security;

import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.jwt.filter.JwtAuthenticationFilter;
import org.sopt.kareer.global.jwt.filter.JwtExceptionFilter;
import org.sopt.kareer.global.jwt.handler.CustomAccessDeniedHandler;
import org.sopt.kareer.global.jwt.handler.CustomAuthenticationEntryPoint;
import org.sopt.kareer.global.oauth.handler.OAuth2AuthenticationFailureHandler;
import org.sopt.kareer.global.oauth.handler.OAuth2AuthenticationSuccessHandler;
import org.sopt.kareer.global.oauth.service.CustomOidcOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // TODO: 추후에 admin Role 추가 필요
    private static final String[] PERMIT_ALL_PATTERNS = new String[] {
            "/",
            "/api/v1/auth/**",
            "/api/v1/documents/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/actuator/health",
            "/h2-console/**",
            "/error",
            "/oauth2/**",
            "/login/oauth2/**",
            "/job-postings"
    };

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final CustomOidcOAuth2UserService customOidcOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler successHandler;
    private final OAuth2AuthenticationFailureHandler failureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(FrameOptionsConfig::sameOrigin))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(info -> info.oidcUserService(customOidcOAuth2UserService))
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PERMIT_ALL_PATTERNS).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);

        return http.build();
    }
}
