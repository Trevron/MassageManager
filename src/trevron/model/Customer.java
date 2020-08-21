package trevron.model;

public class Customer {

    String customerID, name, addressID, address, address2, postalCode, phone, city, cityID, country, countryID;

    public Customer(String customerID, String name, String addressID, String address, String address2, String postalCode, String phone, String city, String cityID, String country, String countryID) {
        this.customerID = customerID;
        this.name = name;
        this.addressID = addressID;
        this.address = address;
        this.address2 = address2;
        this.postalCode = postalCode;
        this.phone = phone;
        this.city = city;
        this.cityID = cityID;
        this.country = country;
        this.countryID = countryID;
    }


    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddressID() {
        return addressID;
    }

    public void setAddressID(String addressID) {
        this.addressID = addressID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityID() {
        return cityID;
    }

    public void setCityID(String cityID) {
        this.cityID = cityID;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }
}
