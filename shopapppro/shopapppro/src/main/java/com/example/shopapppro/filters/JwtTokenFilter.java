package com.example.shopapppro.filters;


import com.example.shopapppro.components.JwtTokenUtils;
import com.example.shopapppro.configuration.SecurityConfig;
import com.example.shopapppro.models.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    @Value("${api.prefix}")
    private String apiPrefix;
    private final UserDetailsService userDetailsService;
    private  final JwtTokenUtils jwtTokenUtils;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            //filterChain.doFilter(request, response);// mở nhất bất cứ thưs gì cx dc phép đi qua
            if(isBypassToken(request)){
                filterChain.doFilter(request, response);
                return;
            }
            final String authHeader = request.getHeader("Authorization");
            if(authHeader != null && authHeader.startsWith("Bearer ")){
                final String token = authHeader.substring(7);
                final String phoneNumber = jwtTokenUtils.extractPhoneNumber(token);
                if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() ==null) {
                    User userDetails = (User) userDetailsService.loadUserByUsername(phoneNumber);
                    if(jwtTokenUtils.validateToken(token,userDetails)){
                        UsernamePasswordAuthenticationToken authenticationToken=
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            }
            filterChain.doFilter(request, response);
        }catch (Exception e){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unauthorized");

//            filterChain.doFilter(request,response);
//            return;
        }

    }

    private boolean isBypassToken(@NotNull HttpServletRequest request){

        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                // cus phaps string.format cho pheps noois chuoi
                Pair.of(String.format("%s/products**", apiPrefix), "GET"),
                Pair.of(String.format("%s/categories**", apiPrefix), "GET"),
                Pair.of(String.format("%s/users/register", apiPrefix), "POST"),
                Pair.of(String.format("%s/users/login", apiPrefix), "POST")
        );
        for (Pair<String,String> bypassToken:bypassTokens){
            if(request.getServletPath().contains(bypassToken.getFirst())&& request.getMethod().equals(bypassToken.getSecond())){
                return true;
            }
        }
        return false;
    }
}
