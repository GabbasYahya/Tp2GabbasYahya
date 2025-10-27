package test3;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.store.embedding.CosineSimilarity;
import java.time.Duration;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        
        String apiKey = System.getenv("GEMINI_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("Erreur : La variable d'environnement GEMINI_KEY n'est pas définie.");
            return;
        }

        
        EmbeddingModel embeddingModel = GoogleAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName("text-embedding-004")
                .timeout(Duration.ofSeconds(20))
                .maxRetries(3)
                .build();

        
        List<String[]> sentencePairs = List.of(
                new String[]{"Quel temps fait-il aujourd'hui ?", "Quelle est la météo du jour ?"},
                new String[]{"J'adore programmer en Java.", "La programmation Java est ma passion."},
                new String[]{"Le chat dort sur le canapé.", "Le chien joue dans le jardin."},
                new String[]{"Cette pomme est délicieuse.", "Cette voiture est très rapide."},
                new String[]{"Bonjour tout le monde.", "Au revoir l'équipe."}
        );

        System.out.println("Calcul de la similarité sémantique entre des paires de phrases :");

        
        for (String[] pair : sentencePairs) {
            String sentence1 = pair[0];
            String sentence2 = pair[1];

            List<TextSegment> segments = List.of(
                    TextSegment.from(sentence1),
                    TextSegment.from(sentence2)
            );
            List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
            Embedding embedding1 = embeddings.get(0);
            Embedding embedding2 = embeddings.get(1);

            
            double similarity = CosineSimilarity.between(embedding1, embedding2);

            
            System.out.printf("----------------------------------------------------%n");
            System.out.printf("Phrase 1: \"%s\"%n", sentence1);
            System.out.printf("Phrase 2: \"%s\"%n", sentence2);
            System.out.printf("Similarité : %.4f%n", similarity);
        }
        System.out.printf("----------------------------------------------------%n");
    }
}
