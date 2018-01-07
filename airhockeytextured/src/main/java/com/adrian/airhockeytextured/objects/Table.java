package com.adrian.airhockeytextured.objects;

import com.adrian.airhockeytextured.data.VertexArray;
import com.adrian.airhockeytextured.programs.TextureShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.adrian.airhockeytextured.Constants.BYTES_PER_FLOAT;

/**
 * Created by qing on 2018/1/4 0004.
 */

public class Table {
    //位置分量计数
    private static final int POSITION_COMPONENT_COUNT = 2;
    //纹理坐标分量计数
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    //跨距
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
            //order of coordinates:X,Y,S,T

            //triangle fan
            0f, 0f, .5f, .5f,
            -.5f, -.8f, 0f, .9f,
            .5f, -.8f, 1f, .9f,
            .5f, .8f, 1f, .1f,
            -.5f, .8f, 0f, .1f,
            -.5f, -.8f, 0f, .9f
    };

    private final VertexArray vertexArray;

    public Table() {
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(TextureShaderProgram textureProgram) {
        vertexArray.setVertexAttribPointer(0, textureProgram.getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
        vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT, textureProgram.getTextureCoordinatesAttributeLocation(), TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
    }

    public void draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }
}
