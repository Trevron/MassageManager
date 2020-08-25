package trevron.model;

import trevron.utility.Query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class Appointment {
    private int appointmentID, customerID, userID, cost;
    private String title, description, contact, type, customerName;
    private LocalDateTime start, end;



    public Appointment(int appointmentID, int customerID, int userID, String title, String description, int cost, String contact, String type, LocalDateTime start, LocalDateTime end) {
        this.appointmentID = appointmentID;
        this.customerID = customerID;
        this.userID = userID;
        this.title = title;
        this.description = description;
        this.cost = cost;
        this.contact = contact;
        this.type = type;
        this.start = start;
        this.end = end;

    }

    public int getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getStart() {
        return start;
    }
    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    // This method uses a sql query to return the customer's name using the customerId.
    // It allows the appointment table view to have a Name column using property value factories.
    public String getCustomerName() {
        String sql = "SELECT customerName FROM customer where customerId = " + customerID;
        Query.executeQuery(sql);
        ResultSet result = Query.getResult();
        try {
            while(result.next()) {
                customerName = result.getString("customerName");
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return customerName;
    }

}
