package trevron.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import trevron.model.Customer;
import trevron.utility.Alerts;
import trevron.utility.Query;
import trevron.utility.State;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CustomerController implements Initializable {

    @FXML private TableView<Customer> customerTableView;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> addressColumn;
    @FXML private TableColumn<Customer, String> address2Column;
    @FXML private TableColumn<Customer, String> cityColumn;
    @FXML private TableColumn<Customer, String> zipcodeColumn;
    @FXML private TableColumn<Customer, String> countryColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML Button deleteCustomerButton;
    @FXML Button editCustomerButton;
    @FXML Button homeButton;
    State state = State.getInstance();
    Stage stage;
    Parent root;

    ObservableList<Customer> customerList = FXCollections.observableArrayList();

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

    public void handleAppointmentButton() throws IOException {
        stage = (Stage) homeButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/appointment.fxml"));
        root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // Enable or disable customer buttons if row is selected
    public void checkSelected() {
        if (customerTableView.getSelectionModel().getSelectedItem() != null) {
            deleteCustomerButton.setDisable(false);
            editCustomerButton.setDisable(false);
        } else {
            deleteCustomerButton.setDisable(true);
            editCustomerButton.setDisable(true);
        }
    }

    // Update the selected customer.
    public void updateButtonHandler() throws IOException {
        // Check to see if there is a customer selected.
        if(customerTableView.getSelectionModel().getSelectedItem() != null) {

            // Set selected customer in State.
            state.setSelectedCustomer(customerTableView.getSelectionModel().getSelectedItem());

            // Switch to update customer screen.
            stage = (Stage) homeButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/updatecustomerwindow.fxml"));
            root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            Alerts.getAlert("nullSelectCustomer").show();
        }

    }
    // Delete selected customer.
    public void deleteButtonHandler() {
        // Check to see if a customer is selected.
        if(customerTableView.getSelectionModel().getSelectedItem() != null) {

            // Set selected customer in State.
            state.setSelectedCustomer(customerTableView.getSelectionModel().getSelectedItem());

            // If deletion is confirmed, check to see if there are any appointments involving that customer.
            Alerts.getAlert("confirmDeletion").showAndWait();
            if(Alerts.getAlert("confirmDeletion").getResult() == ButtonType.OK) {
                int exists = -1;
                ResultSet result;
                String sql1 = "Select * FROM appointment where customerId = " + state.getSelectedCustomer().getCustomerID();
                try {
                    Query.executeQuery(sql1);
                    result = Query.getResult();
                    while(result.next()) {
                        exists = result.getInt("appointmentId");
                    }
                } catch(SQLException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
                // If any appointments are found, tell user to delete appointments and then try again.
                // Otherwise, delete selected customer.
                if(exists > -1) {
                    Alerts.getAlert("appointmentsExist").show();
                } else if (exists == -1) {
                    String sql2 = "DELETE FROM customer WHERE customerId = " + state.getSelectedCustomer().getCustomerID();
                    Query.executeQuery(sql2);
                    updateTableData();
                }
            }
        }
    }

    // Reload table data from database
    private void updateTableData() {
        // Initialize customer list array
        customerList.clear();
        customerTableView.refresh();

        // Get customer table from database and then bind to tableview
        try{
            String sqlCustomers = "SELECT a.customerId, a.customerName, a.addressId, b.address, b.address2, b.postalCode, b.phone, b.cityId, c.city, c.countryId, d.country \n" +
                    "\tFROM customer a JOIN address b on a.addressId = b.addressId\n" +
                    "\tJOIN city c on b.cityId = c.cityId\n" +
                    "\tJOIN country d on c.countryId = d.countryId";
            Query.executeQuery(sqlCustomers);
            ResultSet result = Query.getResult();
            while(result.next()) {
                customerList.add(new Customer(result.getString("a.customerId"), result.getString("a.customerName"),
                        result.getString("a.addressId"), result.getString("b.address"), result.getString("b.address2"),
                        result.getString("b.postalCode"), result.getString("b.phone"), result.getString("c.city"),
                        result.getString("b.cityId"), result.getString("d.country"), result.getString("c.countryId")));
            }
        } catch(SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        customerTableView.setItems(customerList);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Disable buttons
        deleteCustomerButton.setDisable(true);
        editCustomerButton.setDisable(true);
        
        // Initialize columns and table data
        updateTableData();
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        address2Column.setCellValueFactory(new PropertyValueFactory<>("address2"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        zipcodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
    }

}
