package com.kamegatze.map.result.set.processor.university.mapper;

import com.kamegatze.map.result.set.MapResultSet;
import com.kamegatze.map.result.set.processor.university.model.StudentClass;
import java.sql.ResultSet;
import java.util.*;

@MapResultSet
public interface CollectionMapper {

    Iterable<StudentClass> toIterable(ResultSet resultSet);

    Collection<StudentClass> toCollection(ResultSet resultSet);

    List<StudentClass> toList(ResultSet resultSet);

    Set<StudentClass> toSet(ResultSet resultSet);
}
