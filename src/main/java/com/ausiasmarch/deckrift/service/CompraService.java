package com.ausiasmarch.deckrift.service;

import com.ausiasmarch.deckrift.entity.CompraEntity;
import com.ausiasmarch.deckrift.exception.ResourceNotFoundException;
import com.ausiasmarch.deckrift.exception.UnauthorizedAccessException;
import com.ausiasmarch.deckrift.repository.CompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;


@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private AuthService authService;

    public CompraEntity getCompraById(Long id) {
        if (authService.isAdmin() || authService.isAuditorWithItsOwnData(id)) {
            return compraRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con id: " + id));
        } else {
            throw new UnauthorizedAccessException("No tienes permisos para ver esta compra");
        }
    }

    public Page<CompraEntity> getCompras(Pageable pageable, String correo, String estado) {
        if (!authService.isAdmin()) {
            throw new UnauthorizedAccessException("No tienes permisos para ver las compras");
        }
    
        if ((correo != null && !correo.isEmpty()) || (estado != null && !estado.isEmpty())) {
            return compraRepository.findByUsuarioCorreoAndEstado(correo, estado, pageable);
        } else {
            return compraRepository.findAll(pageable);
        }
    }

    public CompraEntity createCompra(CompraEntity compra) {
        return compraRepository.save(compra);
    }

    public Long count() {
        if (!authService.isAdmin()) {
            throw new UnauthorizedAccessException("No tienes permisos para contar las compras");
        }
        return compraRepository.count();
    }

    public Long delete(Long id) {
        if (authService.isAdmin()) {
            compraRepository.deleteById(id);
            return 1L;
        } else {
            throw new UnauthorizedAccessException("No tienes permisos para eliminar esta compra");
        }
    }

    public Long deleteAll() {
        if (!authService.isAdmin()) {
            throw new UnauthorizedAccessException("No tienes permisos para borrar todas las compras");
        }
        compraRepository.deleteAll();
        return this.count();
    }
}
