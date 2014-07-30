package com.github.t1.sMAKe;

import static com.github.t1.sMAKe.Activation.*;
import static com.github.t1.sMAKe.Feature.*;
import static java.util.Arrays.*;

public abstract class AbstractTest {
    protected static final String PRODUCT1_XML = "src/test/resources/product1.xml";
    protected static final String PRODUCT1_POM = "src/test/resources/product1.pom.xml";

    protected Product createProduct() {
        return Product.builder() //
                .id("foo:bar") //
                .version("1.0") //
                .name("Product Name") //
                .description("The Product") //
                .inceptionYear("2014") //
                .activation(activation().expression("activation-expression").build()) //
                .features(asList( //
                        feature().id("javaee-7").build(), //
                        feature().id("org.projectlombok:lombok").version("1.12.6").build(), //
                        feature().id("com.github.t1:webresource-generator").build(), //
                        feature().id("ch.qos.logback:logback-classic").version("1.1+").build(), //
                        feature().id("com.github.t1:junit-hamcrest-mockito").version("1.0").build() //
                        )) //
                .build();
    }
}
