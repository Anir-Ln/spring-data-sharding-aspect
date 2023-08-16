package com.example.accessingdatajpa.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface DirectShardRouting {
    String[] hints() default {};
}
