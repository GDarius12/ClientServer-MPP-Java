package Darius.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import Darius.ServiceException;
import Darius.domain.Employee;
import Darius.IService;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class LoginController {
    private IService service;
    private Optional<Employee> employee;
    @FXML
    TextField usernameTextField;
    @FXML
    TextField passwordTextField;
    @FXML
    Label usernameErrorLabel;
    @FXML
    Label passwordErrorLabel;

    public void setService(IService service){
        this.service = service;
    }

    private boolean validateTextFieldInput(){
        usernameErrorLabel.setVisible(false);
        passwordErrorLabel.setVisible(false);

        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        var findEmployee = service.findOneEmployeeByUsername(username);
        if (findEmployee.isEmpty()){
            usernameErrorLabel.setText("Username gresit!");
            usernameErrorLabel.setVisible(true);

            usernameTextField.clear();
            passwordTextField.clear();

            return false;
        }
        else{
            if (!Objects.equals(findEmployee.get().getPassword(), password)){
                passwordErrorLabel.setText("Parola gresita!");
                passwordErrorLabel.setVisible(true);

                passwordTextField.clear();

                return false;
            }
        }
        return true;
    }

    @FXML
    private void loginButtonHandler(ActionEvent actionEvent) throws IOException{
        usernameErrorLabel.setVisible(false);
        passwordErrorLabel.setVisible(false);

        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
            Pane pane = fxmlLoader.load();
            MainController mainController = fxmlLoader.getController();
            employee = service.login(username, password, mainController);
            if (employee.isPresent()) {
                Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

                stage.setScene(new Scene(pane));

                mainController.setService(service);
                mainController.setEmployee(employee.get());

                stage.setTitle("Main");
                stage.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Username-ul sau Parola sunt gresite!");
                alert.setHeaderText(null);
                alert.show();
            }
        } catch (ServiceException se){
            Alert alert = new Alert(Alert.AlertType.ERROR, se.getMessage());
            alert.setHeaderText(null);
            alert.show();
        }
    }
}
