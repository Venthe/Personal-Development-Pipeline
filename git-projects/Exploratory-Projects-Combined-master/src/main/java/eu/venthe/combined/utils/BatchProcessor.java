package eu.venthe.combined.utils;

import com.google.common.collect.Iterators;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchProcessor {

    private final MeterRegistry meterRegistry;
    private final ClockService clockService;

    public <T, U> void iterate(String name,
                               Collection<T> collection,
                               Function<T, U> mapper,
                               Consumer<Collection<U>> finalizer,
                               int partitionSize,
                               String correlationId) {
        validatePartitionSizeLargerThanZero(name, partitionSize);

        if (collection.isEmpty()) {
            log.info(MessageFormat.format("Collection empty. name={0},correlationId={1}", name, correlationId));
            return;
        }

        BatchProcessingData batchProcessingData = new BatchProcessingData(clockService.now(), name, partitionSize, collection.size(), correlationId);
        MicrometerBatchProcessingData micrometerBatchProcessingData = new MicrometerBatchProcessingData(meterRegistry, batchProcessingData);

        logStartProcessing(batchProcessingData);
        Iterators.partition(collection.iterator(), batchProcessingData.getPartitionSize()).forEachRemaining(partition -> {
                    final Instant partitionStart = localDateToInstant(clockService.now());
                    micrometerBatchProcessingData.record(() ->
                            finalizer.accept(partition.stream()
                                    .map(value -> {
                                        batchProcessingData.incrementElementCounter();
                                        try {
                                            final U mappingResult = mapper.apply(value);
                                            micrometerBatchProcessingData.success();
                                            logMappingSuccess(batchProcessingData, value, mappingResult);
                                            return mappingResult;
                                        } catch (Exception exception) {
                                            logMappingError(batchProcessingData, value, exception);
                                            micrometerBatchProcessingData.error();
                                            return null;
                                        }
                                    })
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toList())
                            )
                    );

                    logPartitionFinished(batchProcessingData);
                    batchProcessingData.addPartitionDuration(Duration.between(partitionStart, localDateToInstant(clockService.now())));
                }
        );
        logEndProcessing(batchProcessingData);
    }

    private void logEndProcessing(BatchProcessingData batchProcessingData) {
        log.info("Task finished. " +
                        "name={}," +
                        "startDate={}," +
                        "endDate={}," +
                        "uniqueTaskId={}",
                batchProcessingData.getTaskName(),
                batchProcessingData.getStartDate(),
                clockService.now(),
                batchProcessingData.getUniqueTaskId()
        );
    }

    private void logPartitionFinished(BatchProcessingData batchProcessingData) {
        log.info("Partition finished. " +
                        "name={}," +
                        "partition={}," +
                        "elapsedTime={}," +
                        "estimatedEndTime={}," +
                        "progress={}," +
                        "correlationId={}",
                batchProcessingData.getTaskName(),
                batchProcessingData.getCurrentPartition(),
                batchProcessingData.getTotalDuration(),
                batchProcessingData.getEndTime(),
                batchProcessingData.getProgress(),
                batchProcessingData.getCorrelationId()
        );
    }

    private void logStartProcessing(BatchProcessingData batchProcessingData) {
        log.info("Starting task. " +
                        "name={}," +
                        "collectionSize={}," +
                        "partitionSize={}," +
                        "startDate={}," +
                        "uniqueTaskId={}," +
                        "correlationId={}",
                batchProcessingData.getTaskName(),
                batchProcessingData.getCollectionSize(),
                batchProcessingData.getPartitionSize(),
                batchProcessingData.getStartDate(),
                batchProcessingData.getUniqueTaskId(),
                batchProcessingData.getCorrelationId()
        );
    }

    private <T, U> void logMappingSuccess(BatchProcessingData batchProcessingData, T value, U mappingResult) {
        log.trace("Element mapped. originalValue={}," +
                        "mappingResult={}," +
                        "correlationId={}",
                value,
                mappingResult,
                batchProcessingData.getCorrelationId());
    }

    private <T> void logMappingError(BatchProcessingData batchProcessingData, T value, Exception exception) {
        log.error("Exception during batch processing. " +
                        "originalValue={}," +
                        "correlationId={}",
                value,
                batchProcessingData.getCorrelationId(),
                exception
        );
    }

    private void validatePartitionSizeLargerThanZero(String name, int partitionSize) {
        if (partitionSize <= 0) {
            throw new RuntimeException(
                    MessageFormat.format("Partition size should be positive. " +
                                    "name={0}," +
                                    "partitionSize={1}",
                            name,
                            partitionSize
                    ));
        }
    }

    private Instant localDateToInstant(LocalDateTime startDate) {
        return Timestamp.valueOf(startDate).toInstant();
    }

}
