package com.github.t1.make.model2;

import static lombok.AccessLevel.*;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.*;

@Value
@Builder(builderMethodName = "lib")
@RequiredArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PUBLIC, force = true)
@JsonSerialize(using = ToStringSerializer.class)
public class Library {
    ArtifactId artifactId;
    String version;

    public Library(String expression) {
        String[] split = expression.split("\\s*(@)\\s*", 2);
        if (split.length == 1) {
            this.artifactId = new ArtifactId(split[0]);
            this.version = null;
        } else {
            this.artifactId = new ArtifactId(split[0]);
            this.version = split[1];
        }
    }

    @Override
    public String toString() {
        return artifactId + ((version == null) ? "" : " @ " + version);
    }
}
