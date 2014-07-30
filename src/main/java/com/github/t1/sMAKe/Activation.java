package com.github.t1.sMAKe;

import static javax.xml.bind.annotation.XmlAccessType.*;

import javax.xml.bind.annotation.*;

import lombok.*;
import lombok.experimental.Builder;

@Data
@Builder(builderMethodName = "activation")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@XmlRootElement
@XmlAccessorType(NONE)
public class Activation {
    @XmlValue
    String expression;
}
