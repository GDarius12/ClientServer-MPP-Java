package Darius;

import Darius.domain.Sprint;
import org.springframework.web.client.RestClientException;

public class SprintTest {
    private final static SprintClient sprintClient = new SprintClient();
    public static void main(String[] args){
        Sprint sprint = new Sprint(2000f);
        try{
            System.out.println("Adaugam un sprint nou " + sprint);
            var addedSprint = sprintClient.create(sprint);
            sprint.setId(addedSprint.getId());
            show(()-> System.out.println(addedSprint));
            System.out.println("\nSe afiseaza toate sprinturile ...");
            show(()->{
                Sprint[] sprints = sprintClient.getAll();
                for (var s : sprints){
                    System.out.println(s.getId() + ": " + s.getDistance());
                }
            });
        }catch (RestClientException ex){
            System.out.println("Exception..." + ex.getMessage());
        }

        System.out.println("\nInformatii pentru sprintul cu id-ul 8");
        show(()-> System.out.println(sprintClient.getById(8)));

        System.out.println("\nSe face update la Sprint-ul cu id=" + sprint.getId());
        Sprint updateSprint = new Sprint(2001f);
        show(()-> System.out.println(sprintClient.update(sprint.getId(), updateSprint)));

        System.out.println("\nSe sterge Sprint-ul cu id=" + sprint.getId());
        show(()->sprintClient.delete(sprint.getId()));
    }

    private static void show(Runnable task) {
        try{
            task.run();
        }catch (ServiceException e){
            System.out.println("Service exception " + e);
        }
    }
}
