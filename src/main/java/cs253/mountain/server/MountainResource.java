package cs253.mountain.server;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.ArrayList;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
public class MountainResource{
    private ArrayList<Mountain> mountains = new ArrayList<Mountain>();
    
    
    @PostMapping("/")
    public ResponseEntity<String> saveNew(@RequestBody ArrayList<Mountain> newMountains){
        for(Mountain m : newMountains){
            if(mountains.contains(m)){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Mountain already exists");
            }else{
                mountains.add(m);
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("Mountains added successfully");
    }

    @GetMapping("/")
    public ResponseEntity<ArrayList<Mountain>> getMountains(){
        return ResponseEntity.status(HttpStatus.OK).body(mountains);
    }
}