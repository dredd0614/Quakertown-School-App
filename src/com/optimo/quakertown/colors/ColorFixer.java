package com.optimo.quakertown.colors;

public class ColorFixer {

	
	
	public String RBGStringToHexString(String red, String green, String blue){
		return intToHex(red)+intToHex(green)+intToHex(blue);		
	}
	
	public String intToHex(String colorRGBValue){
		if(Integer.parseInt(colorRGBValue)>=256)
			colorRGBValue = "255";
		if(Integer.parseInt(colorRGBValue)<=-1)
			colorRGBValue = "0";
		colorRGBValue = Integer.toHexString(Integer.parseInt(colorRGBValue));
		if(colorRGBValue.length()==1){
			colorRGBValue = "0"+colorRGBValue;
		}
		return colorRGBValue;
	}
	
}

