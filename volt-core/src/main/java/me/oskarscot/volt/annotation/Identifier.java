package me.oskarscot.volt.annotation;

import me.oskarscot.volt.entity.PrimaryKeyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Identifier {
    boolean generated() default true;
    PrimaryKeyType type();
}
