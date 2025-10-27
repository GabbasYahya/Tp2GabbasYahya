package test6;



import dev.langchain4j.agent.tool.Tool;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MeteoTool {

    @Tool("Obtient la météo actuelle pour une ville spécifique")
    public String getWeather(String city) {
        try {
            // On nettoie le nom de la ville pour l'URL
            String formattedCity = java.net.URLEncoder.encode(city, java.nio.charset.StandardCharsets.UTF_8.toString());
            
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://wttr.in/" + formattedCity + "?format=3"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                return "Impossible de récupérer la météo pour " + city + ". Statut: " + response.statusCode();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Erreur lors de la récupération de la météo pour " + city;
        }
    }
}