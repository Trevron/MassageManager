package trevron.utility;

/*
    This class constructs alerts based on the required exceptions.
    The method getAlert() is used to access the alerts from the various controllers.
    getAlert() also defines the various text displayed by each alert.
    The alerts pass on useful information to the end user in popup style windows.
 */

import javafx.scene.control.Alert;

import java.util.Locale;
import java.util.ResourceBundle;

public class Alerts {
    // initialize alerts
    private static final Alert invalidLogin = new Alert(Alert.AlertType.ERROR);
    private static final Alert confirmCancel = new Alert(Alert.AlertType.CONFIRMATION);
    private static final Alert confirmLogout = new Alert(Alert.AlertType.CONFIRMATION);
    private static final Alert confirmSubmission = new Alert(Alert.AlertType.CONFIRMATION);
    private static final Alert nullSelectCustomer = new Alert(Alert.AlertType.ERROR);
    private static final Alert nullSelectAppointment = new Alert(Alert.AlertType.ERROR);
    private static final Alert appointmentsExist = new Alert(Alert.AlertType.ERROR);
    private static final Alert appointmentOverlap = new Alert(Alert.AlertType.ERROR);
    private static final Alert appointmentOverlapCustomer = new Alert(Alert.AlertType.ERROR);
    private static final Alert confirmDeletion = new Alert(Alert.AlertType.CONFIRMATION);
    private static final Alert appointmentIncoming = new Alert(Alert.AlertType.INFORMATION);
    private static final Alert invalidCustomerName = new Alert(Alert.AlertType.ERROR);
    private static final Alert invalidAddressAlert = new Alert(Alert.AlertType.ERROR);
    private static final Alert invalidPostalCode = new Alert(Alert.AlertType.ERROR);
    private static final Alert invalidPhone = new Alert(Alert.AlertType.ERROR);
    private static final Alert invalidTitle = new Alert(Alert.AlertType.ERROR);
    private static final Alert invalidDescription = new Alert(Alert.AlertType.ERROR);
    private static final Alert invalidTime = new Alert(Alert.AlertType.ERROR);
    private static final Alert invalidDate = new Alert(Alert.AlertType.ERROR);
    private static final Alert unhandledException = new Alert(Alert.AlertType.ERROR);

    public static Alert getAlert(String alertType) {
        switch(alertType) {
            case "invalidLogin":
                invalidLogin.setTitle("Invalid Login!");
                invalidLogin.setHeaderText("Check username and password.");
                invalidLogin.setContentText("Invalid username and password combination. Please try again.");
                return invalidLogin;

            case "confirmCancel":
                confirmCancel.setTitle("Cancel?");
                confirmCancel.setHeaderText("Are you sure you want to continue?");
                confirmCancel.setContentText("Changes will not be saved!");
                return confirmCancel;

            case "confirmLogout":
                confirmLogout.setTitle("Logout?");
                confirmLogout.setHeaderText("Are you sure you want to continue?");
                confirmLogout.setContentText("You will be logged out of the system!");
                return confirmLogout;

            case "confirmSubmission":
                confirmSubmission.setTitle("Submit?");
                confirmSubmission.setHeaderText("Are you sure you want to continue?");
                confirmSubmission.setContentText("All data will be saved to the system!");
                return confirmSubmission;

            case "nullSelectCustomer":
                nullSelectCustomer.setTitle("Null Select!");
                nullSelectCustomer.setHeaderText("No customer is selected.");
                nullSelectCustomer.setContentText("Please click on a customer and try again");
                return nullSelectCustomer;

            case "nullSelectAppointment":
                nullSelectAppointment.setTitle("Null Select!");
                nullSelectAppointment.setHeaderText("No appointment is selected.");
                nullSelectAppointment.setContentText("Please click on an appointment and try again");
                return nullSelectAppointment;

            case "confirmDeletion":
                confirmDeletion.setTitle("Delete?");
                confirmDeletion.setHeaderText("Are you sure you want to continue?");
                confirmDeletion.setContentText("Deletion cannot be undone!");
                return confirmDeletion;

            case "appointmentsExist":
                appointmentsExist.setTitle("Can not delete!");
                appointmentsExist.setHeaderText("Appointments are scheduled for this customer.");
                appointmentsExist.setContentText("Please modify or delete appointments first and then try again.");
                return appointmentsExist;

            case "appointmentOverlap":
                appointmentOverlap.setTitle("Can not add appointment!");
                appointmentOverlap.setHeaderText("Overlapping appointment found for the selected consultant.");
                appointmentOverlap.setContentText("Please modify the time and try again.");
                return appointmentOverlap;
            case "appointmentOverlapCustomer":
                appointmentOverlapCustomer.setTitle("Can not add appointment!");
                appointmentOverlapCustomer.setHeaderText("Overlapping appointment found for the selected customer.");
                appointmentOverlapCustomer.setContentText("Please modify the time and try again.");
                return appointmentOverlapCustomer;

            case "invalidCustomerName":
                invalidCustomerName.setTitle("Invalid Customer Name!");
                invalidCustomerName.setHeaderText("A valid customer name is required.");
                invalidCustomerName.setContentText("The name should be less than 45 characters.");
                return invalidCustomerName;

            case "invalidAddressAlert":
                invalidAddressAlert.setTitle("Invalid Address!");
                invalidAddressAlert.setHeaderText("A valid address is required.");
                invalidAddressAlert.setContentText("Each address line must be less than 50 characters.");
                return invalidAddressAlert;

            case "invalidPostalCode":
                invalidPostalCode.setTitle("Invalid Postal Code!");
                invalidPostalCode.setHeaderText("Valid postal code is required.");
                invalidPostalCode.setContentText("The postal code must be 10 characters or less.");
                return invalidPostalCode;

            case "invalidPhone":
                invalidPhone.setTitle("Invalid Phone Number!");
                invalidPhone.setHeaderText("Valid phone number is required.");
                invalidPhone.setContentText("The phone number must be 20 characters or less.");
                return invalidPhone;

            case "invalidTitle":
                invalidTitle.setTitle("Invalid Title!");
                invalidTitle.setHeaderText("Valid title is required.");
                invalidTitle.setContentText("The title must be 255 characters or less.");
                return invalidTitle;

            case "invalidDescription":
                invalidDescription.setTitle("Invalid Description!");
                invalidDescription.setHeaderText("Valid description is required.");
                invalidDescription.setContentText("The description must be 10,000 characters or less.");
                return invalidDescription;

            case "invalidTime":
                invalidTime.setTitle("Invalid Time!");
                invalidTime.setHeaderText("Valid time is required.");
                invalidTime.setContentText("Please make sure both the hour and minute have been selected.");
                return invalidTime;

            case "invalidDate":
                invalidDate.setTitle("Invalid Date!");
                invalidDate.setHeaderText("Valid date is required.");
                invalidDate.setContentText("Please make sure to select a date in the future.");
                return invalidDate;

            default:
                return null;
        }
    }

    // Informs user of upcoming appointment.
    public static Alert appointmentAlert(String title, String description, long time) {
        appointmentIncoming.setTitle("Upcoming appointment!");
        appointmentIncoming.setHeaderText("Title: " + title);
        appointmentIncoming.setContentText("Description: " + description + "\n\nStarts in about " + time + " minutes.");
        return appointmentIncoming;
    }

    // Informs user of unhandled exception.
    public static Alert exceptionAlert(String exceptionType, Exception ex) {
        unhandledException.setTitle("Exception!");
        unhandledException.setHeaderText("Exception: " + exceptionType);
        unhandledException.setContentText("Message: " + ex.getMessage());
        return unhandledException;
    }

}