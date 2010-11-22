/**
 * 
 */
package com.humanoid.alarmplus.weather;

import java.util.List;
import java.util.Locale;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * @author min
 *
 */
public class GpsService extends Service implements LocationListener{
	
	public static final String GPS_SERVICE = "com.humanoid.alarmplus.weather.GpsService";
	
	private LocationManager locationMgr;
	private Criteria criteria;
	Location curLoc = null;
	private boolean isGpsEnabled;
	
	private DongInfo dongInfo;
	public static String currAddress;
	public static String currFullAddress;
	private String[] addressCodes;
	
	private final String URL_TEMPLATE = "http://www.kma.go.kr/wid/queryDFS.jsp?gridx=CODE1&gridy=CODE2"; 
	public static String currentWeatherUrl;
	
	public static final String WEATHER_INFORMATION_RECEIVER = "com.humanoid.alarmplus.weather.WeatherInformationUpdateEvent";
	
	private String prevUrl;
	
	public static WeatherInfo wInfo;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate() {
		super.onCreate();
		locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		criteria = new Criteria();
		criteria.setAccuracy(Criteria.NO_REQUIREMENT);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		dongInfo = new DongInfo(this);		
/*		
//		Log.d(WeatherView.TAG, "######## Service onCreate:"+isGpsEnabled);
		try {
			
			dongInfo = new DongInfo(this);
			
			Criteria c = new Criteria();

			c.setAccuracy(Criteria.NO_REQUIREMENT);
			c.setPowerRequirement(Criteria.POWER_LOW);

			locationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			
			//String bestP = locationMgr.getBestProvider(c, false);
			String bestP = locationMgr.getBestProvider(c, true);
			
//			locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000,10, this);
			locationMgr.requestLocationUpdates(bestP, 60000,100, this);
//			locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,1, this);

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
				//reverseGeoCoder();
			} catch (Exception e) {
				
				Log.e(WeatherView.TAG, "######## exception 1:"+e,e);
//				Toast.makeText(this, "######## exception 1:"+e, Toast.LENGTH_LONG).show();
				defaulting to our place
				curLoc = new Location("reverseGeocoded");
				curLoc.setLatitude(46.480302);
				curLoc.setLongitude(11.296005);

				curLoc.setAltitude(300);
			}
		} catch (Exception e) {
//			e.printStackTrace();
			Log.e(WeatherView.TAG, "######## exception 2:"+e,e);
//			Toast.makeText(this, "######## exception 2:"+e, Toast.LENGTH_LONG).show();
		}
		
*/		
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);		
		try {	
			String bestP = locationMgr.getBestProvider(criteria, true);			
			locationMgr.requestLocationUpdates(bestP, 30000,100, this);
//			locationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,1, this);

			isGpsEnabled = locationMgr.isProviderEnabled(bestP);

			Log.d(WeatherView.TAG, "######## onResume isGpsEnabled:"+isGpsEnabled);
			Log.d(WeatherView.TAG, "######## onResume locationMgr:"+locationMgr);
			
			Location tempLocation = locationMgr.getLastKnownLocation(bestP);
			Log.d(WeatherView.TAG, "######## onResume tempLocation:"+tempLocation);
			
			if(tempLocation == null) {
				Toast.makeText(this, "Can not find your current location !!!", Toast.LENGTH_LONG).show();
				return;
			}
			
			try {
				curLoc = new Location(tempLocation);
				reverseGeoCoder();
			} catch (Exception e) {
				
				Log.e(WeatherView.TAG, "######## exception 1:"+e,e);
//				Toast.makeText(this, "######## exception 1:"+e, Toast.LENGTH_LONG).show();
				/*defaulting to our place*/
				curLoc = new Location("reverseGeocoded");
				curLoc.setLatitude(46.480302);
				curLoc.setLongitude(11.296005);

				curLoc.setAltitude(300);
			}

		} catch (Exception e) {
//			e.printStackTrace();
			Log.e(WeatherView.TAG, "######## exception 2:"+e,e);
//			Toast.makeText(this, "######## exception 2:"+e, Toast.LENGTH_LONG).show();
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (locationMgr != null) {
			locationMgr.removeUpdates(this);
		}
		locationMgr = null;
	}
	
