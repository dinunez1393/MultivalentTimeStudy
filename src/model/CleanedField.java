package model;

import java.time.LocalDateTime;

/**
 * Class to create objects representing an instance (a row) of cleaned/processed/parsed raw data
 */
public class CleanedField {
    private String serialNumber;
    private LocalDateTime IPQC2CheckOut;
    private LocalDateTime firstScanCheckIn;
    private LocalDateTime firstScanTransaction;
    private LocalDateTime secondScanCheckIn;
    private LocalDateTime secondScanTransaction;
    private long IPQC2_FirstScanCheckIn_diff;
    private long firstScanCheckIn_firstScan_diff;
    private long firstScan_secondScanCheckIn_diff;
    private long secondScanCheckIn_secondScan_diff;
    private String SKU;
    private String customerPN;
    private String location;

    public CleanedField(String serialNumber, LocalDateTime[] transactions, long[] timeDifferenceMinutes, String SKU,
                        String customerPN, String location) {
        this.serialNumber = serialNumber;
        this.IPQC2CheckOut = transactions[0];
        this.firstScanCheckIn = transactions[1];
        this.firstScanTransaction = transactions[2];
        this.secondScanCheckIn = transactions[3];
        this.secondScanTransaction = transactions[4];
        this.IPQC2_FirstScanCheckIn_diff = timeDifferenceMinutes[0];
        this.firstScanCheckIn_firstScan_diff = timeDifferenceMinutes[1];
        this.firstScan_secondScanCheckIn_diff = timeDifferenceMinutes[2];
        this.secondScanCheckIn_secondScan_diff = timeDifferenceMinutes[3];
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

    public LocalDateTime getIPQC2CheckOut() {
        return IPQC2CheckOut;
    }

    public void setIPQC2CheckOut(LocalDateTime IPQC2CheckOut) {
        this.IPQC2CheckOut = IPQC2CheckOut;
    }

    public LocalDateTime getFirstScanCheckIn() {
        return firstScanCheckIn;
    }

    public void setFirstScanCheckIn(LocalDateTime firstScanCheckIn) {
        this.firstScanCheckIn = firstScanCheckIn;
    }

    public LocalDateTime getFirstScanTransaction() {
        return firstScanTransaction;
    }

    public void setFirstScanTransaction(LocalDateTime firstScanTransaction) {
        this.firstScanTransaction = firstScanTransaction;
    }

    public LocalDateTime getSecondScanCheckIn() {
        return secondScanCheckIn;
    }

    public void setSecondScanCheckIn(LocalDateTime secondScanCheckIn) {
        this.secondScanCheckIn = secondScanCheckIn;
    }

    public LocalDateTime getSecondScanTransaction() {
        return secondScanTransaction;
    }

    public void setSecondScanTransaction(LocalDateTime secondScanTransaction) {
        this.secondScanTransaction = secondScanTransaction;
    }

    public long getIPQC2_FirstScanCheckIn_diff() {
        return IPQC2_FirstScanCheckIn_diff;
    }

    public void setIPQC2_FirstScanCheckIn_diff(long IPQC2_FirstScanCheckIn_diff) {
        this.IPQC2_FirstScanCheckIn_diff = IPQC2_FirstScanCheckIn_diff;
    }

    public long getFirstScanCheckIn_firstScan_diff() {
        return firstScanCheckIn_firstScan_diff;
    }

    public void setFirstScanCheckIn_firstScan_diff(long firstScanCheckIn_firstScan_diff) {
        this.firstScanCheckIn_firstScan_diff = firstScanCheckIn_firstScan_diff;
    }

    public long getFirstScan_secondScanCheckIn_diff() {
        return firstScan_secondScanCheckIn_diff;
    }

    public void setFirstScan_secondScanCheckIn_diff(long firstScan_secondScanCheckIn_diff) {
        this.firstScan_secondScanCheckIn_diff = firstScan_secondScanCheckIn_diff;
    }

    public long getSecondScanCheckIn_secondScan_diff() {
        return secondScanCheckIn_secondScan_diff;
    }

    public void setSecondScanCheckIn_secondScan_diff(long secondScanCheckIn_secondScan_diff) {
        this.secondScanCheckIn_secondScan_diff = secondScanCheckIn_secondScan_diff;
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
}
