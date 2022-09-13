package model;

import java.time.LocalDateTime;

/**
 * Class to create objects that will contain a raw data instance from the database query. In other words, it is a single row from an entire query
 */
public class RawField {
    private String serialNumber;
    private int checkPointId;
    private String checkPointName;
    private LocalDateTime transactionDate;
    private String SKU;
    private String customerPN;
    private String location;

    public RawField(String serialNumber, int checkPointId, String checkPointName,
                    LocalDateTime transactionDate, String SKU, String customerPN, String location) {
        this.serialNumber = serialNumber;
        this.checkPointId = checkPointId;
        this.checkPointName = checkPointName;
        this.transactionDate = transactionDate;
        this.SKU = SKU;
        this.customerPN = customerPN;
        this.location = location;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getCheckPointId() {
        return checkPointId;
    }

    public void setCheckPointId(int checkPointId) {
        this.checkPointId = checkPointId;
    }

    public String getCheckPointName() {
        return checkPointName;
    }

    public void setCheckPointName(String checkPointName) {
        this.checkPointName = checkPointName;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU = SKU;
    }

    public String getCustomerPN() {
        return customerPN;
    }

    public void setCustomerPN(String customerPN) {
        this.customerPN = customerPN;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "RawField{" +
                "serialNumber='" + serialNumber + '\'' +
                ", checkPointId=" + checkPointId +
                ", checkPointName='" + checkPointName + '\'' +
                ", transactionDate=" + transactionDate +
                ", SKU='" + SKU + '\'' +
                ", customerPN='" + customerPN + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
