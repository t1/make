package com.github.t1.make.model;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.*;

import javax.json.*;

import lombok.AllArgsConstructor;

import com.google.common.collect.ImmutableList;

@AllArgsConstructor
public class JsonStoredProduct extends Product {
    private static final String ATTRIBUTE_PREFIX = "-";

    private Map<String, Object> map = new LinkedHashMap<>();

    public JsonStoredProduct(URI uri) {
        JsonObject jsonObject = read(uri);
        for (String key : jsonObject.keySet()) {
            map.put(key, fromJsonValue(jsonObject.get(key)));
        }
    }

    private Object fromJsonValue(JsonValue jsonValue) {
        switch (jsonValue.getValueType()) {
            case OBJECT:
                return fromJsonObject((JsonObject) jsonValue);
            case ARRAY:
                return fromJsonArray((JsonArray) jsonValue);
            case STRING:
                return ((JsonString) jsonValue).getString();
            default:
                throw new UnsupportedOperationException("unsupported JSON value type " + jsonValue.getValueType());
        }
    }

    private Map<String, Object> fromJsonObject(JsonObject jsonObject) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (Map.Entry<String, JsonValue> entry : jsonObject.entrySet()) {
            map.put(entry.getKey(), fromJsonValue(entry.getValue()));
        }
        return map;
    }

    private List<Object> fromJsonArray(JsonArray jsonArray) {
        List<Object> list = new ArrayList<>();
        for (JsonValue item : jsonArray) {
            list.add(fromJsonValue(item));
        }
        return list;
    }

    public JsonStoredProduct(Product feature) {
        this(feature.version());

        feature.features().forEach(f -> add(f));
    }

    public JsonStoredProduct(Version version) {
        attribute(Type.ATTRIBUTE, version.type().typeName());
        attribute(Id.ATTRIBUTE, version.id().idString());
        attribute(Version.ATTRIBUTE, version.versionString());
    }

    private JsonObject read(URI uri) {
        try {
            return Json.createReader(uri.toURL().openStream()).readObject();
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException("can't read " + uri, e);
        }
    }

    @Override
    public Type type() {
        return Type.type(attribute(Type.ATTRIBUTE).get());
    }

    @Override
    protected ImmutableList<Product> unresolvedFeatures() {
        ImmutableList.Builder<Product> list = ImmutableList.builder();
        map.forEach((key, value) -> {
            if (key.startsWith(ATTRIBUTE_PREFIX))
                return;
            System.out.println("---> " + key + ": " + value);
            Version version = Type.type(key).id(Id.EMPTY).version(Version.ANY);
            list.add(new JsonStoredProduct(version).value(value.toString()));
        });
        return list.build();
    }

    @Override
    public Optional<String> value() {
        return Optional.ofNullable((String) map.get(ATTRIBUTE_PREFIX));
    }

    @Override
    public Product value(String value) {
        map.put(ATTRIBUTE_PREFIX, value);
        return this;
    }

    @Override
    public Product addFeature(Id id, String value) {
        String key = id.type().typeName();
        if (!id.isEmpty())
            key += ATTRIBUTE_PREFIX + id.idString();
        map.put(key, value);
        return this;
    }

    @Override
    public Product add(Product feature) {
        if (feature.value().isPresent()) {
            map.put(feature.type().typeName(), feature.value().get());
        } else {
            String plural = feature.type().pluralString();
            @SuppressWarnings("unchecked")
            List<Object> features = (List<Object>) map.computeIfAbsent(plural, (x) -> new ArrayList<>());
            features.add(new JsonStoredProduct(feature).map);
        }
        return this;
    }

    @Override
    public Optional<String> attribute(String key) {
        return Optional.ofNullable((String) map.get(ATTRIBUTE_PREFIX + key));
    }

    @Override
    public JsonStoredProduct attribute(String key, String value) {
        map.put(ATTRIBUTE_PREFIX + key, value);
        return this;
    }

    @Override
    public Product saveTo(Path directory) {
        Path filePath = directory.resolve("product.json");
        try (JsonWriter writer = Json.createWriter(Files.newBufferedWriter(filePath))) {
            writer.write(toJsonObject(map));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private JsonObject toJsonObject(Map<String, Object> map) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        map.forEach((key, value) -> {
            builder.add(key, toJsonValue(value));
        });
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private JsonValue toJsonValue(Object value) {
        if (value instanceof List) {
            return toJsonArray((List<Object>) value);
        } else if (value instanceof Map) {
            return toJsonObject((Map<String, Object>) value);
        } else {
            return toJsonString(value);
        }
    }

    private JsonValue toJsonString(Object value) {
        // how can this be simplified?
        return Json.createObjectBuilder().add("dummy", value.toString()).build().getJsonString("dummy");
    }

    private JsonArray toJsonArray(List<Object> list) {
        JsonArrayBuilder array = Json.createArrayBuilder();
        for (Object item : list) {
            array.add(toJsonValue(item));
        }
        JsonArray build = array.build();
        return build;
    }
}
