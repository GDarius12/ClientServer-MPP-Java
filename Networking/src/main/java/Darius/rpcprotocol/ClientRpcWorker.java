package Darius.rpcprotocol;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Darius.IObserver;
import Darius.IService;
import Darius.domain.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientRpcWorker implements Runnable, IObserver {
    private IService server;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;
    private static Logger logger = LogManager.getLogger(ClientRpcWorker.class);

    public ClientRpcWorker(IService server, Socket connection){
        this.server = server;
        this.connection = connection;

        try{
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            connected=true;
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
    }

    @Override
    public void run() {
        while (connected){
            try {
                Object request = input.readObject();
                logger.debug("Received request from client: " + request);
                Response response = handleRequest((Request) request);
                if (response != null){
                    sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.error(e);
                logger.error(e.getStackTrace());
            }
            try{
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error(e);
                logger.error(e.getStackTrace());
            }
        }
        try{
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            logger.error("Error " + e);
        }
    }

    private static Response okResponse = new Response.Builder().type(ResponseType.OK).build();

    private Response handleRequest(Request request){
        Response response = null;
        if (request.type() == RequestType.LOGIN){
            logger.debug("Login request..." + request.type());
            Employee employee = (Employee)request.data();
            try{
                var empl = server.login(employee.getUsername(), employee.getPassword(), this).get();
                return new Response.Builder().type(ResponseType.LOGGED).data(empl).build();
            } catch (Exception e){
                connected = false;
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type() == RequestType.GET_AGE_GROUPS){
            logger.debug("Get age groups..." + request.type());
            try{
                var ageGroups = server.findAllAgeGroup();
                return new Response.Builder().type(ResponseType.GET_AGE_GROUPS).data(ageGroups).build();
            } catch (Exception e){
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type() == RequestType.GET_EVENTS){
            logger.debug("Get age groups..." + request.type());
            Integer id = (Integer)request.data();
            try{
                var ageGroups = server.findAllByAgeGroupIdEvent(id);
                return new Response.Builder().type(ResponseType.GET_EVENTS).data(ageGroups).build();
            } catch (Exception e){
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type() == RequestType.GET_CHILDREN){
            logger.debug("Get age children..." + request.type());
            List<Object> data = (List<Object>)request.data();

            Sprint sprint = (Sprint) data.get(0);
            AgeGroup ageGroup = (AgeGroup) data.get(1);

            try{
                var registration = server.findAllRegistrationsBySprintAndGroupAge(sprint, ageGroup);
                return new Response.Builder().type(ResponseType.GET_CHILDREN).data(registration).build();
            } catch (Exception e){
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type() == RequestType.GET_REGISTRATION_BY_PERSON){
            logger.debug("Get registration by person");
            Person person = (Person) request.data();

            try{
                var registration = server.findOneRegistrationByPerson(person);
                return new Response.Builder().type(ResponseType.GET_REGISTRATION_BY_PERSON).data(registration.get()).build();
            }catch (Exception e){
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type() == RequestType.ADD_CHILDREN){
            logger.debug("Save child");
            Child child = (Child) request.data();

            try{
                server.saveChild(child);
                return  okResponse;
            }catch (Exception e){
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type() == RequestType.ADD_REGISTER){
            logger.debug("Save registration");
            Registration registration = (Registration) request.data();

            try{
                server.saveRegistration(registration);
                return  okResponse;
            }catch (Exception e){
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type() == RequestType.GET_ONE_AGE_GROUP_BY_AGE){
            logger.debug("Get one age group age");
            int age = (Integer) request.data();

            try{
                var ageGroup = server.findOneByAgeAgeGroup(age);
                return new Response.Builder().type(ResponseType.GET_ONE_AGE_GROUP).data(ageGroup.get()).build();
            }catch (Exception e){
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type() == RequestType.GET_CHILD_BY_CNP){
            logger.debug("Get child by cnp");
            String cnp = (String) request.data();

            try{
                var child = server.findOneByCnpChild(cnp);
                return new Response.Builder().type(ResponseType.GET_CHILD_BY_CNP).data(child.get()).build();
            }catch (Exception e){
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type() == RequestType.LOGOUT){
            logger.debug("Logout");
            try{
                var empl = (Employee)request.data();
                server.logout(empl);
            }catch (Exception e){
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
            connected = false;
            return okResponse;
        }
        return response;
    }

    private void sendResponse(Response response) throws IOException {
        logger.debug("Sending response: " + response);
        synchronized (output){
            output.writeObject(response);
            output.flush();
        }
    }

    @Override
    public void registrationAdded(Registration registration) {
        Response response = new Response.Builder().type(ResponseType.REGISTRATION_SAVED).data(registration).build();
        logger.debug("Registration added!");
        try{
            sendResponse(response);
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
    }
}
