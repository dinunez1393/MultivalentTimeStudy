package Utilities;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import model.CleanedField;
import model.RawField;
import model.RawFieldCluster;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * This abstract class has methods for reading CSV files, cleaning data from the read CSV file, and writing back to a CSV file with cleaned data
 */
public abstract class ReaderWriter {

    /**
     * This method reads raw data from a CSV file
     * @param filePath the file path of the CSV file to be read
     * @param constants various constants to use for initializing other variables in the method
     */
    public static ArrayList csvReader(String filePath, int... constants) throws IOException, CsvValidationException {
        final int MAX_LIST_CAPACITY = constants[0];
        final int MAX_ROW_CAPACITY = constants[1];
        var fileReader = new FileReader(filePath);
        var csvReader = new CSVReader(fileReader);
        var nextRecord = new String[MAX_ROW_CAPACITY];
        var rawData = new ArrayList<RawField>(MAX_LIST_CAPACITY);
        String serialNumber, checkPointName, SKU, customerPN, location;
        int checkPointId;
        LocalDateTime transactionDate;

        //Read all the records and save it on an ArrayList
        while ((nextRecord = csvReader.readNext()) != null) {
            serialNumber = nextRecord[0];
            checkPointId = Integer.parseInt(nextRecord[1]);
            checkPointName = nextRecord[2];
            transactionDate = Validator.fromSQLDateTimeToLocalDateTime(nextRecord[3]);
            SKU = nextRecord[4];
            customerPN = nextRecord[5];
            location = nextRecord[6];

            rawData.add(new RawField(serialNumber, checkPointId, checkPointName, transactionDate, SKU, customerPN, location));
        }
        csvReader.close();
        Logger.getGlobal().info("The data from CSV file \"" + filePath + "\" read successfully\n");
        return rawData;
    }

    /**
     * This method arranges an ArrayList with raw data that has been read from a CSV file into clusters. E.g.: One SerialNumber
     * can contain several checkpoint IDs, checkpoint names and transaction timestamps; all these various items are saved each in
     * a cluster per SerialNumber occurrence.
     * @param rawData the ArrayList with the raw data
     * @return an ArrayList with clustered raw data
     */
    public static ArrayList listCluster(ArrayList<RawField> rawData, int... checkPointIDs) { //The checkPointIDs should be in ASC order
        var rawClusteredData = new ArrayList<RawFieldCluster>();
        String serialNumber, SKU, customerPN, location, tempSerialNumber;
        var tempCheckPointNames = new ArrayList<String>();
        var tempCheckPointIds = new ArrayList<Integer>();
        var tempTransactionDates = new ArrayList<LocalDateTime>();

        /*
        NOTICE: For an apparent and undiscovered bug on the loop below, the CSV file needs to have a dummy value
        on the first row. This dummy value is necessary for the subsequent values to save correctly on the unordered
        and the ordered clustered lists. It can then be omitted for calculations on the exported (written) CSV file
        */

        //Go through all serial numbers
        for (int i = 0; i < rawData.size(); i++) {
            tempSerialNumber = rawData.get(i).getSerialNumber();

            serialNumber = rawData.get(i).getSerialNumber();
            tempCheckPointIds.add(rawData.get(i).getCheckPointId());
            tempCheckPointNames.add(rawData.get(i).getCheckPointName());
            tempTransactionDates.add(rawData.get(i).getTransactionDate());
            SKU = rawData.get(i).getSKU();
            customerPN = rawData.get(i).getCustomerPN();
            location = rawData.get(i).getLocation();

            //Cluster various checkpoint IDs, checkpoint names, and transaction timestamps for one serial number
            for (int j = i + 1; j < rawData.size(); j++) {
                if (!rawData.get(j).getSerialNumber().equals(tempSerialNumber)) {
                    i = j - 1;
                    break;
                }
                else {
                    tempCheckPointIds.add(rawData.get(j).getCheckPointId());
                    tempCheckPointNames.add(rawData.get(j).getCheckPointName());
                    tempTransactionDates.add(rawData.get(j).getTransactionDate());
                }
            }
            var newRawScanCluster = new RawFieldCluster(serialNumber, SKU, customerPN, location);
            newRawScanCluster.setCheckPointId((ArrayList<Integer>) tempCheckPointIds.clone());
            newRawScanCluster.setCheckPointName((ArrayList<String>) tempCheckPointNames.clone());
            newRawScanCluster.setTransactionDate((ArrayList<LocalDateTime>) tempTransactionDates.clone());
            rawClusteredData.add(newRawScanCluster);

            tempCheckPointIds.clear();
            tempCheckPointNames.clear();
            tempTransactionDates.clear();
        }
        Logger.getGlobal().info("The raw data has been assembled in clusters successfully\n");
        return organizeClusteredList(rawClusteredData, checkPointIDs); //IMPORTANT: This last method usage is necessary for the parent method to run correctly
    }

