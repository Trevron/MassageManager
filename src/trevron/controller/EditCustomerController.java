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
import trevron.model.Customer;
import trevron.utility.Alerts;
import trevron.utility.Query;
import trevron.utility.State;
import trevron.utility.Validation;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EditCustomerController implements Initializable {
    @FXML private TextField nameField;
    @FXML private TextField addressField;
    @FXML private TextField address2Field;
    @FXML private ComboBox<String> cityField;
    @FXML private TextField zipField;
    @FXML private Label countryField;
    @FXML private TextField phoneField;

    String country = "";
    String city = "";
    String address = "";
    String address2 = "";
    String postalCode = "";
    String phone = "";
    int countryID = -1;

    // Declare array and observable list
    ArrayList<String> getCountries;
    ObservableList<String> countriesOL;

    State state = State.getInstance();
    Stage stage;
    Parent root;

    // SQL related variables
    ResultSet result;
    Customer selectedCustomer;

    // Update customer and address tables in the database with new information from user input.
    private void updateCustomerRecords(String name, String address, String address2, int cityID, String zipcode, String phone) {
        String sql1 = "UPDATE customer SET customerName = '" + name + "', lastUpdateBy = '" + state.getCurrentUser().getUserName() + "' WHERE customerId = " + selectedCustomer.getCustomerID();
        Query.executeQuery(sql1);
        String sql2 = "UPDATE address SET address = '" + address + "', "
                + "address2 = '" + address2 + "', "
                + "cityId = '" + cityID + "', "
                + "postalCode = '" + zipcode + "', "
                + "lastUpdateBy = '" + state.getCurrentUser().getUserName() + "', "
                + "phone = '" + phone + "' WHERE addressId = " + selectedCustomer.getAddressID();
        Query.executeQuery(sql2);
    }

    // Confirm cancellation and go back to the main screen.
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

    // Confirm submission and
    public void handleSubmitButton() throws IOException {
        // Show submission confirmation
        Alerts.getAlert("confirmSubmission").showAndWait();
        if(Alerts.getAlert("confirmSubmission").getResult() == ButtonType.OK) {

            // get input from user and assign to variables
            String name = nameField.getText();
            String address = addressField.getText();
            String address2 = address2Field.getText();
            String city = cityField.getSelectionModel().getSelectedItem();
            String zipcode = zipField.getText();
            String phone = phoneField.getText();
            String country = countryField.getText();

            int cityID = -1;
            // Get cityID from database
            String sql2 = "Select cityId from city where city = '" + city + "'";
            Query.executeQuery(sql2);
            result = Query.getResult();
            try {
                while(result.next()) {
                    cityID = result.getInt("cityId");
                }
            } catch (SQLException ex) {
                System.out.println("Error: " + ex.getMessage());
            }

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

            if(check.isValid()) {
                // Update inputted form data to database
                updateCustomerRecords(name, address, address2, cityID, zipcode, phone);

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

    // Populate location combo box.
    private void populateLocationsBox() {
        ObservableList<String> cityList = FXCollections.observableArrayList();
        cityList.addAll("Seattle", "Renton", "Tacoma");
        cityField.setItems(cityList);
    }

    // Set country label based on selected city.
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
        // Get customer information from state
        selectedCustomer = state.getSelectedCustomer();
        country = selectedCustomer.getCountry();
        city = selectedCustomer.getCity();
        address = selectedCustomer.getAddress();
        address2 = selectedCustomer.getAddress2();
        postalCode = selectedCustomer.getPostalCode();
        phone = selectedCustomer.getPhone();

        // Fill forms with current data
        nameField.setText(selectedCustomer.getName());
        addressField.setText(address);
        address2Field.setText(address2);
        zipField.setText(postalCode);
        phoneField.setText(phone);
        cityField.getSelectionModel().select(city);

        // Set selected combobox, based off of the index from the observable list
        handleCountryLabel();
    }
}

