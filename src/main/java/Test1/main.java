package test1;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.output.Response;
import java.time.Duration;
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

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Entrez votre question: ");
            String prompt = scanner.nextLine();
            if (prompt == null || prompt.isBlank()) {
                System.err.println("Aucune question fournie, arrêt.");
                return;
            }

            try {
                Response<AiMessage> response = model.generate(UserMessage.from(prompt));

                System.out.println("Reponse du modele :");
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