    /**
     * Helper method to arrange the cluster in DESC chronological order and assign 'null' representations to instances that don't have a value
     * @param rawClusteredData the unordered raw cluster list
     * @param checkPointIDs the checkpoints - in ASC order
     * @return the raw clustered list with the clusters in order
     */
    private static ArrayList organizeClusteredList(ArrayList<RawFieldCluster> rawClusteredData, int... checkPointIDs) { //The checkPointIDs should be in ASC order
        final int NOT_FOUND = -1;
        final int LAST_CHECKPOINT_POS = 0;
        int tempIndexOf;
        final LocalDateTime NULLIFY_DATE = LocalDateTime.of(2000, 1, 1, 0, 1);
        var tempCheckPointIDs = new ArrayList<Integer>();
        var tempCheckPointNames = new ArrayList<String>();
        var tempTransactions = new ArrayList<LocalDateTime>();
        var organizedClusteredList = new ArrayList<RawFieldCluster>(rawClusteredData.size());

        //Go over all SerialNumber clusters
        for (RawFieldCluster rawClusteredField : rawClusteredData) {
            //Order the cluster in DESC order (from the last checkpoint to the first)
            for (int i = 0; i < checkPointIDs.length; i++) {
                if (!rawClusteredField.getCheckPointId().contains(checkPointIDs[checkPointIDs.length - 1 - i])) {
                    tempCheckPointIDs.add(LAST_CHECKPOINT_POS + i, NOT_FOUND);
                    tempCheckPointNames.add(LAST_CHECKPOINT_POS + i, "");
                    tempTransactions.add(LAST_CHECKPOINT_POS + i, NULLIFY_DATE);
                }
                else {
                    tempIndexOf = rawClusteredField.getCheckPointId().indexOf(checkPointIDs[checkPointIDs.length - 1 - i]);

                    tempCheckPointIDs.add(LAST_CHECKPOINT_POS + i, rawClusteredField.getCheckPointId().get(tempIndexOf));
                    tempCheckPointNames.add(LAST_CHECKPOINT_POS + i, rawClusteredField.getCheckPointName().get(tempIndexOf));
                    tempTransactions.add(LAST_CHECKPOINT_POS + i, rawClusteredField.getTransactionDate().get(tempIndexOf));
                }
            }
            rawClusteredField.setCheckPointId((ArrayList<Integer>) tempCheckPointIDs.clone());
            rawClusteredField.setCheckPointName((ArrayList<String>) tempCheckPointNames.clone());
            rawClusteredField.setTransactionDate((ArrayList<LocalDateTime>) tempTransactions.clone());
            organizedClusteredList.add(rawClusteredField);

            tempCheckPointIDs.clear();
            tempCheckPointNames.clear();
            tempTransactions.clear();
        }
        return organizedClusteredList;
    }

