package com.example.shopapppro.configuration;


import com.example.shopapppro.components.JwtTokenUtils;
import com.example.shopapppro.filters.JwtTokenFilter;
import com.example.shopapppro.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableMethodSecurity
//@EnableWebSecurity(debug = true)
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//@EnableWebMvc
@RequiredArgsConstructor
public class WebSecurityConfig {
    private  final JwtTokenFilter jwtTokenFilter;
    @Value("${api.prefix}")
    private String apiPrefix;

    // nguyef kiểm duyệt của request
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
          http
                  .csrf(AbstractHttpConfigurer::disable)
                  //chặn cái filter ở đây
                  .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                  .authorizeHttpRequests(requests -> {
                        requests
                                .requestMatchers(
                                        String.format("%s/users/register", apiPrefix),
                                        String.format("%s/users/login", apiPrefix)
                                        )
                                .permitAll()

                                .requestMatchers(GET,
                                        String.format("%s/categories**", apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                                .requestMatchers(POST,
                                        String.format("%s/categories/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                                .requestMatchers(PUT,
                                        String.format("%s/categories/**", apiPrefix)).hasRole(Role.ADMIN)
                                .requestMatchers(DELETE,
                                        String.format("%s/categories/**", apiPrefix)).hasRole(Role.ADMIN)

                                .requestMatchers(GET,
                                        String.format("%s/products**", apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                                .requestMatchers(POST,
                                        String.format("%s/products/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                                .requestMatchers(PUT,
                                        String.format("%s/products/**", apiPrefix)).hasRole(Role.ADMIN)
                                .requestMatchers(DELETE,
                                        String.format("%s/products/**", apiPrefix)).hasRole(Role.ADMIN)


                               .requestMatchers(POST,
                                       String.format("%s/order_details/**", apiPrefix)).hasRole(Role.USER)
                               .requestMatchers(GET,
                                       String.format("%s/order_details/**", apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                               .requestMatchers(PUT,
                                       String.format("%s/order_details/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                               .requestMatchers(DELETE,
                                       String.format("%s/order_details/**", apiPrefix)).hasAnyRole(Role.ADMIN)

                                .requestMatchers(POST,
                                        String.format("%s/orders/**", apiPrefix)).hasRole(Role.USER)
                                .requestMatchers(GET,
                                        String.format("%s/orders/**", apiPrefix)).hasAnyRole(Role.USER,Role.ADMIN)
                                .requestMatchers(PUT,
                                      String.format("%s/orders/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                                .requestMatchers(DELETE,
                                        String.format("%s/orders/**", apiPrefix)).hasAnyRole(Role.ADMIN)
                                .anyRequest().authenticated();
        })
        .csrf(AbstractHttpConfigurer::disable);
        http.cors(new Customizer<CorsConfigurer<HttpSecurity>>() {
            @Override
            public void customize(CorsConfigurer<HttpSecurity> httpSecurityCorsConfigurer) {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of("*"));
                configuration.setAllowCredentials(true);
                configuration.setAllowedOrigins(List.of("http://localhost:4200"));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
                configuration.setExposedHeaders(List.of("x-auth-token"));
                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                httpSecurityCorsConfigurer.configurationSource(source);

            }
        });



        return http.build();
    }
}
