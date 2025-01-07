package com.ausiasmarch.deckrift.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ausiasmarch.deckrift.entity.AlmacenEntity;
import com.ausiasmarch.deckrift.entity.CartaEntity;
import com.ausiasmarch.deckrift.entity.UsuarioEntity;
import com.ausiasmarch.deckrift.exception.ResourceNotFoundException;
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

   //Crear

    public AlmacenEntity create(AlmacenEntity oAlmacenEntity) {
        return oAlmacenRepository.save(oAlmacenEntity);
    }


    //Delete
    public Long delete(Long id) {
        oAlmacenRepository.deleteById(id);
        return 1L;
    }

    public AlmacenEntity findById(Long id) {
        return oAlmacenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carta no encontrada con id: " + id));
    }

    public Page<AlmacenEntity> findByUsuarioId(Pageable oPageable, Optional<Long> filter) {
        if (filter.isPresent()) {
            return oAlmacenRepository.findByUsuarioId(filter.get(), oPageable);
        } else {
            return oAlmacenRepository.findAll(oPageable);
        }
    }

    public AlmacenEntity get(Long id) {
        return oAlmacenRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Carta no encontrada con id: " + id));
    }

    public Long count() {
        return oAlmacenRepository.count();
    }

    //Update
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

    public void AñadirCartasAUsuario(Long idUsuario, int cantidad) {
        UsuarioEntity usuario = usuarioRepository.findById(idUsuario)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));

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
    }
    }
}
