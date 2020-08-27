package trevron.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import trevron.model.Bill;
import trevron.utility.BillFormatter;
import trevron.utility.Query;
import trevron.utility.State;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ReportController implements Initializable {
    @FXML TableView<Bill> billTableView;
    @FXML TableColumn<Bill, String> customerColumn;
    @FXML TableColumn<Bill, String> costColumn;
    @FXML TableColumn<Bill, String> appointmentColumn;
    @FXML TableColumn<Bill, String> dateColumn;
    @FXML TextField searchField;
    @FXML Button printButton;
    @FXML Button homeButton;
    State state = State.getInstance();
    Stage stage;
    Parent root;

    ObservableList<Bill> billList = FXCollections.observableArrayList();

    private void updateTableData() {
        billList.clear();
        String sql = "SELECT c.customerName, SUM(a.cost) AS totalCost, COUNT(a.appointmentId) AS totalAppointments, start "
                + "FROM customer AS c JOIN appointment AS a ON c.customerId = a.customerId GROUP BY c.customerName, Month(start)";

        Query.executeQuery(sql);
        ResultSet result = Query.getResult();
        try {
            while(result.next()) {
                billList.add(new Bill (result.getString("c.customerName"), result.getTimestamp("a.start").toLocalDateTime(),
                         result.getInt("totalCost"), result.getInt("totalAppointments")));
            }
        } catch(SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        billTableView.setItems(billList);
    }

    public void searchTableData() {
        billList.clear();
        String sql = "SELECT c.customerName, SUM(a.cost) AS totalCost, COUNT(a.appointmentId) AS totalAppointments, start "
                + "FROM customer AS c JOIN appointment AS a ON c.customerId = a.customerId where c.customerName LIKE '%"
                + searchField.getText() +"%' GROUP BY c.customerName, Month(start)";

        Query.executeQuery(sql);
        ResultSet result = Query.getResult();
        try {
            while(result.next()) {
                billList.add(new Bill (result.getString("c.customerName"), result.getTimestamp("a.start").toLocalDateTime(),
                        result.getInt("totalCost"), result.getInt("totalAppointments")));
            }
        } catch(SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        billTableView.setItems(billList);
    }

    public void checkSelected() {
        if (billTableView.getSelectionModel().getSelectedItem() != null) {
            printButton.setDisable(false);
        } else {
            printButton.setDisable(true);
        }
    }

    public void handlePrintButton() throws IOException {
        Bill selectedBill = billTableView.getSelectionModel().getSelectedItem();
        File myFile = new File("billPrinter.txt");
        myFile.createNewFile();
        BufferedWriter output = new BufferedWriter(new FileWriter(myFile));
        output.write(BillFormatter.format(selectedBill.getCustomerName(), selectedBill.getCost(), selectedBill.getAppointmentNumber(), selectedBill.getMonth()));
        output.close();
        ProcessBuilder pb = new ProcessBuilder("Notepad.exe", "billPrinter.txt");
        pb.start();

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

    public void handleAppointmentButton() throws IOException {
        stage = (Stage) homeButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/appointment.fxml"));
        root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize table columns
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));
        appointmentColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentNumber"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("month"));

        updateTableData();
    }
}
