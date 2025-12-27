package me.oskarscot.volt.internal.builders;

import me.oskarscot.volt.entity.EntityDefinition;
import me.oskarscot.volt.entity.FieldDefinition;
import me.oskarscot.volt.query.Query;

import java.util.ArrayList;
import java.util.List;

public class SelectBuilder implements SqlBuilder {

    private final EntityDefinition<?> definition;

    public SelectBuilder(EntityDefinition<?> definition) {
        this.definition = definition;
    }

    @Override
    public String toSql() {
        return buildSelectClause() + " FROM " + definition.getTableName();
    }

    public String toSqlById() {
        return toSql() + " WHERE " + definition.getPrimaryKey().getColumnName() + " = ?";
    }

    public String toSqlWithQuery(Query query) {
        return toSql() + " WHERE " + query.toWhereClause();
    }

    private String buildSelectClause() {
        List<String> columns = new ArrayList<>();

        // Add primary key
        columns.add(definition.getPrimaryKey().getColumnName());

        // Add other fields
        for (FieldDefinition field : definition.getFields()) {
            columns.add(field.getColumnName());
        }

        return "SELECT " + String.join(", ", columns);
    }
}