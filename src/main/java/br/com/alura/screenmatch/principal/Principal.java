package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private List<DadosSerie> dadosSerieList = new ArrayList<>();
    private SerieRepository repository;
    private List<Serie> serieList = new ArrayList<>();

    public Principal (SerieRepository repository) {
        this.repository = repository;
    }

    public void exibeMenu() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("""
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar série por título
                                    
                    0  - sair
                    """);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;

                case 2:
                    buscasEpisodioPorSerie();
                    break;

                case 3:
                    listarSeriesBuscadas();
                    break;

                case 4:
                    buscarSeriePorTitulo();
                    break;

                case 0:
                    opcao = 0;
                    break;
            }
        }
    }
    public void buscarSerieWeb() {
        System.out.println("Digite o nome da série");
        String nomeSerie = leitura.nextLine().replace(" ", "+");
        String busca = ENDERECO + nomeSerie + API_KEY;
        String json = consumo.obterDados(busca);
        DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);
        Serie serie = new Serie(dadosSerie);
        repository.save(serie);
        System.out.println(dadosSerie);
    }

    public void buscasEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("Digite o nome da série");
        String nomeSerie = leitura.nextLine();

        Optional<Serie> serie = repository.findByTituloContainingIgnoreCase(nomeSerie);

        if (serie.isPresent()) {
            Serie serieEncontrada = serie.get();
            List<DadosTemporada> dadosTemporadaList = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                String busca = ENDERECO + serieEncontrada.getTitulo().replace(" ", "+").toLowerCase() + "&season=" + i + API_KEY;
                String json = consumo.obterDados(busca);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                dadosTemporadaList.add(dadosTemporada);
            }
            dadosTemporadaList.forEach(System.out::println);
            List<Episodio> episodioList = dadosTemporadaList.stream()
                    .flatMap(d -> d.episodios()
                            .stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodioList);
            repository.save(serieEncontrada);
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    public void listarSeriesBuscadas() {
        serieList = repository.findAll();
        serieList.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    public void buscarSeriePorTitulo() {
        System.out.println("Digite o nome da série");
        String nomeSerie = leitura.nextLine();
        Optional<Serie> serieOptional = repository.findByTituloContainingIgnoreCase(nomeSerie);
        if (serieOptional.isPresent()) {
            System.out.println(serieOptional.get());
        } else {
            System.out.println("Série não encontrada!");
        }
    }
}