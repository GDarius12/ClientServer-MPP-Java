    package Darius.controller;

    import javafx.application.Platform;
    import javafx.beans.property.SimpleStringProperty;
    import javafx.collections.FXCollections;
    import javafx.collections.ObservableList;
    import javafx.event.ActionEvent;
    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.fxml.Initializable;
    import javafx.scene.Node;
    import javafx.scene.Scene;
    import javafx.scene.control.*;
    import javafx.scene.layout.AnchorPane;
    import javafx.scene.layout.Pane;
    import javafx.stage.Stage;
    import javafx.util.StringConverter;
    import org.apache.logging.log4j.LogManager;
    import org.apache.logging.log4j.Logger;
    import Darius.IObserver;
    import Darius.domain.*;
    import Darius.IService;
    import Darius.utils.Calculeaza_Varsta;

    import java.io.IOException;
    import java.net.URL;
    import java.util.Objects;
    import java.util.ResourceBundle;

    public class MainController implements IObserver {
        private IService service;

        private ObservableList<AgeGroup> ageGroupModel = FXCollections.observableArrayList();
        private ObservableList<Event> eventModel = FXCollections.observableArrayList();
        private ObservableList<Registration> tableModel = FXCollections.observableArrayList();

        private Employee employee;

        @FXML
        private ChoiceBox<AgeGroup> ageGroupChoiceBox;
        @FXML
        private ChoiceBox<Event> eventChoiceBox;

        @FXML
        private TableView<Registration> tableView;
        @FXML
        private TableColumn<Registration, String> surnameColumn;
        @FXML
        private TableColumn<Registration, String> nameColumn;
        @FXML
        private TableColumn<Registration, Integer> ageColumn;
        @FXML
        private TableColumn<Registration, Integer> noOfSprintsColumn;

        @FXML
        private Label registeredChildrenLabel;

        private static Logger logger = LogManager.getLogger(MainController.class);

        public void setService(IService service) {
            this.service = service;
            initModel();
        }

        public void setEmployee(Employee employee){

            this.employee = employee;}

        @FXML
        private void initialize(){
            ageGroupChoiceBox.setItems(ageGroupModel);

            setAgeGroupChoiceBoxConverter();

            ageGroupChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                initEventChoiceModel(newValue.getId());
            });

            eventChoiceBox.setItems(eventModel);
            setEventChoiceBoxConverter();
        }

        private void initModel(){
            initAgeGroupModel();
        }

        private void initAgeGroupModel(){
            var ageGroups = service.findAllAgeGroup();

            System.out.println("AGE GROUPS:");
            ageGroups.forEach(System.out::println);

            ageGroupModel.clear();
            ageGroups.forEach(ageGroup -> ageGroupModel.add(ageGroup));
        }

        private void setAgeGroupChoiceBoxConverter(){
            ageGroupChoiceBox.setConverter(new StringConverter<AgeGroup>() {
                @Override
                public String toString(AgeGroup ageGroup) {
                    if (ageGroup != null)
                        return ageGroup.getLower() + "-" + ageGroup.getUpper();
                    return "Select age group";
                }

                @Override
                public AgeGroup fromString(String s) {
                    return null;
                }
            });
        }
        private void initEventChoiceModel(int id){
            var events = service.findAllByAgeGroupIdEvent(id);

            eventModel.clear();
            events.forEach(eventModel::add);
        }

        private void setEventChoiceBoxConverter(){
            eventChoiceBox.setConverter(new StringConverter<Event>() {
                @Override
                public String toString(Event event) {
                    if (event != null) {
                        return String.valueOf(event.getEventName().getDistance().intValue()) + "m";
                    }
                    return "Select sprint";
                }

                @Override
                public Event fromString(String s) {
                    return null;
                }
            });
        }

        private void initTableModel(){
            var ageGroup = ageGroupChoiceBox.getSelectionModel().getSelectedItem();
            var sprint = eventChoiceBox.getSelectionModel().getSelectedItem().getEventName();
            var registrations = service.findAllRegistrationsBySprintAndGroupAge(sprint, ageGroup);

            tableModel.clear();

            registrations.forEach(tableModel::add);
        }

        private void setTableViewCellFactory(){
            nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPerson().getName()));
            surnameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPerson().getSurname()));
            ageColumn.setCellValueFactory(cellData -> {
                Person person = cellData.getValue().getPerson();
                int age = Calculeaza_Varsta.Varsta_CNP(person.getCnp());
                return new javafx.beans.property.SimpleIntegerProperty(age).asObject();
            });
            noOfSprintsColumn.setCellValueFactory(cellData -> {
                Person person = cellData.getValue().getPerson();
                var registration = service.findOneRegistrationByPerson(person);
                var noOfSprints = registration.get().getEvents().size();
                return new javafx.beans.property.SimpleIntegerProperty(noOfSprints).asObject();
            });
        }

        @FXML
        private void searchRegistrationButtonHandler(ActionEvent actionEvent){
            if (ageGroupChoiceBox.getSelectionModel().getSelectedItem() == null || eventChoiceBox.getSelectionModel().getSelectedItem() == null){
                Alert error = new Alert(Alert.AlertType.ERROR, "Trebuie sa selectezi un AgeGroup si un Sprint!", ButtonType.OK);
                error.setTitle("Error");
                error.setHeaderText(null);
                error.showAndWait();
            }
            else {
                registeredChildrenLabel.setVisible(true);
                tableView.setVisible(true);
                try {
                    initTableModel();
                    tableView.setItems(tableModel);
                    setTableViewCellFactory();

                }catch (Exception e){
                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.show();
                }
            }
        }

        @FXML
        private void logoutButtonHandler(ActionEvent actionEvent) throws IOException {
            service.logout(employee);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            Pane pane = fxmlLoader.load();

            LoginController loginController = fxmlLoader.getController();
            loginController.setService(service);

            stage.setScene(new Scene(pane));
            stage.setTitle("Login");
            stage.show();
        }

        @FXML
        private void addRegistration(ActionEvent actionEvent) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/registration.fxml"));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            SplitPane pane = fxmlLoader.load();

            RegistrationController registrationController = fxmlLoader.getController();
            registrationController.setService(service);
            registrationController.setEmployee(employee);

            stage.setScene(new Scene(pane));
            stage.setTitle("Registration");
            stage.show();
        }

        @Override
        public void registrationAdded(Registration registration) {
            Platform.runLater(()->{
                tableView.getItems().add(registration);
                logger.debug(("O noua inregistrare in tabel " + registration.getId()));
            });
        }
        @FXML
        private void openAllRegistrationsWindow(ActionEvent event) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/all-registrations.fxml"));
            AnchorPane pane = fxmlLoader.load();

            AllRegistrationsController controller = fxmlLoader.getController();
            controller.setService(service);

            Stage stage = new Stage();
            stage.setTitle("Toate inregistrarile");
            stage.setScene(new Scene(pane));
            stage.show();
        }

    }
