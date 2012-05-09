package com.optimo.quakertown.drawable;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.util.StateSet;

public class SchoolAppGradientDrawable {
	
	static float lum = 1.3f;
	
	static public GradientDrawable generateGradientDrawable(String initialColor){
		Log.d("gradient Initial color: ",initialColor);
		int[] colors = new int[2];
		String newColor = "";
		if(initialColor.charAt(0)=='#'){
			initialColor = initialColor.substring(1,initialColor.length());
		}
		
		int color = 0;
		
		for(int i=0;i<3;i++){
			color = Integer.parseInt(initialColor.substring(i*2, (i*2+2)),16);
			color = Math.round(Math.min(Math.max(0, color + (color*lum)), 255));
			if(color<=15){
				//newColor += Integer.toHexString(color);
				newColor += "0";
				newColor += Integer.toHexString(color);
			}else
				newColor += Integer.toHexString(color);
		}
		Log.d("New color: ",newColor);
		colors[0] = Color.parseColor("#"+newColor);
		colors[1] = Color.parseColor("#"+initialColor);
		
		GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
		return gd;
	}
	
	static public StateListDrawable generateStateListDrawable(String initialColor){
		Log.d("Statelist Initial color: ",initialColor);

		int[] colors = new int[2];
		String newColor = "";
		if(initialColor.charAt(0)=='#'){
			initialColor = initialColor.substring(1,initialColor.length());
		}
		
		int color = 0;
		
		for(int i=0;i<3;i++){
			color = Integer.parseInt(initialColor.substring(i*2, (i*2+2)),16);
			color = Math.round(Math.min(Math.max(0, color + (color*lum)), 255));
			if(color<=15){
				//newColor += Integer.toHexString(color);
				newColor += "0";
				newColor += Integer.toHexString(color);
			}else
				newColor += Integer.toHexString(color);
		}
		Log.d("New color: ",newColor);
		colors[0] = Color.parseColor("#"+newColor);
		colors[1] = Color.parseColor("#"+initialColor);
		
		
		GradientDrawable gd1 = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
		gd1.setStroke(1, Color.parseColor("#000000"));
		GradientDrawable gd2 = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors);
		gd2.setStroke(1, Color.parseColor("#000000"));

		
		//State pressed:  Constant Value: 16842919 (0x010100a7)
		
		StateListDrawable sld = new StateListDrawable();
		sld.addState(new int[] {android.R.attr.state_pressed}, gd2);
		sld.addState(StateSet.WILD_CARD, gd1);

		
		return sld;
	}
	
}
