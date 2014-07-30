package com.github.t1.sMAKe;

import java.util.List;

import javax.xml.bind.annotation.*;

import lombok.*;
import lombok.experimental.Builder;

import org.w3c.dom.Element;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "id", "version", "name", "description", "inceptionYear", "activation", "features", "other" })
public class Product {
    @XmlAttribute
    String id;

    @XmlAttribute
    String version;

    @XmlElement
    private String name;
    @XmlElement
    private String description;
    @XmlElement
    private String inceptionYear;

    @XmlElement
    private Activation activation;

    @XmlElement(name = "feature")
    private List<Feature> features;

    @XmlAnyElement
    private List<Element> other;

    public String getGroupId() {
        return (id == null) ? null : id.split(":", 2)[0];
    }

    public String getArtifactId() {
        return (id == null) ? null : id.split(":", 2)[1];
    }
}
