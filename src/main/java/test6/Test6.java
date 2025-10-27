package test6;

import dev.langchain4j.service.AiServices;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import java.time.Duration;
import java.util.Scanner;

public class Test6 {

    // Interface pour l'assistant IA, capable d'utiliser des outils.
    interface AssistantMeteo {
        String chat(String userMessage);
    }

    public static void main(String[] args) {
        String llmKey = System.getenv("GEMINI_KEY");
        if (llmKey == null || llmKey.isBlank()) {
            System.err.println("GEMINI_KEY environment variable is not set.");
            return;
        }

        // Création du modèle de chat
        ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(llmKey)
                                .modelName("gemini-2.5-flash")
                .timeout(Duration.ofSeconds(60))
                .build();

        // Création de l'assistant en lui fournissant l'outil météo
        AssistantMeteo assistant = AiServices.builder(AssistantMeteo.class)
                .chatLanguageModel(model)
                .tools(new MeteoTool()) // L'assistant peut maintenant utiliser cet outil
                .build();

        System.out.println("Assistant météo prêt. Posez vos questions (tapez 'exit' pour quitter).");

        // Boucle de conversation pour tester l'assistant
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("\nVotre question : ");
                String question = scanner.nextLine();

                if ("exit".equalsIgnoreCase(question) || "quit".equalsIgnoreCase(question)) {
                    break;
                }

                // Le LLM va décider s'il doit utiliser l'outil météo pour répondre.
                String reponse = assistant.chat(question);
                System.out.println("Assistant : " + reponse);
            }
        }
    }
}