package me.oskarscot.volt.test;

import com.zaxxer.hikari.HikariConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import me.oskarscot.volt.Result;
import me.oskarscot.volt.Transaction;
import me.oskarscot.volt.Volt;
import me.oskarscot.volt.VoltFactory;
import me.oskarscot.volt.entity.*;
import me.oskarscot.volt.exception.VoltError;
import me.oskarscot.volt.query.Query;

import java.util.List;

public final class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        var app = new Application();
        app.run(args);
    }

    private void run(String[] args) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("org.postgresql.Driver");
        hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres");
        hikariConfig.setUsername("postgres");
        hikariConfig.setPassword("test");
        Volt volt = VoltFactory.createVolt(hikariConfig);
        volt.registerEntity(TestEntity.class);

        TestEntity entity = new TestEntity("Test", "Description", 10);
        Result<TestEntity, VoltError> saveResult = volt.save(entity);

        if (saveResult.isSuccess()) {
            log.info("Saved with ID: {}", saveResult.getValue().getId());
        } else {
            log.error("Save failed: {}", saveResult.getError().message());
        }

        Result<TestEntity, VoltError> findResult = volt.findById(TestEntity.class, entity.getId());

        if (findResult.isSuccess()) {
            log.info("Found: {}", findResult.getValue().getName());
        }

        try (Transaction tx = volt.beginTransaction()) {
            TestEntity entity1 = new TestEntity("First", "First description", 100);
            TestEntity entity2 = new TestEntity("Second", "Second description", 200);

            Result<TestEntity, VoltError> result1 = tx.save(entity1);
            if (result1.isFailure()) {
                log.error("Failed to save entity1: {}", result1.getError().message());
                tx.rollback();
                return;
            }
            log.info("Saved entity1 with ID: {}", result1.getValue().getId());

            Result<TestEntity, VoltError> result2 = tx.save(entity2);
            if (result2.isFailure()) {
                log.error("Failed to save entity2: {}", result2.getError().message());
                tx.rollback();
                return;
            }
            log.info("Saved entity2 with ID: {}", result2.getValue().getId());

            Result<List<TestEntity>, VoltError> allResult = tx.findAll(TestEntity.class);
            if (allResult.isSuccess()) {
                log.info("Found {} entities:", allResult.getValue().size());
                for (TestEntity e : allResult.getValue()) {
                    log.info("  - {}: {}", e.getId(), e.getName());
                }
            }

            Query query = Query.where("quantity").gte(100);
            Result<List<TestEntity>, VoltError> queryResult = tx.findAllBy(TestEntity.class, query);
            if (queryResult.isSuccess()) {
                log.info("Found {} entities with quantity >= 100", queryResult.getValue().size());
            }

            TestEntity toUpdate = result1.getValue();
            toUpdate.setName("Updated First");
            Result<TestEntity, VoltError> updateResult = tx.save(toUpdate);
            if (updateResult.isSuccess()) {
                log.info("Updated entity: {}", updateResult.getValue().getName());
            }

            Result<Void, VoltError> deleteResult = tx.delete(result2.getValue());
            if (deleteResult.isSuccess()) {
                log.info("Deleted entity2");
            }

            tx.commit();
            log.info("Transaction committed successfully");
        }

        try (Transaction tx = volt.beginTransaction()) {
            Result<List<TestEntity>, VoltError> finalResult = tx.findAll(TestEntity.class);
            if (finalResult.isSuccess()) {
                log.info("Final state - {} entities:", finalResult.getValue().size());
                for (TestEntity e : finalResult.getValue()) {
                    log.info("  - {}: {} (qty: {})", e.getId(), e.getName(), e.getQuantity());
                }
            }
            tx.commit();
        }
    }
}
