package Darius;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Darius.controller.LoginController;
import Darius.rpcprotocol.ServiceRpcProxy;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class StartRpcClient extends Application {
    private static int defaultPort = 55556;
    private static String defaultServer = "localhost";

    private static Logger logger = LogManager.getLogger(StartRpcClient.class);

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        logger.debug("In start");
        Properties clientProps = new Properties();
        try{
            clientProps.load(StartRpcClient.class.getResourceAsStream("/client.properties"));
            logger.info("Proprietatile clientului au fost setate {} ", clientProps);
        } catch (IOException e){
            logger.error("Nu se poate gasi client.properties " + e);
            logger.debug("Cautam in folder {}", (new File(".")).getAbsolutePath());
            return;
        }

        String serverIP = clientProps.getProperty("server.host", defaultServer);
        int serverPort = defaultPort;

        try{
            serverPort = Integer.parseInt(clientProps.getProperty("server.port"));
        } catch (NumberFormatException ex){
            logger.error("Port gresit " + ex.getMessage());
            logger.debug("Folosim Port-ul default: " + defaultPort);
        }

        logger.info("Folosim Server IP " + serverIP);
        logger.info("Folosim Server Port " + serverPort);

        //IService server = new ServiceRpcProxy(serverIP, serverPort);
        IService server = new ServiceRpcProxy(serverIP, serverPort);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        LoginController loginController = fxmlLoader.getController();
        loginController.setService(server);

        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}
