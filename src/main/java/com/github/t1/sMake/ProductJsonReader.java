package com.github.t1.sMake;

import static com.github.t1.sMake.Type.*;

import java.io.Reader;
import java.time.LocalDateTime;

import javax.json.*;

public class ProductJsonReader {
    private final JsonObject json;

    public ProductJsonReader(Reader reader) {
        this.json = Json.createReader(reader).readObject();
    }

    public Product read() {
        return type(getString("type")) //
                .id(getString("id")) //
                .version(getString("version")) //
                .name(getString("name")) //
                .description(getString("description")) //
                .releaseTimestamp(getLocalDateTime("releaseTimestamp")) //
                .build();
    }

    private String getString(String fieldName) {
        JsonString jsonString = json.getJsonString(fieldName);
        return (jsonString == null) ? null : jsonString.getString();
    }

    private LocalDateTime getLocalDateTime(String fieldName) {
        JsonString jsonString = json.getJsonString(fieldName);
        return (jsonString == null) ? null : LocalDateTime.parse(jsonString.getString());
    }
}
