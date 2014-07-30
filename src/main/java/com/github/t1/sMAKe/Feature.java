package com.github.t1.sMAKe;

import static javax.xml.bind.annotation.XmlAccessType.*;

import java.util.List;

import javax.xml.bind.annotation.*;

import lombok.*;
import lombok.experimental.Builder;

import org.w3c.dom.Element;

@Data
@Builder(builderMethodName = "feature")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@XmlRootElement
@XmlAccessorType(NONE)
@XmlType(propOrder = { "id", "version", "features", "other" })
public class Feature {
    @XmlAttribute
    String id;

    @XmlAttribute
    String version;

    @XmlElement(name = "feature")
    private List<Feature> features;

    @XmlAnyElement
    private List<Element> other;
}
