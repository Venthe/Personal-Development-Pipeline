package eu.venthe.combined.utils;

import lombok.Value;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Value
class BatchProcessingData {
    LocalDateTime startDate;
    String uniqueTaskId;
    AtomicReference<Integer> elementCounter;
    Collection<Duration> durationOfPartition;
    String taskName;
    int partitionSize;
    int collectionSize;
    String correlationId;

    public BatchProcessingData(LocalDateTime startDate,
                               String taskName,
                               int partitionSize,
                               int collectionSize,
                               String correlationId) {
        this.startDate = startDate;
        this.taskName = taskName;
        this.partitionSize = partitionSize;
        this.collectionSize = collectionSize;
        this.correlationId = correlationId;
        this.uniqueTaskId = getUniqueTaskId(getStartDate());
        this.elementCounter = new AtomicReference<>(0);
        this.durationOfPartition = new ArrayList<>();
    }

    private String getUniqueTaskId(LocalDateTime startDate) {
        return startDate + "_" + UUID.randomUUID().toString();
    }

    public void incrementElementCounter() {
        elementCounter.updateAndGet(v -> v + 1);
    }

    public void addPartitionDuration(Duration between) {
        durationOfPartition.add(between);
    }

    public LocalDateTime getEndTime() {
        return getElapsedTime().plus(getEstimatedRemainingDuration());
    }

    private Duration getEstimatedRemainingDuration() {
        return Duration.ofNanos(getAveragePartitionDuration().toNanos() * getRemainingPartitions());
    }

    public LocalDateTime getElapsedTime() {
        return getStartDate().plusNanos(getTotalDuration().toNanos());
    }

    public int getRemainingPartitions() {
        return getMaxPartitions() - getCurrentPartition();
    }

    public int getMaxPartitions() {
        return collectionSize / partitionSize;
    }

    public int getCurrentPartition() {
        return elementCounter.get() / partitionSize;
    }

    public double getProgress() {
        return (double) getCurrentPartition() / getMaxPartitions();
    }

    private Duration getAveragePartitionDuration() {
        return Duration.ofNanos(
                (long) durationOfPartition.stream()
                        .map(Duration::getNano)
                        .mapToDouble(a -> a)
                        .average().orElse(0)
        );
    }

    public Duration getTotalDuration() {
        return Duration.ofNanos(
                (long) durationOfPartition.stream()
                        .map(Duration::getNano)
                        .mapToDouble(a -> a)
                        .sum()
        );
    }
}
