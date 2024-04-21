package cs253.mountain;

import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.concurrent.locks.ReentrantReadWriteLock;  


@RestController
public class MountainResource{

    // Variable Initialization --------------------------------------------------------------------------------------

    private ArrayList<Mountain> mountains = new ArrayList<Mountain>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    // POST Methods -----------------------------------------------------------------------------------------------q

    @PostMapping("/mountains")
    public ResponseEntity<String> saveNew(@RequestHeader String newMountains){
        System.out.println("adding new mountains: " + newMountains);

        List<Mountain> newMountainsList;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            newMountainsList = Arrays.asList(objectMapper.readValue(newMountains, Mountain[].class));            
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing JSON");
        }

        lock.writeLock().lock();
        try{
            for(Mountain m : newMountainsList){
                if(mountains.contains(m)){
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Mountain already exists");
                }else{
                    m.setId(mountains.size() + m.getAltitude() + 1);
                    mountains.add(m);
                }
            }
        }finally{
            lock.writeLock().unlock();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Mountains added successfully");
    }

    // GET Methods ------------------------------------------------------------------------------------------------

    @GetMapping("/mountains")
    public ResponseEntity<?> handleRequest(@RequestParam(name = "country", required = false) String country,
                                           @RequestParam(name = "range", required = false) String range,
                                           @RequestParam(name = "hemisphere", required = false) String hemisphere,
                                           @RequestParam(name = "altitude", required = false) Integer altitude,
                                           @RequestParam(name = "name", required = false) String name){
        lock.readLock().lock();
        try{
            if(country != null && range == null){
                return getMountainsByCountry(country);
            }else if(range != null && country != null){
                return getMountainsByCountryAndRange(country, range);
            }else if(hemisphere != null){
                return getMountainsByHemisphere(hemisphere);
            }else if(altitude != null && country != null){
                return getMountainsByCountryAltitude(country, altitude);
            }else if(name != null){
                return getMountainByName(name);
            }else{
                return getAllMountains();
            }
        }finally{
            lock.readLock().unlock();
        }
    }

    @GetMapping("/mountains/{id}")
    public ResponseEntity<?> getMountainByID(@PathVariable int id){
        for(Mountain m : mountains){
            if(m.getId() == id){
                return ResponseEntity.ok(m);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mountain with ID " + id + " not found");
    }

    // Helper Methods ---------------------------------------------------------------------------------------------

    private ResponseEntity<?> getAllMountains(){
        if(mountains.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("There are no mountains currently stored.");
        }else{
            System.out.println("Mountains found");
            return ResponseEntity.ok(mountains);
        }
    }

    private ResponseEntity<?> getMountainsByCountry(String country){
        ArrayList<Mountain> countryMountains = new ArrayList<Mountain>();
        for(Mountain m : mountains){
            if(m.getCountry().equals(country)){
                countryMountains.add(m);
            }
        }
        if(countryMountains.isEmpty()){
            String response = "No mountains found in " + country;
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }else{
            return ResponseEntity.ok(countryMountains);
        }
    }

    private ResponseEntity<?> getMountainsByCountryAndRange(String country, String range){
        ArrayList<Mountain> countryRangeMountains = new ArrayList<Mountain>();
        for(Mountain m : mountains){
            if(m.getCountry().equals(country) && m.getRange().equals(range)){
                countryRangeMountains.add(m);
            }
        }
        if(countryRangeMountains.isEmpty()){
            String response = "No mountains found in " + country + " in the " + range + " range";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }else{
            return ResponseEntity.ok(countryRangeMountains);
        }
    }

    private ResponseEntity<?> getMountainsByHemisphere(String hemisphere){
        ArrayList<Mountain> hemisphereMountains = new ArrayList<Mountain>();
        for(Mountain m : mountains){
            if(hemisphere == "northern" && m.getIsNorthern()){
                hemisphereMountains.add(m);
            }else if(hemisphere == "southern" && !m.getIsNorthern()){
                hemisphereMountains.add(m);
            }
        }
        if(hemisphereMountains.isEmpty()){
            String response = "No mountains found in the " + hemisphere + " hemisphere";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }else{
            return ResponseEntity.ok(hemisphereMountains);
        }
    }

    private ResponseEntity<?> getMountainsByCountryAltitude(String country, int altitude){
        ArrayList<Mountain> countryAltitudeMountains = new ArrayList<Mountain>();
        for(Mountain m : mountains){
            if(m.getCountry().equals(country) && m.getAltitude() == altitude){
                countryAltitudeMountains.add(m);
            }
        }
        if(countryAltitudeMountains.isEmpty()){
            String response = "No mountains found in " + country + " at an altitude of " + altitude + "m";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }else{
            return ResponseEntity.ok(countryAltitudeMountains);
        }
    }

    private ResponseEntity<?> getMountainByName(String name){
        for(Mountain m : mountains){
            if(m.getName().equals(name)){
                return ResponseEntity.ok(m);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mountain with name " + name + " not found");
    }
}

/*
 * getAll - no data required
 * getByCountry - path param? ?country={country}
 * getByCountryAndRange - url? /range/{range}/country/{country}
 * getByHemisphere - query param? ?hemisphere={hemisphere}
 * getByAltitude - query param? ?altitude={altitude}
 * getByID - query param? ?id={id}
 * getByName - query param? ?name={name}
*/