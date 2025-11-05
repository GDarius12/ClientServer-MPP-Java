package Darius.domain;

import java.io.Serializable;

public class Employee extends Person implements Serializable {
    private String phoneNumber;
    private String address;
    private String username;
    private String password;

    public Employee(String name, String surname, String cnp, String phoneNumber, String address, String username, String password) {
        super(name, surname, cnp);
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.username = username;
        this.password = password;
    }
    public Employee() {
        super();
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
