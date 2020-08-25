package trevron.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import trevron.utility.*;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddCustomerController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField address2Field;
    @FXML private ComboBox<String> cityField;
    @FXML private TextField zipField;
    @FXML private Label countryField;
    @FXML private TextField phoneField;

    // Declare array and observable list
    ArrayList<String> getCountries;
    ObservableList<String> countriesOL;

    State state = State.getInstance();
    Stage stage;
    Parent root;

    // SQL related variables
    ResultSet result;


    int countryID = -1;

    // Set branch locations in combo box
    private void populateLocationsBox() {
        ObservableList<String> cityList = FXCollections.observableArrayList();
        cityList.addAll("Seattle", "Renton", "Tacoma");
        cityField.setItems(cityList);
    }

    // Insert new customer data into database from user input
    private void addCustomer(String name, String address, String address2, String city, String zipcode, String country1, String phone) {
        int addressID = -1;
        int cityID = -1;

        try{
            // Get cityID from database
            String sql2 = "Select cityId from city where city = '" + city + "'";
            Query.executeQuery(sql2);
            result = Query.getResult();
            while(result.next()) {
                cityID = result.getInt("cityId");
            }

            // SQL Insert for address table
            String sqlAddress = "INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdateBy)" +
                    "VALUES(" +
                    "'" + address + "', " +
                    "'" + address2 + "', " +
                    cityID + ", " +
                    "'" + zipcode + "', " +
                    "'" + phone + "', " +
                    "'" + TimeConverter.format(TimeConverter.toUTC(LocalDateTime.now())) + "', " +
                    "'" + state.getCurrentUser().getUserName() + "', " +
                    "'" + state.getCurrentUser().getUserName() + "'" +
                    ")";
            Query.executeQuery(sqlAddress);

            // Get addressID from database
            String sql3 = "Select addressId from address where address = '" + address + "'";
            Query.executeQuery(sql3);
            result = Query.getResult();
            while(result.next()) {
                addressID = result.getInt("addressId");
            }

            // SQL insert statement for customer table
            String sqlCustomer = "INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdateBy)" +
                    "VALUES(" +
                    "'" + name + "', " +
                    addressID + ", " +
                    1 + ", " +
                    "'" + TimeConverter.format(TimeConverter.toUTC(LocalDateTime.now())) + "', " +
                    "'" + state.getCurrentUser().getUserName() + "', " +
                    "'" + state.getCurrentUser().getUserName() + "'" +
                    ")";
            Query.executeQuery(sqlCustomer);

        } catch(SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    public void handleCancelButton() throws IOException {
        // Show cancel confirmation
        Alerts.getAlert("confirmCancel").showAndWait();
        if(Alerts.getAlert("confirmCancel").getResult() == ButtonType.OK) {
            // return to previous screen using customerName as a reference to current window
            stage = (Stage) nameField.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/customer.fxml"));
            root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    public void handleSubmitButton() throws IOException {
        // Show submission confirmation
        Alerts.getAlert("confirmSubmission").showAndWait();
        if(Alerts.getAlert("confirmSubmission").getResult() == ButtonType.OK) {

            // get input from user and assign to variables
            String name = nameField.getText();
            String address = addressField.getText();
            String address2 = address2Field.getText();
            String city = cityField.getValue();
            String zipcode = zipField.getText();
            String phone = phoneField.getText();
            String country = countryField.getText();

            // Check for input validation with a lambda!
            // The use of lambda is more efficient here because it allows me to check inputs, show alerts
            // and then provide a boolean in a separate statement based on how the checks went. I do
            // not need to create a separate boolean method for each controller I need input validation in.
            // The Validation interface can be reused wherever I need and I can check each variable's value directly
            // after it has been declared.
            Validation check = () -> {
                Boolean isValid = false;
                if(name.equals("") || name.length() > 45) {
                    Alerts.getAlert("invalidCustomerName").showAndWait();
                } else if(address.equals("") || address.length() > 50 || address2.length() > 50) {
                    Alerts.getAlert("invalidAddressAlert").showAndWait();
                } else if (zipcode.equals("") || zipcode.length() > 10) {
                    Alerts.getAlert("invalidPostalCode").showAndWait();
                } else if (phone.equals("") || phone.length() > 20) {
                    Alerts.getAlert("invalidPhone").showAndWait();
                } else
                    return true;

                return isValid;
            };

            if (check.isValid()) {
                // Add inputted data to database
                addCustomer(name, address, address2, city, zipcode, country, phone);

                // return to previous screen using customerName as a reference to current window
                stage = (Stage) nameField.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/customer.fxml"));
                root = fxmlLoader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
        }
    }

    // Obtain countryID from currently selected city.
    // Change country label to reflect correct city/country relation.
    public void handleCountryLabel() {
        String country = "";
        String city = cityField.getValue();
        String sql1 = "SELECT countryId from city where city = '" + city + "'";
        try {
            Query.executeQuery(sql1);
            result = Query.getResult();
            while(result.next()) {
                countryID = result.getInt("countryId");
            }
            String sql2 = "SELECT country from country where countryId = " + countryID;
            Query.executeQuery(sql2);
            result = Query.getResult();
            while(result.next()) {
                country = result.getString("country");
            }
        } catch(SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        countryField.setText(country);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateLocationsBox();
        cityField.getSelectionModel().select(0);
        handleCountryLabel();
    }

}
