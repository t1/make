package com.github.t1.make.model2;

import static lombok.AccessLevel.*;

import com.fasterxml.jackson.annotation.*;
import com.github.t1.make.model2.Package.PackageBuilder;
import com.github.t1.make.model2.Product.ProductBuilder;

import lombok.*;

@Value
@Builder(builderMethodName = "pom")
@RequiredArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PUBLIC, force = true)
@JsonPropertyOrder({ "product", "pack", "containing" })
public class ProductObjectModel {
    public static class ProductObjectModelBuilder {
        public ProductObjectModelBuilder product(ProductBuilder productBuilder) {
            this.product = productBuilder.build();
            return this;
        }

        public ProductObjectModelBuilder pack(PackageBuilder packageBuilder) {
            this.pack = packageBuilder.build();
            return this;
        }
    }

    private Product product;

    @JsonProperty("package")
    private Package pack;

    @Override
    public String toString() {
        return "POM\n"
                + product + "\n"
                + pack + "\n";
    }
}
