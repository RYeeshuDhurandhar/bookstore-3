package edu.cmu.bookstore.model.request;

/**
 * Request model for creating a new customer.
 *
 * This class represents the JSON payload accepted by the customer creation
 * endpoint. It is used to deserialize client input into a Java object
 * before validation and service-layer processing.
 */
public class CreateCustomerRequest {

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
     * Default constructor required for JSON deserialization.
     */
    public CreateCustomerRequest() {
    }

    /**
     * Returns the user identifier provided in the request.
     *
     * @return customer user identifier
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user identifier from the request payload.
     *
     * @param userId customer user identifier
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns the customer name provided in the request.
     *
     * @return customer name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the customer name from the request payload.
     *
     * @param name customer name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the phone number provided in the request.
     *
     * @return customer phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number from the request payload.
     *
     * @param phone customer phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns the primary address line provided in the request.
     *
     * @return primary address line
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the primary address line from the request payload.
     *
     * @param address primary address line
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns the secondary address line provided in the request.
     *
     * @return secondary address line
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * Sets the secondary address line from the request payload.
     *
     * @param address2 secondary address line
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * Returns the city provided in the request.
     *
     * @return city name
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city from the request payload.
     *
     * @param city city name
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Returns the state provided in the request.
     *
     * @return state code or name
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state from the request payload.
     *
     * @param state state code or name
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Returns the ZIP code provided in the request.
     *
     * @return ZIP code
     */
    public String getZipcode() {
        return zipcode;
    }

    /**
     * Sets the ZIP code from the request payload.
     *
     * @param zipcode ZIP code
     */
    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}