    /**
     * This method writes cleaned data to a CSV file
     * @param filePath the path of the new CSV file
     * @param headerNames the names of each of the columns of the header row
     * @param cleanedData the ArrayList containing the cleaned data
     * @param maxRowCapacity the maximum columns in one row
     * @param isClustered whether the ArrayList with raw data is clustered or not
     */
    public static void csvWriter(String filePath, String[] headerNames, ArrayList<CleanedField> cleanedData, int maxRowCapacity, boolean isClustered) throws IOException {
        var file = new File(filePath);
        var fileWriter = new FileWriter(file);
        var csvWriter = new CSVWriter(fileWriter);
        var nextRecord = new String[maxRowCapacity];
        final String NULLIFY_STR = "null";
        final LocalDateTime NULLIFY_DATE = LocalDateTime.of(2000, 1, 1, 0, 1);
        final int NOT_FOUND = -1;

        //Write all the cleaned data from the ArrayList into a CSV
        csvWriter.writeNext(headerNames); //header row

        for (CleanedField cleanedField : cleanedData) {
            nextRecord[0] = cleanedField.getSerialNumber();
            nextRecord[10] = cleanedField.getSKU();
            nextRecord[11] = cleanedField.getCustomerPN();
            nextRecord[12] = cleanedField.getLocation();
            if (isClustered) { //Transaction and minutes difference writer for a clustered list
                if (cleanedField.getIPQC2CheckOut().equals(NULLIFY_DATE)) {
                    nextRecord[1] = NULLIFY_STR;
                }
                else {
                    nextRecord[1] = Validator.fromLocalDateTimeToExcelFormat(cleanedField.getIPQC2CheckOut());
                }
                if (cleanedField.getFirstScanCheckIn().equals(NULLIFY_DATE)) {
                    nextRecord[2] = NULLIFY_STR;
                }
                else {
                    nextRecord[2] = Validator.fromLocalDateTimeToExcelFormat(cleanedField.getFirstScanCheckIn());
                }
                if (cleanedField.getFirstScanTransaction().equals(NULLIFY_DATE)) {
                    nextRecord[3] = NULLIFY_STR;
                }
                else {
                    nextRecord[3] = Validator.fromLocalDateTimeToExcelFormat(cleanedField.getFirstScanTransaction());
                }
                if (cleanedField.getSecondScanCheckIn().equals(NULLIFY_DATE)) {
                    nextRecord[4] = NULLIFY_STR;
                }
                else {
                    nextRecord[4] = Validator.fromLocalDateTimeToExcelFormat(cleanedField.getSecondScanCheckIn());
                }
                if (cleanedField.getSecondScanTransaction().equals(NULLIFY_DATE)) {
                    nextRecord[5] = NULLIFY_STR;
                }
                else {
                    nextRecord[5] = Validator.fromLocalDateTimeToExcelFormat(cleanedField.getSecondScanTransaction());
                }
                if (cleanedField.getIPQC2_FirstScanCheckIn_diff() == NOT_FOUND) {
                    nextRecord[6] = NULLIFY_STR;
                }
                else {
                    nextRecord[6] = Long.toString(cleanedField.getIPQC2_FirstScanCheckIn_diff());
                }
                if (cleanedField.getFirstScanCheckIn_firstScan_diff() == NOT_FOUND) {
                    nextRecord[7] = NULLIFY_STR;
                }
                else {
                    nextRecord[7] = Long.toString(cleanedField.getFirstScanCheckIn_firstScan_diff());
                }
                if (cleanedField.getFirstScan_secondScanCheckIn_diff() == NOT_FOUND) {
                    nextRecord[8] = NULLIFY_STR;
                }
                else {
                    nextRecord[8] = Long.toString(cleanedField.getFirstScan_secondScanCheckIn_diff());
                }
                if (cleanedField.getSecondScanCheckIn_secondScan_diff() == NOT_FOUND) {
                    nextRecord[9] = NULLIFY_STR;
                }
                else {
                    nextRecord[9] = Long.toString(cleanedField.getSecondScanCheckIn_secondScan_diff());
                }
            }
            else { //Non clustered list
                nextRecord[1] = Validator.fromLocalDateTimeToExcelFormat(cleanedField.getIPQC2CheckOut());
                nextRecord[2] = Validator.fromLocalDateTimeToExcelFormat(cleanedField.getFirstScanCheckIn());
                nextRecord[3] = Validator.fromLocalDateTimeToExcelFormat(cleanedField.getFirstScanTransaction());
                nextRecord[4] = Validator.fromLocalDateTimeToExcelFormat(cleanedField.getSecondScanCheckIn());
                nextRecord[5] = Validator.fromLocalDateTimeToExcelFormat(cleanedField.getSecondScanTransaction());
                nextRecord[6] = Long.toString(cleanedField.getIPQC2_FirstScanCheckIn_diff());
                nextRecord[7] = Long.toString(cleanedField.getFirstScanCheckIn_firstScan_diff());
                nextRecord[8] = Long.toString(cleanedField.getFirstScan_secondScanCheckIn_diff());
                nextRecord[9] = Long.toString(cleanedField.getSecondScanCheckIn_secondScan_diff());
            }
            csvWriter.writeNext(nextRecord);
        }

        csvWriter.close();
        Logger.getGlobal().info("The data is cleaned and saved successfully on CSV file named \"" + filePath + "\" in the main folder\n");
    }

