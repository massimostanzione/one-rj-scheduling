package it.uniroma2.dicii.amod.onerjscheduling.dao;

import it.uniroma2.dicii.amod.onerjscheduling.entities.Job;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static it.uniroma2.dicii.amod.onerjscheduling.utils.Consts.*;

/**
 * A DAO class used to parse the CSV instance files.
 */
public class DataDAO {
    private static DataDAO daoInst = null;

    public static DataDAO getSingletonInstance() {
        if (daoInst == null) {
            daoInst = new DataDAO();
        }
        return daoInst;
    }

    /**
     * Load the file pointed by <code>pathStr</code>, then parse it and convert its content to
     * a list of <code>Job</code>s.
     *
     * @param pathStr URI of the CSV instance file
     * @return list of jobs.
     */
    public List<Job> getDataInstances(String pathStr) {
        Path path = Paths.get(pathStr);
        List<Job> jobList = new ArrayList();
        CSVFormat csvFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase();
        CSVParser csvParser = null;
        try {
            csvParser = CSVParser.parse(path, StandardCharsets.UTF_8, csvFormat);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (CSVRecord csvRecord : csvParser) {
            Job j = new Job(Integer.parseInt(csvRecord.get(CSV_HEADER_JOB_ID)),
                    Integer.parseInt(csvRecord.get(CSV_HEADER_RELEASE_DATE)),
                    Integer.parseInt(csvRecord.get(CSV_HEADER_PROCESSING_TIME))
            );
            jobList.add(j);
        }
        return jobList;
    }
}
