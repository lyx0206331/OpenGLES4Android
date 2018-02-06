package com.adrian.airhockeytouch;

import android.content.Context;
import android.opengl.GLSurfaceView;


import com.adrian.airhockeytouch.objects.Mallet;
import com.adrian.airhockeytouch.objects.Puck;
import com.adrian.airhockeytouch.objects.Table;
import com.adrian.airhockeytouch.programs.ColorShaderProgram;
import com.adrian.airhockeytouch.programs.TextureShaderProgram;
import com.adrian.airhockeytouch.util.Geometry;
import com.adrian.airhockeytouch.util.MatrixHelper;
import com.adrian.airhockeytouch.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

/**
 * Created by adrian on 17-1-12.
 */

public class AirHockeyRender implements GLSurfaceView.Renderer {

    private Context context;

    //视图矩阵
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private final float[] invertedViewProjectionMatrix = new float[16];

    private Table table;
    private Mallet mallet;
    private Puck puck;

    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;

    private int texture;

    private boolean malletPressed = false;
    private Geometry.Point blueMalletPosition;

    public AirHockeyRender(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0f, 0f, 0f, 0f);

        table = new Table();
        mallet = new Mallet(.08f, .15f, 32);
        puck = new Puck(.06f, .02f, 32);

        textureProgram = new TextureShaderProgram(context);
        colorProgram = new ColorShaderProgram(context);

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);

        blueMalletPosition = new Geometry.Point(0f, mallet.height / 2f, .4f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);

        //用45度视野创建一个透视投影，z值为-1到-10。默认z在0位置,需要把坐标移动-1到-10以内才可见
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 10f);
//        Matrix.perspectiveM(projectionMatrix, 0, 45, (float)width/(float)height, 1f, 10f);
        //创建视图矩阵
        setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);

        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        //创建反转矩阵，取消视图矩阵和投影矩阵
        invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);

        //Draw the table.
        positionTableInScene();
        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        //Draw the mallets.
        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
        colorProgram.useProgram();
        colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f);
        mallet.bindData(colorProgram);
        mallet.draw();

        positionObjectInScene(0f, mallet.height / 2f, .4f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
        //Note that we don't have to define the project data twice -- we just draw the same mallet again but in a different position and with a different color.
        mallet.draw();

        //Draw the puck
        positionObjectInScene(0f, puck.height / 2f, 0f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, .8f, 1f);
        puck.bindData(colorProgram);
        puck.draw();
    }

    private void positionTableInScene() {
        //The table is defined in terms of X & Y coordinates, so we rotate it 90 degrees to lie flat on the XZ plane.
        setIdentityM(modelMatrix, 0);
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }

    private void positionObjectInScene(float x, float y, float z) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }

    public void handleTouchPress(float normalizedX, float normalizedY) {
        Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);

        //Now test if this ray intersects with the mallet by creating a bounding sphere that wraps the mallet.
        Geometry.Sphere malletBoundingSphere = new Geometry.Sphere(
                new Geometry.Point(
                        blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z
                ), mallet.height / 2f);

        //If the ray intersects (if the user touched a part of the screen that intersects the mallet's bounding sphere), the set malletPressed = true.
        malletPressed = Geometry.intersects(malletBoundingSphere, ray);
    }

    public void handleTouchDrag(float normalizedX, float normalizedY) {
        if (malletPressed) {
            Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
            //Define a plane representing our air hockey table.
            Geometry.Plane plane = new Geometry.Plane(new Geometry.Point(0, 0, 0), new Geometry.Vector(0, 1, 0));
            //Find out where the touched point intersects the plane representing our table.We'll move the mallet along this plane.
            Geometry.Point touchedPoint = Geometry.intersectionPoint(ray, plane);
            blueMalletPosition = new Geometry.Point(touchedPoint.x, mallet.height / 2f, touchedPoint.z);
        }
    }

    private Geometry.Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY) {
        //We'll convert these normalized device coordinates into world-space
        //coordinates. We'll pick a point on the near and far planes, and draw a
        //line between them. To do this transform, we need to first multiply by
        //the inverse matrix, and then we need to undo the perspective divide.
        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};

        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];

        multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);

        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        Geometry.Point nearPointRay = new Geometry.Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        Geometry.Point farPointRay = new Geometry.Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);

        return new Geometry.Ray(nearPointRay, Geometry.vectorBetween(nearPointRay, farPointRay));
    }

    /**
     * 撤销透视除法的影响
     *
     * @param vector
     */
    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }

}
