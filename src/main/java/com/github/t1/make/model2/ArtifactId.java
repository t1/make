package com.github.t1.make.model2;

import static lombok.AccessLevel.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import lombok.*;

@Value
@RequiredArgsConstructor
@NoArgsConstructor(access = PUBLIC, force = true)
@JsonSerialize(using = ToStringSerializer.class)
public class ArtifactId {
    String groupId, artifactId;

    @JsonCreator
    public ArtifactId(String groupAndArtifact) {
        String[] split = groupAndArtifact.split(":", 2);
        if (split.length != 2)
            throw new IllegalArgumentException("invalid artifactId:groupId: '" + groupAndArtifact + "'");
        this.groupId = split[0];
        this.artifactId = split[1];
    }

    @Override
    public String toString() {
        return groupId + ':' + artifactId;
    }
}
