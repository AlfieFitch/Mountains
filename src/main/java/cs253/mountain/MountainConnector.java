package cs253.mountain;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;




public class MountainConnector {

    HttpClient client = HttpClient.newHttpClient();
    private static String URI_VAL = "http://localhost:8080";

    public MountainConnector(String uri){
        URI_VAL = uri;
    }

    public Optional<Response> addMountains(List<Mountain> mountains) {
        try{
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(URI_VAL)).PUT(
                HttpRequest.BodyPublishers.ofString(mountains.toString())).build();

            HttpResponse<Void> response =
                client.send(request, HttpResponse.BodyHandlers.discarding());

            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Response> getAll(){
        try{
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(URI_VAL +"/mountains")).GET().build();

            HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            List<Mountain> mountains = mapper.readValue(response.body(), new TypeReference<List<Mountain>>() {
            });

            return Optional.of(new Response(mountains, response));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Response> getByCountry(String country){
        try{
            // add country as a param to the URI
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(URI_VAL + "/mountains?country=" + country)).GET().build();
            
            HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            List<Mountain> mountains = mapper.readValue(response.body(), new TypeReference<List<Mountain>>() {
            });

            return Optional.of(new Response(mountains, response));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
