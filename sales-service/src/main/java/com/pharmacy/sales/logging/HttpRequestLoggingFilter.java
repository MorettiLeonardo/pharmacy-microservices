package com.pharmacy.sales.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class HttpRequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.startsWith("/actuator/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        String method = request.getMethod();
        long startTime = System.currentTimeMillis();
        log.info("HTTP IN  -> {} {}", method, path);

        filterChain.doFilter(request, response);

        long durationMs = System.currentTimeMillis() - startTime;
        log.info("HTTP OUT <- {} {} status={} took={}ms", method, path, response.getStatus(), durationMs);
    }
}
