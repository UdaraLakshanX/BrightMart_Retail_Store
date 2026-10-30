package model;

public class Supplier {
    private String supplierCode;
    private String name;
    private String address;
    private String contactDetails;

    public Supplier() {
    }

    public Supplier(String supplierCode, String name, String address, String contactDetails) {
        this.supplierCode = supplierCode;
        this.name = name;
        this.address = address;
        this.contactDetails = contactDetails;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }

    @Override
    public String toString() {
        return supplierCode + " - " + name;
    }
}
