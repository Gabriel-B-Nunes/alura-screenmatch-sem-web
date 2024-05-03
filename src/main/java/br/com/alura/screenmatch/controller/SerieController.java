package br.com.alura.screenmatch.controller;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/series")
public class SerieController {

    @Autowired
    private SerieService serieService = new SerieService();

    @GetMapping
    public List<SerieDTO> obterSeries() {
        return serieService.obterTodasAsSeries();
    }

    @GetMapping("/top5")
    public List<SerieDTO> obterTop5() {
        return serieService.obterTop5Series();
    }

    @GetMapping("/lancamentos")
    public List<SerieDTO> obterLancamentos() {
        return serieService.obterLancamentos();
    }

    @GetMapping("/{id}")
    public SerieDTO obterPorId(@PathVariable long id) {
        return serieService.obterPorId(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    public List<EpisodioDTO> obterTodosOsEpisodios(@PathVariable long id) {
        return serieService.obterTodosOsEpisodios(id);
    }

    @GetMapping("/{id}/temporadas/{idTemporada}")
    public List<EpisodioDTO> obterEpisodiosPorTemporada(@PathVariable long id, @PathVariable long idTemporada) {
        return serieService.obterEpisodiosPorTemporada(id, idTemporada);
    }

    @GetMapping("/{id}/temporadas/top")
    public List<EpisodioDTO> obterTop5Episodios(@PathVariable long id) {
        return serieService.obterTop5Episodios(id);
    }
}
