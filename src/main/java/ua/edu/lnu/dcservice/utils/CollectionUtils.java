package ua.edu.lnu.dcservice.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CollectionUtils {

    private CollectionUtils() {
        throw new UnsupportedOperationException();
    }

    public static <T, K> List<K> mapToList(Collection<T> collection, Function<T, K> mapper) {
        return collection.parallelStream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    public static <T, K> Map<K, T> buildMapFromCollection(Collection<T> collection, Function<T, K> keyMapper) {
        return collection.parallelStream()
                .collect(Collectors.toMap(keyMapper, e -> e));
    }

    public static <T, K, E> Map<K, E> buildMapFromCollection(Collection<T> collection, Function<T, K> keyMapper, Function<T, E> valueMapper) {
        return collection.parallelStream()
                .collect(Collectors.toMap(keyMapper, valueMapper));
    }
}
