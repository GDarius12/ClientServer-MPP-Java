package Darius;

import Darius.domain.Sprint;
import Darius.repository.ISprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("org/sprints")
public class SprintController {
    @Autowired
    private ISprintRepository sprintRepository;

    @RequestMapping(method = RequestMethod.POST)
    public Sprint create(@RequestBody Sprint sprint){
            return sprintRepository.SprintSave(sprint);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Sprint update(@RequestBody Sprint sprint, @PathVariable String id){
        System.out.println("Se face update la un sprint ");
        sprint.setId(Integer.parseInt(id));
        sprintRepository.update(sprint);
        return sprint;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable String id){
        System.out.println("Stergem un sprint " + id);
        try{
            sprintRepository.delete(Integer.parseInt(id));
            return new ResponseEntity<Sprint>(HttpStatus.OK);
        }catch (Exception ex){
            System.out.println("Ctrl Delete sprint exception");
            return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Sprint> getAll(){
        System.out.println("GetAll sprints ");
        return sprintRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getById(@PathVariable String id){
        System.out.println("Functia Get dupa id " + id);
        var sprint = sprintRepository.findOne(Integer.parseInt(id));
        if (sprint.isEmpty()){
            return new ResponseEntity<String>("Sprint not found", HttpStatus.NOT_FOUND);
        }
        else{
            return new ResponseEntity<Sprint>(sprint.get(), HttpStatus.OK);
        }
    }
}
