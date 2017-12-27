package com.adrian.airhockey3d;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.adrian.airhockey3d.util.LoggerConfig;
import com.adrian.airhockey3d.util.ShaderHelper;
import com.adrian.airhockey3d.util.TextResourceReader;

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

public class AirHockeyRender_bak implements GLSurfaceView.Renderer {

    private static final String U_MATRIX = "u_Matrix";
    private final float[] projectionMatrix = new float[16];
    private int uMatrixLocation;
    private static final int POSITION_COMPONENT_COUNT = 4;
    private static final int BYTES_PER_FLOAT = 4;
    private FloatBuffer vertexData;
    private Context context;
    private int program;
    //    private static final String U_COLOR = "u_Color";
//    private int uColorLocation;
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    private static final String A_COLOR = "a_Color";
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT; //跨距。告之Opengl每个位置或者颜色之间有多少个字节，即需要跳过多少
    private int aColorLocation;

    public AirHockeyRender_bak(Context context) {
        this.context = context;
        float[] tableVertices = {
                //Order of coordinates:x,y,r,g b

                //Triangle Fan
                0f, 0f, 0f, 1.5f, 1f, 1f, 1f,
                -.5f, -.8f, 0f, 1f, .7f, .7f, .7f,
                .5f, -.8f, 0f, 1f, .7f, .7f, .7f,
                .5f, .8f, 0f, 2f, .7f, .7f, .7f,
                -.5f, .8f, 0f, 2f, .7f, .7f, .7f,
                -.5f, -.8f, 0f, 1f, .7f, .7f, .7f,

                //center line
                -0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,
                0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,

                //Mallets
                0f, -0.4f, 0f, 1.25f, 0f, 0f, 1f,
                0f, 0.4f, 0f, 1.75f, 1f, 0f, 0f
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

        aColorLocation = glGetAttribLocation(program, A_COLOR);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);

        glEnableVertexAttribArray(aPositionLocation);

        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aColorLocation);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        final float aspectRatio = width > height ? (float) width / (float) height : (float) height / (float) width;

        if (width > height) { //landscape
            Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {    //portrait or square
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);

        //绘制桌面
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

        //绘制分隔线
        glDrawArrays(GL_LINES, 6, 2);

        //绘制木锤
        glDrawArrays(GL_POINTS, 8, 1);
        glDrawArrays(GL_POINTS, 9, 1);
    }
}
