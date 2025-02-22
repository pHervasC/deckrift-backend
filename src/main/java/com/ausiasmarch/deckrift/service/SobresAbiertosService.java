package com.ausiasmarch.deckrift.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ausiasmarch.deckrift.entity.SobresAbiertosEntity;
import com.ausiasmarch.deckrift.entity.UsuarioEntity;
import com.ausiasmarch.deckrift.repository.SobresAbiertosRepository;
import com.ausiasmarch.deckrift.repository.UsuarioRepository;

@Service
public class SobresAbiertosService {
    
    @Autowired
    SobresAbiertosRepository sobresAbiertosRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    public boolean puedeAbrirSobre(Long usuarioId) {
        LocalDate hoy = LocalDate.now();
        Optional<SobresAbiertosEntity> registro = sobresAbiertosRepository.findByUsuarioIdAndFecha(usuarioId, hoy);
    
        System.out.println("Usuario ID: " + usuarioId + " - Fecha: " + hoy);
        System.out.println("Registro encontrado: " + registro);
    
        return (registro.isEmpty() || registro.get().getCantidad() < 2);
    }

    public boolean registrarAperturaSobre(Long usuarioId, boolean usarMonedas) {
        LocalDate hoy = LocalDate.now();
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
        Optional<SobresAbiertosEntity> registroExistente = sobresAbiertosRepository.findByUsuarioIdAndFecha(usuarioId, hoy);
    
        if (registroExistente.isPresent()) {
            SobresAbiertosEntity registro = registroExistente.get();
    
            if (registro.getCantidad() < 2) {
                registro.setCantidad(registro.getCantidad() + 1);
                sobresAbiertosRepository.save(registro);
                return true;
            } else if (usarMonedas && usuario.getMonedas() >= 10) {
                usuario.setMonedas(usuario.getMonedas() - 10);
                usuarioRepository.save(usuario);
                registro.setCantidad(registro.getCantidad() + 1);
                sobresAbiertosRepository.save(registro);
                return true;
            } else {
                throw new RuntimeException("Has alcanzado el límite de 2 sobres por día y no tienes suficientes monedas.");
            }
        } else {
            SobresAbiertosEntity nuevoRegistro = new SobresAbiertosEntity(usuario, hoy, 1);
            sobresAbiertosRepository.save(nuevoRegistro);
            return true;
        }
    }
    
}
