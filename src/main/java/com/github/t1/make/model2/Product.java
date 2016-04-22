package com.github.t1.make.model2;

import static lombok.AccessLevel.*;

import lombok.*;

@Value
@Builder(builderMethodName = "product")
@RequiredArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PUBLIC, force = true)
public class Product {
    ArtifactId id;
    String version;
    String name;
    String description;

    @Override
    public String toString() {
        return "  id: " + id + "\n"
                + "  version: " + version + "\n"
                + "  name: " + name + "\n"
                + "  description: " + description + "\n";
    }
}
