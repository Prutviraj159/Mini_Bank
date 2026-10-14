package model;

import model.annotation.Id;
import model.annotation.MaxLength;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a bank customer.
 * Introduced in Practical 2 (Part B), enhanced with nested Address & Cloneable in Practical 3,
 * annotations in Practical 7, Serializable in Practical 11, and Id for Repository in Practical 13.
 */
public class Customer implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private final String customerId;

    @MaxLength(50)
    private String name;

    @MaxLength(50)
    private String email;

    @MaxLength(10)
    private String mobile;

    private Address address;

    private static long customerCounter = 100;

    /**
     * Nested static class Address as specified in Practical 3 (Part B).
     */
    public static class Address implements Serializable, Cloneable {
        private static final long serialVersionUID = 1L;

        private String line;
        private String city;
        private String pincode;

        public Address(String line, String city, String pincode) {
            this.line = line;
            this.city = city;
            this.pincode = pincode;
        }

        public String getLine() {
            return line;
        }

        public String getCity() {
            return city;
        }

        public String getPincode() {
            return pincode;
        }

        @Override
        public String toString() {
            return line + ", " + city + " - " + pincode;
        }

        @Override
        public Address clone() {
            try {
                return (Address) super.clone();
            } catch (CloneNotSupportedException e) {
                return new Address(this.line, this.city, this.pincode);
            }
        }
    }

    public Customer(String name, String email, String mobile) {
        this(name, email, mobile, null);
    }

    public Customer(String name, String email, String mobile, Address address) {
        this.customerId = generateCustomerId();
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.address = address;
    }

    public static synchronized String generateCustomerId() {
        return "CUST" + (++customerCounter);
    }

    public static synchronized void resetCounter(long start) {
        customerCounter = start;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public Customer clone() {
        try {
            Customer copy = (Customer) super.clone();
            if (this.address != null) {
                copy.address = this.address.clone();
            }
            return copy;
        } catch (CloneNotSupportedException e) {
            Customer copy = new Customer(this.name, this.email, this.mobile);
            if (this.address != null) {
                copy.setAddress(this.address.clone());
            }
            return copy;
        }
    }

    @Override
    public String toString() {
        return "Customer [" + customerId + " | " + name + " | " + email + " | " + mobile + 
               (address != null ? " | " + address : "") + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer other)) return false;
        return Objects.equals(customerId, other.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }
}
