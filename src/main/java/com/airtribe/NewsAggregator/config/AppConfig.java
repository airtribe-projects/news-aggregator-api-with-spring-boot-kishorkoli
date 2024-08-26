package com.airtribe.NewsAggregator.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.airtribe.NewsAggregator.filter.JwtAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class AppConfig {

  @Autowired
  private AuthenticationProvider authenticationProvider;

  @Autowired
  private JwtAuthenticationFilter jwtAuthenticationFilter;
  
  @Value("${news.api.key}")
  private String apiKey;

  @Value("${news.api.url}")
  private String apiUrl;

  public String getApiKey() {
      return apiKey;
  }

  public String getApiUrl() {
      return apiUrl;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) // Disable CSRF if necessary
        .authorizeHttpRequests(authorizeRequests ->
            authorizeRequests
                .requestMatchers("/register", "/verifyRegistration", "/login").permitAll() // Allow unauthenticated access to /register
                .anyRequest().authenticated() // Require authentication for other requests
        ).authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }
}