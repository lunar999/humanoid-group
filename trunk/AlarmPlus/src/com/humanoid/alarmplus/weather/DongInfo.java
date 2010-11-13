/*
 * @(#)DongInfo.java 1.0 2010. 10. 29.
 */
package com.humanoid.alarmplus.weather;

import java.util.ArrayList;
import java.util.HashMap;

import com.humanoid.alarmplus.util.UtilFile;

import android.content.Context;
import android.util.Log;

/**
 * <pre>
 * 1.기능 : . <br>
 * 2.처리 개요
 *     - 
 *     - 
 * 3.주의사항 
 *     -
 * </pre>
 * @author  천성민 2010. 10. 29.
 * @version  v1.0.0 
 * @see 
 * @since JDK v1.4
 */
public class DongInfo {

	private HashMap<String, String> dongMap = null;	
	
	public DongInfo(Context context) {
		initDongMap(context);		
	}
	
	private void initDongMap(Context context) {
		dongMap = new HashMap();
		ArrayList<String> dongList = UtilFile.readAssetsFileLine("dong_list.txt",context);
		if(dongList == null) {
			return;
		}
		
		Log.d(WeatherView.TAG, "####### dongList.size:"+dongList.size());
		
		String line = "";
		String[] dongInfo = null;
		
		//강원도 강릉시 초당동,93 132
		for (int i = 0; i < dongList.size(); i++) {
			line = dongList.get(i);
			dongInfo = line.split(",");
			
			dongMap.put(dongInfo[0], dongInfo[1]);
		}
	}
	
	public String[] getWeatherCode(String weather) {
		
		String codes = dongMap.get(weather);
		if(codes == null) {
			return null;
		}
		
		return codes.split(" ");
	}
}
