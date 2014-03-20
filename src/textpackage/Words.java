package textpackage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import loaders.ShaderHandles;

import android.opengl.GLES20;
import android.util.Log;

public class Words {

	float[] vertices;
	float[] uv;
	float numCharacters = 0;
	public float numLines = 0;
	ArrayList<Float> vertex = new ArrayList<Float>();
	ArrayList<Float> texCords = new ArrayList<Float>();
	FloatBuffer vertexBuffer,texBuffer;
	int buffers[] = new int[2];
	int positions,texcords;
	public float maxCharacters = 0;
	
	public Words()
	{
	}
	
	public void addLetter(Character character) {
		vertex.add(numCharacters+0.0f);
		vertex.add(1.0f-numLines);
		vertex.add(0.0f);
		
		vertex.add(numCharacters+0.0f);
		vertex.add(0.0f-numLines);
		vertex.add(0.0f);
		
		vertex.add(numCharacters+1.0f);
		vertex.add(0.0f-numLines);
		vertex.add(0.0f);
		
		vertex.add(numCharacters+0.0f);
		vertex.add(1.0f-numLines);
		vertex.add(0.0f);
		
		vertex.add(numCharacters+1.0f);
		vertex.add(0.0f-numLines);
		vertex.add(0.0f);
		
		vertex.add(numCharacters+1.0f);
		vertex.add(1.0f-numLines);
		vertex.add(0.0f);

		numCharacters++;
		
		float[] cords = character.getTexCords();
		
		texCords.add(cords[0]);
		texCords.add(cords[1]);
		texCords.add(cords[2]);
		texCords.add(cords[3]);
		texCords.add(cords[4]);
		texCords.add(cords[5]);
		
		texCords.add(cords[0]);
		texCords.add(cords[1]);
		texCords.add(cords[4]);
		texCords.add(cords[5]);
		texCords.add(cords[6]);
		texCords.add(cords[7]);
		
	}
	
	public void newLine()
	{
		numLines++;
		if(maxCharacters < numCharacters)
			maxCharacters = numCharacters;
		numCharacters = 0;
	}
	
	public void combineText()
	{
		vertices = new float[vertex.size()];
		uv = new float[texCords.size()];
		for(int x = 0; x < vertex.size();x++)
		{
			vertices[x] = vertex.get(x);
		}
		
		for(int x = 0; x < texCords.size();x++)
		{
			uv[x] = texCords.get(x);
		}
		
		// buffer for cube vertices
		ByteBuffer buffer = ByteBuffer.allocateDirect(4 * vertices.length);
		buffer.order(ByteOrder.nativeOrder());

		vertexBuffer = buffer.asFloatBuffer();
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);
		
		//buffer for texture cords
		ByteBuffer tBuffer = ByteBuffer.allocateDirect(4 * uv.length);
		tBuffer.order(ByteOrder.nativeOrder());

		texBuffer = tBuffer.asFloatBuffer();
		texBuffer.put(uv);
		texBuffer.position(0);
		
	}
	
	public void draw( float[] projection, float[] view, float[] model,ShaderHandles Shader, int textures)
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
		
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.length/3);
        
		GLES20.glDisableVertexAttribArray(Shader.positionHandle);
		GLES20.glDisableVertexAttribArray(Shader.mTextureCoordinateHandle);
	}
	
	public void draw( float[] projection, float[] mv,ShaderHandles Shader, int textures)
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
		GLES20.glUniformMatrix4fv(Shader.mvHandle, 1, false, mv, 0);
		
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertices.length/3);
        
		GLES20.glDisableVertexAttribArray(Shader.positionHandle);
		GLES20.glDisableVertexAttribArray(Shader.mTextureCoordinateHandle);
	}
	
	
}
