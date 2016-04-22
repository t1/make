package com.github.t1.make.model2;

import static com.github.t1.make.model2.StringTools.toLines;
import static lombok.AccessLevel.*;

import java.util.*;

import lombok.*;

@Value
@Builder(builderMethodName = "pack")
@RequiredArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PUBLIC, force = true)
public class Package {
    public static class PackageBuilder {
        private final List<Library> containing = new ArrayList<>();

        public PackageBuilder containing(String groupId, String artifactId,
                String version) {
            return containing(Library.lib().artifactId(new ArtifactId(groupId, artifactId)).version(version));
        }

        public PackageBuilder containing(Library.LibraryBuilder libraryBuilder) {
            this.containing.add(libraryBuilder.build());
            return this;
        }
    }

    String as;

    // Main-Class=com.github.t1.make.cli.Main

    List<Library> containing;

    @Override
    public String toString() {
        return "package as: " + as + "\n"
                + "  containing:" + toLines(containing) + "\n";
    }
}
