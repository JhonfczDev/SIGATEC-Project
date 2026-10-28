package com.sigatec.model;

import java.math.BigDecimal;

/**
 * Individual technology product, identified by its serial number.
 *
 * <p>The serial number is the identity and never changes after creation.
 * The remaining data is modified atomically through {@link #updateDetails}.</p>
 */
public final class Product implements Identifiable<String> {

    private final String serialNumber;
    private String name;
    private String brand;
    private String model;
    private BigDecimal price;

    public Product(String name, String brand, String model, String serialNumber, BigDecimal price) {
        this.serialNumber = normalizeSerial(serialNumber);
        this.name = Validations.requireText(name, "El nombre");
        this.brand = Validations.requireText(brand, "La marca");
        this.model = Validations.requireText(model, "El modelo");
        this.price = Validations.requirePositive(price, "El precio");
    }

    /**
     * Updates all modifiable data. If any value is invalid none is applied
     * (the object is never left half-updated).
     */
    public void updateDetails(String name, String brand, String model, BigDecimal price) {
        String newName = Validations.requireText(name, "El nombre");
        String newBrand = Validations.requireText(brand, "La marca");
        String newModel = Validations.requireText(model, "El modelo");
        BigDecimal newPrice = Validations.requirePositive(price, "El precio");

        this.name = newName;
        this.brand = newBrand;
        this.model = newModel;
        this.price = newPrice;
    }

    /** Compares with a serial number typed by the user (ignores case and surrounding spaces). */
    public boolean hasSerialNumber(String serial) {
        return serial != null && serialNumber.equals(serial.strip().toUpperCase());
    }

    private static String normalizeSerial(String serial) {
        return Validations.requireText(serial, "El número de serie").toUpperCase();
    }

    @Override
    public String getId() {
        return serialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Product other && serialNumber.equals(other.serialNumber));
    }

    @Override
    public int hashCode() {
        return serialNumber.hashCode();
    }

    @Override
    public String toString() {
        return name + " (" + brand + " " + model + ", serial " + serialNumber + ")";
    }
}
