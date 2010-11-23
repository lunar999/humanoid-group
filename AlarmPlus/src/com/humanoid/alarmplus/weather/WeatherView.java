/*
 * @(#)WeatherView.java 1.0 2010. 10. 29.
 */
package com.humanoid.alarmplus.weather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.humanoid.alarmplus.R;

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
public class WeatherView extends View {

	public static String TAG =  WeatherView.class.getSimpleName();
	
	Context context = null;
	private Rect gameRect = null;
	private int WIDTH;
	private int HEIGHT;
	private final int SMALL_HEIGHT = 150;
	
	private Bitmap w_00; 
	private Bitmap w_01_1;
	private Bitmap w_01_2;
	private Bitmap w_02_1;
	private Bitmap w_02_2;
	private Bitmap w_03_1;
	private Bitmap w_03_2;
	private Bitmap w_04;
	private Bitmap w_05;
	private Bitmap w_07;
	private Bitmap w_08;
	private Bitmap w_11;
	private Bitmap w_14;
	private Bitmap w_15;
	private Bitmap w_16;
	private Bitmap w_17;
	private Bitmap w_rs;
	private Bitmap w_sr;
	
	private Bitmap mainImage;
	
	private WeatherInfo weatherInfo;
	
	public WeatherView(Context context) {
		super(context);
		init(context);
	}
	
	public WeatherInfo getWeatherInfo() {
		return weatherInfo;
	}

	public void setWeatherInfo(WeatherInfo weatherInfo) {
		this.weatherInfo = weatherInfo;
		
		mainImage = w_01_1;
	}
	
	private void init(Context context) {
		loadImage(context);
		
		mainImage = w_01_1;
	}
	