    /**
     * This method cleans organized raw data from a clustered list
     * @param rawClusteredData the organized raw clustered list
     * @param constants various constants to use for initializing other variables in the method: checkpoint IDs, number of Deltas in minutes, number of timestamps
     *                  (a.k.a. transactions)
     * @return a new ArrayList with cleaned data
     */
    public static ArrayList clusteredListDataCleaner(ArrayList<RawFieldCluster> rawClusteredData, int... constants) {
        var cleanedData = new ArrayList<CleanedField>();
        var minutesDifference = new long[constants[0]];
        var transactions = new LocalDateTime[constants[1]];
        var tempReversedTransactions = new ArrayList<LocalDateTime>(); //To temporarily store the original transaction cluster of each instance in reverse - ASC order
        final int FIRST_CHECKPOINT_ID_POS = constants[2];
        final int NOT_FOUND = -1;

        //Traverse all the list with raw clustered data to get the minutes difference of each checkpoint transaction
        for (RawFieldCluster rawClusteredField : rawClusteredData) {
            for (int i = 0; i < minutesDifference.length; i++) {
                if (rawClusteredField.getCheckPointId().get(FIRST_CHECKPOINT_ID_POS - i) != NOT_FOUND &&
                        rawClusteredField.getCheckPointId().get(FIRST_CHECKPOINT_ID_POS - 1 - i) != NOT_FOUND) {
                    minutesDifference[i] = ChronoUnit.MINUTES.between(rawClusteredField.getTransactionDate().get(FIRST_CHECKPOINT_ID_POS - i),
                                            rawClusteredField.getTransactionDate().get(FIRST_CHECKPOINT_ID_POS - 1 - i));
                }
                else {
                    minutesDifference[i] = -1;
                }
            }
            tempReversedTransactions = rawClusteredField.getTransactionDate();
            Collections.reverse(tempReversedTransactions);
            tempReversedTransactions.toArray(transactions);

            cleanedData.add(new CleanedField(rawClusteredField.getSerialNumber(), transactions, minutesDifference, rawClusteredField.getSKU(),
                    rawClusteredField.getCustomerPN(), rawClusteredField.getLocation()));

            tempReversedTransactions.clear();
        }
        return cleanedData;
    }

