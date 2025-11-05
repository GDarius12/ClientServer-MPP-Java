package Darius.rpcprotocol;

import Darius.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Darius.domain.*;

public class ServiceRpcProxy implements IService {
    private IEmployeeService employeeService;
    private IEventService eventService;
    private IAgeGroupService ageGroupService;
    private IRegistrationService registrationService;
    private IChildService childService;

    private IObserver client;

    protected String host;
    protected int port;

    protected ObjectInputStream input;
    protected ObjectOutputStream output;
    protected Socket connection;

    protected BlockingQueue<Response> qResponses;
    protected volatile boolean finished;

    protected static Logger logger = LogManager.getLogger(String.valueOf(ServiceRpcProxy.class));

    public ServiceRpcProxy(String host, int port){
        this.host = host;
        this.port = port;
        qResponses = new LinkedBlockingQueue<>();
    }

    protected Response readResponse(){
        Response response = null;
        try{
            response = qResponses.take();
        } catch (InterruptedException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
        return response;
    }

    protected void closeConnection(){
        logger.debug("Closing connection");
        finished = true;
        try{
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            logger.error(e);
            logger.error(e.getStackTrace());
        }
    }

    protected void sendRequest(Request request){
        logger.debug("Sending request {}", request);
        try{
            output.writeObject(request);
        } catch (IOException e) {
            throw new ServiceException("Error sending object " + e);
        }
    }

    protected void initializeConnection(){
        try{
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (IOException e) {
            logger.error("Error initializing connection "+e);
            logger.error(e.getStackTrace());
        }
    }

    private void startReader(){
        Thread thread = new Thread(new ReaderThread());
        thread.start();
    }

    @Override
    public void setEmployeeService(IEmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public void setEventService(IEventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public void setAgeGroupService(IAgeGroupService ageGroupService) {
        this.ageGroupService = ageGroupService;
    }

    @Override
    public void setRegistrationService(IRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Override
    public void setChildService(IChildService childService) {
        this.childService = childService;
    }

    @Override
    public Iterable<AgeGroup> findAllAgeGroup() {
        Request request = new Request.Builder().type(RequestType.GET_AGE_GROUPS).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR){
            String error = response.data().toString();
            closeConnection();
            throw new ServiceException(error);
        }
        return (Iterable<AgeGroup>)response.data();
    }

    @Override
    public Optional<AgeGroup> findOneByAgeAgeGroup(int age) {
        Request request = new Request.Builder().type(RequestType.GET_ONE_AGE_GROUP_BY_AGE).data(age).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR){
            String error = response.data().toString();
            throw new ServiceException(error);
        }
        if (response.type() == ResponseType.GET_ONE_AGE_GROUP){
            return Optional.ofNullable((AgeGroup)response.data());
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Event> findAllEvent() {
        return null;
    }

    @Override
    public Optional<Event> findOneEvent(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<Event> findOneBySprintIdEvent(int id) {
        return Optional.empty();
    }

    @Override
    public Iterable<Event> findAllByAgeGroupIdEvent(int id) {
        Request request = new Request.Builder().type(RequestType.GET_EVENTS).data(id).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR){
            String error = response.data().toString();
            throw new ServiceException(error);
        }
        return (Iterable<Event>)response.data();
    }

    @Override
    public void saveChild(Child child) {
        Request request = new Request.Builder().type(RequestType.ADD_CHILDREN).data(child).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR){
            String error = response.data().toString();
            throw new ServiceException(error);
        }
    }

    @Override
    public Optional<Child> findOneByCnpChild(String cnp) {
        Request request = new Request.Builder().type(RequestType.GET_CHILD_BY_CNP).data(cnp).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR){
            String error = response.data().toString();
            throw new ServiceException(error);
        }
        if (response.type() == ResponseType.GET_CHILD_BY_CNP){
            return Optional.ofNullable((Child) response.data());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Employee> findOneEmployeeByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<Employee> login(String username, String password, IObserver client) {
        initializeConnection();
        Employee employee = new Employee("ds", "sdsd", "1234567891234", "1234567890", "df", username, password);
        Request request = new Request.Builder().type(RequestType.LOGIN).data(employee).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.LOGGED){
            this.client = client;
            return Optional.ofNullable((Employee)response.data());
        }
        if (response.type() == ResponseType.ERROR){
            String error = response.data().toString();
            throw new ServiceException(error);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Registration> findAllRegistrationsBySprintAndGroupAge(Sprint sprint, AgeGroup ageGroup) {
        Request request = new Request.Builder().type(RequestType.GET_CHILDREN).data(Arrays.asList(sprint, ageGroup)).build();
        sendRequest(request);
        Response response = readResponse();
        Iterable<Registration> children = null;
        if (response.type() == ResponseType.GET_CHILDREN){
            children = (Iterable<Registration>)response.data();
        }
        if (response.type() == ResponseType.ERROR){
            String error = response.data().toString();
            closeConnection();
            throw new ServiceException(error);
        }
        return children;
    }

    @Override
    public Optional<Registration> findOneRegistrationByPerson(Person person) {
        Request request = new Request.Builder().type(RequestType.GET_REGISTRATION_BY_PERSON).data(person).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR){
            String error = response.data().toString();
            closeConnection();
            throw new ServiceException(error);
        }
        return Optional.ofNullable((Registration)response.data());
    }

    @Override
    public Optional<Registration> findOneRegistration(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<Registration> findOneRegistrationByEvent(Event event) {
        return Optional.empty();
    }

    @Override
    public void saveRegistration(Registration registration) {
        Request request = new Request.Builder().type(RequestType.ADD_REGISTER).data(registration).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.ERROR){
            String error = response.data().toString();
            throw new ServiceException(error);
        }
    }

    @Override
    public boolean logout(Employee employee) {
        Request request = new Request.Builder().type(RequestType.LOGOUT).data(employee).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.OK)
            closeConnection();
        if (response.type() == ResponseType.ERROR){
            String error = response.data().toString();
            throw new ServiceException(error);
        }
        return true;
    }

    private boolean isUpdate(Response response){
        return response.type() == ResponseType.REGISTRATION_SAVED;
    }

    private void handleUpdate(Response response){
        if (response.type() == ResponseType.REGISTRATION_SAVED){
            Registration registration = (Registration) response.data();
            try{
                client.registrationAdded(registration);
            }catch (Exception e){
                logger.error(e);
                logger.error(e.getStackTrace());
            }
        }
    }

    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    logger.debug("Response received " + response);

                    if (!(response instanceof Response)) {
                        logger.warn("Invalid object received: " + response);
                        continue;
                    }

                    Response resp = (Response) response;

                    if (isUpdate(resp)) {
                        handleUpdate(resp); // procesăm notificarea separat
                    } else {
                        try {
                            qResponses.put(resp); // doar răspunsurile normale intră în coadă
                        } catch (InterruptedException e) {
                            logger.error("Interrupted while putting response in queue", e);
                            Thread.currentThread().interrupt();
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    logger.error("Error reading object from input", e);
                    finished = true;
                }
            }
        }
    }

}
