package com.logistic.platform.Configuration;

public record PortalPrincipal(String loginType, Integer id, String displayName) {

    public boolean isAdmin() {
        return "admin".equals(loginType);
    }

    public boolean isUser() {
        return "user".equals(loginType);
    }

    public boolean isDriver() {
        return "driver".equals(loginType);
    }
}
