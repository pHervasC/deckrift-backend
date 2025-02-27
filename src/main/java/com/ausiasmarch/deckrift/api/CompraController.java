package com.ausiasmarch.deckrift.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ausiasmarch.deckrift.entity.CompraEntity;
import com.ausiasmarch.deckrift.service.CompraService;

@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RestController
@RequestMapping("/compra")
public class CompraController {
    
     @Autowired
    private CompraService compraService;

    @GetMapping("/{id}")
    public CompraEntity getCompraById(@PathVariable Long id) {
        return compraService.getCompraById(id);
    }

    @GetMapping("/page")
public ResponseEntity<Page<CompraEntity>> getCompras(
    @RequestParam(required = false) String correo,
    @RequestParam(required = false) String estado,
    Pageable pageable) {
    return ResponseEntity.ok(compraService.getCompras(pageable, correo, estado));
}

    @PostMapping("/create")
    public CompraEntity createCompra(@RequestBody CompraEntity compra) {
        return compraService.createCompra(compra);
    }
}
