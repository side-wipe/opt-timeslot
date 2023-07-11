package com.example.timeslot.shift.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author 龙也
 * @date 2022/3/24 3:12 PM
 */
public class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER
                .enable(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, JsonParser.Feature.ALLOW_MISSING_VALUES)
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(SerializationFeature.WRITE_NULL_MAP_VALUES)
        ;
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
    }

    public static <T> String toJsonString(T obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        try {
            if (obj instanceof String) {
                String str = StringEscapeUtils.unescapeHtml4((String) obj);
                if (JSONUtil.isTypeJSON(str)) {
                    return StringEscapeUtils.escapeJson(str);
                } else {
                    return str;
                }
            }
            String json = OBJECT_MAPPER.writeValueAsString(obj);
            return StringEscapeUtils.unescapeHtml4(json);
        } catch (JsonProcessingException e) {
            ExceptionUtil.wrapAndThrow(e);
            throw new RuntimeException(e);
        }
    }

    public static <T> String toPrettyJsonString(T obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        try {
            if (obj instanceof String) {
                String str = StringEscapeUtils.unescapeHtml4((String) obj);
                if (JSONUtil.isTypeJSON(str)) {
                    return StringEscapeUtils.escapeJson(str);
                } else {
                    return str;
                }
            }
            String json = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            return StringEscapeUtils.unescapeHtml4(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parse(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        if (!JSONUtil.isTypeJSON(json)) {
            throw new RuntimeException("非法的json");
        }
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parse(String json, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        if (!JSONUtil.isTypeJSON(json)) {
            throw new RuntimeException("非法的json");
        }
        try {
            OBJECT_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, Long.class);
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parse(String json, JavaType javaType) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        if (!JSONUtil.isTypeJSON(json)) {
            throw new RuntimeException("非法的json");
        }
        try {
            return OBJECT_MAPPER.readValue(json, javaType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * json转成对象
     * @param str
     * @param collectionClass
     * @param elementClasses
     * @param <T>
     * @return
     */
    public static <T> T parse(String str, Class<?> collectionClass, Class<?>... elementClasses) {
        try {
            return OBJECT_MAPPER.readValue(str, OBJECT_MAPPER.getTypeFactory().constructParametricType(collectionClass, elementClasses));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T copy(Object obj, Class<T> clazz) {
        if (Objects.isNull(obj)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsString(obj), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T copy(Object obj, TypeReference<T> typeReference) {
        if (Objects.isNull(obj)) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(OBJECT_MAPPER.writeValueAsString(obj), typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static TypeFactory typeFactory() {
        return OBJECT_MAPPER.getTypeFactory();
    }
}
