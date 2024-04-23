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

/**
 * MountainConnector class that acts as the client to interact with the REST API.
 * @version 1.0
 * @author Alfie Fitch-May
 */


public class MountainConnector {

    HttpClient client = HttpClient.newHttpClient();
    private static String URI_VAL = "http://localhost:8080";

    /**
     * Constructor for the MountainConnector Class.
     * @param uri
     */
    public MountainConnector(String uri){
        URI_VAL = uri;
    }

    /**
     * Method to add a mountain to the database.
     * @param mountains - List of mountains to add.
     * @return Optional<Response> - Response object containing the list of mountains and the response.
     */
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

    /**
     * Method to add a single mountain to the database.
     * @param id - ID of the mountain to add.
     * @param mountain - Mountain object to add.
     * @return Optional<Response> - Response object containing the list of mountains and the response.
     */
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

    /**
     * Method to delete a mountain from the database.
     * @param id - ID of the mountain to delete.
     * @return Optional<Response> - Response object containing the list of mountains and the response.
     */
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

    /**
     * Method to handle the GET requests.
     * @param URI - URI to send the GET request to.
     * @return Optional<Response> - Response object containing the list of mountains and the response.
     */
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

    /**
     * Method to get all the mountains from the database.
     * @return Optional<Response> - Response object containing the list of mountains and the response.
     */
    public Optional<Response> getAll(){
        return GetHandler(URI_VAL);
    }

    /**
     * Method to get all the mountains from the database by country.
     * @param country - Country to get the mountains from.
     * @return Optional<Response> - Response object containing the list of mountains and the response.
     */
    public Optional<Response> getByCountry(String country){
        return GetHandler(URI_VAL + "country/" + country);
    }

    /**
     * Method to get all the mountains from the database by range.
     * @param range - Range to get the mountains from.
     * @return Optional<Response> - Response object containing the list of mountains and the response.
     */
    public Optional<Response> getByCountryAndRange(String country, String range){
        return GetHandler(URI_VAL + "country/" + country + "/range/" + range);
    }

    /**
     * Method to get all the mountains from the database by altitude.
     * @param altitude - Altitude to get the mountains from.
     * @return Optional<Response> - Response object containing the list of mountains and the response.
     */
    public Optional<Response> getByHemisphere(boolean isNorthern){
        return GetHandler(URI_VAL + "?hemisphere=" + (isNorthern ? "north" : "south"));
    }

    /**
     * Method to get all the mountains from the database by country and altitude.
     * @param country - Country to get the mountains from.
     * @param altitude - Altitude to get the mountains from.
     * @return Optional<Response> - Response object containing the list of mountains and the response.
     */
    public Optional<Response> getByCountryAltitude(String country, int altitude){
        return GetHandler(URI_VAL + "country/" + country + "?altitude=" + altitude);
    }

    /**
     * Method to get all the mountains from the database by country, range and name.
     * @param country - Country to get the mountains from.
     * @param range - Range to get the mountains from.
     * @param name - Name to get the mountains from.
     * @return Optional<Response> - Response object containing the list of mountains and the response.
     */
    public Optional<Response> getByName(String country, String range, String name){
        return GetHandler(URI_VAL + "country/" + country + "/range/" + range + "/name/" + name);
    }

    /**
     * Method to get a mountain from the database by ID.
     * @param id - ID of the mountain to get.
     * @return Optional<Response> - Response object containing the list of mountains and the response.
     */
    public Optional<Response> getById(int id){
        return GetHandler(URI_VAL + "id/" + id);
    }

}
