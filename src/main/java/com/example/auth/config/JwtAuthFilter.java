package com.example.auth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException{

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;
        String role = null;

        if(authHeader == null || !authHeader.startsWith("Bearer ") ){
            filterChain.doFilter(request, response);
            return;
        }


        token = authHeader.substring(7);

        if(jwtUtil.isTokenExpired(token)){
            email = jwtUtil.extractUserEmail(token);
            role = jwtUtil.extractRole(token);
            if(role == null){
                System.out.println("role is not have");
                filterChain.doFilter(request, response);
                return;
            }

            SimpleGrantedAuthority authority =
                    new SimpleGrantedAuthority("ROLE_"+role);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    List.of(authority)
            );
//  ---------------------------------------------------------------------------------------
//            when use this code,
//              Spring Security formLogin
//              Session based authentication
//              Browser + cookies

//            authenticationToken.setDetails(
//                    new WebAuthenticationDetailsSource().buildDetails(request)
//            );
//  ---------------------------------------------------------------------------------------
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request, response);

    }

}
