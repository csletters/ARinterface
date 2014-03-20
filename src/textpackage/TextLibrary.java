package textpackage;

import java.util.ArrayList;

import android.util.Log;

public class TextLibrary {
	
	//dimensions of text texture must be 512x200
	//max amount of characters per row is 16
	//max amount of characters per column is 6
	float maxNumCharsWidth = 16.0f;
	float maxNumCharsHeight = 6.0f;
	public ArrayList<Character> characters = new ArrayList<Character>();
	int asciiStart = 32;
	float characterWidth, characterHeight;
	float currentXLocation = 0.0f, currentYLocation = 1.0f;
	Words phrase =  new Words();
	
	public TextLibrary()
	{
		characterWidth = (1.0f/maxNumCharsWidth);
		characterHeight = 0.975f/maxNumCharsHeight;
		
		assignUVCords();
	}
	
	public void assignUVCords()
	{
		for(int y = 0; y < maxNumCharsHeight; y++)
		{
			for(int x = 0; x <maxNumCharsWidth; x++)
			{
				Character chara = new Character();
				float[] uv = new float[12];
				//top left vertex
				uv[0] = currentXLocation;
				uv[1] = currentYLocation;
				
				//bottom left vertex
				uv[2] = currentXLocation;
				uv[3] = currentYLocation-characterHeight;
				
				//bottom right vertex
				uv[4] = currentXLocation+characterWidth;
				uv[5] = currentYLocation-characterHeight;
				
				//top right vertex
				uv[6] = currentXLocation+characterWidth;
				uv[7] = currentYLocation;
				
				
				chara.setUVCords(uv);
				characters.add(chara);
				currentXLocation += characterWidth;
			}
			currentXLocation = 0.0f;
			currentYLocation -= characterHeight;
		}
	}
	
	public Character getCharacter(int ascii)
	{
		if(ascii == 63)
			ascii = 47;
		if(ascii == 47)
			ascii = 63;
		return characters.get(ascii - asciiStart);
	}
	
	public void addWords(String words)
	{
		for(int x = 0; x < words.length(); x++)
		{
			phrase.addLetter(getCharacter(words.charAt(x)));
		}
		phrase.newLine();
	}
	
	public Words createText()
	{
		phrase.combineText();
		return phrase;
	}
	
	public void newWord()
	{
		phrase =  new Words();
	}
	
	
	
	
	
}
