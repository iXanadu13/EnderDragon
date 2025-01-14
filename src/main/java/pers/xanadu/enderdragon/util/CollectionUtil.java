package pers.xanadu.enderdragon.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class CollectionUtil {
    public static <T> List<T> vec(T ...elems) {
        return Arrays.stream(elems).collect(Collectors.toList());
    }
}
