/*
 * @(#)HttpClient.java 1.0 2010. 6. 20.
 */
package com.humanoid.alarmplus;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

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
 * @author  천성민 2010. 6. 20.
 * @version  v1.0.0 
 * @see 
 * @since JDK v1.4
 */
public enum HttpClient {

	INSTANCE;//singleton object


	/** http request type */
	public static final int GET_TYPE = 1;
	public static final int POST_TYPE = 2;

	/** http result type */
	public static final int STRING_TYPE = 1;
	public static final int BINARY_TYPE = 2;
	public static final int STREAM_TYPE = 3;
	public static final int FILE_TYPE = 4;

	private static final String PROTO = "http";

	private static final int IO_BUFFER_SIZE = 1000 * 1024;

	private ArrayList <NameValuePair> params;
	private ArrayList <NameValuePair> headers;

//	private String url;
	private URI uri;

	private int responseCode;
	private String message;
	
	private String writeFilePath;

	//	private String response;


	public String getWriteFilePath() {
		return writeFilePath;
	}


	public void setWriteFilePath(String writeFilePath) {
		this.writeFilePath = writeFilePath;
	}


	public static HttpClient getInstance() {
		return INSTANCE;
	}


	public void AddParam(String name, String value) {
		params.add(new BasicNameValuePair(name, value));
	}

	public void RemoveParam(String name, String value) {
		params.remove(new BasicNameValuePair(name, value));
	}

	public void AddHeader(String name, String value) {
		headers.add(new BasicNameValuePair(name, value));
	}

	/**
	 * 
	 * <pre>
	 * 1.기능 : . <br>
	 * 2.처리 개요
	 *     - 
	 *     - 
	 * 3.주의사항 
	 *     -
	 *</pre>
	 * @param requestType
	 * @param resultType
	 * @return
	 * @throws Exception
	 */
	public Object httpRequest(int requestType , int resultType) throws Exception {

		HttpUriRequest request = null;

		switch(requestType) {
			case GET_TYPE:
			{
				//add parameters
				//			String combinedParams = "";
				//			if(!params.isEmpty()) {
				//				combinedParams += "?";
				//				for(NameValuePair p : params) {
				//					String paramString = p.getName() + "=" + p.getValue();
				//					if(combinedParams.length() > 1) {
				//						combinedParams  +=  "&" + paramString;
				//					}
				//					else {
				//						combinedParams += paramString;
				//					}
				//				}
				//			}
	
				//			request = new HttpGet(url + combinedParams);
				request = new HttpGet();
				((HttpGet)request).setURI(uri);
	
				//add headers
				for(NameValuePair h : headers) {
					request.addHeader(h.getName(), h.getValue());
				}
	
				break;
			}
			case POST_TYPE:
			{
				request = new HttpPost(uri);
	
				//add headers
				for(NameValuePair h : headers) {
					request.addHeader(h.getName(), h.getValue());
				}
	
				if(!params.isEmpty()){
					((HttpPost)request).setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				}
	
				break;
			}
		}

		return executeRequest(request , resultType);
	}



