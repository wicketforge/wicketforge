package wicketforge.test;

import java.util.List;

/**
 * Simple test class used to test property model completion.
 *
 * Created by IntelliJ IDEA.
 * User: nick
 * Date: Dec 24, 2009
 * Time: 10:38:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class Person {

    private String firstName;
    private String lastName;
    private List<Address> addresses;
    private Address homeAddress;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }
}
