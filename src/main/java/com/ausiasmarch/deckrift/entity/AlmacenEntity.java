package com.ausiasmarch.deckrift.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "Almacen")
public class AlmacenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "carta_id", nullable = false)
    private CartaEntity carta;

    @NotNull
    private Integer cantidad;

    public AlmacenEntity() {
    }

    public AlmacenEntity(Long id, UsuarioEntity usuario, CartaEntity carta, Integer cantidad) {
        this.id = id;
        this.usuario = usuario;
        this.carta = carta;
        this.cantidad = cantidad;
    }

    public AlmacenEntity(UsuarioEntity usuario, CartaEntity carta, Integer cantidad) {
        this.usuario = usuario;
        this.carta = carta;
        this.cantidad = cantidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioEntity getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioEntity usuario) {
        this.usuario = usuario;
    }

    public CartaEntity getCarta() {
        return carta;
    }

    public void setCarta(CartaEntity carta) {
        this.carta = carta;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
