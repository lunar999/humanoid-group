package com.humanoid.alarmplus.weather;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class GpsSampleActivity extends Activity {

	private WeatherView view;
	
//	private String prevUrl;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		view = new WeatherView(this);
		
        // Request progress bar
//        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(view);
		
//		registerReceiver(receiver, new IntentFilter(
//				GpsService.WEATHER_INFORMATION_RECEIVER));
				
		Log.d(WeatherView.TAG, "######## onCreate");
	}
	
//	private BroadcastReceiver receiver = new BroadcastReceiver() {
//		public void onReceive(Context context, Intent intent) {
//			weatherSearch();
//		}
//	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if(GpsService.wInfo != null) {
			
			Toast.makeText(this, "전체주소:"+GpsService.currFullAddress, Toast.LENGTH_LONG).show();
			view.setWeatherInfo(GpsService.wInfo);
			view.invalidate();
		}
		
//		Toast.makeText(this, "onResume", Toast.LENGTH_LONG).show();
//		Log.d(WeatherView.TAG, "######## onResume");
//		registerReceiver(receiver, new IntentFilter(
//				GpsService.WEATHER_INFORMATION_RECEIVER));
		
//		setProgressBarIndeterminateVisibility(true);
//		setProgressBarIndeterminateVisibility(false);
//		Log.d(WeatherView.TAG, "######## onResume end");
	}

//	private void weatherSearch() {
//
//		Log.d(WeatherView.TAG, "######## weatherSearch start  GpsService.currentWeatherUrl:"+GpsService.currentWeatherUrl);
//		
//		if(GpsService.currentWeatherUrl == null) {
//			return;
//		}
//		
//		if(GpsService.currentWeatherUrl.equals(prevUrl)) {
//			return;
//		}
//		
//		prevUrl = GpsService.currentWeatherUrl;
//		
//		try {
//			URL m_UrlRecWeather = new URL(GpsService.currentWeatherUrl);
//			InputStream is = m_UrlRecWeather.openStream();
//			
//			XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); 
//			factory.setNamespaceAware(true); 
//			XmlPullParser xpp = factory.newPullParser();
//			
//			xpp.setInput(is, "utf-8");
//			ReceiveParsing(xpp);
//		} catch (Exception e) {
//			
//			Log.e(WeatherView.TAG, "error?", e);
//			e.printStackTrace();
//		}
//		
//		view.invalidate();
//	}
//	
//	private void ReceiveParsing(XmlPullParser ReceiveStream)
//	{
//		Log.d(WeatherView.TAG, "@@@@@@@@@@ ReceiveParsing");
//		boolean isHeader = false;
//		boolean isData = false;
//		boolean isSubData = false;
//
//		try	{			
//			WeatherInfo wInfo = new WeatherInfo();
//			wInfo.setAddress(GpsService.currAddress);
//			WeatherData wData = new WeatherData();
//
//			String sTag = "";
//			String sValue = "";
//
//			int eventType = ReceiveStream.getEventType(); 
//			while (eventType != XmlPullParser.END_DOCUMENT) {
//
//				//Wait(10);
//				switch (eventType)
//				{
//				case XmlPullParser.START_DOCUMENT:
//					break;
//				case XmlPullParser.END_DOCUMENT:
//					break;
//				case XmlPullParser.START_TAG:
//					//items.add(xpp.getAttributeValue(0));
//					sTag = ReceiveStream.getName();
//
////					Log.d(WeatherView.TAG, "============= sTag:"+sTag);
//					
//					if ( sTag.equals("header") ){
//						isHeader = true;
//					}
//					else if ( sTag.equals("data") ){
//						isData = true;
//						wData = new WeatherData();
//					}
//
//					if( isHeader == true )
//					{
////						if ( sTag.equals("city") ){
////							String sValue = ReceiveStream.getAttributeValue(0);
////						}
////						else if ( sTag.equals("current_date_time") ){
////							String sValue = ReceiveStream.getAttributeValue(0);
////						}
//					}
//					else if( isData == true ) {
//						
//						sValue = ReceiveStream.getName();
////						Log.d(WeatherView.TAG, "@@@@@@@@@@ sValue:"+sValue);
//						isSubData = true;
//					}
//
//					break;
//
//				case XmlPullParser.END_TAG:
//					sTag = ReceiveStream.getName();
//
//					if ( sTag.equals("header") ){
//						isHeader = false;
//					}       	
//					else if ( sTag.equals("data") ){
//						isData = false;
//						
//						wInfo.addWeatherData(wData);
//						wData = null;
//					}
//					
//					break;		            	
//
//				case XmlPullParser.TEXT:
//					
////					Log.d(WeatherView.TAG, "@@@@@@@@@@ sValue222222222:"+ReceiveStream.getText()+",isSubData:"+isSubData+" , sValue:"+sValue);
//					
//					if(isSubData) {
//						String sValue2 = ReceiveStream.getText();
//						
//						if ( sValue.equals("hour") ){
//							wData.setHour(sValue2);
//						}
//						else if ( sValue.equals("day") ){
//							wData.setDay(sValue2);
//						}
//						else if ( sValue.equals("temp") ){
//							wData.setTemp(sValue2);
//						}
//						else if ( sValue.equals("tmx") ){
//							wData.setTmx(sValue2);
//						}
//						else if ( sValue.equals("tmn") ){
//							wData.setTmn(sValue2);
//						}
//						else if ( sValue.equals("sky") ){
//							wData.setSky(sValue2);
//						}
//						else if ( sValue.equals("pty") ){
//							wData.setPty(sValue2);
//						}
//						else if ( sValue.equals("wfKor") ){
//							wData.setWfKor(sValue2);
//						}
//						else if ( sValue.equals("wfEn") ){
//							wData.setWfEn(sValue2);
//						}
//						else if ( sValue.equals("pop") ){
//							wData.setPop(sValue2);
//						}
//						else if ( sValue.equals("r12") ){
//							wData.setR12(sValue2);
//						}
//						else if ( sValue.equals("s12") ){
//							wData.setS12(sValue2);
//						}
//						else if ( sValue.equals("ws") ){
//							wData.setWs(sValue2);
//						}
//						else if ( sValue.equals("wdKor") ){
//							wData.setWdKor(sValue2);
//						}
//						else if ( sValue.equals("wdEn") ){
//							wData.setWdEn(sValue2);
//						}
//						else if ( sValue.equals("reh") ){
//							wData.setReh(sValue2);
//						}
//						isSubData = false;
//					}
//					
//					break;
//				}
//
//				eventType = ReceiveStream.next(); 
//			}
//			
//			view.setWeatherInfo(wInfo);
//		}
//		catch(Exception e)
//		{
//			Log.e(WeatherView.TAG, "뭔 에러냐?",e);
//		}
//	}


}