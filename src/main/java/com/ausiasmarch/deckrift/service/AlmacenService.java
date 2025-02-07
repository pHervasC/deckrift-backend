package com.ausiasmarch.deckrift.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ausiasmarch.deckrift.entity.AlmacenEntity;
import com.ausiasmarch.deckrift.entity.CartaEntity;
import com.ausiasmarch.deckrift.entity.UsuarioEntity;
import com.ausiasmarch.deckrift.exception.ResourceNotFoundException;
import com.ausiasmarch.deckrift.exception.UnauthorizedAccessException;
import com.ausiasmarch.deckrift.repository.AlmacenRepository;
import com.ausiasmarch.deckrift.repository.CartaRepository;
import com.ausiasmarch.deckrift.repository.UsuarioRepository;

@Service
public class AlmacenService implements ServiceInterface<AlmacenEntity> {

    @Autowired
    AlmacenRepository oAlmacenRepository;

    @Autowired
    RandomService oRandomService;

    @Autowired
    private CartaRepository oCartaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    AuthService oAuthService;

    // Crear

    public AlmacenEntity create(AlmacenEntity oAlmacenEntity) {
        return oAlmacenRepository.save(oAlmacenEntity);
    }

    public Long deleteByUsuarioAndCarta(Long usuarioId, Long cartaId) {
        AlmacenEntity almacenExistente = oAlmacenRepository.findByUsuarioIdAndCartaId(usuarioId, cartaId);
        if (almacenExistente != null) {
            oAlmacenRepository.delete(almacenExistente);
            return 1L;
        } else {
            throw new RuntimeException("No se encontró la carta en el almacén del usuario.");
        }
    }

    // Delete
    public Long delete(Long id) {
        oAlmacenRepository.deleteById(id);
        return 1L;
    }

    public AlmacenEntity findById(Long id) {
        return oAlmacenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carta no encontrada con id: " + id));
    }

    public Page<AlmacenEntity> findByUsuarioId(Long usuarioId, Pageable pageable, Optional<String> filter) {
        Page<Object[]> rawResults;
        if (oAuthService.isAdmin() || oAuthService.isAuditorWithItsOwnData(usuarioId)) {
        if (filter.isPresent() && !filter.get().isEmpty()) {
            rawResults = oAlmacenRepository.findByUsuarioIdAndCartaNombreContaining(usuarioId, filter.get(), pageable);
        } else {
            return oAlmacenRepository.findByUsuarioId(usuarioId, pageable);
        }
        } else {
            throw new UnauthorizedAccessException("No tienes permisos para ver el usuario");
        }
    
        // Convertimos los resultados a entidades de AlmacenEntity
        return rawResults.map(obj -> {
            AlmacenEntity almacen = new AlmacenEntity();
            almacen.setId(((Number) obj[0]).longValue());
            almacen.setCantidad(((Number) obj[3]).intValue());
    
            CartaEntity carta = new CartaEntity();
            carta.setId(((Number) obj[4]).longValue());
            carta.setNombre((String) obj[5]);
            carta.setTipo((String) obj[6]);
            carta.setRareza((String) obj[7]);
            almacen.setCarta(carta);
    
            return almacen;
        });
    }
    
    

    public AlmacenEntity get(Long id) {
        return oAlmacenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carta no encontrada con id: " + id));
    }

    public Long count() {
        return oAlmacenRepository.count();
    }

    // Update
    public AlmacenEntity update(AlmacenEntity oAlmacenEntity) {
        AlmacenEntity oAlmacenFromDb = findById(oAlmacenEntity.getId());
        if (oAlmacenEntity.getUsuario() != null) {
            oAlmacenFromDb.setUsuario(oAlmacenEntity.getUsuario());
        }
        if (oAlmacenEntity.getCarta() != null) {
            oAlmacenFromDb.setCarta(oAlmacenEntity.getCarta());
        }
        if (oAlmacenEntity.getCantidad() != null) {
            oAlmacenFromDb.setCantidad(oAlmacenEntity.getCantidad());
        }
        return oAlmacenRepository.save(oAlmacenFromDb);
    }

    public Page<AlmacenEntity> getPage(Pageable oPageable, Optional<String> filter) {
        if (filter.isPresent()) {
            return oAlmacenRepository
                    .findAll(
                            oPageable);
        } else {
            return oAlmacenRepository.findAll(oPageable);
        }
    }

    public Long deleteAll() {
        oAlmacenRepository.deleteAll();
        return this.count();
    }

    public AlmacenEntity randomSelection() {
        return oAlmacenRepository.findById((long) oRandomService.getRandomInt(1, (int) (long) this.count())).get();
    }

    public List<CartaEntity> AñadirCartasAUsuario(Long idUsuario, int cantidad) {
    UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));
            if (oAuthService.isAdmin() || oAuthService.isAuditorWithItsOwnData(idUsuario)) {
    List<CartaEntity> cartasAñadidas = new ArrayList<>();
    for (int i = 0; i < cantidad; i++) {
        CartaEntity carta = oCartaRepository.GetRandomCard();
        AlmacenEntity almacenExistente = oAlmacenRepository.findByUsuarioIdAndCartaId(idUsuario, carta.getId());

        if (almacenExistente != null) {
            almacenExistente.setCantidad(almacenExistente.getCantidad() + 1);
            oAlmacenRepository.save(almacenExistente);
        } else {
            AlmacenEntity almacen = new AlmacenEntity();
            almacen.setUsuario(usuario);
            almacen.setCarta(carta);
            almacen.setCantidad(1);
            oAlmacenRepository.save(almacen);
        }

        cartasAñadidas.add(carta);  // Guardamos la carta para enviarla al frontend
    }
    return cartasAñadidas;
} else {
    throw new UnauthorizedAccessException("No tienes permisos para ver el usuario");
}
}


    public void addCarta(Long usuarioId, Long cartaId) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + usuarioId));
        
            if (oAuthService.isAdmin()) {

        CartaEntity carta = oCartaRepository.findById(cartaId)
            .orElseThrow(() -> new RuntimeException("Carta no encontrada con ID: " + cartaId));
    
        AlmacenEntity almacenExistente = oAlmacenRepository.findByUsuarioIdAndCartaId(usuarioId, cartaId);
    
        if (almacenExistente != null) {
            almacenExistente.setCantidad(almacenExistente.getCantidad() + 1);
            oAlmacenRepository.save(almacenExistente);
        } else {
            AlmacenEntity almacen = new AlmacenEntity();
            almacen.setUsuario(usuario);
            almacen.setCarta(carta);
            almacen.setCantidad(1);
            oAlmacenRepository.save(almacen);
        }
    } else {
        throw new UnauthorizedAccessException("No tienes permisos para ver el usuario");
    }
    }


}
