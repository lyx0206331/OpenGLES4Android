package com.adrian.airhockeytextured.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;

/**
 * Created by qing on 2018/1/3 0003.
 */

public class TextureHelper {

    private static final String TAG = "TextureHelper";

    /**
     * 加载图形文件到OpenGL，生成纹理ID，用作纹理引用
     *
     * @param context
     * @param resourceId
     * @return
     */
    public static int loadTexture(Context context, int resourceId) {
        final int[] textureObjectIds = new int[1];
        //创建纹理对象
        glGenTextures(1, textureObjectIds, 0);
        if (textureObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                LoggerConfig.LogW(TAG, "Could not generate a new OpenGL texture object.");
            }
            return 0;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        //取图片的原始数据，非缩放版本
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

        if (bitmap == null) {
            if (LoggerConfig.ON) {
                LoggerConfig.LogW(TAG, "ResourceID:" + resourceId + " could not be decoded.");
            }
            glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

        //设置纹理过滤参数。
        //缩小（GL_TEXTURE_MIN_FILTER）时，使用三线性过滤（GL_LINEAR_MIPMAP_LINEAR）
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        //放大（GL_TEXTURE_MAG_FILTER）时，采用双线性过滤（GL_LINEAR）
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        /*
        纹理过滤模式：
        GL_NEAREST 最近邻过滤
        GL_NEAREST_MIPMAP_NEAREST 使用MIP贴图的最近邻过滤
        GL_NEAREST_MIPMAP_LINEAR 使用MIP贴图级别之间插值的最近邻过滤
        GL_LINEAR 双线性过滤
        GL_LINEAR_MIPMAP_NEAREST 使用MIP贴图的双线性过滤
        GL_LINEAR_MIPMAP_LINEAR 三线性过滤(使用MIP贴图级别之间插值的双线性过滤)
        GL_NEAREST和GL_LINEAR可用于缩小放大两种情况，其它只能用于缩小
         */

        //读入bitmap定义的位图数据，并把它复制到当前绑定的纹理对象
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        //数据已经加载进了OpenGL，立即释放数据
        bitmap.recycle();
        //生成所有必要级别的MIP贴图
        glGenerateMipmap(GL_TEXTURE_2D);
        //完成纹理加载后，解除与这个纹理的绑定，防止其它纹理方法调用意外地改变这个纹理。传递0值即为解除绑定
        glBindTexture(GL_TEXTURE_2D, 0);
        return textureObjectIds[0];
    }
}
