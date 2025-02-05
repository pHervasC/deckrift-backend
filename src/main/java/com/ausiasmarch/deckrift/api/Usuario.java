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

import com.ausiasmarch.deckrift.entity.UsuarioEntity;
import com.ausiasmarch.deckrift.service.UsuarioService;

@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RestController
@RequestMapping("/usuario")
public class Usuario {

    @Autowired
    UsuarioService oUsuarioService;

    @GetMapping("/byemail/{correo}")
    public ResponseEntity<UsuarioEntity> getUsuarioByEmail(@PathVariable(value = "correo") String correo) {
        return ResponseEntity.ok(oUsuarioService.getByEmail(correo));
    }

    // Obtener todos los usuarios (con filtro opcional)
    @GetMapping("")
    public ResponseEntity<Page<UsuarioEntity>> getPage(
            Pageable oPageable,
            @RequestParam Optional<String> filter) {
        return new ResponseEntity<>(oUsuarioService.getPage(oPageable, filter), HttpStatus.OK);
    }

    // Obtener un usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioEntity> getUsuario(@PathVariable Long id) {
        return new ResponseEntity<>(oUsuarioService.findById(id), HttpStatus.OK);
    }

    // Eliminar un usuario por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        oUsuarioService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Crear un nuevo usuario
    @PostMapping("")
    public ResponseEntity<UsuarioEntity> create(@RequestBody UsuarioEntity oUsuarioEntity) {
        return new ResponseEntity<>(oUsuarioService.create(oUsuarioEntity), HttpStatus.CREATED);
    }

    //Crear usuario siendo admin
    @PostMapping("/admin-create")
public ResponseEntity<UsuarioEntity> adminCreate(@RequestBody UsuarioEntity oUsuarioEntity) {
    // Verificar que se ha enviado un tipo de usuario
    if (oUsuarioEntity.getTipousuario() == null || oUsuarioEntity.getTipousuario().getId() == null) {
        throw new RuntimeException("El tipo de usuario es obligatorio.");
    }

    // Lógica de creación delegada al servicio
    UsuarioEntity nuevoUsuario = oUsuarioService.adminCreate(oUsuarioEntity);
    return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
}


    // Actualizar un usuario existente
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioEntity> update(@RequestBody UsuarioEntity oUsuarioEntity) {
        return new ResponseEntity<>(oUsuarioService.update(oUsuarioEntity), HttpStatus.OK);
    }

}
