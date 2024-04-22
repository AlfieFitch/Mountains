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

    public Optional<Response> updateMountain(int id, Mountain mountain){
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(mountain);
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(URI_VAL + "id/" + id)).PUT(HttpRequest.BodyPublishers.ofString(json)).build();

            HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

            List<Mountain> mountainList = new ArrayList<Mountain>();
    
            return Optional.of(new Response(mountainList, response));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Response> deleteMountain(int id){
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(URI_VAL + "id/" + id)).DELETE().build();

            HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

            List<Mountain> mountainList = new ArrayList<Mountain>();
            ObjectMapper mapper = new ObjectMapper();

            if(!response.body().isEmpty()){
                mountainList = mapper.readValue(response.body(), new TypeReference<List<Mountain>>() {
                });
            }
    
            return Optional.of(new Response(mountainList, response));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<Response> GetHandler(String URI){
        try{
            List<Mountain> mountains = new ArrayList<Mountain>();

            HttpRequest request = HttpRequest.newBuilder().uri(new URI(URI)).GET().build();
            HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
        
            if(!response.body().isEmpty()){
                mountains = mapper.readValue(response.body(), new TypeReference<List<Mountain>>() {
                });
            }    

            return Optional.of(new Response(mountains, response));
        } catch (Exception e) {
            System.out.println(e);
            return Optional.empty();
        }
    }

    public Optional<Response> getAll(){
        return GetHandler(URI_VAL);
    }

    public Optional<Response> getByCountry(String country){
        return GetHandler(URI_VAL + "country/" + country);
    }

    public Optional<Response> getByCountryAndRange(String country, String range){
        return GetHandler(URI_VAL + "country/" + country + "/range/" + range);
    }

    public Optional<Response> getByHemisphere(boolean isNorthern){
        return GetHandler(URI_VAL + "?hemisphere=" + (isNorthern ? "north" : "south"));
    }

    public Optional<Response> getByCountryAltitude(String country, int altitude){
        return GetHandler(URI_VAL + "country/" + country + "?altitude=" + altitude);
    }

    public Optional<Response> getByName(String country, String range, String name){
        return GetHandler(URI_VAL + "country/" + country + "/range/" + range + "/name/" + name);
    }

    public Optional<Response> getById(int id){
        return GetHandler(URI_VAL + "id/" + id);
    }

}
