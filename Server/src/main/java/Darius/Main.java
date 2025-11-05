package Darius;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Darius.controller.LoginController;
import Darius.repository.*;
import Darius.repository.database.*;
import Darius.service.*;

import java.io.IOException;
import java.util.Properties;

public class Main extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Properties props=new Properties();
        try {
            props.load(Main.class.getResourceAsStream("/server.properties"));
        } catch (IOException e) {
            System.out.println("Cannot find server.properties "+e);
        }

        IEmployeeRepository employeeRepository = new EmployeeDatabaseRepository(props);
        IEmployeeService employeeService = new EmployeeService(employeeRepository);
        IService service = new Service();
        service.setEmployeeService(employeeService);

        IAgeGroupRepository ageGroupRepository = new AgeGroupDatabaseRepository(props);
        IAgeGroupService ageGroupService = new AgeGroupService(ageGroupRepository);
        service.setAgeGroupService(ageGroupService);

        IEventRepository eventRepository = new EventDatabaseRepository(props);
        IEventService eventService = new EventService(eventRepository);
        service.setEventService(eventService);

        IRegistrationRepository registrationRepository = new RegistrationDatabaseRepository(props);
        IRegistrationService registrationService = new RegistrationService(registrationRepository);
        service.setRegistrationService(registrationService);

        IChildRepository childRepository = new ChildDatabaseRepository(props);
        IChildService childService = new ChildService(childRepository);
        service.setChildService(childService);


        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        LoginController loginController = fxmlLoader.getController();
        loginController.setService(service);

        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}