	private void loadImage(Context context) {
		w_00 = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_00);
		w_01_1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_01_1);
		w_01_2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_01_2);
		w_02_1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_02_1);
		w_02_2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_02_2);
		w_03_1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_03_1);
		w_03_2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_03_2);
		w_04 = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_04);
		w_05 = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_05);
		w_07 = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_07);
		w_08 = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_08);
		w_11 = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_11);
		w_14 = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_14);
		w_15 = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_15);
		w_16 = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_16);
		w_17 = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_17);
		w_rs = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_rs);
		w_sr = BitmapFactory.decodeResource(context.getResources(), R.drawable.w_sr);
	}
	
	public Bitmap getCurrWeatherImage(String weather, String hour) {
		String tempHour = hour;		
		if("맑음".equals(weather)) {
			if (tempHour.equals("18") || tempHour.equals("21") || tempHour.equals("24") || tempHour.equals("3")) {
				return w_01_2;
			}
			return w_01_1;
		}
		else if("구름조금".equals(weather)) {
			if (tempHour.equals("18") || tempHour.equals("21") || tempHour.equals("24") || tempHour.equals("3")) {
				return w_02_2;
			}
			return w_02_1;
		}
		else if("구름많음".equals(weather)) {
			if (tempHour.equals("18") || tempHour.equals("21") || tempHour.equals("24") || tempHour.equals("03")) {
				return w_03_2;
			}
			return w_03_1;
		}
		else if("흐림".equals(weather)) {
			return w_04;
		}
		else if("추후매핑예정1".equals(weather)) {
			return w_05;
		}
		else if("추후매핑예정2".equals(weather)) {
			return w_07;
		}
		else if("비".equals(weather)) {
			return w_08;
		}
		else if("눈".equals(weather)) {
			return w_11;
		}
		else if("추후매핑예정3".equals(weather)) {
			return w_14;
		}
		else if("추후매핑예정4".equals(weather)) {
			return w_15;
		}
		else if("추후매핑예정5".equals(weather)) {
			return w_16;
		}
		else if("추후매핑예정6".equals(weather)) {
			return w_17;
		}
		else if("추후매핑예정7".equals(weather)) {
			return w_rs;
		}
		else if("추후매핑예정8".equals(weather)) {
			return w_sr;
		}
		else {
			return w_00;
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		drawBackGround(canvas);
		
		Paint paint = new Paint();
		
//		Log.d(TAG, "############# weatherInfo:"+weatherInfo);
		if(weatherInfo != null) {
//			paint.setStyle(Paint.Style.STROKE);  
//			paint.setStrokeWidth(1);
			paint.setStyle(Paint.Style.FILL);  
			paint.setStrokeWidth(1);  
			paint.setAntiAlias(true);
//			paint.setColor(Color.WHITE);  
			paint.setColor(0xff808080);  		
			
			WeatherData wData = weatherInfo.getWeatherList().get(0);
			
			paint.setTextSize(40);
			canvas.drawText(wData.getTemp(), 30, 50, paint);
			
			paint.setTextSize(25);
			canvas.drawText("o", 120, 35, paint);
			
			paint.setTextSize(25);
			canvas.drawText(wData.getWs(), WIDTH-100, 35, paint);
			canvas.drawText("m/s", WIDTH-60, 35, paint);
			
			canvas.drawText(wData.getWdKor(), WIDTH-100, 60, paint);			
			
			mainImage = getCurrWeatherImage(wData.getWfKor(), wData.getHour());
			canvas.drawBitmap(mainImage, (WIDTH-mainImage.getWidth())/2, (HEIGHT-SMALL_HEIGHT-mainImage.getHeight())/2, paint);
		
			Bitmap tempImage = null;
//			for (int i = 0; i < weatherInfo.getWeatherList().size(); i++) {
			for (int i = 0; i < 4; i++) {
				paint.setTextSize(25);
				canvas.drawText(weatherInfo.getWeatherList().get(i).getHour(), WIDTH/4*i+35, HEIGHT-SMALL_HEIGHT+30, paint);
				canvas.drawText("시", WIDTH/4*i+62, HEIGHT-SMALL_HEIGHT+30, paint);

				tempImage = getCurrWeatherImage(weatherInfo.getWeatherList().get(i).getWfKor(), weatherInfo.getWeatherList().get(i).getHour());
				int w = tempImage.getWidth();  
				int h = tempImage.getHeight();  
				Rect src = new Rect(0, 0, w, h);
				int nx = WIDTH/4*i+20;
				int ny = HEIGHT-120;
				Rect dst = new Rect(nx, ny , nx+w/5, ny + h/5);  
				canvas.drawBitmap(tempImage, src, dst, paint);
			
				paint.setTextSize(20);
				if(!"-999.0".equals(weatherInfo.getWeatherList().get(i).getTmn()) && !"-999.0".equals(weatherInfo.getWeatherList().get(i).getTmx())) {
					canvas.drawText(weatherInfo.getWeatherList().get(i).getTmn(), WIDTH/4*i+10, HEIGHT-20, paint);
					canvas.drawText("/", WIDTH/4*i+55, HEIGHT-20, paint);
					canvas.drawText(weatherInfo.getWeatherList().get(i).getTmx(), WIDTH/4*i+62, HEIGHT-20, paint);
					paint.setTextSize(12);
					canvas.drawText("o", WIDTH/4*i+47, HEIGHT-27, paint);
					canvas.drawText("o", WIDTH/4*i+110, HEIGHT-27, paint);
				}
				else {
				canvas.drawText("  -", WIDTH/4*i+20, HEIGHT-20, paint);
				canvas.drawText("/", WIDTH/4*i+55, HEIGHT-20, paint);
				canvas.drawText("  -", WIDTH/4*i+62, HEIGHT-20, paint);
				paint.setTextSize(12);
				canvas.drawText("o", WIDTH/4*i+47, HEIGHT-27, paint);
				canvas.drawText("o", WIDTH/4*i+100, HEIGHT-27, paint);
				}
			}
			
			paint.setTextSize(33);
			paint.setTextAlign(Paint.Align.CENTER);
			//canvas.drawText(weatherInfo.getAddress(), 30, HEIGHT-SMALL_HEIGHT-120, paint);
			canvas.drawText(weatherInfo.getAddress(), getWidth()/2, HEIGHT-SMALL_HEIGHT-120, paint);
			
			paint.setTextSize(25);
			//canvas.drawText(wData.getWfKor(), WIDTH/2-50, HEIGHT-SMALL_HEIGHT-90, paint);
			canvas.drawText(wData.getWfKor(), getWidth()/2, HEIGHT-SMALL_HEIGHT-90, paint);
		}
		else {			
			String displayText1 = this.getContext().getString(R.string.alarm_weather_none1);
			String displayText2 = this.getContext().getString(R.string.alarm_weather_none2);
			
			paint.setStyle(Paint.Style.FILL);  
			paint.setStrokeWidth(1);  
			paint.setAntiAlias(true);
			paint.setColor(0xff808080);  
			paint.setTextSize(30);
			paint.setTextAlign(Paint.Align.CENTER);
			canvas.drawText(displayText1, getWidth()/2, getHeight()/2-100, paint);
			canvas.drawText(displayText2, getWidth()/2, getHeight()/2-50, paint);
		}
	}
	
	private void drawBackGround(Canvas canvas) {
		
		if(gameRect != null ) {
			
			Paint paint = new Paint();
//			paint.setColor(color.background_light);
//			paint.setColor(0xffffffff);
			paint.setColor(0x80808080);
			
			canvas.drawLine(0, HEIGHT-SMALL_HEIGHT, WIDTH, HEIGHT-SMALL_HEIGHT, paint);
			
			canvas.drawLine(0, 0, WIDTH, 0, paint);
			canvas.drawLine(0, 0, 0, HEIGHT-SMALL_HEIGHT, paint);
			canvas.drawLine(WIDTH-1, 0, WIDTH-1, HEIGHT-SMALL_HEIGHT, paint);
			
			for (int i = 0; i < 5; i++) {
				if(i==4) {
					canvas.drawLine(WIDTH/4*i-1, HEIGHT-SMALL_HEIGHT, WIDTH/4*i-1, HEIGHT, paint);
				}
				else {
					canvas.drawLine(WIDTH/4*i, HEIGHT-SMALL_HEIGHT, WIDTH/4*i, HEIGHT, paint);	
				}
			}
		}
		
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
//		Log.d(TAG, "onSizeChanged ### w:"+w+" , h:"+h);
		gameRect = new Rect(0,0,w,h);
		WIDTH = w;
		HEIGHT = h;
	}
}
