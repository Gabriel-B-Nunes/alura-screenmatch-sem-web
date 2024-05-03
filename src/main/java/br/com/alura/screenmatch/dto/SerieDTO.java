package br.com.alura.screenmatch.dto;

import br.com.alura.screenmatch.model.Categoria;

public record SerieDTO(long id,
                       String titulo,
                       Double avaliacao,
                       int totalTemporadas,
                       Categoria genero,
                       String atores,
                       String poster,
                       String sinopse) {
}
