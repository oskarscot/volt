package me.oskarscot.volt;

import me.oskarscot.volt.converter.BidirectionalTypeConverter;
import me.oskarscot.volt.exception.VoltError;

public interface Volt {

    void registerEntity(Class<?> entityClass);

    <T> void registerConverter(Class<T> type, BidirectionalTypeConverter<T> converter);

    Transaction beginTransaction();

    boolean testConnection();

    <T> Result<T, VoltError> findById(Class<T> type, Object id);
    <T> Result<T, VoltError> save(T entity);
}
