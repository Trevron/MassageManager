package trevron.utility;

import trevron.model.Appointment;
import trevron.model.Customer;
import trevron.model.User;

public class State {

    private static final State instance = new State();
    private Customer selectedCustomer;
    private Appointment selectedAppointment;
    private User currentUser;


    private State(){
    }

    // Getters and setters
    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public Appointment getSelectedAppointment() {
        return selectedAppointment;
    }

    public void setSelectedAppointment(Appointment selectedAppointment) {
        this.selectedAppointment = selectedAppointment;
    }

    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }

    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    public static State getInstance(){
        return instance;
    }
}
