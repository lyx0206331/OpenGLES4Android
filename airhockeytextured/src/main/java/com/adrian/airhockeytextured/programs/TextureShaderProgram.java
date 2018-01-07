package com.adrian.airhockeytextured.programs;

import android.content.Context;

import com.adrian.airhockeytextured.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by ranqing on 2018/1/6.
 */

public class TextureShaderProgram extends ShaderProgram {
    //Uniform locations
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;

    //Attribute locations
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

    public TextureShaderProgram(Context context) {
        super(context, R.raw.textrue_vertex_shader, R.raw.texture_fragment_shader);

        //Retrieve uniform locations for the shader program.
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);

        //Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    /**
     * 传递矩阵和纹理给它们的Uniform
     *
     * @param matrix
     * @param textureId
     */
    public void setUniforms(float[] matrix, int textureId) {
        //Pass the matrix into the shader program.
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

        //Set the active texture unit to texture unit 0.
        //把活动的纹理单元设置为纹理单元0
        glActiveTexture(GL_TEXTURE0);

        //Bind the texture to this unit.
        //把纹理绑定到这个单元
        glBindTexture(GL_TEXTURE_2D, textureId);

        //Tell the texture uniform sampler to use this texture in the shader by telling it to read from texture unit 0.
        //把被选定的纹理单元传递给片段着色器中的u_TextureUnit
        glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }
}
