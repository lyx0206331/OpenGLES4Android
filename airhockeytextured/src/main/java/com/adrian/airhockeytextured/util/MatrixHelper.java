package com.adrian.airhockeytextured.util;

/**
 * Created by qing on 2017/12/28 0028.
 */

public class MatrixHelper {

    /**
     * 自定义透视投影，如：
     * Matrix.perspectiveM(float[] m, int offset, float fovy, float aspect, float zNear, float zFar)
     * 矩阵数据按照以列为主的顺序存储，而不是行，至少16个元素
     *
     * @param m
     * @param yFovInDegrees
     * @param aspect
     * @param n
     * @param f
     */
    public static void perspectiveM(float[] m, float yFovInDegrees, float aspect, float n, float f) {
        final float angleInRadians = (float) (yFovInDegrees * Math.PI / 180.0);
        final float a = (float) (1.0 / Math.tan(angleInRadians / 2.0));

        m[0] = a / aspect;
        m[1] = 0f;
        m[2] = 0f;
        m[3] = 0f;

        m[4] = 0f;
        m[5] = a;
        m[6] = 0f;
        m[7] = 0f;

        m[8] = 0f;
        m[9] = 0f;
        m[10] = -((f + n) / (f - n));
        m[11] = -1f;

        m[12] = 0f;
        m[13] = 0f;
        m[14] = -((2f * f * n) / (f - n));
        m[15] = 0f;
    }
}
