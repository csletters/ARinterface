package textpackage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import loaders.ShaderHandles;

import android.opengl.GLES20;

public class Character {

	float vertices[] = {
			0.0f, 1.0f, 0.0f,
			0.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			1.0f,1.0f,0.0f
			};
	
	
	short vertexOrder[] = {0,1,2,0,2,3};
	
	FloatBuffer vertexBuffer,normalBuffer,texBuffer;
	ShortBuffer drawlistBuffer;
	private float[] uvCords;
	public Character()
	{
		// buffer for cube vertices
		ByteBuffer buffer = ByteBuffer.allocateDirect(4 * vertices.length);
		buffer.order(ByteOrder.nativeOrder());

		vertexBuffer = buffer.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
		
		// buffer for vertex order
		ByteBuffer bufferOrder = ByteBuffer.allocateDirect(2 * vertexOrder.length);
		bufferOrder.order(ByteOrder.nativeOrder());

		drawlistBuffer = bufferOrder.asShortBuffer();
		drawlistBuffer.put(vertexOrder);
		drawlistBuffer.position(0);

	}
	
	public void setUVCords(float[] cords)
	{
		uvCords = cords;
		//buffer for texture cords
		ByteBuffer tBuffer = ByteBuffer.allocateDirect(4 * cords.length);
		tBuffer.order(ByteOrder.nativeOrder());

		texBuffer = tBuffer.asFloatBuffer();
		texBuffer.put(cords);
		texBuffer.position(0);
	}
	
	public float[] getTexCords()
	{
		return uvCords;
	}
	
	public void draw(float[] projection, float[] view, float[] model,ShaderHandles Shader, int textures)
	{
		//position
		GLES20.glEnableVertexAttribArray(Shader.positionHandle);
		GLES20.glVertexAttribPointer(Shader.positionHandle, 3, GLES20.GL_FLOAT, false,	3 * 4, vertexBuffer);
		
		// Bind the texture to this unit.
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures);
		GLES20.glUniform1i(Shader.mTextureUniformHandle.get(0), 0);
		
		//texture
		GLES20.glEnableVertexAttribArray(Shader.mTextureCoordinateHandle);
		GLES20.glVertexAttribPointer(Shader.mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false,2 * 4, texBuffer);
		
		//send uniforms
		GLES20.glUniformMatrix4fv(Shader.projectionHandle, 1, false, projection, 0);
		GLES20.glUniformMatrix4fv(Shader.viewHandle, 1, false, view, 0);
		GLES20.glUniformMatrix4fv(Shader.modelHandle, 1, false, model, 0);
		
		
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, vertexOrder.length,GLES20.GL_UNSIGNED_SHORT, drawlistBuffer);
		
		GLES20.glDisableVertexAttribArray(Shader.positionHandle);
		GLES20.glDisableVertexAttribArray(Shader.mTextureCoordinateHandle);
	}
}
