package com.github.t1.builder;

import static javax.xml.bind.annotation.XmlAccessType.*;

import java.util.List;

import javax.xml.bind.annotation.*;

import lombok.*;
import lombok.experimental.Builder;

import org.w3c.dom.Element;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
@XmlAccessorType(NONE)
public class Feature {
    @XmlAttribute
    String id;

    @XmlAttribute
    String version;

    @XmlAnyElement
    List<Element> other;
}
