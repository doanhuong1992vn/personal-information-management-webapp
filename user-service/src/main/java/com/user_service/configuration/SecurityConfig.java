package com.user_service.configuration;

import com.user_service.security.UserDetailsServiceImpl;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {
    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final UserDetailsServiceImpl userDetailsService;

    private final Filter jwtFilter;

    @Value("${app.cors.allowedOrigins}")
    private String allowedOrigin;

    public SecurityConfig(AuthenticationEntryPoint authenticationEntryPoint,
                          UserDetailsServiceImpl userDetailsService,
                          @Qualifier("jwtFilter") Filter filter) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.userDetailsService = userDetailsService;
        this.jwtFilter = filter;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        final long MAX_AGE_SECS = 3600;
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigin)
                .allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE")
                .allowCredentials(false)
                .maxAge(MAX_AGE_SECS);
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOriginPattern("**");
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests()
                .requestMatchers(
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/check-username/**",
                        "/docs/**"
                )
                .permitAll();

        http.authorizeHttpRequests()
                .requestMatchers(
                        "/api/users/**",
                        "/api/auth/logout"
                )
                .hasAnyRole("ADMIN", "USER");

        http.authorizeHttpRequests()
                .requestMatchers("/api/**")
                .authenticated();

        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.cors()
                .configurationSource(corsConfigurationSource())
                .and()
                .csrf()
                .ignoringRequestMatchers("/api/**");

        return http.build();
    }
}