    /**
     * This method cleans raw data from a read CSV file
     * @param rawData the ArrayList containing raw data
     * @param constants various constants to use for initializing other variables in the method: checkpoint IDs, number of Deltas in minutes, number of timestamps
     *                  (a.k.a. transactions)
     * @return a new ArrayList with cleaned data
     */
    public static ArrayList dataCleaner(ArrayList<RawField> rawData, int... constants) {
        final int FIRST_CHECKPOINT_ID = constants[0];
        final int SECOND_CHECKPOINT_ID = constants[1];
        final int THIRD_CHECKPOINT_ID = constants[2];
        final int FOURTH_CHECKPOINT_ID = constants[3];
        final int FIFTH_CHECKPOINT_ID = constants[4];
        var cleanedData = new ArrayList<CleanedField>();
        String serialNumber, SKU, customerPN, location;
        var minutesDifference = new long[constants[5]];
        var transactions = new LocalDateTime[constants[6]];

        //Clean all the raw data into a new list. The list from Excel is sorted by Transaction date first in DESC order and then by Serial Number in ASC order
        for (int i = 0; i < rawData.size(); i++) {
            if (rawData.get(i).getCheckPointId() == FIFTH_CHECKPOINT_ID) {
                clean_data:
                for (int j = i + 1; j < rawData.size(); j++) {
                    if (!rawData.get(j).getSerialNumber().equals(rawData.get(i).getSerialNumber()))
                        break;
                    else if (rawData.get(j).getCheckPointId() == FOURTH_CHECKPOINT_ID) {
                        for (int k = j + 1; k < rawData.size(); k++) {
                            if (!rawData.get(k).getSerialNumber().equals(rawData.get(i).getSerialNumber()))
                                break clean_data;
                            else if (rawData.get(k).getCheckPointId() == THIRD_CHECKPOINT_ID) {
                                for (int l = k + 1; l < rawData.size(); l++) {
                                    if (!rawData.get(l).getSerialNumber().equals(rawData.get(i).getSerialNumber()))
                                        break clean_data;
                                    else if (rawData.get(l).getCheckPointId() == SECOND_CHECKPOINT_ID) {
                                        for (int m = l + 1; m < rawData.size(); m++) {
                                            if (!rawData.get(m).getSerialNumber().equals(rawData.get(i).getSerialNumber()))
                                                break clean_data;
                                            else if (rawData.get(m).getCheckPointId() == FIRST_CHECKPOINT_ID) {
                                                serialNumber = rawData.get(i).getSerialNumber();

                                                transactions[0] = rawData.get(m).getTransactionDate();
                                                transactions[1] = rawData.get(l).getTransactionDate();
                                                transactions[2] = rawData.get(k).getTransactionDate();
                                                transactions[3] = rawData.get(j).getTransactionDate();
                                                transactions[4] = rawData.get(i).getTransactionDate();

                                                minutesDifference[0] = ChronoUnit.MINUTES.between(rawData.get(m).getTransactionDate(),
                                                        rawData.get(l).getTransactionDate());
                                                minutesDifference[1] = ChronoUnit.MINUTES.between(rawData.get(l).getTransactionDate(),
                                                        rawData.get(k).getTransactionDate());
                                                minutesDifference[2] = ChronoUnit.MINUTES.between(rawData.get(k).getTransactionDate(),
                                                        rawData.get(j).getTransactionDate());
                                                minutesDifference[3] = ChronoUnit.MINUTES.between(rawData.get(j).getTransactionDate(),
                                                        rawData.get(i).getTransactionDate());

                                                SKU = rawData.get(i).getSKU();
                                                customerPN = rawData.get(i).getCustomerPN();
                                                location = rawData.get(i).getLocation();

                                                cleanedData.add(new CleanedField(serialNumber, transactions, minutesDifference, SKU, customerPN, location));
                                                i = m; //Jump into the next occurrence
                                                break clean_data;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return cleanedData;
    }
}

//PROTO CODE

//    final int MAX_LIST_CAPACITY = 13008;
//    final int MAX_ROW_CAPACITY = 7;
//    final int FIRST_RACK_SCAN_ID = 202;
//    final int SECOND_RACK_SCAN_ID = 237;
//    String inFile = "MSF_RackScan1-2_avg_time_for_Feb-Aug_2022.csv";
//    String outFile = "MSF_Cleaned_RackScan_Data_for_Feb-Aug_2022.csv";
//    var file = new File(outFile); //for file writer use
//    var fileReader = new FileReader(inFile);
//    var fileWriter = new FileWriter(file);
//    var csvReader = new CSVReader(fileReader);
//    var csvWriter = new CSVWriter(fileWriter);
//    var nextRecord = new String[MAX_ROW_CAPACITY];
//    var rawScans = new ArrayList<RawField>(MAX_LIST_CAPACITY);
//    var cleanedScans = new ArrayList<CleanedField>();
//    String serialNumber, checkPointName, SKU, customerPN, location;
//    String outFileHeader[] = {"SerialNumber", "FirstScanTransaction", "SecondScanTransaction", "TimeDifferenceMinutes", "SKU", "CustomerPN", "Location"};
//    int checkPointId;
//    long minutesDifference;
//    LocalDateTime transactionDate, firstScanTransaction, secondScanTransaction;
//
////Read all the records and save it on an ArrayList
//        for (int i = 0; i < MAX_LIST_CAPACITY; i++) {
//        if ((nextRecord = csvReader.readNext()) != null) {
//        serialNumber = nextRecord[0];
//        checkPointId = Integer.parseInt(nextRecord[1]);
//        checkPointName = nextRecord[2];
//        transactionDate = Validator.fromSQLDateTimeToLocalDateTime(nextRecord[3]);
//        SKU = nextRecord[4];
//        customerPN = nextRecord[5];
//        location = nextRecord[6];
//
//        rawScans.add(new RawField(serialNumber, checkPointId, checkPointName, transactionDate, SKU, customerPN, location));
//        }
//        }
//        csvReader.close();
//        Logger.getGlobal().info("The data from CSV file \"" + inFile + "\" read successfully");

//        for (RawField field : rawScans) {
//            System.out.println(field.getSerialNumber() + "\t" + field.getCheckPointId() + "\t" + field.getCheckPointName() + "\t" +
//                    field.getTransactionDate() + "\t" + field.getSKU() + "\t" + field.getCustomerPN() + "\t" + field.getLocation());
//            System.out.println();
//        }
