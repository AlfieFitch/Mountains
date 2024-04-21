package cs253.mountain;

import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.MediaType;
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

    @PostMapping("/")
    public ResponseEntity<String> saveNew(@RequestBody String newMountains){
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
                    mountains.add(m);
                }
            }
        }finally{
            lock.writeLock().unlock();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Mountains added successfully");
    }

    // GET Methods ------------------------------------------------------------------------------------------------

    @GetMapping(value = {"/", "/id/{id}", "/country/{country}", "/country/{country}/range/{range}", "/country/{country}/range/{range}/name/{name}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleRequest(@PathVariable(name = "country", required = false) String country,
                                           @PathVariable(name = "range", required = false) String range,
                                           @PathVariable(name = "name", required = false) String name,
                                           @PathVariable(name = "id", required = false) Integer id,
                                           @RequestParam(name = "hemisphere", required = false) String hemisphere,
                                           @RequestParam(name = "altitude", required = false) Integer altitude){
        lock.readLock().lock();
        try{
            System.out.println("Country: " + country + " Range: " + range + " Name: " + name + " ID: " + id + " Hemisphere: " + hemisphere + " Altitude: " + altitude);
            if(country != null && range == null && altitude == null){
                return getMountainsByCountry(country);
            }else if(range != null){
                return getMountainsByCountryAndRange(country, range);
            }else if(hemisphere != null){
                return getMountainsByHemisphere(hemisphere);
            }else if(altitude != null && country != null){
                return getMountainsByCountryAltitude(country, altitude);
            }else if(name != null){
                return getMountainByName(range, country, name);
            }else if(id != null){
                return getMountainByID(id);
            }else{
                return getAllMountains();
            }
        }finally{
            lock.readLock().unlock();
        }
    }

    // Helper Methods ---------------------------------------------------------------------------------------------

    private ResponseEntity<?> getMountainByID(int id){
        for(Mountain m : mountains){
            if(m.getId() == id){
                return ResponseEntity.ok(m);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mountain with ID " + id + " not found");
    }

    private ResponseEntity<?> getAllMountains(){
        if(mountains.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(mountains);
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(countryMountains);
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
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(countryRangeMountains);
        }else{
            return ResponseEntity.ok(countryRangeMountains);
        }
    }

    private ResponseEntity<?> getMountainsByHemisphere(String hemisphere){
        ArrayList<Mountain> hemisphereMountains = new ArrayList<Mountain>();
        for(Mountain m : mountains){
            if(hemisphere.equals("northern") && m.getIsNorthern()){
                hemisphereMountains.add(m);
            }else if(hemisphere.equals("southern") && !m.getIsNorthern()){
                hemisphereMountains.add(m);
            }
        }
        System.out.println(hemisphereMountains);
        if(hemisphereMountains.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(hemisphereMountains);
        }else{
            return ResponseEntity.ok(hemisphereMountains);
        }
    }

    private ResponseEntity<?> getMountainsByCountryAltitude(String country, int altitude){
        System.out.println("Country: " + country + " Altitude: " + altitude);
        ArrayList<Mountain> countryAltitudeMountains = new ArrayList<Mountain>();
        for(Mountain m : mountains){
            if(m.getCountry().equals(country) && m.getAltitude() > altitude){
                countryAltitudeMountains.add(m);
            }
        }
        if(countryAltitudeMountains.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(countryAltitudeMountains);
        }else{
            return ResponseEntity.ok(countryAltitudeMountains);
        }
    }

    private ResponseEntity<?> getMountainByName( String range, String country, String name){
        for(Mountain m : mountains){
            if(m.getName().equals(name) && m.getRange().equals(range) && m.getCountry().equals(country)){
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
 * 
 * 
 * altitude name and id as query, 
 * Hemisphere, range, country as path param
*/