	private void reverseGeoCoder() {

		Log.d(WeatherView.TAG, "######## reverseGeoCoder start");
		Geocoder mGeoCoder = new Geocoder(getApplicationContext(), Locale.KOREA);
		try {
//			Log.d(WeatherView.TAG, "######## reverseGeoCoder curLoc:"+curLoc);
			if(this.curLoc == null) {
				Toast.makeText(this, "curLoc 없음", Toast.LENGTH_LONG).show();
				Log.d(WeatherView.TAG, "curLoc 없음");
				return;
			}
			
			List<Address> addr = mGeoCoder.getFromLocation( (double)curLoc.getLatitude(), (double)curLoc.getLongitude(), 5 );
			Address address = null;
			
//			Log.d(WeatherView.TAG, " addr.size():"+addr.size());
			if( addr.size() > 0 ) {
				address = addr.get(0);
//				Log.d(WeatherView.TAG, " ####### address:"+address);
//				Log.d(WeatherView.TAG, " address.getMaxAddressLineIndex():"+address.getMaxAddressLineIndex());
//				Log.d(WeatherView.TAG, " address.getCountryName():"+address.getCountryName());
//				Log.d(WeatherView.TAG, " address.getPostalCode():"+address.getPostalCode());
				
//				if(address.getMaxAddressLineIndex()>0) {
//					for( int i = 0; i < address.getMaxAddressLineIndex(); i++ ) {
//						Toast.makeText(this, "주소 "+0+" :"+address.getAddressLine(0), Toast.LENGTH_LONG).show();
//						Log.d(WeatherView.TAG, "주소 "+0+" :"+address.getCountryName() + address.getPostalCode());
						
						currAddress = address.getAddressLine(0);
						currFullAddress = currAddress;
						String[] splitData = currAddress.split(" ");
//						Log.d(WeatherView.TAG, "splitData size :"+splitData.length);
						
						currAddress =  splitData[1]+" "+splitData[2]+" "+splitData[3];
//						Toast.makeText(this, "currAddress :"+currAddress, Toast.LENGTH_LONG).show();
						if(dongInfo != null) {
							addressCodes = dongInfo.getWeatherCode(currAddress);
							if(addressCodes != null) {
								currentWeatherUrl = URL_TEMPLATE;
								currentWeatherUrl = currentWeatherUrl.replaceAll("CODE1", addressCodes[0]);
								currentWeatherUrl = currentWeatherUrl.replaceAll("CODE2", addressCodes[1]);
								
								Log.d(WeatherView.TAG, "addressCodes[0] :"+addressCodes[0]);
//								Log.d(WeatherView.TAG, "addressCodes[1] :"+addressCodes[1]);
//								Log.d(WeatherView.TAG, "currentWeatherUrl :"+currentWeatherUrl);
//								Toast.makeText(this, "URL :"+currentWeatherUrl, Toast.LENGTH_LONG).show();
								
							}
							else {
								currentWeatherUrl = "http://www.kma.go.kr/wid/queryDFS.jsp?gridx=61&gridy=125";
							}
							
							Log.d(WeatherView.TAG, "currentWeatherUrl:"+currentWeatherUrl);
							Log.d(WeatherView.TAG, "prevUrl          :"+prevUrl);
							if(!currentWeatherUrl.equals(prevUrl)) {
								prevUrl = currentWeatherUrl;
								
//								weatherSearch();
//								Intent intent = new Intent(WEATHER_INFORMATION_RECEIVER);
//								sendBroadcast(intent);
							}
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
			Log.e(WeatherView.TAG, "######## exception 3:"+e,e);
//			Toast.makeText(this, "######## exception 3:"+e, Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		
		Log.d(WeatherView.TAG, "onLocationChanged 111");
		try {
			//			killOnError();
			if (LocationManager.GPS_PROVIDER.equals(location.getProvider())) {
				Log.d(WeatherView.TAG, "onLocationChanged 222");
				synchronized (curLoc) {
					curLoc = location;
				}
				isGpsEnabled = true;
				reverseGeoCoder();
			}
			else {
				Log.d(WeatherView.TAG, "onLocationChanged 333");
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
