/*
 * @(#)WeatherData.java 1.0 2010. 10. 29.
 */
package com.humanoid.alarmplus.weather;

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
public class WeatherData {

	/*
		  <hour>21</hour> 
		  <day>0</day> 
		  <temp>12.0</temp> 
		  <tmx>-999.0</tmx> 
		  <tmn>-999.0</tmn> 
		  <sky>1</sky> 
		  <pty>0</pty> 
		  <wfKor>맑음</wfKor> 
		  <wfEn>Clear</wfEn> 
		  <pop>0</pop> 
		  <r12>0.0</r12> 
		  <s12>0.0</s12> 
		  <ws>1.6</ws> 
		  <wd>1</wd> 
		  <wdKor>북동</wdKor> 
		  <wdEn>NE</wdEn> 
		  <reh>40</reh> 
	 */
	
	private String hour;
	private String day;
	private String temp;
	private String tmx;
	private String tmn;
	private String sky;
	private String pty;
	private String wfKor;
	private String wfEn;
	private String pop;
	private String r12;
	private String s12;
	private String ws;
	private String wdKor;
	private String wdEn;
	private String reh;
	
	public String getHour() {
		return hour;
	}
	public void setHour(String hour) {
		this.hour = hour;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getTemp() {
		return temp;
	}
	public void setTemp(String temp) {
		this.temp = temp;
	}
	public String getTmx() {
		return tmx;
	}
	public void setTmx(String tmx) {
		this.tmx = tmx;
	}
	public String getTmn() {
		return tmn;
	}
	public void setTmn(String tmn) {
		this.tmn = tmn;
	}
	public String getSky() {
		return sky;
	}
	public void setSky(String sky) {
		this.sky = sky;
	}
	public String getPty() {
		return pty;
	}
	public void setPty(String pty) {
		this.pty = pty;
	}
	public String getWfKor() {
		return wfKor;
	}
	public void setWfKor(String wfKor) {
		this.wfKor = wfKor;
	}
	public String getWfEn() {
		return wfEn;
	}
	public void setWfEn(String wfEn) {
		this.wfEn = wfEn;
	}
	public String getPop() {
		return pop;
	}
	public void setPop(String pop) {
		this.pop = pop;
	}
	public String getR12() {
		return r12;
	}
	public void setR12(String r12) {
		this.r12 = r12;
	}
	public String getS12() {
		return s12;
	}
	public void setS12(String s12) {
		this.s12 = s12;
	}
	public String getWs() {
		return ws;
	}
	public void setWs(String ws) {
		this.ws = ws;
	}
	public String getWdKor() {
		return wdKor;
	}
	public void setWdKor(String wdKor) {
		this.wdKor = wdKor;
	}
	public String getWdEn() {
		return wdEn;
	}
	public void setWdEn(String wdEn) {
		this.wdEn = wdEn;
	}
	public String getReh() {
		return reh;
	}
	public void setReh(String reh) {
		this.reh = reh;
	}
}
