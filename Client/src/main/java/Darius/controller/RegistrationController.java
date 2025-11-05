package Darius.controller;

import Darius.ServiceException;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import Darius.domain.Event;
import Darius.domain.Child;
import Darius.domain.Employee;
import Darius.domain.Registration;
import Darius.IService;
import Darius.utils.Calculeaza_Varsta;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class RegistrationController {
    private IService service;

    private ObservableList<Event> eventModel = FXCollections.observableArrayList();

    private Employee employee;


    @FXML
    private TextField cnpTextField;
    @FXML
    private TextField surnameTextField;
    @FXML
    private TextField nameTextField;

    @FXML
    private Label cnpErrorLabel;
    @FXML
    private Label surnameErrorLabel;
    @FXML
    private Label nameErrorLabel;
    @FXML
    private Label eventsLabel;

    @FXML
    private ListView<Event> eventListView;

    @FXML
    private Button registerButton;

    @FXML
    private void initialize(){
        eventListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        eventListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Event>) change -> {
            if (eventListView.getSelectionModel().getSelectedItems().size() > 2) {
                Event removedItem = eventListView.getSelectionModel().getSelectedItems().get(2);
                eventListView.getSelectionModel().clearSelection(
                        eventModel.indexOf(removedItem)
                );
            }
        });
    }

    public void setService(IService service){
        this.service = service;
    }

    public void setEmployee(Employee employee){
        this.employee = employee;
    }

    private void initEventsListView(){
        eventModel.clear();
        String cnp = cnpTextField.getText();
        if (!cnp.isEmpty()) {
            int age = Calculeaza_Varsta.Varsta_CNP(cnp);

            try {
                var ageGroup = service.findOneByAgeAgeGroup(age);
                if (ageGroup.isPresent()) {
                    var events = service.findAllByAgeGroupIdEvent(ageGroup.get().getId());
                    events.forEach(eventModel::add);
                } else {
                    showError("Nu există grupă de vârstă pentru vârsta " + age);
                }
            } catch (ServiceException e) {
                showError("Eroare la obținerea grupei de vârstă: " + e.getMessage());
            }
        }
    }
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Eroare");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void setEventListViewCellFactory() {
        eventListView.setCellFactory(new Callback<ListView<Event>, ListCell<Event>>() {
            @Override
            public ListCell<Event> call(ListView<Event> listView) {
                return new ListCell<Event>() {
                    @Override
                    protected void updateItem(Event sprint, boolean empty) {
                        super.updateItem(sprint, empty);

                        if (empty || sprint == null) {
                            setText(null);
                        } else {
                            setText(sprint.getEventName().getDistance().intValue() + "m");
                        }
                    }
                };
            }
        });
    }

    @FXML
    private void confirmButtonHandler(ActionEvent actionEvent){
        cnpErrorLabel.setVisible(false);
        surnameErrorLabel.setVisible(false);
        nameErrorLabel.setVisible(false);

        String cnp = cnpTextField.getText();
        String surname = surnameTextField.getText();
        String name = nameTextField.getText();

        boolean isValid = true;

        if (isValid){
            Child child = new Child(name, surname, cnp);
            try {
                service.saveChild(child);
            }catch (Exception exception){
                Alert alert = new Alert(Alert.AlertType.ERROR, exception.getMessage(), ButtonType.OK);
                alert.setHeaderText(null);
                alert.show();
            }

            initEventsListView();
            eventListView.setItems(eventModel);
            setEventListViewCellFactory();
            eventsLabel.setVisible(true);
            registerButton.setVisible(true);
        }
    }

    @FXML
    private void registerButtonHandler(ActionEvent actionEvent){
        String cnp = cnpTextField.getText();

        var selectedEvents = eventListView.getSelectionModel().getSelectedItems();
        var savedChild = service.findOneByCnpChild(cnp);
        if (!selectedEvents.isEmpty() && selectedEvents.size() <= Registration.numberOfEvents) {
            if (savedChild.isPresent()) {
                var eventsSerializableList = new ArrayList<Event>(selectedEvents);
                Registration registration = new Registration(savedChild.get(), eventsSerializableList, LocalDateTime.now());
                try {
                    service.saveRegistration(registration);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Inregistrarea a fost completata cu succes!", ButtonType.OK);
                    alert.setTitle("Info");
                    alert.setHeaderText(null);
                    alert.show();
                } catch (Exception exception) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, exception.getMessage(), ButtonType.OK);
                    alert.setHeaderText(null);
                    alert.show();
                }
            }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING, "Trebuie sa alegi cel putin un sprint si cel mult " + Registration.numberOfEvents + "sprinturi!", ButtonType.OK);
            alert.setHeaderText(null);
            alert.show();
        }
    }

    @FXML
    private void backButtonHandler(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/home.fxml"));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        Pane pane = fxmlLoader.load();

        MainController mainController = fxmlLoader.getController();
        mainController.setService(service);
        mainController.setEmployee(employee);

        stage.setScene(new Scene(pane));
        stage.setTitle("Home");
        stage.show();
    }
}
