package cs253.mountain.server;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
public class MountainResource{
    private ArrayList<Mountain> mountains = new ArrayList<Mountain>();
    
    @PostMapping("/")
    public ResponseEntity<String> saveNew(@RequestHeader String newMountains){
        System.out.println("adding new mountains: " + newMountains);

        List<Mountain> newMountainsList;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            newMountainsList = Arrays.asList(objectMapper.readValue(newMountains, Mountain[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing JSON");
        }

        for(Mountain m : newMountainsList){
            if(mountains.contains(m)){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Mountain already exists");
            }else{
                mountains.add(m);
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Mountains added successfully");
    }

    @GetMapping("/")
    public ResponseEntity<String> getMountains(){
        if(mountains.isEmpty()){
            String response = "No mountains found";
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }else{
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String mountainsJson = objectMapper.writeValueAsString(mountains);
                System.out.println("Mountains found");
                return ResponseEntity.status(HttpStatus.OK).body(mountainsJson);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing JSON");
            }
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getMountainByID(@PathVariable int id){
        for(Mountain m : mountains){
            if(m.getId() == id){
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String mountainJson = objectMapper.writeValueAsString(m);
                    System.out.println("Mountain found");
                    return ResponseEntity.status(HttpStatus.OK).body(mountainJson);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing JSON");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mountain not found");
    }

    @GetMapping("/country/{country}")
    public ResponseEntity<String> getMountainsByCountry(@PathVariable String country){
        ArrayList<Mountain> countryMountains = new ArrayList<Mountain>();
        for(Mountain m : mountains){
            if(m.getCountry().equals(country)){
                countryMountains.add(m);
            }
        }
        if(countryMountains.isEmpty()){
            String response = "No mountains found in " + country;
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }else{
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String mountainsJson = objectMapper.writeValueAsString(countryMountains);
                System.out.println("Mountains found in " + country);
                return ResponseEntity.status(HttpStatus.OK).body(mountainsJson);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing JSON");
            }
        }
    }