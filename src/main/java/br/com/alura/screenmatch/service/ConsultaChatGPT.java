package br.com.alura.screenmatch.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.service.OpenAiService;

public class ConsultaChatGPT {
    public static String obterTraducao(String texto) {
        OpenAiService openAiService = new OpenAiService(System.getenv("OPENAI_APIKEY"));
        CompletionRequest completionRequest = CompletionRequest.builder()
                .model("gpt-3.5-turbo-instruct")
                .temperature(0.7)
                .maxTokens(100)
                .prompt("Traduza para o português o texto: " + texto)
                .build();

        try {
            CompletionResult resposta = openAiService.createCompletion(completionRequest);
            return resposta.getChoices().get(0).getText();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
