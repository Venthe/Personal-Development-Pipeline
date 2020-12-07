package eu.venthe.combined.utils;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.tomcat.util.buf.StringUtils;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

public class MicrometerBatchProcessingData {
    public static final String SERIES_NAME = "batch_with_stopwatch";
    public static final String UNIQUE_ID = "unique_id";
    public static final String CORRELATION_ID = "correlation_id";
    public static final String TASK_NAME = "task_name";
    public static final String STATUS = "status";

    private final BatchProcessingData batchProcessingData;
    private final MeterRegistry meterRegistry;

    private LongTaskTimer totalTaskDurationTimer;
    private Counter errorCounter;
    private Counter successCounter;

    public MicrometerBatchProcessingData(MeterRegistry meterRegistry, BatchProcessingData batchProcessingData) {
        this.meterRegistry = meterRegistry;
        this.batchProcessingData = batchProcessingData;

        totalTaskDurationTimer = getTaskTimer("total_task", "Total time of batch task");
        errorCounter = createElementsConsumedCounterWithStatus("Total error count", "error");
        successCounter = createElementsConsumedCounterWithStatus("Total success count", "success");

        createGauge("total_size", "Processed collection size", BatchProcessingData::getCollectionSize);
        createGauge("partition_size", "Size of batch", BatchProcessingData::getPartitionSize);
        createGauge("current_element", "Current element counter", bpd -> bpd.getElementCounter().get());
        createGauge("start_timestamp", "Start timestamp", bpd -> localDateToInstant(bpd.getStartDate()));
        createGauge("end_timestamp", "End timestamp", bpd -> localDateToInstant(bpd.getEndTime()));
    }

    public void record(Runnable runnable) {
        totalTaskDurationTimer.record(runnable);
    }

    public void error() {
        errorCounter.increment();
    }

    public void success() {
        successCounter.increment();
    }

    private long localDateToInstant(LocalDateTime startDate) {
        return Timestamp.valueOf(startDate).getTime();
    }

    private LongTaskTimer getTaskTimer(String name, String description) {
        return LongTaskTimer.builder(createSeriesName(name))
                .description(description)
                .tag(TASK_NAME, batchProcessingData.getTaskName())
                .tag(UNIQUE_ID, batchProcessingData.getUniqueTaskId())
                .tag(CORRELATION_ID, batchProcessingData.getCorrelationId())
                .minimumExpectedValue(Duration.ZERO)
                .register(meterRegistry);
    }

    private Gauge createGauge(String name, String description, ToDoubleFunction<BatchProcessingData> valueSupplier) {
        return Gauge.builder(createSeriesName(name), batchProcessingData, valueSupplier)
                .description(description)
                .tag(TASK_NAME, batchProcessingData.getTaskName())
                .tag(UNIQUE_ID, batchProcessingData.getUniqueTaskId())
                .tag(CORRELATION_ID, batchProcessingData.getCorrelationId())
                .register(meterRegistry);
    }

    private Counter createElementsConsumedCounterWithStatus(String description, String status) {
        return Counter.builder(createSeriesName("elements_consumed"))
                .description(description)
                .tag(TASK_NAME, batchProcessingData.getTaskName())
                .tag(UNIQUE_ID, batchProcessingData.getUniqueTaskId())
                .tag(CORRELATION_ID, batchProcessingData.getCorrelationId())
                .tag(STATUS, status)
                .register(meterRegistry);
    }

    private String createSeriesName(String... fields) {
        final List<String> collection = new ArrayList<>(Arrays.asList(fields));
        collection.add(0, SERIES_NAME);
        return StringUtils.join(collection, '_');
    }
}
