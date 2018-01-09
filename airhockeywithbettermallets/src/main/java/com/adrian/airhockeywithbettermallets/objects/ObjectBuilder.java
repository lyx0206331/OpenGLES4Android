package com.adrian.airhockeywithbettermallets.objects;

/**
 * 物体构建器
 * Created by qing on 2018/1/9 0009.
 */

public class ObjectBuilder {
    private static final int FLOATS_PER_VERTEX = 3;
    private final float[] vertexData;
    private int offset = 0;

    public ObjectBuilder(int sizeInVertices) {
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
    }
}
