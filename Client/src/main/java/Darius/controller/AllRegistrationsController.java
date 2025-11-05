package Darius.controller;

import Darius.IService;
import Darius.domain.Registration;
import Darius.domain.Person;
import Darius.utils.Calculeaza_Varsta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class AllRegistrationsController {
    private IService service;

    private ObservableList<Registration> registrationModel = FXCollections.observableArrayList();

    @FXML
    private TableView<Registration> registrationsTable;

    @FXML
    private TableColumn<Registration, String> nameColumn;
    @FXML
    private TableColumn<Registration, String> surnameColumn;
    @FXML
    private TableColumn<Registration, Integer> ageColumn;
    @FXML
    private TableColumn<Registration, Integer> noOfSprintsColumn;

    public void setService(IService service) {
        this.service = service;
        initModel();
    }

    private void initModel() {
        service.findAllAgeGroup().forEach(ageGroup -> {
            service.findAllByAgeGroupIdEvent(ageGroup.getId()).forEach(event -> {
                service.findAllRegistrationsBySprintAndGroupAge(event.getEventName(), ageGroup)
                        .forEach(registration -> {
                            if (!registrationModel.contains(registration))
                                registrationModel.add(registration);
                        });
            });
        });

        registrationsTable.setItems(registrationModel);

        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPerson().getName()));
        surnameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPerson().getSurname()));
        ageColumn.setCellValueFactory(cellData -> {
            Person person = cellData.getValue().getPerson();
            int age = Calculeaza_Varsta.Varsta_CNP(person.getCnp());
            return new SimpleIntegerProperty(age).asObject();
        });
        noOfSprintsColumn.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getEvents().size()).asObject());
    }
}
