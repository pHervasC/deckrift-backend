package com.ausiasmarch.filter;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ausiasmarch.deckrift.service.JWTService;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTFilter implements Filter {

    @Autowired
    private JWTService JWTHelper;

    @Autowired
    public JWTFilter(JWTService JWTHelper) {
        this.JWTHelper = JWTHelper;
    }

    @Override
    public void doFilter(ServletRequest oServletRequest,
                         ServletResponse oServletResponse,
                         FilterChain oFilterChain)
            throws IOException, ServletException {

        HttpServletRequest oHttpServletRequest = (HttpServletRequest) oServletRequest;
        HttpServletResponse oHttpServletResponse = (HttpServletResponse) oServletResponse;

        if ("OPTIONS".equals(oHttpServletRequest.getMethod())) {
            oHttpServletResponse.setStatus(HttpServletResponse.SC_OK);
            oFilterChain.doFilter(oServletRequest, oServletResponse);
            return;
        }

        String sToken = oHttpServletRequest.getHeader("Authorization");

        if (sToken == null) {
            System.out.println("⛔ No se encontró token en la solicitud.");
            oFilterChain.doFilter(oServletRequest, oServletResponse);
            return;
        }

        System.out.println("🔹 Token recibido en el header: " + sToken);

        if (!sToken.startsWith("Bearer ")) {
            System.out.println("⛔ Token no empieza con 'Bearer '");
            oHttpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token no válido");
            return;
        }

        String sTokenReal = sToken.substring(7);
        System.out.println("🔹 Token sin 'Bearer ': " + sTokenReal);

        String correo = JWTHelper.validateToken(sTokenReal);

        if (correo == null) {
            System.out.println("🚨 Token inválido, `validateToken` devolvió null.");
            oHttpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token no válido");
            return;
        }

        System.out.println("✅ Token válido para usuario: " + correo);
        oHttpServletRequest.setAttribute("correo", correo);
        oFilterChain.doFilter(oServletRequest, oServletResponse);
    }
}