	/**
	 * 
	 * <pre>
	 * 1.기능 : . <br>
	 * 2.처리 개요
	 *     - 
	 *     - 
	 * 3.주의사항 
	 *     -
	 *</pre>
	 * @param request
	 * @param resultType
	 * @return
	 */
	private Object executeRequest(HttpUriRequest request , int resultType) {

		DefaultHttpClient client = new DefaultHttpClient();


		Object result = null;
		try {

			if(STRING_TYPE == resultType) {
				ResponseHandler<String> h = new BasicResponseHandler();
				
				Log.d(WeatherView.TAG, "################## STRING_TYPE");
				result = client.execute(request, h);
			}
			else if (BINARY_TYPE == resultType) {
				HttpResponse httpResponse;

				httpResponse = client.execute(request);
				responseCode = httpResponse.getStatusLine().getStatusCode();
				//				message = httpResponse.getStatusLine().getReasonPhrase();

				HttpEntity entity = httpResponse.getEntity();

				if (entity != null) {
					Log.d(WeatherView.TAG, "################## entity.getContentLength():"+entity.getContentLength());
					InputStream instream = entity.getContent();
					result = convertStreamToBytes(instream);
					instream.close();
				}
			}
			else if (STREAM_TYPE == resultType) {
				HttpResponse httpResponse;

				httpResponse = client.execute(request);
				responseCode = httpResponse.getStatusLine().getStatusCode();
				//				message = httpResponse.getStatusLine().getReasonPhrase();

				HttpEntity entity = httpResponse.getEntity();

				if (entity != null) {
					Log.d(WeatherView.TAG, "################## entity.getContentLength():"+entity.getContentLength());
					InputStream instream = entity.getContent();
					result = instream;
				}
			}
			else if (FILE_TYPE == resultType) {
				HttpResponse httpResponse;

				httpResponse = client.execute(request);
				responseCode = httpResponse.getStatusLine().getStatusCode();
				//				message = httpResponse.getStatusLine().getReasonPhrase();

				HttpEntity entity = httpResponse.getEntity();

				if (entity != null) {
					Log.d(WeatherView.TAG, "################## entity.getContentLength():"+entity.getContentLength());
					InputStream instream = entity.getContent();
					result = convertStreamToFile(instream);
					instream.close();
				}
			}
			else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}


	private byte[] convertStreamToBytes(InputStream in) {
		Log.d(WeatherView.TAG, "convertStreamToFile is:"+in);

		BufferedOutputStream out = null;
		ByteArrayOutputStream bo = null;
		try {
			bo = new ByteArrayOutputStream();
			out = new BufferedOutputStream(bo, IO_BUFFER_SIZE);
			out = new BufferedOutputStream(bo);
			copy(in, out);
			out.flush();
		} catch (Exception e) {
			Log.d(WeatherView.TAG, "convertStreamToFile error:"+e);
			e.printStackTrace();
		} finally {
			try {
				bo.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return bo.toByteArray();
	}

	private boolean convertStreamToFile(InputStream in) {
		Log.d(WeatherView.TAG, "convertStreamToFile is:"+in);

		FileOutputStream fo = null;
		BufferedOutputStream out = null;

		try {
//			fo = new FileOutputStream(new File("/sdcard/"+UtilDate.getCurrentDate()+".zip"));
			if(writeFilePath == null) {
				return false;
			}
			
			fo = new FileOutputStream(new File(writeFilePath));
			out = new BufferedOutputStream(fo);
			copy(in, out);
			out.flush();
		} catch (Exception e) {
			Log.d(WeatherView.TAG, "convertStreamToFile error:"+e);
			e.printStackTrace();
			
			return false;
			
		} finally {
			try {
				fo.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	private void copy(InputStream in, OutputStream out) throws IOException {
		byte[] b = new byte[IO_BUFFER_SIZE];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
//			Log.d(WeatherView.TAG, "read:"+read);
		}
	}


	public String getErrorMessage() {
		return message;
	}

	public int getResponseCode() {
		return responseCode;
	}

//	public String getUrl() {
//		return url;
//	}


	public void init() {
		params = new ArrayList<NameValuePair>();
		headers = new ArrayList<NameValuePair>();
	}

//	public void setUrl(String url) {
//		this.url = url;
//	}

	public void setUri(String host , String path) {

		try {
			uri = URIUtils.createURI(PROTO, host, -1, path, URLEncodedUtils
					.format(params, "UTF-8"), null);

			Log.i(WeatherView.TAG, "################# URI:"+uri.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setUri(String uri) {
		try {
			this.uri = URI.create(uri);
			Log.i(WeatherView.TAG, "################# URI:"+uri.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

//		HttpClient.getInstance().setUrl("http://smart.t-plex.net/news/zip/100621.zip");
		//		HttpClient.getInstance().AddParam("accountType", "GOOGLE");
		//		HttpClient.getInstance().AddParam("source", "tboda-widgalytics-0.1");
		//		HttpClient.getInstance().AddParam("Email", "min8839@gmail.com");
		//		HttpClient.getInstance().AddParam("Passwd", "1q2w3e4r");
		//		HttpClient.getInstance().AddParam("service", "analytics");
		//		HttpClient.getInstance().AddHeader("GData-Version", "2");

		try {
			HttpClient.getInstance().httpRequest(HttpClient.GET_TYPE , HttpClient.STRING_TYPE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//		String response = HttpClient.getInstance().getResponse();
		//		Log.d(WeatherView.TAG, "######### response:"+response);
	}
	
	public InputStream getHttpGETInputStream(String urlStr)
	throws Exception {
		InputStream is = null;
		HttpURLConnection conn = null;

//		if (urlStr.startsWith("content://"))
//			return getContentInputStream(urlStr, null);

		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(10000);

			is = conn.getInputStream();

			return is;
		} catch (Exception ex) {
			try {
				is.close();
			} catch (Exception ignore) {
			}
			try {
				conn.disconnect();
			} catch (Exception ignore) {
			}

			throw ex;
		}
	}

	public String getHttpInputString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is),
				8 * 1024);
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}
