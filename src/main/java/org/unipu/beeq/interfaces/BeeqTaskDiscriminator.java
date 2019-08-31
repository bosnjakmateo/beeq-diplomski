package org.unipu.beeq.interfaces;

import java.io.Serializable;

public interface BeeqTaskDiscriminator<T> extends Serializable {
    public boolean doesMatch(T taskDescriptor);
}
