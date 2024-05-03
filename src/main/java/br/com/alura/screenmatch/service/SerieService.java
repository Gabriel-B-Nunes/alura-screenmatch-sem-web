package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {
    @Autowired
    private SerieRepository repository;

    public List<SerieDTO> converterDados (List<Serie> series) {
        return series.stream()
                .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getAvaliacao(), s.getTotalTemporadas(), s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse()))
                .collect(Collectors.toList());
    }
    public List<SerieDTO> obterTodasAsSeries() {
        return converterDados(repository.findAll());
    }

    public List<SerieDTO> obterTop5Series() {
        return converterDados(repository.findTop5ByOrderByAvaliacaoDesc());
    }

    public List<SerieDTO> obterLancamentos() {
        return converterDados(repository.buscarTop5Lancamentos());
    }

    public SerieDTO obterPorId(long id) {
        Optional<Serie> serieOptional = repository.findById(id);
        if (serieOptional.isPresent()) {
            Serie s = serieOptional.get();
            SerieDTO serieDTO = new SerieDTO(s.getId(), s.getTitulo(), s.getAvaliacao(), s.getTotalTemporadas(), s.getGenero(), s.getAtores(), s.getPoster(), s.getSinopse());
            return serieDTO;
        } else {
            return null;
        }
    }

    public List<EpisodioDTO> obterTodosOsEpisodios(long id) {
        Optional<Serie> serieOptional = repository.findById(id);
        if (serieOptional.isPresent()) {
            Serie s = serieOptional.get();
            List<Episodio> episodioList = s.getEpisodios();
            return episodioList.stream()
                    .map(e -> new EpisodioDTO(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
                    .collect(Collectors.toList());
        } else {
            return null;
        }
    }

    public List<EpisodioDTO> obterEpisodiosPorTemporada(long id, long idTemporada) {
        List<Episodio> episodioList = repository.obterEpisodiosPorTemporada(id, idTemporada);
        return episodioList.stream()
                .map(e -> new EpisodioDTO(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
                .collect(Collectors.toList());
    }

    public List<EpisodioDTO> obterTop5Episodios(long id) {
        List<Episodio> episodioList = repository.obterTop5Episodios(id);
        return episodioList.stream()
                .map(e -> new EpisodioDTO(e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()))
                .collect(Collectors.toList());
    }
}
