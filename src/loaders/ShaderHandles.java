package loaders;

import java.util.ArrayList;

public class ShaderHandles {
	public int programHandle;
	public int positionHandle,normalHandle,mTextureCoordinateHandle,modelHandle,viewHandle,projectionHandle,isColored,colorHandle,lightHandle,viewPointHandle,tangentHandle,mvHandle;
	public ArrayList<Integer> mTextureDataHandle = new ArrayList<Integer>();
	public ArrayList<Integer> mTextureUniformHandle = new ArrayList<Integer>();
	
	
	public ShaderHandles()
	{
	}
}
