package org.unipu.beeq.services;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class TaskSerializer<T> extends Serializer<T> {
    @Override
    public void write(Kryo kryo, Output output, T type) {
        Class valueClass = kryo.getGenerics().nextGenericClass();

        if (valueClass != null && kryo.isFinal(valueClass)) {
            Serializer serializer = kryo.getSerializer(valueClass);
            kryo.writeObjectOrNull(output, type, serializer);
        } else {
            kryo.writeClassAndObject(output, type);
        }

        kryo.getGenerics().popGenericType();
    }

    @Override
    public T read(Kryo kryo, Input input, Class<? extends T> type) {
        Class valueClass = kryo.getGenerics().nextGenericClass();

        T object = null;
        kryo.reference(object);

        if (valueClass != null && kryo.isFinal(valueClass)) {
            Serializer serializer = kryo.getSerializer(valueClass);
            object = (T) kryo.readObjectOrNull(input, valueClass, serializer);
        } else {
            object = (T) kryo.readClassAndObject(input);
        }

        kryo.getGenerics().popGenericType();
        return object;
    }
}
