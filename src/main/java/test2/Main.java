package test2;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.output.Response;
import java.time.Duration;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String apiKey = System.getenv("GEMINI_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("Missing GEMINI_KEY environment variable.");
            return;
        }

        String modelName = System.getenv("GEMINI_MODEL");
        if (modelName == null || modelName.isBlank()) {
            modelName = "gemini-2.5-flash";
        }

        ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(0.7)
                .maxOutputTokens(1024)
                .timeout(Duration.ofSeconds(120))
                .build();

        PromptTemplate translateTemplate = PromptTemplate.from(
                "Traduis le texte suivant en anglais : {{texte}}"
        );

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Texte à traduire : ");
            String texte = scanner.nextLine();
            if (texte == null || texte.isBlank()) {
                System.err.println("Aucun texte fourni, arrêt.");
                return;
            }
            try {
                Prompt prompt = translateTemplate.apply(Map.of("texte", texte));
                Response<AiMessage> response = model.generate(prompt.toUserMessage());
                System.out.println("Traduction :");
                System.out.println(response.content().text());
                System.out.println(response.content().text());

                System.out.println("\nToken usage: " + response.tokenUsage());
            } catch (RuntimeException e) {
                if (causedByTimeout(e)) {
                    System.err.println("La requete a expire avant d'obtenir une reponse. Veuillez reessayer ou poser une question plus concise.");
                } else {
                    throw e;
                }
            }
        }
    }

    private static boolean causedByTimeout(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof java.net.SocketTimeoutException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

}
