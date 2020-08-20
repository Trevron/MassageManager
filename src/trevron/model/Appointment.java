package trevron.model;

import trevron.utility.Query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class Appointment {
    private int appointmentId, customerId, userId, cost;
    private String title, description, contact, type, customerName;
    private LocalDateTime start, end;



    public Appointment(int appointmentId, int customerId, int userId, String title, String description, String contact, String type, LocalDateTime start, LocalDateTime end, int cost) {
        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.contact = contact;
        this.type = type;
        this.start = start;
        this.end = end;
        this.cost = cost;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
        String sql = "SELECT customerName FROM customer where customerId = " + customerId;
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
