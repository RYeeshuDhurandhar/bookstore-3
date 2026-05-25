package edu.cmu.bookstore.model;

/**
 * Domain model representing a customer in the bookstore system.
 *
 * This class stores the customer identifier and contact/address
 * information used by the customer-related API endpoints and
 * persistence layer.
 */
public class Customer {

    /**
     * Internal unique identifier for the customer.
     */
    private Long id;

    /**
     * User identifier associated with the customer, such as an email address.
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
     * Primary street address of the customer.
     */
    private String address;

    /**
     * Secondary address line such as apartment, suite, or unit number.
     */
    private String address2;

    /**
     * City portion of the customer's address.
     */
    private String city;

    /**
     * State portion of the customer's address.
     */
    private String state;

    /**
     * ZIP code portion of the customer's address.
     */
    private String zipcode;

    /**
     * Default constructor required for serialization/deserialization
     * and framework usage.
     */
    public Customer() {
    }

    /**
     * Constructs a customer with all fields initialized.
     *
     * @param id internal unique identifier
     * @param userId user identifier associated with the customer
     * @param name full customer name
     * @param phone customer phone number
     * @param address primary address line
     * @param address2 secondary address line
     * @param city city name
     * @param state state code or name
     * @param zipcode ZIP code
     */
    public Customer(Long id, String userId, String name, String phone, String address,
                    String address2, String city, String state, String zipcode) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
    }

    /**
     * Returns the internal identifier of the customer.
     *
     * @return customer ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the internal identifier of the customer.
     *
     * @param id customer ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the user identifier associated with the customer.
     *
     * @return customer user identifier
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user identifier associated with the customer.
     *
     * @param userId customer user identifier
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns the full name of the customer.
     *
     * @return customer name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the full name of the customer.
     *
     * @param name customer name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the phone number of the customer.
     *
     * @return customer phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number of the customer.
     *
     * @param phone customer phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns the primary address line of the customer.
     *
     * @return primary address line
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the primary address line of the customer.
     *
     * @param address primary address line
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns the secondary address line of the customer.
     *
     * @return secondary address line
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * Sets the secondary address line of the customer.
     *
     * @param address2 secondary address line
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * Returns the city of the customer.
     *
     * @return city name
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city of the customer.
     *
     * @param city city name
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Returns the state of the customer.
     *
     * @return state code or name
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state of the customer.
     *
     * @param state state code or name
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Returns the ZIP code of the customer.
     *
     * @return ZIP code
     */
    public String getZipcode() {
        return zipcode;
    }

    /**
     * Sets the ZIP code of the customer.
     *
     * @param zipcode ZIP code
     */
    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}