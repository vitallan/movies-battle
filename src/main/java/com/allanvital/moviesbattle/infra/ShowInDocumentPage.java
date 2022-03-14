package com.allanvital.moviesbattle.infra;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ShowInDocumentPage {
    String value() default "";
}