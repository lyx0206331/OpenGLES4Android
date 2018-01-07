package com.adrian.airhockeytextured;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.adrian.airhockeytextured.objects.Mallet;
import com.adrian.airhockeytextured.objects.Table;
import com.adrian.airhockeytextured.programs.ColorShaderProgram;
import com.adrian.airhockeytextured.programs.TextureShaderProgram;
import com.adrian.airhockeytextured.util.LoggerConfig;
import com.adrian.airhockeytextured.util.MatrixHelper;
import com.adrian.airhockeytextured.util.ShaderHelper;
import com.adrian.airhockeytextured.util.TextResourceReader;
import com.adrian.airhockeytextured.util.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * Created by adrian on 17-1-12.
 */

public class AirHockeyRender implements GLSurfaceView.Renderer {

    private Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private Table table;
    private Mallet mallet;

    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;

    private int texture;

    public AirHockeyRender(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0f, 0f, 0f, 0f);

        table = new Table();
        mallet = new Mallet();

        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        //用45度视野创建一个透视投影，z值为-1到-10。默认z在0位置,需要把坐标移动-1到-10以内才可见
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 10f);
//        Matrix.perspectiveM(projectionMatrix, 0, 45, (float)width/(float)height, 1f, 10f);

        //设置模型矩阵为单位矩阵
        Matrix.setIdentityM(modelMatrix, 0);
        //坐标沿z轴负方向移动2个单位
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f);
        //绕X轴旋转-60度
        Matrix.rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);

        final float[] temp = new float[16];
        //投影矩阵乘以模型矩阵
        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);

        //Draw the table.
        textureProgram.useProgram();
        textureProgram.setUniforms(projectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        //Draw the mallets.
        colorProgram.useProgram();
        colorProgram.setUniforms(projectionMatrix);
        mallet.bindData(colorProgram);
        mallet.draw();
    }

}
