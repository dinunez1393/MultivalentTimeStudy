package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Class to create objects that will contain a raw data instance from the database query. In other words, it is a single row from an entire query.
 * Unlike the RawField class, this class contains all the entries for a single SerialNumber; e.g.: One serial number has several transaction
 * dates, check point names and checkpoint IDs; all these attributes are contained in one single SerialNumber
 */
public class RawFieldCluster {
    private String serialNumber;
    private ArrayList<Integer> checkPointId;
    private ArrayList<String> checkPointName;
    private ArrayList<LocalDateTime> transactionDate;
    private String SKU;
    private String customerPN;
    private String location;

    public RawFieldCluster(String serialNumber, String SKU, String customerPN, String location) {
        this.serialNumber = serialNumber;
        this.checkPointId = new ArrayList<>();
        this.checkPointName = new ArrayList<>();
        this.transactionDate = new ArrayList<>();
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

    public ArrayList<Integer> getCheckPointId() {
        return (ArrayList<Integer>) checkPointId.clone();
    }

    public void setCheckPointId(ArrayList<Integer> checkPointId) {
        this.checkPointId = checkPointId;
    }

    public ArrayList<String> getCheckPointName() {
        return (ArrayList<String>) checkPointName.clone();
    }

    public void setCheckPointName(ArrayList<String> checkPointName) {
        this.checkPointName = checkPointName;
    }

    public ArrayList<LocalDateTime> getTransactionDate() {
        return (ArrayList<LocalDateTime>) transactionDate.clone();
    }

    public void setTransactionDate(ArrayList<LocalDateTime> transactionDate) {
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
        return "RawFieldCluster{" +
                "serialNumber='" + serialNumber + '\'' +
                "\n, checkPointId=" + getCheckPointId().get(0) + getCheckPointId().get(1) + getCheckPointId().get(2) + getCheckPointId().get(3) + getCheckPointId().get(4) +
                "\n, checkPointName=" + getCheckPointName().get(0) + getCheckPointName().get(1) + getCheckPointName().get(2) + getCheckPointName().get(3) + getCheckPointName().get(4) +
                "\n, transactionDate=" + getTransactionDate().get(0) + getTransactionDate().get(1) + getTransactionDate().get(2) + getTransactionDate().get(3) + getTransactionDate().get(4) +
                "\n, SKU='" + SKU + '\'' +
                ", customerPN='" + customerPN + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}