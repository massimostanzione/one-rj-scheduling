package it.uniroma2.dicii.amod.onerjscheduling.entities;

public class Job {
    private int id;
    private int releaseDate;
    private int processingTime;

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
}
