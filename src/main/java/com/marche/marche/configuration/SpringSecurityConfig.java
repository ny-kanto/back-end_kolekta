package com.marche.marche.configuration;

import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

import com.marche.marche.authentification.JwtAuthorizationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SpringSecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final LogoutHandler logoutHandler;

    public SpringSecurityConfig(CustomUserDetailsService customUserDetailsService,
            JwtAuthorizationFilter jwtAuthorizationFilter, LogoutHandler logoutHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        this.logoutHandler = logoutHandler;
    }

    // @Bean
    // public AuthenticationManager authenticationManagerBean(NoOpPasswordEncoder
    // noOpPasswordEncoder) throws Exception {
    // return new CustomAuthenticationManager(noOpPasswordEncoder,
    // customUserDetailsService);
    // }

    @Bean
    public AuthenticationManager authenticationManagerBean(PasswordEncoder passwordEncoder) throws Exception {
        return new CustomAuthenticationManager(passwordEncoder, customUserDetailsService);
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService);
    }

    // @Bean
    // public AuthenticationManager authenticationManager(HttpSecurity http,
    // NoOpPasswordEncoder noOpPasswordEncoder)
    // throws Exception {
    // AuthenticationManagerBuilder authenticationManagerBuilder = http
    // .getSharedObject(AuthenticationManagerBuilder.class);
    // authenticationManagerBuilder.userDetailsService(customUserDetailsService)
    // .passwordEncoder(noOpPasswordEncoder);
    // //
    // authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider);
    // return authenticationManagerBuilder.build();
    // }

    @SuppressWarnings({ "deprecated", "removal", "deprecation" })
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().and()
                .authorizeRequests()
                .requestMatchers("/rest/auth/**", "/user/signup", "/type-production/all", "/user/info",
                        "/user/new-admin", "/user/update-fcm-token", "/ws/**", "/user/send-notification")
                .permitAll()
                .requestMatchers("/produit/save", "/produit/all", "/produit/update", "/produit/get", "/produit/delete",
                        "/stock/**", "/statistique/all", "/commande/list-produit", "/commande/list", "/commande/count",
                        "/commande/update_status", "/message/**", "/user/profile-vendeur",
                        "/user/profile-acheteur", "/unite/**", "/categorie/**")
                .hasRole("USER_VENDEUR")
                .requestMatchers("/produit/user-all", "/panier/**", "/produit/get-user", "/evaluation/**",
                        "/commentaire/**", "/conversation/**", "/user/profile-acheteur", "/user/profile-vendeur")
                .hasRole("USER_ACHETEUR")
                .requestMatchers("/statistique/admin", "/user/list", "/commande/list-admin")
                .hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/auth/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler(
                        (request, response, authentication) -> SecurityContextHolder.clearContext());
        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:8081"));
        config.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    // @SuppressWarnings("deprecated")
    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // http.csrf().disable().authorizeRequests()
    // .requestMatchers("/rest/auth/**", "/user/signup").permitAll()
    // .anyRequest().authenticated()
    // .and()
    // .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    // .and()
    // .addFilterBefore(jwtAuthorizationFilter,
    // UsernamePasswordAuthenticationFilter.class);
    // return http.build();
    // }

    @Bean
    public UserDetailsService users() {
        UserDetails user = User.builder().username("user").password(passwordEncoder().encode("user")).roles("USER")
                .build();
        UserDetails admin = User.builder().username("admin").password(passwordEncoder().encode("admin"))
                .roles("USER", "ADMIN").build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    // @Bean
    // public NoOpPasswordEncoder passwordEncoder() {
    // return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    // }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // @Bean
    // public BCryptPasswordEncoder passwordEncoder() {
    // return new BCryptPasswordEncoder();
    // }
}
