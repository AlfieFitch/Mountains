package cs253.mountain;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.ArrayList;




public class MountainConnector {

    HttpClient client = HttpClient.newHttpClient();
    private static String URI_VAL = "http://localhost:8080";

    public MountainConnector(String uri){
        URI_VAL = uri;
    }

    public Optional<Response> addMountains(List<Mountain> mountains) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(mountains);
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(URI_VAL)).POST(HttpRequest.BodyPublishers.ofString(json)).build();

            HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

            List<Mountain> mountainList = new ArrayList<Mountain>();
            return Optional.of(new Response(mountainList, response));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Response> getAll(){
        try{
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(URI_VAL)).GET().build();

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
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(URI_VAL + "country/" + country)).GET().build();

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

    public Optional<Response> getByCountryAndRange(String country, String range){
        try{
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(URI_VAL + "country/" + country + "/range/" + range)).GET().build();

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

    public Optional<Response> getByHemisphere(boolean isNorthern){
        try{
            String hemisphere = isNorthern ? "northern" : "southern";
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(URI_VAL + "?hemisphere=" + hemisphere)).GET().build();

            HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            List<Mountain> mountains = mapper.readValue(response.body(), new TypeReference<List<Mountain>>() {
            });

            return Optional.of(new Response(mountains, response));
        } catch (Exception e) {
            System.out.println(e);
            return Optional.empty();
        }
    }

    public Optional<Response> getByCountryAltitude(String country, int altitude){
        try{
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(URI_VAL + "country/" + country + "?altitude=" + altitude)).GET().build();

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

    public Optional<Response> getByName(String country, String range, String name){
        try{
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(URI_VAL + "country/" + country + "/range/" + range + "/name/" + name)).GET().build();

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

    public Optional<Response> getById(int id){
        try{
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(URI_VAL + "/id/" + id)).GET().build();

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
