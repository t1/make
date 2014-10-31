package com.github.t1.make.pom;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface PomSection {
    /** The elements in the product.xml that are added to the POM */
    String from();

    /** The path where this section is in the resulting POM */
    String to();
}
