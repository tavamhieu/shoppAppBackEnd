package com.example.shopapppro.configuration;

import com.example.shopapppro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.net.PasswordAuthentication;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    // đối tượng user detail
    //khi đăng nhập  va hệ thống phải t dối tượng và quản lý thông qua dối tượng
//    user detai; này

    private  final UserRepository userRepository;//sẽ lưu lại token vf ko phải nhập la pass trong1 khoảng thời gian
    @Bean
    public UserDetailsService userDetailsService() {
        return phoneNumber -> userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Cannot find user with phone number = " + phoneNumber));

    // khi bật appp lên sẽ khỏi tạo 1 đối tượng userdetail(user)

    }
    // mã hóa mật khẩu SHA 256
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

//    pahir tạo đủ WebSecurityConfig và SecurityConfig
}
