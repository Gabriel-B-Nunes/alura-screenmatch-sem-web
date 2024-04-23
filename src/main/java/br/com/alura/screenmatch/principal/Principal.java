package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private List<DadosSerie> dadosSerieList = new ArrayList<>();

    public void exibeMenu() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("""
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                                    
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
        dadosSerieList.add(dadosSerie);
        System.out.println(dadosSerie);
    }

    public void buscasEpisodioPorSerie() {
        System.out.println("Digite o nome da série");
        String nomeSerie = leitura.nextLine().replace(" ", "+");
        String busca = ENDERECO + nomeSerie + API_KEY;
        String json = consumo.obterDados(busca);
        DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);
        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            busca = ENDERECO + nomeSerie + "&season=" + i + API_KEY;
            json = consumo.obterDados(busca);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            dadosTemporada.episodios().forEach(System.out::println);
        }
    }

    public void listarSeriesBuscadas() {
        List<Serie> serieList = new ArrayList<>();
        serieList = dadosSerieList.stream()
                .map(d -> new Serie(d))
                        .collect(Collectors.toList());
        serieList.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }
}