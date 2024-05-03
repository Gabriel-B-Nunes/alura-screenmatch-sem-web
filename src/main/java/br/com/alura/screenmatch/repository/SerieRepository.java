package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTituloContainingIgnoreCase (String nomeSerie);
    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual (String nomeAtor, Double avaliacao);

    List<Serie> findTop5ByOrderByAvaliacaoDesc ();

    List<Serie> findByGenero (Categoria categoria);

    @Query("SELECT s FROM Serie s WHERE s.totalTemporadas <= :totalTemporadas AND s.avaliacao >= :avaliacao")
    List<Serie> seriesPorTemporadaEAvaliacao (Integer totalTemporadas, Double avaliacao);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:trechoEpisodio%")
    List<Episodio> buscaEpisodiosPorTrecho(String trechoEpisodio);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.avaliacao DESC LIMIT 5")
    List<Episodio> buscaTop5EpisodiosPorSerie(Serie serie);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie AND YEAR(e.dataLancamento) >= :ano")
    List<Episodio> buscarEpisodiosAPartirDeUmaData(Serie serie, Integer ano);

    @Query("SELECT s FROM Serie s JOIN s.episodios e GROUP BY s ORDER BY MAX(e.dataLancamento) DESC LIMIT 5")
    List<Serie> buscarTop5Lancamentos();

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s.id = :id AND e.temporada = :idTemporada")
    List<Episodio> obterEpisodiosPorTemporada(long id, long idTemporada);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s.id = :id ORDER BY e.avaliacao DESC LIMIT 5")
    List<Episodio> obterTop5Episodios(long id);
}
