package edu.cmu.bookstore.model;

/**
 * Domain model representing a customer, used for deserializing
 * Kafka messages in the CRM service.
 */
public class Customer {

    /**
     * Internal database identifier for the customer.
     */
    private Long id;

    /**
     * Unique user identifier, typically the customer's email address.
     */
    private String userId;

    /**
     * Full name of the customer.
     */
    private String name;

    /**
     * Customer phone number.
     */
    private String phone;

    /**
     * Primary street address.
     */
    private String address;

    /**
     * Secondary address information such as apartment or suite number.
     */
    private String address2;

    /**
     * City part of the customer's address.
     */
    private String city;

    /**
     * State part of the customer's address.
     */
    private String state;

    /**
     * ZIP code of the customer's address.
     */
    private String zipcode;

    /**
     * Default no-argument constructor required for object creation
     * and deserialization frameworks.
     */
    public Customer() {
    }

    /**
     * Returns the internal customer ID.
     *
     * @return customer ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the internal customer ID.
     *
     * @param id customer ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the user identifier.
     *
     * @return user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user identifier.
     *
     * @param userId user ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns the customer's full name.
     *
     * @return customer name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the customer's full name.
     *
     * @param name customer name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the customer's phone number.
     *
     * @return phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the customer's phone number.
     *
     * @param phone phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns the primary address line.
     *
     * @return primary address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the primary address line.
     *
     * @param address primary address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns the secondary address line.
     *
     * @return secondary address
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * Sets the secondary address line.
     *
     * @param address2 secondary address
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * Returns the city.
     *
     * @return city name
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city.
     *
     * @param city city name
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Returns the state.
     *
     * @return state code or name
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state.
     *
     * @param state state code or name
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Returns the ZIP code.
     *
     * @return ZIP code
     */
    public String getZipcode() {
        return zipcode;
    }

    /**
     * Sets the ZIP code.
     *
     * @param zipcode ZIP code
     */
    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}