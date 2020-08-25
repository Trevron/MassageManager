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
import javafx.util.Callback;
import trevron.model.Customer;
import trevron.utility.*;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AddAppointmentController implements Initializable {
    @FXML TextField titleField;
    @FXML TextField descriptionField;
    @FXML TextField costField;
    @FXML ComboBox<String> consultantBox;
    @FXML ComboBox<String> typeField;
    @FXML DatePicker startField;
    @FXML ComboBox<String> startHourBox;
    @FXML ComboBox<String> startMinuteBox;
    @FXML TableView<Customer> customerTableView;
    @FXML TableColumn<Customer, String> col_customerID;
    @FXML TableColumn<Customer, String> col_name;
    @FXML Label customerLabel;
    State state = State.getInstance();
    Stage stage;
    Parent root;
    ResultSet result;
    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // Variables to pass to database
    ZonedDateTime start, end;
    String customerID, userID, title, description, cost, contact, type;

    // Formatter for date cell picker.
    // Lambda expression is used to override default date cell.
    // The use of a lambda expression here allows me to create a new dayCellFactory without having to create a separate class.
    // Allows for the ability to disable certain dates based off of boolean checks.
    private Callback<DatePicker, DateCell> disableDates() {
        final Callback<DatePicker, DateCell> dayCellFactory = (final DatePicker datePicker) -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                // Check if date is a weekend and disable date cell if true.
                if (item.getDayOfWeek() == DayOfWeek.SATURDAY || item.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    setDisable(true);
                }
                // Check if date is before current date and disable if true.
                if (item.isBefore(LocalDate.now())) {
                    setDisable(true);
                }
            }
        };
        return dayCellFactory;
    }

    // Get Date and Time
    private void setDateTimes() {
        LocalDate startDate = startField.getValue();
        LocalDate endDate = startField.getValue();
        String startHour = startHourBox.getValue();
        String startMinute = startMinuteBox.getValue();

        // Add 15 minutes for appointment Interval
        // Check if it rolls into a new hour and update time accordingly
        int hour = Integer.parseInt(startHour);
        int minute = Integer.parseInt(startMinute) + 15;
        if (minute >= 60) {
            hour += 1;
            minute = minute - 60;
        }
        String endHour = Integer.toString(hour);
        String endMinute = Integer.toString(minute);
        // Convert time to UTC
        start = TimeConverter.toUTC(startDate, startHour, startMinute);
        end = TimeConverter.toUTC(endDate, endHour, endMinute);
    }

    // Add default time values to time combo boxes
    private void populateTimes() {
        ObservableList<String> minuteList = FXCollections.observableArrayList();
        ObservableList<String> hourList = FXCollections.observableArrayList();

        // Populate hours with business hours. 8am to 5pm
        for(int i = 8; i < 17; i++) {
            if(i < 10) {
                hourList.add("0" + i);
            } else {
                hourList.add(Integer.toString(i));
            }
        }
        // Populate minutes with intervals of 15.
        for(int i = 0; i < 60; i += 15) {
            if(i < 10) {
                minuteList.add("0" + i);
            } else {
                minuteList.add(Integer.toString(i));
            }
        }
        startHourBox.setItems(hourList);
        startMinuteBox.setItems(minuteList);

        // Set the default selection instead of empty box
        startHourBox.getSelectionModel().select(0);
        startMinuteBox.getSelectionModel().select(0);
    }

    // Add list of types to the Type combo box.
    private void populateTypeBox() {
        ObservableList<String> typeList = FXCollections.observableArrayList();
        typeList.addAll("Swedish Massage", "Hot Stone Massage", "Shiatsu Massage", "Deep Tissue", "Sports Massage");
        typeField.setItems(typeList);
        // Set default selection.
        typeField.getSelectionModel().select(0);
    }

    // Add list of consultants to the consultant combo box.
    private void populateConsultants() {
        ObservableList<String> consultantList = FXCollections.observableArrayList();
        consultantList.addAll("Ryan Gosling", "Harrison Ford", "Sylvia Hoeks", "Ana de Armas");
        consultantBox.setItems(consultantList);
        // Set default selection.
        consultantBox.getSelectionModel().select(0);
    }

    // Add customer list to combo box
    private void populateCustomers() {
        ObservableList<Customer> custList = FXCollections.observableArrayList();
        String sql1 = "SELECT a.customerId, a.customerName, a.addressId, b.address, b.address2, b.postalCode, b.phone, b.cityId, c.city, c.countryId, d.country \n" +
                "\tFROM customer a JOIN address b on a.addressId = b.addressId\n" +
                "\tJOIN city c on b.cityId = c.cityId\n" +
                "\tJOIN country d on c.countryId = d.countryId";
        Query.executeQuery(sql1);
        result = Query.getResult();
        try {
            // Retrieve customers from the database
            while(result.next()) {
                custList.add(new Customer(result.getString("a.customerId"), result.getString("a.customerName"),
                        result.getString("a.addressId"), result.getString("b.address"), result.getString("b.address2"),
                        result.getString("b.postalCode"), result.getString("b.phone"), result.getString("c.city"),
                        result.getString("b.cityId"), result.getString("d.country"), result.getString("c.countryId")));
            }
        } catch(SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        customerTableView.setItems(custList);
    }


    // Confirm cancellation, go back to main window.
    public void handleCancelButton() throws IOException {
        // Show cancel confirmation
        Alerts.getAlert("confirmCancel").showAndWait();
        if(Alerts.getAlert("confirmCancel").getResult() == ButtonType.OK) {
            // return to previous screen using titlefield as a reference to current window
            stage = (Stage) titleField.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/appointment.fxml"));
            root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    // Retrieve the userId based on the selected contact/consultant.
    private int getUserID() {
        int uID = -1;
        String sql = "SELECT * from user where userName = '" + contact +"'";
        Query.executeQuery(sql);
        result = Query.getResult();
        try {
            while(result.next()) {
                uID = result.getInt("userId");
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return uID;
    }

    // Set user input to variables.
    private void setInputs() {
        // Check that all necessary information is entered
        if(customerTableView.getSelectionModel().getSelectedItem() != null) {
            Customer selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();
            customerID = selectedCustomer.getCustomerID();
            contact = consultantBox.getValue();
            userID = Integer.toString(getUserID());
            title = titleField.getText();
            description = descriptionField.getText();
            cost = costField.getText();
            type = typeField.getValue();
        }
    }

    private void overlappingAppointments() throws AppointmentException {
        // Check for overlapping appointments with selected user.
        String sql = "SELECT * FROM appointment where userID = " + userID;
        LocalDateTime apptStart, apptEnd;
        LocalDateTime attemptedStart = start.toLocalDateTime();
        Query.executeQuery(sql);
        result = Query.getResult();
        try {
            while(result.next()) {
                apptStart = result.getTimestamp("start").toLocalDateTime();
                apptEnd = result.getTimestamp("end").toLocalDateTime();
                if(attemptedStart.isAfter(apptStart) && attemptedStart.isBefore(apptEnd)) {
                    throw new AppointmentException("User Appointment Conflict");
                } else if (attemptedStart.equals(apptStart)) {
                    throw new AppointmentException("User Appointment Conflict");
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }

        // Check for overlapping appointments with selected customer.
        String sql2 = "SELECT * FROM appointment where customerId = " + customerID;
        apptStart = null;
        apptEnd = null;
        Query.executeQuery(sql2);
        result = Query.getResult();
        try {
            while(result.next()) {
                apptStart = result.getTimestamp("start").toLocalDateTime();
                apptEnd = result.getTimestamp("end").toLocalDateTime();
                if(attemptedStart.isAfter(apptStart) && attemptedStart.isBefore(apptEnd)) {
                    throw new AppointmentException("Customer Appointment Conflict");
                } else if (attemptedStart.equals(apptStart)) {
                    throw new AppointmentException("Customer Appointment Conflict");
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    // Add new appointment to database.
    private void addAppointment() {
        String sql1 = "INSERT INTO appointment (customerId, userId, title, description, cost, contact, type, start, end, createDate, createdBy, LastUpdateBy, url) "
                + "VALUES(" + customerID + ", " + userID + ", '" + title +"', '" + description + "', '"
                + cost + "', '" + contact + "', '" + type + "', '" + df.format(start) + "', '" + df.format(end) + "', '" + df.format(TimeConverter.toUTC(LocalDateTime.now()))
                + "', '" + state.getCurrentUser().getUserName() + "', '" + state.getCurrentUser().getUserName() + "', 'not needed')";
        Query.executeQuery(sql1);
    }

    // Validate, if everything is okay, submit appointment to database.
    public void handleSubmitButton() throws IOException {
        Alerts.getAlert("confirmSubmission").showAndWait();
        if(Alerts.getAlert("confirmSubmission").getResult() == ButtonType.OK) {

            // Retrieve user inputs and set variables
            setDateTimes();
            setInputs();

            try {
                // Check for overlapping appointments. Will throw appointment exception if any overlaps exist.
                overlappingAppointments();

                // Check for input validation with a lambda!
                // The use of lambda is more efficient here because it allows me to check inputs, show alerts
                // and then provide a boolean in a separate statement based on how the checks went. I do
                // not need to create a separate boolean method for each controller I need input validation in.
                // The Validation interface can be reused wherever I need and I can check each variable's value directly
                // after it has been declared.
                Validation check = () -> {
                    Boolean isValid = false;
                    if(title.equals("") || title.length() > 255) {
                        Alerts.getAlert("invalidTitle").showAndWait();
                    } else if(description.equals("") || description.length() > 10000) {
                        Alerts.getAlert("invalidDescription").showAndWait();
                    } else
                        return true;

                    return isValid;
                };
                if (check.isValid()) {
                    addAppointment();
                    // Return to main window
                    stage = (Stage) titleField.getScene().getWindow();
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/appointment.fxml"));
                    root = fxmlLoader.load();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                }
            } catch (AppointmentException ex) {
                // Show corresponding alert whether there is a conflict with customer or consultant.
                if(ex.getMessage().equals("User Appointment Conflict")) {
                    Alerts.getAlert("appointmentOverlap").show();
                } else if (ex.getMessage().equals("Customer Appointment Conflict")) {
                    Alerts.getAlert("appointmentOverlapCustomer").show();
                }

            }
        }
    }

    // Set customer label based on selected customer in tableview
    public void handleCustomerLabel() {
        if (customerTableView.getSelectionModel().getSelectedItem() != null) {
            customerLabel.setText(customerTableView.getSelectionModel().getSelectedItem().getName());
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateTimes();
        populateCustomers();
        populateTypeBox();
        populateConsultants();
        col_customerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        // Disable weekends and dates in the past.
        Callback<DatePicker, DateCell> dayCellFactory = this.disableDates();
        startField.setDayCellFactory(dayCellFactory);
        // Set default date to current date.
        startField.setValue(LocalDate.now());
        customerTableView.getSelectionModel().select(0);
        handleCustomerLabel();
    }
}
