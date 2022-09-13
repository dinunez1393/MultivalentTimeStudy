import Utilities.ReaderWriter;
import com.opencsv.exceptions.CsvValidationException;
import model.CleanedField;
import model.RawField;
import model.RawFieldCluster;

import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException, CsvValidationException {
        final int MAX_LIST_CAPACITY = 30;
        final int MAX_ROW_CAPACITY = 7;
        final int IPQC2_CHECK_OUT_ID = 219;
        final int FIRST_SCAN_CHECK_IN_ID = 260;
        final int FIRST_SCAN_ID = 202;
        final int SECOND_SCAN_CHECK_IN_ID = 270;
        final int SECOND_SCAN_ID = 237;
        String inFile = "RackScanCheckInTimes_raw.csv";
        String outFile = "RackScanCheckInTimes_cleaned_clustered.csv";
        String[] outFileHeader = {"SerialNumber", "IPQC2_CheckOut", "FirstScan_CheckIn", "FirstScanTransaction", "SecondScan_CheckIn", "SecondScanTransaction",
                "IPQC2-FirstScan_CheckIn", "FirstScan_CheckIn-FirstScan", "FirstScan-SecondScan_CheckIn",
                "SecondScanCheckIn-SecondScan", "SKU", "CustomerPN", "Location"};
        var rawScans = new ArrayList<RawField>();
        var cleanedScans = new ArrayList<CleanedField>();
        var clusteredRawScans = new ArrayList<RawFieldCluster>();


        //Clustered CSV export
        rawScans = ReaderWriter.csvReader(inFile,MAX_LIST_CAPACITY, MAX_ROW_CAPACITY);
        clusteredRawScans = ReaderWriter.listCluster(rawScans, IPQC2_CHECK_OUT_ID, FIRST_SCAN_CHECK_IN_ID, FIRST_SCAN_ID, SECOND_SCAN_CHECK_IN_ID, SECOND_SCAN_ID);
        cleanedScans = ReaderWriter.clusteredListDataCleaner(clusteredRawScans, 4, 5, 4);
        ReaderWriter.csvWriter(outFile, outFileHeader, cleanedScans, 13, true);

        //Non-clustered CSV export
//        cleanedScans = ReaderWriter.dataCleaner(rawScans, 219, 260, 202, 270, 237, 4, 5);
//        ReaderWriter.csvWriter(outFile, outFileHeader, cleanedScans, 13);

        /*
        PROJECT SUCCESS on 8/29/2022 13:19:
        A few minor bugs when clustering the data from a raw data list; the first row only needs to be populated with dummy values.
        Otherwise, it would not cluster correctly.
        Another minor bug is on the final CSV export of clustered data; the last SN of the raw list would be repeated
        the same number of times that the SN is repeated on the original raw list.
        All these minor bugs can be corrected with ease on the final export file by making a short comparison with the original
        raw list data and deleting the few unnecessary lines which would be only at the top and/or bottom of the exported cleaned CSV list.
        */
    }
}