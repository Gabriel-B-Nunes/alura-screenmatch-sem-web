package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
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
    private Optional<Serie> serieOptional;

    public Principal(SerieRepository repository) {
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
                    5 - Buscar séries por ator
                    6 - Top 5 séries
                    7 - Buscar séries por categoria
                    8 - Filtrar séries
                    9 - Buscar episódios por trecho
                    10 - Top 5 episódios por série
                    11 - Buscar episódios a partir de uma data
                                    
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

                case 5:
                    buscarSeriePorAtor();
                    break;

                case 6:
                    buscarTop5Series();
                    break;

                case 7:
                    buscarSeriesPorCategoria();
                    break;

                case 8:
                    buscarSeriesPorTemporadaAvaliacao();
                    break;

                case 9:
                    buscarEpisodiosPorTrecho();
                    break;

                case 10:
                    buscarTop5EpisodiosPorSerie();
                    break;

                case 11:
                    buscarEpisodiosAPartirDeUmaData();
                    break;

                case 0:
                    opcao = 0;
                    break;

                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarEpisodiosAPartirDeUmaData() {
        buscarSeriePorTitulo();
        System.out.println("Informe o ano");
        Integer ano = leitura.nextInt();
        leitura.nextLine();
        List<Episodio> episodioList = repository.buscarEpisodiosAPartirDeUmaData(serieOptional.get(), ano);
        if (!episodioList.isEmpty()) {
            episodioList.forEach(System.out::println);
        } else {
            System.out.println("Nenhum resultado encontrado!");
        }
    }

    private void buscarTop5EpisodiosPorSerie() {
        buscarSeriePorTitulo();
        List<Episodio> episodioList = repository.buscaTop5EpisodiosPorSerie(serieOptional.get());
        if (!episodioList.isEmpty()) {
            episodioList.forEach(System.out::println);
        }
    }

    private void buscarEpisodiosPorTrecho() {
        System.out.println("Digite um trecho do nome do episódio");
        String trechoEpisodio = leitura.nextLine();
        List<Episodio> episodioList = repository.buscaEpisodiosPorTrecho(trechoEpisodio);
        if (!episodioList.isEmpty()) {
            episodioList.forEach(System.out::println);
        } else {
            System.out.println("Nenhum resultado encontrado!");
        }
    }

    private void buscarSerieWeb() {
        System.out.println("Digite o nome da série");
        String nomeSerie = leitura.nextLine().replace(" ", "+");
        String busca = ENDERECO + nomeSerie + API_KEY;
        String json = consumo.obterDados(busca);
        DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);
        Serie serie = new Serie(dadosSerie);
        repository.save(serie);
        System.out.println(dadosSerie);
    }

    private void buscasEpisodioPorSerie() {
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

    private void listarSeriesBuscadas() {
        serieList = repository.findAll();
        serieList.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Digite o nome da série");
        String nomeSerie = leitura.nextLine();
        serieOptional = repository.findByTituloContainingIgnoreCase(nomeSerie);
        if (serieOptional.isPresent()) {
            System.out.println(serieOptional.get());
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void buscarSeriePorAtor() {
        System.out.println("Digite o nome do ator");
        String nomeAtor = leitura.nextLine();
        System.out.println("Avaliações a partir de que valor?");
        Double avaliacao = leitura.nextDouble();
        leitura.nextLine();

        List<Serie> serieList = repository.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        if (!serieList.isEmpty()) {
            serieList.forEach(s -> System.out.println(s.getTitulo() + ", avaliação=" + s.getAvaliacao()));
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void buscarTop5Series() {
        List<Serie> serieList = repository.findTop5ByOrderByAvaliacaoDesc();
        if (!serieList.isEmpty()) {
            serieList.forEach(s -> System.out.println(s.getTitulo() + ", avaliação=" + s.getAvaliacao()));
        }
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Digite a categoria");
        String nomeCategoria = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeCategoria);
        List<Serie> serieList = repository.findByGenero(categoria);
        if (!serieList.isEmpty()) {
            System.out.println("Séries com categoria=" + categoria);
            serieList.forEach(System.out::println);
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void buscarSeriesPorTemporadaAvaliacao() {
        System.out.println("Digite o total de temporadas");
        Integer totalTemporadas = leitura.nextInt();
        leitura.nextLine();
        System.out.println("Avaliações a partir de qual valor?");
        Double avaliacao = leitura.nextDouble();
        List<Serie> serieList = repository.seriesPorTemporadaEAvaliacao(totalTemporadas, avaliacao);
        if (!serieList.isEmpty()) {
            serieList.forEach(System.out::println);
        } else {
            System.out.println("Série não encontrada!");
        }
    }
}