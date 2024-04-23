package cs253.mountain;

import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.http.MediaType;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.concurrent.locks.ReentrantReadWriteLock;  

/**
 * Mountain Resource class that handle the REST API requests.
 * @version 1.0
 * @Author: Alfie Fitch-May
 */


@RestController
public class MountainResource{

    // Variable Initialization --------------------------------------------------------------------------------------

    private ArrayList<Mountain> mountains = new ArrayList<Mountain>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    // POST Methods -----------------------------------------------------------------------------------------------q

    /**
     * POST method that adds new mountains to the list, only if all mountains in the list are new.
     * @param newMountains - JSON String containng the new mountains data.
     * @return ResponseEntity<String> - Response message.
     */
    @PostMapping("/")
    public ResponseEntity<String> saveNew(@RequestBody String newMountains){

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
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Mountain " + m.getName() + " already exists");
                }else if(m.getAltitude() <= 0){
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Altitude of " + m.getName() + " must be greater than 0");
                }else{
                    mountains.add(m);
                }
            }
        }finally{
            lock.writeLock().unlock();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Mountains added successfully");
    }

    // PUT Methods ------------------------------------------------------------------------------------------------

    /**
     * PUT method that updates the data of a mountain with a specific ID.
     * @param id - ID of the mountain to be updated.
     * @param updatedMountain - JSON String containing the updated mountain data.
     * @return ResponseEntity<?> - Response message.
     */
    @PutMapping("/id/{id}")
    public ResponseEntity<?> updateMountain(@PathVariable(name = "id") int id, @RequestBody String updatedMountain){
        Mountain newMountainData = new Mountain();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            newMountainData = objectMapper.readValue(updatedMountain, Mountain.class);            
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing JSON");
        }

        if(mountains.stream().noneMatch(m -> m.getId() == id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mountain with ID " + id + " not found");
        }else{
            if(newMountainData.getAltitude() <= 0){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Altitude of " + newMountainData.getName() + " must be greater than 0");
            }else{
                lock.writeLock().lock();
                try{
                    int index = mountains.indexOf(mountains.stream().filter(m -> m.getId() == id).findFirst().get());
                    newMountainData.setId(id);
                    mountains.set(index, newMountainData);

                }finally{
                    lock.writeLock().unlock();
                }
                return ResponseEntity.status(HttpStatus.OK).body(mountains);
            }
        }
    }

    // DELETE Methods ---------------------------------------------------------------------------------------------

    /**
     * DELETE method that deletes a mountain with a specific ID.
     * @param id - ID of the mountain to be deleted.
     * @return ResponseEntity<?> - Response message.
     */
    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> deleteMountain(@PathVariable(name = "id") int id){
        lock.writeLock().lock();
        try{
            Mountain mountainToDelete = mountains.stream().filter(m -> m.getId() == id).findFirst().orElse(null);
            if(mountainToDelete != null){
                mountains.remove(mountainToDelete);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mountain with ID " + id + " not found");
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    // GET Methods ------------------------------------------------------------------------------------------------

    /**
     * GET method that returns a list of mountains based on the parameters provided.
     * @param country - Country of the mountain.
     * @param range - Range of the mountain.
     * @param name - Name of the mountain.
     * @param id - ID of the mountain.
     * @param hemisphere - Hemisphere of the mountain.
     * @param altitude - Altitude of the mountain.
     * @return ResponseEntity<?> - Response message.
     */
    @GetMapping(value = {"/", "/id/{id}", "/country/{country}", "/country/{country}/range/{range}", "/country/{country}/range/{range}/name/{name}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> handleRequest(@PathVariable(name = "country", required = false) String country,
                                           @PathVariable(name = "range", required = false) String range,
                                           @PathVariable(name = "name", required = false) String name,
                                           @PathVariable(name = "id", required = false) Integer id,
                                           @RequestParam(name = "hemisphere", required = false) String hemisphere,
                                           @RequestParam(name = "altitude", required = false) Integer altitude){
        lock.readLock().lock();
        try{
            if(name != null){
                return getMountainByName(range, country, name);
            }else if(range != null){
                return getMountainsByCountryAndRange(country, range);
            }else if(hemisphere != null){
                return getMountainsByHemisphere(hemisphere);
            }else if(altitude != null && country != null){
                return getMountainsByCountryAltitude(country, altitude);
            }else if(country != null){
                return getMountainsByCountry(country);
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

    /**
     * Helper method that returns a mountain with a specific ID.
     * @param id - ID of the mountain.
     * @return ResponseEntity<?> - Mountain Data.
     */
    private ResponseEntity<?> getMountainByID(int id){
        List<Mountain> filteredList = mountains.stream().filter(m -> m.getId() == id).toList();
        if(filteredList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(filteredList);
        }else{
            return ResponseEntity.ok(filteredList);
        }
    }

    /**
     * Helper method that returns all mountains in the list.
     * @return ResponseEntity<?> - List of all mountains.
     */
    private ResponseEntity<?> getAllMountains(){
        if(mountains.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }else{
            return ResponseEntity.ok(mountains);
        }
    }

    /**
     * Helper method that returns a list of mountains based on the country provided.
     * @param country - Country of the mountain.
     * @return ResponseEntity<?> - List of mountains.
     */
    private ResponseEntity<?> getMountainsByCountry(String country){
        List<Mountain> filteredList = mountains.stream().filter(m -> m.getCountry().equals(country)).toList();
        if(filteredList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }else{
            return ResponseEntity.ok(filteredList);
        }   
    }

    /**
     * Helper method that returns a list of mountains based on the country and range provided.
     * @param country - Country of the mountain.
     * @param range - Range of the mountain.
     * @return ResponseEntity<?> - List of mountains.
     */
    private ResponseEntity<?> getMountainsByCountryAndRange(String country, String range){
        List<Mountain> filteredList = mountains.stream().filter(m -> m.getCountry().equals(country) && m.getRange().equals(range)).toList();
        if(filteredList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }else{
            return ResponseEntity.ok(filteredList);
        }
    }

    /**
     * Helper method that returns a list of mountains based on the hemisphere provided.
     * @param hemisphere - Hemisphere of the mountain.
     * @return ResponseEntity<?> - List of mountains.
     */
    private ResponseEntity<?> getMountainsByHemisphere(String hemisphere){
        List<Mountain> filteredList;
        if(hemisphere.equals("north")){
            filteredList = mountains.stream().filter(m -> m.getIsNorthern()).toList();
        }else{
            filteredList = mountains.stream().filter(m -> !m.getIsNorthern()).toList();
        }
        if(filteredList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }else{
            return ResponseEntity.ok(filteredList);
        }
    }

    /**
     * Helper method that returns a list of mountains based on the country and altitude provided.
     * @param country - Country of the mountain.
     * @param altitude - Altitude of the mountain.
     * @return ResponseEntity<?> - List of mountains.
     */
    private ResponseEntity<?> getMountainsByCountryAltitude(String country, int altitude){
        List<Mountain> filteredList = mountains.stream().filter(m -> m.getCountry().equals(country) && m.getAltitude() > altitude).toList();
        if(filteredList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }else{
            return ResponseEntity.ok(filteredList);
        }
    }

    /**
     * Helper method that returns a mountain with a specific name.
     * @param range - Range of the mountain.
     * @param country - Country of the mountain.
     * @param name - Name of the mountain.
     * @return ResponseEntity<?> - Mountain Data.
     */
    private ResponseEntity<?> getMountainByName( String range, String country, String name){
        List<Mountain> filteredList = mountains.stream().filter(m -> m.getCountry().equals(country) && m.getRange().equals(range) && m.getName().equals(name)).toList();
        if(filteredList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(filteredList);
        }else{
            return ResponseEntity.ok(filteredList);
        }
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