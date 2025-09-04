package com.bgmi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig 
{

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {


        return http.csrf(customizer->customizer.disable()) // Disabling the csrf token
        .authorizeHttpRequests(request->request
        .requestMatchers("/bgmi/register", "/bgmi/login", "/bgmi/process-login", 
                "/bgmi/add", "/css/**", "/js/**", "/images/**", 
                "/chat", "/app/**", "/topic/**", "/chatbot").permitAll()  // permited without authentication

        .anyRequest().authenticated())  // authenticate all request
        .formLogin(Customizer.withDefaults())  // gives login form with default settings in web browser
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // get new session id every time
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
        
    }


   @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder()); // Use the bean
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }
    
    @Bean
    public  AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception
    {
        return config.getAuthenticationManager();
    }

    @Bean
public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
}

    
    
}
