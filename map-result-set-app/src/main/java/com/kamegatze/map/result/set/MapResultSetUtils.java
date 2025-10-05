package com.kamegatze.map.result.set;

import com.kamegatze.map.result.set.exceptions.ImpossibleCreateMapperException;
import com.kamegatze.map.result.set.processor.utilities.GeneralConstantUtility;
import java.lang.reflect.InvocationTargetException;

public final class MapResultSetUtils {
    private MapResultSetUtils() {}

    @SuppressWarnings("unchecked")
    public static <T> T getMapper(Class<T> tClass) {
        try {
            return (T)
                    Class.forName(
                                    tClass.getCanonicalName()
                                            + GeneralConstantUtility.POSTFIX_IMPLEMENT_INTERFACE)
                            .getDeclaredConstructor()
                            .newInstance();
        } catch (ClassNotFoundException
                | InvocationTargetException
                | InstantiationException
                | IllegalAccessException
                | NoSuchMethodException e) {
            throw new ImpossibleCreateMapperException(e);
        }
    }
}
