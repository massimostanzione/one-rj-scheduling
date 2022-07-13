package it.uniroma2.dicii.amod.onerjscheduling.entities;

import it.uniroma2.dicii.amod.onerjscheduling.io.ExportableAsDatasetRecord;

import java.util.List;

/**
 * Class modeling a single job.
 */
public class Job extends ExportableAsDatasetRecord {
    private final int id;
    private final int releaseDate;
    private final int processingTime;

    public Job(int id, int releaseDate, int processingTime) {
        this.id = id;
        this.releaseDate = releaseDate;
        this.processingTime = processingTime;
    }

    public int getId() {
        return id;
    }

    public int getReleaseDate() {
        return releaseDate;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                '}';
    }

    @Override
    public List<List<String>> getDatasetAttributes() {
        this.setDatasetAttributes("JOB_ID", "RELEASE_DATE", "PROCESSING_TIME");
        return this.datasetAttributes;
    }

    @Override
    public List<List<String>> getDatasetRecord() {
        this.setDatasetRecord(this.id, this.releaseDate, this.processingTime);
        return this.datasetRecord;
    }
}
