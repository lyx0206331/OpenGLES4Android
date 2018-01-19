package com.adrian.airhockeywithbettermallets.objects;


import com.adrian.airhockeywithbettermallets.data.VertexArray;
import com.adrian.airhockeywithbettermallets.programs.ColorShaderProgram;
import com.adrian.airhockeywithbettermallets.util.Geometry;

import java.util.List;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;
import static com.adrian.airhockeywithbettermallets.Constants.BYTES_PER_FLOAT;

/**
 * Created by qing on 2018/1/5 0005.
 */

public class Mallet {

    private static final int POSITION_COMPONENT_COUNT = 3;

    public final float radius;
    public final float height;

    private final VertexArray vertexArray;
    private final List<ObjectBuilder.DrawCommand> drawList;

    public Mallet(float radius, float height, int numPointsAroundMallet) {
        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createMallet(new Geometry.Point(0f, 0f, 0f), radius, height, numPointsAroundMallet);

        this.radius = radius;
        this.height = height;

        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }

    public void bindData(ColorShaderProgram colorProgram) {
        vertexArray.setVertexAttribPointer(0, colorProgram.getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, 0);
    }

    public void draw() {
        for (ObjectBuilder.DrawCommand drawCommand :
                drawList) {
            drawCommand.draw();
        }
    }
}
