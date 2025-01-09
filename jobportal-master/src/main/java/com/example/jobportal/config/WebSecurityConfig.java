package com.example.jobportal.config;

import com.example.jobportal.service.CustomUserDetailsService;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CusomAuthenticationSuccessHandler cusomAuthenticationSuccessHandler;

    public WebSecurityConfig(CustomUserDetailsService userDetailsService,
                             CusomAuthenticationSuccessHandler cusomAuthenticationSuccessHandler) {
                           this.userDetailsService = userDetailsService;
                           this.cusomAuthenticationSuccessHandler = cusomAuthenticationSuccessHandler;
    }

    private final String [] paths = {"/",
            "/global-search/**",
            "/register",
            "/register/**",
            "/webjars/**",
            "/resources/**",
            "/assets/**",
            "/css/**",
            "/summernote/**",
            "/js/**",
            "/*.css",
            "/*.js",
            "/*.js.map",
            "/fonts**", "/favicon.ico", "/resources/**", "/error"};

    @Bean
    protected SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());
        http.authorizeHttpRequests(authorizeRequests -> {
                    authorizeRequests.requestMatchers(paths).permitAll();
                    authorizeRequests.anyRequest().authenticated();
                }
                );
        http.formLogin(formLogin -> formLogin.loginPage("/login").permitAll()
                .successHandler(cusomAuthenticationSuccessHandler))
                .logout(logout -> {
                    logout.logoutUrl("/logout")
                            .logoutSuccessUrl("/");
                }).cors(Customizer.withDefaults())
                .csrf(crsf-> crsf.disable());

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordsEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordsEncoder() {
        return new BCryptPasswordEncoder();
    }
}
