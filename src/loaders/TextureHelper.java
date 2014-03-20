package loaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class TextureHelper {

	public static int[] loadTexture(final Context context, final int resourceId)
    {
            final int[] textureHandle = new int[1];
            
            GLES20.glGenTextures(1, textureHandle, 0);
            
            if (textureHandle[0] != 0)
            {
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                  //  Config config = Config.ARGB_4444; 
                  //  options.inPreferredConfig = config;
                    options.inScaled = false;        // No pre-scaling

                    // Read in the resource
                    final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
                                            
                    // Bind to the texture in OpenGL
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
                    
                    // Set filtering
                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                    //GLES20.glTexParameteri( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST );
                    //GLES20.glTexParameteri( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST );
                    //GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                    
                    // Load the bitmap into the bound texture.
                    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
                    
                    // Recycle the bitmap, since its data has been loaded into OpenGL.
                    bitmap.recycle();
            }
            
            if (textureHandle[0] == 0)
            {
                    throw new RuntimeException("Error loading texture.");
            }
            
            return textureHandle;
    }
	
	
	public static int[] loadTexture(Bitmap map)
	{
		int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);


        final Bitmap bitmap =  map;
        
        // Bind to the texture in OpenGL
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);


        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        // Load the bitmap into the bound texture.

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);


        // Recycle the bitmap, since its data has been loaded into OpenGL.
        bitmap.recycle();

		return textureHandle;
	}
	
	public static void subTexture(Bitmap bitmap,int[] textureHandle)
	{
		// Bind to the texture in OpenGL
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

        GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, bitmap);
        
        // Recycle the bitmap, since its data has been loaded into OpenGL.
        bitmap.recycle();
	}
	
	
	public static int[] getHandle()
	{
		int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);
		return textureHandle;
		
	}
	
	

}
