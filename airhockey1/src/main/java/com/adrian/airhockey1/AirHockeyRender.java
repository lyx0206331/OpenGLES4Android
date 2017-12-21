package com.adrian.airhockey1;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.adrian.airhockey1.util.LoggerConfig;
import com.adrian.airhockey1.util.ShaderHelper;
import com.adrian.airhockey1.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * Created by adrian on 17-1-12.
 */

public class AirHockeyRender implements GLSurfaceView.Renderer {

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTES_PER_FLOAT = 4;
    private FloatBuffer vertexData;
    private Context context;
    private int program;
    private static final String U_COLOR = "u_Color";
    private int uColorLocation;
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    public AirHockeyRender(Context context) {
        this.context = context;
        float[] tableVertices = {
                //border
                -0.51f, -0.51f,
                0.51f, 0.51f,
                -0.51f, 0.51f,

                -0.51f, -0.51f,
                0.51f, -0.51f,
                0.51f, 0.51f,

                //Triangle1
                -0.5f, -0.5f,
                0.5f, 0.5f,
                -0.5f, 0.5f,

                //Triangle2
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.5f, 0.5f,

                //center line
                -0.5f, 0f,
                0.5f, 0f,

                //Mallets
                0f, -0.25f,
                0f, 0.25f,

                //ball
                -0.2f, -0.2f,
                0.2f, 0.2f,
                -0.2f, 0.2f,
                -0.2f, -0.2f,
                0.2f, -0.2f,
                0.2f, 0.2f
        };

        vertexData = ByteBuffer.allocateDirect(tableVertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(tableVertices);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        String vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmengShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        program = ShaderHelper.linkProgram(vertexShader, fragmengShader);

        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }

        glUseProgram(program);

        uColorLocation = glGetUniformLocation(program, U_COLOR);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);

        glEnableVertexAttribArray(aPositionLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        //绘制外边框
        glUniform4f(uColorLocation, 0.0f, 1.0f, 0.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 0, 6);

        //绘制桌面
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 6, 6);

        //绘制分隔线
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 12, 2);

        //绘制木锤
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_POINTS, 14, 1);
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 15, 1);

        //绘制冰球
        glUniform4f(uColorLocation, .5f, .5f, .5f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 16, 6);
    }
}
