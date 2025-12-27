package me.oskarscot.volt.test;

import me.oskarscot.volt.annotation.Entity;
import me.oskarscot.volt.annotation.Identifier;
import me.oskarscot.volt.annotation.NamedField;
import me.oskarscot.volt.entity.PrimaryKeyType;

@Entity("users")
public class TestEntity {

    @Identifier(type = PrimaryKeyType.NUMBER, generated = true)
    private Long id;

    @NamedField(name = "entity_name")
    private String name;
    private String description;
    private int quantity;

    public TestEntity() { }

    public TestEntity(String name, String description, int quantity) {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }
}
