package eu.venthe.pipeline.orchestrator.utilities;

import lombok.experimental.UtilityClass;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@UtilityClass
public class CollectionUtilities {

    public static <T> Stream<T> iteratorToStream(Iterator<T> elements) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(elements, Spliterator.ORDERED), true);
    }
}
