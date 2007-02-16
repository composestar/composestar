package Composestar.Core.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
public @interface In
{
	String value();
	boolean required() default true;
}
