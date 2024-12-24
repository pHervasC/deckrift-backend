package com.ausiasmarch.deckrift.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceInterface<T> {

    public T randomSelection();

    public Page<T> getPage(Pageable oPageable, Optional<String> filter);

    public T get(Long id);

    public Long count();

    public Long delete(Long id);

    public T create(T oUsuarioentity);

    public T update(T oUsuarioentity);

    public Long deleteAll();
}

