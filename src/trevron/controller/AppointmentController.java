package trevron.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import trevron.model.Appointment;
import trevron.utility.Alerts;
import trevron.utility.Query;
import trevron.utility.State;
import trevron.utility.TimeConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ResourceBundle;

public class AppointmentController implements Initializable {

    @FXML private TableView<Appointment> appointmentTableView;
    @FXML private TableColumn<Appointment, String> titleColumn;
    @FXML private TableColumn<Appointment, String> customerColumn;
    @FXML private TableColumn<Appointment, String> costColumn;
    @FXML private TableColumn<Appointment, String> contactColumn;
    @FXML private TableColumn<Appointment, String> typeColumn;
    @FXML private TableColumn<Appointment, LocalDateTime> startColumn;
    @FXML TextField searchField;
    @FXML Button homeButton;
    @FXML Button deleteAppointmentButton;
    @FXML Button editAppointmentButton;
    State state = State.getInstance();
    Stage stage;
    Parent root;

    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    // Enable or disable customer buttons if row is selected
    public void checkSelected() {
        if (appointmentTableView.getSelectionModel().getSelectedItem() != null) {
            deleteAppointmentButton.setDisable(false);
            editAppointmentButton.setDisable(false);
        } else {
            deleteAppointmentButton.setDisable(true);
            editAppointmentButton.setDisable(true);
        }
    }


    // Update selected appointment.
    public void updateButtonHandler() throws IOException{
        // Check if an appointment is selected.
        if(appointmentTableView.getSelectionModel().getSelectedItem() != null) {
            // Set selected appointment in State, then go to update window.
            state.setSelectedAppointment(appointmentTableView.getSelectionModel().getSelectedItem());
            stage = (Stage) homeButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/update_appointment.fxml"));
            root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            Alerts.getAlert("nullSelectAppointment").show();
        }
    }

    // Delete selected appointment
    public void deleteButtonHandler() {
        // Check if an appointment is selected.
        if(appointmentTableView.getSelectionModel().getSelectedItem() != null) {
            // Set selected appointment in State.
            state.setSelectedAppointment(appointmentTableView.getSelectionModel().getSelectedItem());
            // If deletion is confirmed, delete selected appointment in database.
            Alerts.getAlert("confirmDeletion").showAndWait();
            if(Alerts.getAlert("confirmDeletion").getResult() == ButtonType.OK) {
                String sql1 = "DELETE FROM appointment WHERE appointmentId = " + state.getSelectedAppointment().getAppointmentID();
                Query.executeQuery(sql1);
                // Reload table data and set selected appointment in State to null.
                updateTableData();
                state.setSelectedAppointment(null);
            }
        }
    }

    // Updates table view by clearing list and doing a fresh database query.
    private void updateTableData() {
        // Initialize table before retrieving data.
        appointmentList.clear();
        // Get customer table from database and then bind to table view.
        try{
            String sql1 = "SELECT * FROM appointment";
            Query.executeQuery(sql1);
            ResultSet result = Query.getResult();
            while(result.next()) {
                // CHECK TO MAKE SURE ALL TIMES MAKE SENSE, MAYBE USE TIME CONVERTER
                appointmentList.add(new Appointment(result.getInt("appointmentId"), result.getInt("customerId"),
                        result.getInt("userId"), result.getString("title"), result.getString("description"),
                        result.getInt("cost"), result.getString("contact"), result.getString("type"),
                        result.getTimestamp("start").toLocalDateTime(), result.getTimestamp("end").toLocalDateTime()));
            }
        } catch(SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        appointmentTableView.setItems(appointmentList);
    }

    // Search button handler. Subquery to match customer names
    public void searchTableData() {
        // Initialize table before retrieving data.
        appointmentList.clear();
        // Get customer table from database and then bind to tableview.
        try{
            String sql1 = "SELECT * FROM appointment WHERE customerId IN (SELECT customerId FROM customer WHERE customerName LIKE '%" + searchField.getText() + "%')";
            Query.executeQuery(sql1);
            ResultSet result = Query.getResult();
            while(result.next()) {
                // getTimestamp or getDate? I have no idea result.getDate("start").toLocalDate() <- doesnt have times
                appointmentList.add(new Appointment(result.getInt("appointmentId"), result.getInt("customerId"),
                        result.getInt("userId"), result.getString("title"), result.getString("description"),
                        result.getInt("cost"), result.getString("contact"), result.getString("type"),
                        result.getTimestamp("start").toLocalDateTime(), result.getTimestamp("end").toLocalDateTime()));
            }
        } catch(SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        appointmentTableView.setItems(appointmentList);
    }

    public void handleAddAppointmentButton() throws IOException {
        stage = (Stage) homeButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/add_appointment.fxml"));
        root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void handleCustomerButton() throws IOException {
        stage = (Stage) homeButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/customer.fxml"));
        root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void handleHomeButton() throws IOException {
        stage = (Stage) homeButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/dashboard.fxml"));
        root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void handleReportButton() throws IOException {
        stage = (Stage) homeButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/report.fxml"));
        root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deleteAppointmentButton.setDisable(true);
        editAppointmentButton.setDisable(true);
        // Initialize columns and table data.
        updateTableData();
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
    }
}
