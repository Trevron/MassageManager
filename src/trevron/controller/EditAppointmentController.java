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
import trevron.model.Appointment;
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

public class EditAppointmentController implements Initializable {

    @FXML
    TextField titleField;
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
    ObservableList<Customer> custList;
    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // Variables to pass to database
    ZonedDateTime start, end;
    String customerID, userID, title, description, cost, contact, type;
    Appointment selectedAppointment;


    // Formatter for date cell picker
    // This lambda is more efficient because it allows an entire function to be passed as an argument.
    // Without the use of the lambda, we would need to create a larger method that had multiple layers of nesting.
    // That would result in many more lines of code. In this case, we pass some of the nesting in as a function.
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

        // Use TimeConverter class to convert time to UTC
        start = TimeConverter.toUTC(startDate, startHour, startMinute);
        end = TimeConverter.toUTC(endDate, endHour, endMinute);
    }

    // Add default time values to time combo boxes
    private void populateTimes() {
        ObservableList<String> minuteList = FXCollections.observableArrayList();
        ObservableList<String> hourList = FXCollections.observableArrayList();
        // Add hours to hour list
        for(int i = 8; i < 17; i++) {
            hourList.add(Integer.toString(i));
        }
        // Add minutes to minute list
        for(int i = 0; i < 60; i += 15) {
            if(i < 10) {
                minuteList.add("0" + i);
            } else {
                minuteList.add(Integer.toString(i));
            }
        }
        startHourBox.setItems(hourList);
        startMinuteBox.setItems(minuteList);

        // Find selected appointments hour and minutes and set values in combo box
        // Convert back to local time from UTC
        int hourIndex = -1;
        for(int i = 0; i < hourList.size(); i++) {
            if (Integer.parseInt(hourList.get(i)) == TimeConverter.fromUTC(selectedAppointment.getStart()).getHour()) {
                hourIndex = i;
            }
        }
        int minuteIndex = -1;
        for(int i = 0; i < minuteList.size(); i++) {
            if (Integer.parseInt(minuteList.get(i)) == TimeConverter.fromUTC(selectedAppointment.getStart()).getMinute()) {
                minuteIndex = i;
            }
        }
        startHourBox.getSelectionModel().select(hourIndex);
        startMinuteBox.getSelectionModel().select(minuteIndex);

    }

    // Set types in type combo box
    private void populateTypeBox() {
        ObservableList<String> typeList = FXCollections.observableArrayList();
        typeList.addAll("Consultation", "Presentation", "Stand-Up", "Stand-Down");
        typeField.setItems(typeList);
    }

    // Add list of consultants to the consultant combo box.
    private void populateConsultants() {
        ObservableList<String> consultantList = FXCollections.observableArrayList();
        consultantList.addAll("Ryan Gosling", "Harrison Ford", "Sylvia Hoeks", "Ana de Armas");
        consultantBox.setItems(consultantList);
    }

    // Add customer list to combo box
    private void populateCustomers() {
        custList = FXCollections.observableArrayList();
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

    public void handleCancelButton() throws IOException {
        // Show cancel confirmation
        Alerts.getAlert("confirmCancel").showAndWait();
        if(Alerts.getAlert("confirmCancel").getResult() == ButtonType.OK) {
            // return to previous screen using titleField as a reference to current window
            stage = (Stage) titleField.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/appointment.fxml"));
            root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    // Get userId from database using selected consultant's name.
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

    // Retrieve user inputs and set variables
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

    // Check to see if an overlapping appointment exists for the selected consultant.
    private boolean appointmentOverlap() {
        String sql = "SELECT * FROM appointment where userID = " + userID;
        LocalDateTime currentStart = selectedAppointment.getStart();
        LocalDateTime apptStart, apptEnd;
        LocalDateTime attemptedStart = start.toLocalDateTime();
        Query.executeQuery(sql);
        result = Query.getResult();
        try {
            while(result.next()) {
                apptStart = result.getTimestamp("start").toLocalDateTime();
                apptEnd = result.getTimestamp("end").toLocalDateTime();
                if (attemptedStart.equals(currentStart)) {
                    return false;
                } else if (attemptedStart.isAfter(apptStart) && attemptedStart.isBefore(apptEnd)) {
                    return true;
                } else if (attemptedStart.equals(apptStart)) {
                    return true;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return false;
    }

    // Update appointment in database.
    private void updateAppointment() {
        String sql1 = "UPDATE appointment SET customerId = '" + customerID + "', "
                + "userId = '" + userID + "', "
                + "title = '" + title + "', "
                + "description = '" + description + "', "
                + "cost = '" + cost + "', "
                + "contact = '" + contact + "', "
                + "type = '" + type + "', "
                + "start = '" + df.format(start) + "', "
                + "end = '" + df.format(end) + "', "
                + "lastUpdateBy = '" + state.getCurrentUser().getUserName() + "' "
                + "WHERE appointmentId = " + selectedAppointment.getAppointmentID();
        Query.executeQuery(sql1);
    }

    public void handleSubmitButton() throws IOException {
        // Lambda for checking if time is valid prior to setting date and time.
        // The lambda is more efficient here because it is a one time use validation check so
        // it would not make sense to create a method. Utilizes the Validation interface.
        Validation timeCheck = () -> {
            if(startHourBox.getSelectionModel().isEmpty()) {
                Alerts.getAlert("invalidTime").show();
                return false;
            }
            return true;
        };
        if (timeCheck.isValid()) {
            Alerts.getAlert("confirmSubmission").showAndWait();
            if(Alerts.getAlert("confirmSubmission").getResult() == ButtonType.OK) {

                // Retrieve user inputs and set variables
                setDateTimes();
                setInputs();

                if (!appointmentOverlap()) {
                    // Check for input validation with a lambda!
                    // The use of lambda is more efficient here because it allows me to check inputs, show alerts
                    // and then provide a boolean in a separate statement based on how the checks went. I do
                    // not need to create a separate boolean method for each controller I need input validation in.
                    // The Validation interface can be reused wherever I need and I can check each variable's value directly
                    // after it has been declared.
                    Validation check = () -> {
                        Boolean isValid = false;
                        if (title.equals("") || title.length() > 255) {
                            Alerts.getAlert("invalidTitle").showAndWait();
                        } else if (description.equals("") || description.length() > 10000) {
                            Alerts.getAlert("invalidDescription").showAndWait();
                        } else if (start.isBefore(TimeConverter.toUTC(LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth(), 0, 0)))) {
                            Alerts.getAlert("invalidDate").show();
                        } else
                            return true;

                        return isValid;
                    };

                    if (check.isValid()) {
                        updateAppointment();
                        // Return to main window
                        stage = (Stage) titleField.getScene().getWindow();
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/appointment.fxml"));
                        root = fxmlLoader.load();
                        Scene scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                    }

                } else {
                    Alerts.getAlert("appointmentOverlap").show();
                }
            }
        }
    }

    private void initializeFields(){
        titleField.setText(selectedAppointment.getTitle());
        descriptionField.setText(selectedAppointment.getDescription());
        costField.setText(Integer.toString(selectedAppointment.getCost()));
        consultantBox.getSelectionModel().select(selectedAppointment.getContact());
        typeField.getSelectionModel().select(selectedAppointment.getType());
        startField.setValue(selectedAppointment.getStart().toLocalDate());

        // Set selected customer in tableview
        int custIndex = -1;
        for(int i = 0; i < custList.size(); i++){
            if(Integer.parseInt(custList.get(i).getCustomerID()) == selectedAppointment.getCustomerID()) {
                custIndex = i;
            }
        }
        customerTableView.getSelectionModel().select(custIndex);
    }

    // Set customer label based on currently selected customer. Triggers updateLocationLabel
    public void handleCustomerLabel() {
        if (customerTableView.getSelectionModel().getSelectedItem() != null) {
            customerLabel.setText(customerTableView.getSelectionModel().getSelectedItem().getName());
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedAppointment = state.getSelectedAppointment();
        populateTimes();
        populateCustomers();
        populateTypeBox();
        populateConsultants();
        initializeFields();
        handleCustomerLabel();

        col_customerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        Callback<DatePicker, DateCell> dayCellFactory = this.disableDates();
        startField.setDayCellFactory(dayCellFactory);
    }
}
