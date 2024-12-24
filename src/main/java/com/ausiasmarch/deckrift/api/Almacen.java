package com.ausiasmarch.deckrift.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ausiasmarch.deckrift.service.AlmacenService;
import com.ausiasmarch.deckrift.entity.AlmacenEntity;

@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RestController
@RequestMapping("/almacen")
public class Almacen {
    
    @Autowired
    AlmacenService oAlmacenService;

    // Obtener todas las cartas
    @GetMapping("")
    public ResponseEntity<Page<AlmacenEntity>> getPage(Pageable oPageable, @RequestParam Optional<Long> filter) {
        return new ResponseEntity<>(oAlmacenService.findByUsuarioId(oPageable, filter), HttpStatus.OK);
    }
    
}
