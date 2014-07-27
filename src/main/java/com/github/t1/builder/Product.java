package com.github.t1.builder;

import java.util.*;

import javax.xml.bind.annotation.*;

import lombok.*;
import lombok.experimental.Builder;

import org.w3c.dom.Element;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Product {
    public static class ProductBuilder {
        public ProductBuilder feature(Feature feature) {
            if (features == null)
                features = new ArrayList<>();
            features.add(feature);
            return this;
        }
    }

    @XmlAttribute
    private String id;
    @XmlAttribute
    private String version;

    @XmlElement
    private String name;
    @XmlElement
    private String description;
    @XmlElement
    private String inceptionYear;

    @XmlAnyElement
    private List<Element> other;

    @XmlElement(name = "feature")
    private List<Feature> features;

    public String getGroupId() {
        return id.split(":", 2)[0];
    }

    public String getArtifactId() {
        return id.split(":", 2)[1];
    }
}
