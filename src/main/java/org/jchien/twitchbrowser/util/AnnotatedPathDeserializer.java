package org.jchien.twitchbrowser.util;

import com.google.common.base.Joiner;
import com.google.gson.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

/**
 * Generic type must have a no-args constructor.
 * This JsonDeserializer will identify any fields annotated with {@link JsonPath}
 * and set the annotated fields using reflection magic.
 *
 * @author jchien
 */
public class AnnotatedPathDeserializer <E> implements JsonDeserializer<E> {
    private final Class<E> klazz;

    public AnnotatedPathDeserializer(Class<E> klazz) {
        this.klazz = klazz;
    }

    @Override
    public E deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        final E ret;
        try {
            final Constructor<E> ctor = klazz.getDeclaredConstructor(new Class[0]);
            // let me access your privates
            ctor.setAccessible(true);
            ret = ctor.newInstance();
        } catch (InstantiationException
                | IllegalAccessException
                | NoSuchMethodException
                | InvocationTargetException e) {
            // blow up for now
            throw new RuntimeException("unable to create instance of " + klazz + ", does it have a no-args constructor?", e);
        }

        final JsonObject root = json.getAsJsonObject();
        final Field[] fields = klazz.getDeclaredFields();
        for (Field field : fields) {
            final Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation a : annotations) {
                if (JsonPath.class == a.annotationType()) {
                    final JsonPath jsonPath = (JsonPath)a;

                    // could avoid repeating work navigating
                    // down same paths for different annotations,
                    // but this is fast enough for now
                    try {
                        parseValue(ret, field, jsonPath, root);
                    } catch (Exception e) {
                        // blow up for now until logging is handled properly
                        throw new RuntimeException("unable to parse path " + Joiner.on("/").join(jsonPath.path()), e);
                    }

                    // only one annotation of each type is allowed per field,
                    // so we don't even have to worry about two JsonPaths on the same field
                    break;
                }
            }
        }
        return ret;
    }

    private void parseValue(final E destObj,
                            final Field field,
                            final JsonPath jsonPath,
                            final JsonObject root) {
        final String[] path = jsonPath.path();
        JsonObject o = root;
        for (int i=0 ; i < path.length - 1; i++) {
            o = o.getAsJsonObject(path[i]);
        }
        final JsonPrimitive p = o.getAsJsonPrimitive(path[path.length-1]);

        final Class fieldType = field.getType();
        // let me access your privates
        field.setAccessible(true);

        // interpret value using appropriate method based on field type
        try {
            if (fieldType == int.class) {
                field.set(destObj, p.getAsInt());
            } else if (fieldType == String.class) {
                field.set(destObj, p.getAsString());
            } else {
                throw new RuntimeException("unsupported type: " + fieldType + " for field " + field.getName());
            }
        } catch (IllegalAccessException e) {
            // blow up for now
            throw new RuntimeException("unable to set field " + field.getName(), e);
        }
    }
}
