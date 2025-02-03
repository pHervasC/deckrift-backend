package com.ausiasmarch.deckrift.service;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import com.ausiasmarch.deckrift.bean.LogindataBean;
import com.ausiasmarch.deckrift.entity.UsuarioEntity;
import com.ausiasmarch.deckrift.exception.UnauthorizedAccessException;
import com.ausiasmarch.deckrift.repository.UsuarioRepository;

@Service
public class AuthService {

    @Autowired
    JWTService JWTHelper;

    @Autowired
    UsuarioRepository oUsuarioRepository;

    @Autowired
    HttpServletRequest oHttpServletRequest;

    public boolean checkLogin(LogindataBean oLogindataBean) {
        if (oUsuarioRepository.findByCorreoAndPassword(oLogindataBean.getCorreo(), oLogindataBean.getPassword())
                .isPresent()) {
            return true;
        } else {
            return false;
        }
    }

    private Map<String, String> getClaims(String correo) {
        Map<String, String> claims = new HashMap<>();
        claims.put("correo", correo);
        return claims;
    };

    public String getToken(String correo) {
        return JWTHelper.generateToken(getClaims(correo));
    }

    public UsuarioEntity getUsuarioFromToken() {

        Enumeration<String> attributeNames = oHttpServletRequest.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
        }

        Object correoAttr = oHttpServletRequest.getAttribute("correo");

        if (correoAttr == null) {
            throw new UnauthorizedAccessException("No hay usuario en la sesión");
        }

        return oUsuarioRepository.findByCorreo(correoAttr.toString())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public boolean isSessionActive() {
        return oHttpServletRequest.getAttribute("correo") != null;
    }

    public boolean isAdmin() {
        return this.getUsuarioFromToken().getTipousuario().getId() == 1L;
    }

    public boolean isAuditor() {
        return this.getUsuarioFromToken().getTipousuario().getId() == 2L;
    }

    public boolean isAuditorWithItsOwnData(Long id) {
        UsuarioEntity oUsuarioEntity = this.getUsuarioFromToken();
        return this.isAuditor() && oUsuarioEntity.getId() == id;
    }

}