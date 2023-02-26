package com.hps.bookshop.config;

import com.hps.bookshop.entity.RoleType;
import com.hps.bookshop.security.AuthTokenFilter;
import com.hps.bookshop.security.oauth2.CustomOAuth2UserService;
import com.hps.bookshop.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.hps.bookshop.security.oauth2.OAuth2AuthenticationSuccessHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final AuthenticationEntryPoint unauthorizedHandler;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    public static final String[] WHITE_LIST_URLS = {
            "/login", "/register", "/verifyRegistration", "/error/**", "/logout", "/forgotPassword",
            "/confirmCode", "/changePassword", "/oauth2/**", "/css/**","/webjars/**", "/validate-token"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .authorizeHttpRequests(a -> a
                        .requestMatchers(WHITE_LIST_URLS).permitAll()
                        .requestMatchers("/").hasAuthority(RoleType.CUSTOMER.name())
                        .requestMatchers("/vendor/**").hasAuthority(RoleType.VENDOR.name())
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(o -> o
                        .authorizationEndpoint()
                            .baseUri("/oauth2/authorization")
                            .and()
                        .userInfoEndpoint()
                            .userService(customOAuth2UserService)
                            .and()
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                )
                .logout().disable()
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(unauthorizedHandler)
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
