package Darius;
import Darius.repository.hibernate.ChildHibernateRepository;
import Darius.repository.hibernate.EmployeeHibernateRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import Darius.repository.*;
import Darius.repository.database.*;
import Darius.service.*;
import Darius.utils.AbstractServer;
import Darius.utils.RpcConcurrentServer;

import java.io.File;
import java.io.IOException;
import java.rmi.ServerException;
import java.util.Properties;

public class StartRpcServer {
    private static int defaultPort = 5555;
    private static Logger logger = LogManager.getLogger(StartRpcServer.class);

    public static void main(String[] args){
        Properties serverProps = new Properties();
        try{
            serverProps.load(StartRpcServer.class.getResourceAsStream("/server.properties"));
            logger.info("Server properties set {}", serverProps);
        } catch (IOException e) {
            logger.error("Nu se gasesc server.properties " + e);
            logger.debug("Cautam fisierul in " + (new File(".")).getAbsolutePath());
            return;
        }

        IEmployeeRepository employeeRepository = new EmployeeHibernateRepository();
        IEmployeeService employeeService = new EmployeeService(employeeRepository);
        IService service = new Service();
        service.setEmployeeService(employeeService);

        IAgeGroupRepository ageGroupRepository = new AgeGroupDatabaseRepository(serverProps);
        IAgeGroupService ageGroupService = new AgeGroupService(ageGroupRepository);
        service.setAgeGroupService(ageGroupService);

        IEventRepository eventRepository = new EventDatabaseRepository(serverProps);
        IEventService eventService = new EventService(eventRepository);
        service.setEventService(eventService);

        IRegistrationRepository registrationRepository = new RegistrationDatabaseRepository(serverProps);
        IRegistrationService registrationService = new RegistrationService(registrationRepository);
        service.setRegistrationService(registrationService);

        IChildRepository childRepository = new ChildHibernateRepository();
        IChildService childService = new ChildService(childRepository);
        service.setChildService(childService);


        int serverPort = defaultPort;
        try{
            serverPort = Integer.parseInt(serverProps.getProperty("server.port"));
        }catch (NumberFormatException nef){
            logger.error("Port gresit" + nef.getMessage());
            logger.debug("Folosim default port " + defaultPort);
        }
        logger.debug("Pornim serverul pe port-ul: " + serverPort);
        AbstractServer server = new RpcConcurrentServer(serverPort, service);
        try{
            server.start();
        } catch (ServerException e) {
            logger.error("Error starting the server" + e.getMessage());
        }finally {
            try{
                server.stop();
            } catch (ServerException e) {
                logger.error("Error stopping server " + e.getMessage());
            }
        }
    }
}
