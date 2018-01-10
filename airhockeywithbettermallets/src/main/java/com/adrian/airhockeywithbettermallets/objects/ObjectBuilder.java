package com.adrian.airhockeywithbettermallets.objects;

import java.util.List;

/**
 * 物体构建器
 * Created by qing on 2018/1/9 0009.
 */

public class ObjectBuilder {

    static interface DrawCommand {
        void draw();
    }

    static class GeneratedData {
        final float[] vertexData;
        final List<DrawCommand> drawList;

        GeneratedData(float[] vertexData, List<DrawCommand> drawList) {
            this.vertexData = vertexData;
            this.drawList = drawList;
        }

        private GeneratedData build() {
            return new GeneratedData(vertexData, drawList);
        }
    }

    private static final int FLOATS_PER_VERTEX = 3;
    private final float[] vertexData;
    private int offset = 0;

    private ObjectBuilder(int sizeInVertices) {
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
    }

    /**
     * 计算圆柱体顶部顶点数量
     * 一个圆柱体的顶部是一个用三角形扇构造的圆;它有一个顶点在圆心，围着圆的每个点都有一个顶点，并且围着圆的第一个顶点要重复两次才能使圆闭合
     *
     * @param numPoints
     * @return
     */
    private static int sizeOfCircleInVertices(int numPoints) {
        return 1 + (numPoints + 1);
    }

    /**
     * 计算圆柱体侧面顶点的数量
     * 一个圆柱体的侧面是一个卷起来的长方形，由一个三角形带构造，围着顶部圆的每个点都需要两个顶点，并且前两个顶点要重复两次才能使这个管闭合
     *
     * @param numPoints
     * @return
     */
    private static int sizeOfOpenCylinderInVertices(int numPoints) {
        return (numPoints + 1) * 2;
    }
}
