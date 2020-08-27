package trevron.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import trevron.model.User;
import trevron.utility.Alerts;
import trevron.utility.Query;
import trevron.utility.State;
import trevron.utility.TimeConverter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private Button loginButton;
    @FXML private TextField loginUsername;
    @FXML private PasswordField loginPassword;


    boolean disableButton;
    String sqlStatement;
    String user, pass;
    ResultSet result;
    Stage stage;
    Parent root;
    State state = State.getInstance();

    // Use SQL statement to verify login credentials
    public void handleLoginButton() {
        boolean auth = false;
        try {
            // Retrieve user input and craft sql statement
            user = loginUsername.getText();
            pass = loginPassword.getText();
            sqlStatement = "SELECT * from user where userName = '"+ user + "'";

            // Execute sql statement and compare results with user input
            Query.executeQuery(sqlStatement);
            result = Query.getResult();
            while(result.next() && auth == false){
                if(pass.equals(result.getString("password"))){
                    state.setCurrentUser(new User(result.getString("userId"), result.getString("userName"), result.getString("password")));
                    auth = true;
                }
            }
            // If there is a user/password match, set currentUser in State
            // checkForAppointment before switching scenes.
            // Use handleSceneChange() to go to next screen
            if (auth == true) {
                System.out.println("Login successful!");
                loginUsername.clear();
                loginPassword.clear();
                checkForAppointment();
                handleSceneChange();
            } else {
                throw new SecurityException("Invalid Login Credentials");
            }
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
            if(ex.getMessage().equals("Invalid Login Credentials")) {
                Alerts.getAlert("invalidLogin").show();
            }
        }
    }
    // Enables or disables login button whether text is present in both fields or not
    public void handleKeyReleased() {
        user = loginUsername.getText();
        pass = loginPassword.getText();
        disableButton = user.isEmpty() || user.trim().isEmpty()
                || pass.isEmpty() || pass.trim().isEmpty();
        loginButton.setDisable(disableButton);
    }

    // Switch to next screen
    private void handleSceneChange() throws IOException {
        stage = (Stage) loginButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/dashboard.fxml"));
        root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // Allow login using ENTER instead of clicking on the button
    public void handleKeyPressed(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER) && !disableButton) {
            handleLoginButton();
        }
    }

    // Check to see if appointment starts within 15 minutes.
    private void checkForAppointment() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusMinutes(15);
        // Format and convert to UTC time for SQL query
        String sql = "SELECT * FROM appointment where userId = " + state.getCurrentUser().getUserId() + " and start < '"
                + TimeConverter.format(TimeConverter.toUTC(LocalDateTime.now().plusMinutes(15))) + "' and start > '"
                + TimeConverter.format(TimeConverter.toUTC(LocalDateTime.now())) + "'";
        Query.executeQuery(sql);
        result = Query.getResult();
        try{
            while(result.next()){
                if(result.getTimestamp("start") != null) {
                    // If an appointment is found, check time until start.
                    long timeTilStart = ChronoUnit.MINUTES.between(result.getTimestamp("start").toLocalDateTime(), TimeConverter.toUTC(LocalDateTime.now()));
                    long minutes = (timeTilStart + -1) * -1;
                    System.out.println(timeTilStart);
                    // Pass title and description to Alerts.
                    Alerts.appointmentAlert(result.getString("title"), result.getString("description"), minutes).show();
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginButton.setDisable(true);
    }
}
