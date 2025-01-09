package com.ausiasmarch.deckrift.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "cartas")
public class CartaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 100)
    private String nombre;

    @NotNull
    @Size(min = 1, max = 50)
    private String tipo;

    @Size(max = 50)
    private String rareza;

    @Lob
    private byte[] imagen;

    public CartaEntity() {
    }

    public CartaEntity(Long id, String nombre, String tipo, String rareza) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.rareza = rareza;
    }

    public CartaEntity(String nombre, String tipo, String rareza) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.rareza = rareza;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getRareza() {
        return rareza;
    }

    public void setRareza(String rareza) {
        this.rareza = rareza;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

}
