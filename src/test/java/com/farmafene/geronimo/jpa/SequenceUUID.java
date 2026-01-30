package com.farmafene.geronimo.jpa;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.hibernate.annotations.IdGeneratorType;

import com.farmafene.geronimo.generator.SequenceUUIDGenerator;

@IdGeneratorType(SequenceUUIDGenerator.class)
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface SequenceUUID  {

}
