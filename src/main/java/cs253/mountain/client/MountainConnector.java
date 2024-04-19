package cs253.mountain.client;

import java.util.List;
import java.util.Optional;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class MountainConnector {
    public Optional<Response> addMountains(List<Mountain> mountains) {
        try{
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = 
                HttpRequest.newBuilder().build();
            HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());
            return Optional.of(new Response(response.statusCode(), response.body()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
