package com.jadaptive.api.template;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
@Repeatable(ObjectViews.class)
public @interface ObjectViewDefinition {

	String value();
	
	ViewType type() default ViewType.TAB;
	
	int weight() default 0;
}
