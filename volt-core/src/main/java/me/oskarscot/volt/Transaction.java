package me.oskarscot.volt;

import me.oskarscot.volt.exception.VoltError;
import me.oskarscot.volt.query.Query;

import java.util.List;
import java.util.Optional;

public interface Transaction extends AutoCloseable {

    <T> Result<T, VoltError> save(T entity);

    <T> Result<T, VoltError> findById(Class<T> type, Object id);

    <T> Result<Optional<T>, VoltError> findFirstBy(Class<T> type, String field, Object value);
    <T> Result<T, VoltError> findOneBy(Class<T> type, String field, Object value);
    <T> Result<List<T>, VoltError> findAllBy(Class<T> type, String field, Object value);

    <T> Result<List<T>, VoltError> findAll(Class<T> type);

    <T> Result<Optional<T>, VoltError> findFirstBy(Class<T> type, Query query);
    <T> Result<T, VoltError> findOneBy(Class<T> type, Query query);
    <T> Result<List<T>, VoltError> findAllBy(Class<T> type, Query query);

    // Delete
    <T> Result<Void, VoltError> delete(T entity);
    <T> Result<Void, VoltError> deleteById(Class<T> type, Object id);

    Result<Void, VoltError> commit();
    Result<Void, VoltError> rollback();

    @Override
    void close();
}