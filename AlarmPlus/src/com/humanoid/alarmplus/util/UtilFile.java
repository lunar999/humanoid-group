package com.humanoid.alarmplus.util;
/*
 * @(#)UtilFile.java 1.0 2010. 6. 17.
 */

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.humanoid.alarmplus.AlarmConstantIf;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;


/**
 * 
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
public class UtilFile {

	static final String TAG = UtilFile.class.getSimpleName();
	static final int COMPRESSION_LEVEL = 8;
	static final int BUFFER_SIZE = 64*1024;
	static final char FS = File.separatorChar;

	public static void unZip(String unZipFilePath , String foutputPath) {

		/*
		 * 실행 시스템의 File Seperator를 확인하여 실행 시스템의 File Seperator로 변환한다.
		 */
		Log.d(TAG,"Current System File Seperator is : >> " + FS);
		unZipFilePath.replace('\\', FS);
		unZipFilePath.replace('/', FS);

		//		String foutputPath = unZipFilePath.substring(0, unZipFilePath.lastIndexOf(".ZIP"));
		long beginTime = System.currentTimeMillis();
		byte[] buffer = new byte[BUFFER_SIZE];

		File file = new File(unZipFilePath);

		try {

			FileInputStream finput = new FileInputStream(file);
			ZipInputStream zinput = new ZipInputStream((InputStream)finput);
			ZipEntry entry;

			/*
			 * Zip 파일 타입이 아니라면 실행을 끝낸다.
			 */
			if ((unZipFilePath.toUpperCase()).indexOf(".ZIP") == -1) {
				return;
			}

			/*
			 * 압축을 풀 디렉토리를 생성한다. 디렉토리 명은 압축 파일명으로 한다.
			 */
			File zipDir = new File(foutputPath);
			if (!zipDir.exists()) {
				zipDir.mkdirs();
			}

			while ( (entry = zinput.getNextEntry()) != null) {

				String outputFileNm = entry.getName().replace('/', File.separatorChar);
				Log.d(TAG,"2.outputFileNm : " + outputFileNm);

				File entryFile = new File(foutputPath + FS + outputFileNm);

				if(entry.isDirectory()) {

					if (!entryFile.exists()) {
						entryFile.mkdirs();
					}
					continue;
				}

				Log.d(TAG,"3.Entry File : " + entryFile.toString());

				FileOutputStream foutput = new FileOutputStream(entryFile);
				Log.d(TAG,"4.FileOutputStream : " + foutput.toString());

				Log.d(TAG,"Uncompress the Zip File........");

				int cnt;

				while ((cnt = zinput.read(buffer)) != -1) {
					foutput.write(buffer, 0, cnt);
				}

				Log.d(TAG,"7. Final Output File : " + foutput.toString());

				foutput.flush();
				if (foutput != null) {
					foutput.close();
				}
			}
		} catch (Exception e) {
			Log.d(TAG,"Zip.unZip().... Error");
			e.printStackTrace();
		}
		long msec = System.currentTimeMillis() - beginTime;

		Log.d(TAG,"Unzipe Time Check :: >> " + msec/1000 + "."	+ (msec % 1000) + " sec. elapsed...");
	}


	public static void delete(String filename) {
		File f = new File(filename);
		f.delete();
	}


	public static boolean isExistFile(String filename) {
		File f = new File(filename);
		boolean isExists = f.exists();

		return isExists;
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
	 * @param f
	 */
	public static boolean mkDir(File f) {
		if (!f.exists()) {
			return f.mkdirs();
		}

		return true;
	}

	/**
	 * 
	 * <pre>
	 * 1.기능 : 파일명에 데이터를 기록한다. <br>
	 * 2.처리 개요
	 *     - 
	 *     - 
	 * 3.주의사항 
	 *     -
	 *</pre>
	 * @param f
	 * @param data
	 * @throws Exception
	 */
	public static void write(File f,byte[] data) throws Exception{

		BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(f));
		output.write(data);
		output.close();
		output = null;
	}


	/**
	 * 
	 * <pre>
	 * 1.기능 : 파일명에 데이터를 기록한다. <br>
	 * 2.처리 개요
	 *     - 
	 *     - 
	 * 3.주의사항 
	 *     -
	 *</pre>
	 * @param filename
	 * @param data
	 * @throws Exception
	 */
	public static void write(String filename,byte[] data , boolean append) throws Exception{
		File f = new File(filename);

		BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(f,append));
		output.write(data);
		output.close();
		output = null;
	}

	/**
	 * <pre>
	 * 1.기능 : 특정 파일을 읽어 그 내용을 반환하는 메소드. <BR>
	 * 2.처리 개요
	 *     - 특정 파일을 읽어 그 내용을 반환한다.
	 *     - 
	 * 3.주의사항 
	 *     - null 이면 파일이 없는 경우.
	 *     - 배열 길이가 0 이면 파일은 있으나 그 내용을 읽지 못 한 경우임.
	 *</pre>
	 * @param fileName 읽을 파일명
	 * @return byte[] 파일 내용
	 */
	public static byte[] readFile(String fileName) {
		byte[] contents = null;
		FileInputStream fis = null;

		try {
			File f = new File(fileName);
			fis = new FileInputStream(f);
			contents = new byte[(int)f.length()];
			fis.read( contents , 0 , contents.length );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			contents = null;
		} catch (IOException e) {
			contents = new byte[0];
		} finally {
			try {
				if(fis!=null) 
					fis.close();
				fis = null;
			} catch (Exception n) {
			}
		}

		return contents;
	}


	/**
	 * 
	 * <pre>
	 * 1.기능 :  특정 파일을 라인단위로 읽어서 ArrayList로 반환하는 메소드. <br>
	 * 2.처리 개요
	 *     - 
	 *     - 
	 * 3.주의사항 
	 *     -
	 *</pre>
	 * @param fileName
	 * @return
	 */
	public static ArrayList<String> readFileLine(String fileName) {


		BufferedReader buffer = null; 
		File f = null;

		ArrayList<String> lineList = new ArrayList<String>();

		try
		{
			f = new File(fileName);
			if (f == null)
				return null;
			buffer = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			String line;
			while(true)
			{
				line = buffer.readLine();
				if(line == null)
					break;

				if(line.trim().length()<= 0)
					continue;

				if(line.substring(0, 2).equals("//"))
					continue;

				lineList.add(line);
				//				
			}
		} catch(FileNotFoundException fe) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally 
		{
			if (buffer != null) 
			{
				try 
				{
					buffer.close();
				} 
				catch (Exception e) {}
				buffer = null;
			}
		}

		return lineList;

	}

	public static byte[] readAssetsFile(String fileName , Context context , int fileSize) {
		byte[] contents = null;
		//		FileInputStream fis = null;
		InputStream is = null;

		try {
			//			File f = new File(fileName);
			is = context.getAssets().open(fileName);
			//			fis = new FileInputStream(f);
			contents = new byte[fileSize];
			is.read( contents , 0 , contents.length );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			contents = null;
		} catch (IOException e) {
			contents = new byte[0];
		} finally {
			try {
				if(is!=null) 
					is.close();
				is = null;
			} catch (Exception n) {
			}
		}

		return contents;
	}

	/**
	 * 
	 * <pre>
	 * 1.기능 :  특정 파일을 라인단위로 읽어서 ArrayList로 반환하는 메소드. <br>
	 * 2.처리 개요
	 *     - 
	 *     - 
	 * 3.주의사항 
	 *     -
	 *</pre>
	 * @param fileName
	 * @return
	 */
	public static ArrayList<String> readAssetsFileLine(String fileName , Context context) {

		InputStream is = null;
		BufferedReader buffer = null; 
		//		File f = null;

		ArrayList<String> lineList = new ArrayList<String>();

		try
		{
			is = context.getAssets().open(fileName);

			//			f = new File(fileName);
			if (is == null)
				return null;
			buffer = new BufferedReader(new InputStreamReader(is));
			String line;
			while(true)
			{
				line = buffer.readLine();
				if(line == null)
					break;

				if(line.trim().length()<= 0)
					continue;

				if(line.substring(0, 2).equals("//"))
					continue;

				lineList.add(line);
				//				
			}
		} catch(FileNotFoundException fe) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		finally 
		{
			if (buffer != null) 
			{
				try 
				{
					buffer.close();
				} 
				catch (Exception e) {}
				buffer = null;
			}
		}

		return lineList;

	}


	//	private static final String ALARM_REC_POSTFIX = ".mp4";
	//	private static final String SD2 = "sd";
	//	private static final String ALARM_REC_PREFIX = "/alarm_rec_";
	//	private static final String SAMSUNG_GALAXYS = "SHW-M110S";
	public static String recpath;  // 10.11.22 update redmars

	public static String getSdCardAlarmPath(String fileName) {

		File recFile = Environment.getExternalStorageDirectory();
		String path = "";
		recpath = fileName;        // 10.11.22 update redmars fime name 공유
		path = recFile.getAbsolutePath() + AlarmConstantIf.ALARM_FILE_PATH + File.separator + fileName;
		File tempFile = new File(recFile.getAbsolutePath() + AlarmConstantIf.ALARM_FILE_PATH);
		if(!tempFile.exists()) {
			tempFile.mkdirs();
		}

		//		/sdcard 하위폴더로 교체
		//		if(SAMSUNG_GALAXYS.equals(Build.MODEL)){
		//			File sd2 = new File(recFile.getAbsolutePath()+ File.separator + SD2 );
		//			if(sd2.exists()){
		//				path = recFile.getAbsolutePath() + File.separator + SD2  + AlarmConstantIf.ALARM_FILE_PATH + File.separator + fileName;
		//				tempFile = new File(recFile.getAbsolutePath() + File.separator + SD2  + AlarmConstantIf.ALARM_FILE_PATH);
		//				if(!tempFile.exists()) {
		//					tempFile.mkdirs();
		//				}
		//			} else {
		//				path = recFile.getAbsolutePath() + AlarmConstantIf.ALARM_FILE_PATH + File.separator + fileName;
		//			
		//				tempFile = new File(recFile.getAbsolutePath() + AlarmConstantIf.ALARM_FILE_PATH);
		//				if(!tempFile.exists()) {
		//					tempFile.mkdirs();
		//				}
		//			}
		//		}

		return path;
	}
	
	/**
	 * 저장 디렉토리 만들기
	 */
	public static void makeAlarmDir(){
		String state = android.os.Environment.getExternalStorageState();
		if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  {

			File recFile = Environment.getExternalStorageDirectory();

			File tempFile = new File(recFile.getAbsolutePath() + AlarmConstantIf.ALARM_FILE_PATH);
			if(!tempFile.exists()) {
				tempFile.mkdirs();
			}
		}
	}


	//	public static void main(String[] args) {
	//
	//		String unZipFilePath = "C:\\100617.zip";
	//		unZip(unZipFilePath , "C:\\daily\\");
	//	}


}
