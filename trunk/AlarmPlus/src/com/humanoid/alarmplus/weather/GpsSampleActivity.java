package com.humanoid.alarmplus.weather;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

public class GpsSampleActivity extends Activity implements LocationListener{


	private LocationManager locationMgr;
	Location curLoc = null;
	private boolean isGpsEnabled;

	private DongInfo dongInfo;
	private String currAddress;
	private String[] addressCodes; 

	private final String URL_TEMPLATE = "http://www.kma.go.kr/wid/queryDFS.jsp?gridx=CODE1&gridy=CODE2"; 
	private String currentWeatherUrl;

	private WeatherView view;

	private Handler wHandler;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//		setContentView(R.layout.main);

		dongInfo = new DongInfo(this);
		view = new WeatherView(this);

		// Request progress bar
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(view);

		wHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				recvGpsData();
			}
		};
		Log.d(WeatherView.TAG, "######## onCreate");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();

		try {
			locationMgr.removeUpdates(this);
		} catch (Exception ignore) {
		}
		locationMgr = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		//		Toast.makeText(this, "onResume", Toast.LENGTH_LONG).show();
		Log.d(WeatherView.TAG, "######## onResume");
		wHandler.sendMessage(wHandler.obtainMessage());
		Log.d(WeatherView.TAG, "######## onResume end");
	}

	private void recvGpsData() {
		setProgressBarIndeterminateVisibility(true);

		try {
			Criteria c = new Criteria();

			c.setAccuracy(Criteria.ACCURACY_FINE);
			//c.setBearingRequired(true);

			locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,10, this);

			String bestP = locationMgr.getBestProvider(c, false);
			isGpsEnabled = locationMgr.isProviderEnabled(bestP);

			Log.d(WeatherView.TAG, "######## onResume isGpsEnabled:"+isGpsEnabled);
			Log.d(WeatherView.TAG, "######## onResume locationMgr:"+locationMgr);

			Location tempLocation = locationMgr.getLastKnownLocation(bestP);
			Log.d(WeatherView.TAG, "######## onResume tempLocation:"+tempLocation);

			if(tempLocation == null) {
				Toast.makeText(this, "onResume tempLocation null !!!", Toast.LENGTH_LONG).show();
				return;
			}

			try {
				curLoc = new Location(tempLocation);

				//				new Thread( null, reverseGeoCoderThread, "reverseGeoCoderThread" ).start();
				reverseGeoCoder();
			} catch (Exception e) {
				e.printStackTrace();

				Log.d(WeatherView.TAG, "######## exception 1:"+e);
				Toast.makeText(this, "######## exception 1:"+e, Toast.LENGTH_LONG).show();
				/*defaulting to our place*/
				curLoc = new Location("reverseGeocoded");

				//				hardFix.setLatitude(0);
				//				hardFix.setLongitude(0);

				curLoc.setLatitude(46.480302);
				curLoc.setLongitude(11.296005);

				/*New York*/
				//				hardFix.setLatitude(40.731510);
				//				hardFix.setLongitude(-73.991547);
				curLoc.setAltitude(300);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.d(WeatherView.TAG, "######## exception 2:"+e);
			Toast.makeText(this, "######## exception 2:"+e, Toast.LENGTH_LONG).show();
		}

		setProgressBarIndeterminateVisibility(false);
	}

	private void reverseGeoCoder() {

		Log.d(WeatherView.TAG, "######## reverseGeoCoder start");
		Geocoder mGeoCoder = new Geocoder(getApplicationContext(), Locale.KOREA);
		try {

			Log.d(WeatherView.TAG, "######## reverseGeoCoder curLoc:"+curLoc);

			if(this.curLoc == null) {
				Toast.makeText(this, "curLoc 없음", Toast.LENGTH_LONG).show();
				Log.d(WeatherView.TAG, "curLoc 없음");
				return;
			}

			//			List<Address> addr = mGeoCoder.getFromLocation( (double)curLoc.getLatitude()/1000000, (double)curLoc.getLongitude()/1000000, 5 );
			List<Address> addr = mGeoCoder.getFromLocation( (double)curLoc.getLatitude(), (double)curLoc.getLongitude(), 5 );
			Address address = null;

			Log.d(WeatherView.TAG, " addr.size():"+addr.size());
			if( addr.size() > 0 ) {
				address = addr.get(0);
				Log.d(WeatherView.TAG, " ####### address:"+address);
				Log.d(WeatherView.TAG, " address.getMaxAddressLineIndex():"+address.getMaxAddressLineIndex());
				Log.d(WeatherView.TAG, " address.getCountryName():"+address.getCountryName());
				Log.d(WeatherView.TAG, " address.getPostalCode():"+address.getPostalCode());

				//				if(address.getMaxAddressLineIndex()>0) {
				//					for( int i = 0; i < address.getMaxAddressLineIndex(); i++ ) {
				Toast.makeText(this, "주소 "+0+" :"+address.getAddressLine(0), Toast.LENGTH_LONG).show();
				Log.d(WeatherView.TAG, "주소 "+0+" :"+address.getCountryName() + address.getPostalCode());

				currAddress = address.getAddressLine(0);
				String[] splitData = currAddress.split(" ");
				Log.d(WeatherView.TAG, "splitData size :"+splitData.length);

				currAddress =  splitData[1]+" "+splitData[2]+" "+splitData[3];
				//						Toast.makeText(this, "currAddress :"+currAddress, Toast.LENGTH_LONG).show();
				if(dongInfo != null) {
					addressCodes = dongInfo.getWeatherCode(currAddress);
					if(addressCodes != null) {
						currentWeatherUrl = URL_TEMPLATE;
						currentWeatherUrl = currentWeatherUrl.replaceAll("CODE1", addressCodes[0]);
						currentWeatherUrl = currentWeatherUrl.replaceAll("CODE2", addressCodes[1]);

						Log.d(WeatherView.TAG, "addressCodes[0] :"+addressCodes[0]);
						Log.d(WeatherView.TAG, "addressCodes[1] :"+addressCodes[1]);
						Log.d(WeatherView.TAG, "currentWeatherUrl :"+currentWeatherUrl);
						//								Toast.makeText(this, "URL :"+currentWeatherUrl, Toast.LENGTH_LONG).show();

					}
					else {
						currentWeatherUrl = "http://www.kma.go.kr/wid/queryDFS.jsp?gridx=61&gridy=125";
					}

					//							InputStream is = HttpClient.getInstance().getHttpGETInputStream(currentWeatherUrl);
					//							String result = HttpClient.getInstance().getHttpInputString(is);
					//							Log.d(WeatherView.TAG, "result :"+result);

					//							HttpClient.getInstance().init();
					//							HttpClient.getInstance().setUri(currentWeatherUrl);
					//							try {
					//								Object result3 = HttpClient.getInstance().httpRequest(HttpClient.GET_TYPE , HttpClient.STRING_TYPE);
					//								Log.d(WeatherView.TAG, "result3 :"+result3);
					//
					//							} catch (Exception e) {
					//								Log.d(WeatherView.TAG, "###### e:"+e);
					//								e.printStackTrace();
					//							}

					URL m_UrlRecWeather = new URL(currentWeatherUrl);
					InputStream is = m_UrlRecWeather.openStream();
					//							String result = HttpClient.getInstance().getHttpInputString(is);
					//							Log.d(WeatherView.TAG, "result :"+result);

					XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); 
					factory.setNamespaceAware(true); 
					XmlPullParser xpp = factory.newPullParser();

					//							xpp.setInput(is, "euc-kr"); 
					xpp.setInput(is, "utf-8");
					ReceiveParsing(xpp);

					view.invalidate();

				}

				//					}					
				//				}
				//				else {
				//					Toast.makeText(this, "없음2", Toast.LENGTH_LONG).show();
				//					Log.d(WeatherView.TAG, "없음2");
				//				}
			}
			else {
				Toast.makeText(this, "없음", Toast.LENGTH_LONG).show();
				Log.d(WeatherView.TAG, "없음");
				//				handler.post( new Runnable() {
				//					public void run() {
				//						srcText.setText( "없음" );
				//						srcText.setVisibility( View.VISIBLE );
				//					}
				//				});
			}
		} catch( Exception e ) {
			Log.d(WeatherView.TAG, "######## exception 3:"+e);
			Toast.makeText(this, "######## exception 3:"+e, Toast.LENGTH_LONG).show();
		}
	}

	private void ReceiveParsing(XmlPullParser ReceiveStream)
	{
		Log.d(WeatherView.TAG, "@@@@@@@@@@ ReceiveParsing");
		boolean isHeader = false;
		boolean isData = false;
		boolean isSubData = false;

		try	{			
			WeatherInfo wInfo = new WeatherInfo();
			wInfo.setAddress(currAddress);
			WeatherData wData = new WeatherData();

			String sTag = "";
			String sValue = "";

			int eventType = ReceiveStream.getEventType(); 
			while (eventType != XmlPullParser.END_DOCUMENT) {

				//Wait(10);
				switch (eventType)
				{
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.END_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					//items.add(xpp.getAttributeValue(0));
					sTag = ReceiveStream.getName();

					//					Log.d(WeatherView.TAG, "============= sTag:"+sTag);

					if ( sTag.equals("header") ){
						isHeader = true;
					}
					else if ( sTag.equals("data") ){
						isData = true;
						wData = new WeatherData();
					}

					if( isHeader == true )
					{
						//						if ( sTag.equals("city") ){
						//							String sValue = ReceiveStream.getAttributeValue(0);
						//						}
						//						else if ( sTag.equals("current_date_time") ){
						//							String sValue = ReceiveStream.getAttributeValue(0);
						//						}
					}
					else if( isData == true ) {

						sValue = ReceiveStream.getName();
						//						Log.d(WeatherView.TAG, "@@@@@@@@@@ sValue:"+sValue);
						isSubData = true;
					}

					break;

				case XmlPullParser.END_TAG:
					sTag = ReceiveStream.getName();

					if ( sTag.equals("header") ){
						isHeader = false;
					}       	
					else if ( sTag.equals("data") ){
						isData = false;

						wInfo.addWeatherData(wData);
						wData = null;
					}

					break;		            	

				case XmlPullParser.TEXT:

					//					Log.d(WeatherView.TAG, "@@@@@@@@@@ sValue222222222:"+ReceiveStream.getText()+",isSubData:"+isSubData+" , sValue:"+sValue);

					if(isSubData) {
						String sValue2 = ReceiveStream.getText();

						if ( sValue.equals("hour") ){
							wData.setHour(sValue2);
						}
						else if ( sValue.equals("day") ){
							wData.setDay(sValue2);
						}
						else if ( sValue.equals("temp") ){
							wData.setTemp(sValue2);
						}
						else if ( sValue.equals("tmx") ){
							wData.setTmx(sValue2);
						}
						else if ( sValue.equals("tmn") ){
							wData.setTmn(sValue2);
						}
						else if ( sValue.equals("sky") ){
							wData.setSky(sValue2);
						}
						else if ( sValue.equals("pty") ){
							wData.setPty(sValue2);
						}
						else if ( sValue.equals("wfKor") ){
							wData.setWfKor(sValue2);
						}
						else if ( sValue.equals("wfEn") ){
							wData.setWfEn(sValue2);
						}
						else if ( sValue.equals("pop") ){
							wData.setPop(sValue2);
						}
						else if ( sValue.equals("r12") ){
							wData.setR12(sValue2);
						}
						else if ( sValue.equals("s12") ){
							wData.setS12(sValue2);
						}
						else if ( sValue.equals("ws") ){
							wData.setWs(sValue2);
						}
						else if ( sValue.equals("wdKor") ){
							wData.setWdKor(sValue2);
						}
						else if ( sValue.equals("wdEn") ){
							wData.setWdEn(sValue2);
						}
						else if ( sValue.equals("reh") ){
							wData.setReh(sValue2);
						}
						isSubData = false;
					}

					break;
				}

				eventType = ReceiveStream.next(); 
			}

			view.setWeatherInfo(wInfo);
		}
		catch(Exception e)
		{
			Log.e(WeatherView.TAG, "뭔 에러냐?",e);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		try {
			//			killOnError();
			if (LocationManager.GPS_PROVIDER.equals(location.getProvider())) {
				synchronized (curLoc) {
					curLoc = location;
				}
				isGpsEnabled = true;
				reverseGeoCoder();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.d(WeatherView.TAG, "######## exception 4:"+ex);
		}		
	}

	@Override
	public void onProviderDisabled(String provider) {
		isGpsEnabled = locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	@Override
	public void onProviderEnabled(String provider) {
		isGpsEnabled = locationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}


}