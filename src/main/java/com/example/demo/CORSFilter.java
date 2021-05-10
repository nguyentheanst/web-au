package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

@Slf4j
@Component
public class CORSFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String origin = "";
        Enumeration<String> enumHeaders = request.getHeaders("Origin");
        while (enumHeaders != null && enumHeaders.hasMoreElements()) {
            origin = enumHeaders.nextElement();
        }

        response.setHeader("Access-Control-Allow-Origin", "http://localhost:12000");
        response.setHeader("Access-Control-Allow-credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Origin, cache-control, credentials, authorization, content-type, xsrf-token, Accept-Language, Set-Cookie, Clear-Site-Data");
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition, xsrf-token");
        response.addHeader("Content-Security-Policy", "frame-ancestors 'self'");

        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
