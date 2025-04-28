package com.example.usermngsystem.security;

import com.example.usermngsystem.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component

public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // Blacklist để lưu token đã logout
    private static final Set<String> tokenBlacklist = ConcurrentHashMap.newKeySet();

    public static void addToBlacklist(String token) {
        tokenBlacklist.add(token);
    }

    public static boolean isBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Tách token từ header
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7); // bỏ "Bearer " (7 ký tự đầu)

            if (isBlacklisted(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token is blacklisted. Please login again.\"}");
                return;
            }
            try {
                username = jwtUtils.getUsernameFromJwt(token);
            } catch (Exception e) {
                System.out.println("Lỗi khi lấy username từ token: " + e.getMessage());
            }
        }

        // Nếu có username và chưa có Authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtUtils.validateJwt(token)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
        if (token == null) {
            System.out.println("Không tìm thấy token trong header Authorization");
        } else if (username == null) {
            System.out.println("Không lấy được username từ token");
        } else if (!jwtUtils.validateJwt(token)) {
            System.out.println("Token không hợp lệ hoặc hết hạn");
        }

    }
}
