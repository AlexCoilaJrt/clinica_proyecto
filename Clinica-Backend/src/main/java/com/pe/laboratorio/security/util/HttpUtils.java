package com.pe.laboratorio.security.util;

import jakarta.servlet.http.HttpServletRequest;

public class HttpUtils {

    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    /**
     * Obtiene la dirección IP real del cliente, considerando proxies y
     * balanceadores
     * de carga
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        for (String header : IP_HEADER_CANDIDATES) {
            String ipList = request.getHeader(header);
            if (ipList != null && ipList.length() != 0 && !"unknown".equalsIgnoreCase(ipList)) {
                // Si hay múltiples IPs (por proxies), tomar la primera
                String ip = ipList.split(",")[0];
                return ip.trim();
            }
        }
        return request.getRemoteAddr();
    }

    /**
     * Obtiene el User-Agent del navegador/cliente
     */
    public static String getUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null ? userAgent : "Unknown";
    }
}
