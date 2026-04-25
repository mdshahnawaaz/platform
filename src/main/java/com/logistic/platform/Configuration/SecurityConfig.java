package com.logistic.platform.Configuration;

import java.util.LinkedHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/",
                                "/frontuser",
                                "/portal/user",
                                "/portal/user/login",
                                "/portal/admin",
                                "/portal/driver",
                                "/logistics/admin/login",
                                "/logistics/drivers/portal",
                                "/logistics/drivers/login",
                                "/logistics/contact/**",
                                "/logistics/global_reach",
                                "/logistics/secure_package",
                                "/logistics/secure_handle",
                                "/logistics/business_int",
                                "/logistics/eco_friendly",
                                "/error",
                                "/css/**",
                                "/images/**")
                        .permitAll()
                        .requestMatchers("/logistics/admin/**").hasRole("ADMIN")
                        .requestMatchers("/portal/user/logout", "/hi").hasRole("USER")
                        .requestMatchers("/logistics/drivers/**").hasAnyRole("DRIVER", "ADMIN")
                        .anyRequest().permitAll())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler()));

        return http.build();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<>();
        entryPoints.put(new AntPathRequestMatcher("/logistics/admin/**"), new LoginUrlAuthenticationEntryPoint("/logistics/admin/login"));
        entryPoints.put(new AntPathRequestMatcher("/logistics/drivers/**"), new LoginUrlAuthenticationEntryPoint("/logistics/drivers/portal"));
        entryPoints.put(new AntPathRequestMatcher("/portal/user/**"), new LoginUrlAuthenticationEntryPoint("/portal/user"));
        entryPoints.put(new AntPathRequestMatcher("/hi"), new LoginUrlAuthenticationEntryPoint("/portal/user"));

        DelegatingAuthenticationEntryPoint entryPoint = new DelegatingAuthenticationEntryPoint(entryPoints);
        entryPoint.setDefaultEntryPoint(new LoginUrlAuthenticationEntryPoint("/"));
        return entryPoint;
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            String path = request.getRequestURI();
            if (path.startsWith("/logistics/admin/")) {
                response.sendRedirect(request.getContextPath() + "/logistics/admin/login");
                return;
            }
            if (path.startsWith("/logistics/drivers/")) {
                response.sendRedirect(request.getContextPath() + "/logistics/drivers/portal");
                return;
            }
            if (path.startsWith("/portal/user/") || "/hi".equals(path)) {
                response.sendRedirect(request.getContextPath() + "/portal/user");
                return;
            }

            AccessDeniedHandlerImpl fallbackHandler = new AccessDeniedHandlerImpl();
            fallbackHandler.handle(request, response, accessDeniedException);
        };
    }
}
