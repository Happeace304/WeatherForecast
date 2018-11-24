package DoAn;

import java.net.*;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.teamdev.jxbrowser.chromium.JSArray;

import java.io.*;

public class Server {

	public static void main(String args[]) throws IOException {

		ServerSocket listener = null;

		System.out.println("Server is waiting to accept user...");
		int clientNumber = 0;

	
		try {
			listener = new ServerSocket(7777);
		} catch (IOException e) {
			System.out.println(e);
			System.exit(1);
		}

		try {
			while (true) {

			
				Socket socketOfServer = listener.accept();
				new ServiceThread(socketOfServer, clientNumber++).start();
			}
		} finally {
			listener.close();
		}

	}

	private static void log(String message) {
		System.out.println(message);
	}

	private static class ServiceThread extends Thread {

		private int clientNumber;
		private Socket socketOfServer;

		public ServiceThread(Socket socketOfServer, int clientNumber) {
			this.clientNumber = clientNumber;
			this.socketOfServer = socketOfServer;

			// Log
			log("New connection with client# " + this.clientNumber + " at " + socketOfServer);
		}

		@Override
		public void run() {

			try {

				
				BufferedReader is = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
				BufferedWriter os = new BufferedWriter(new OutputStreamWriter(socketOfServer.getOutputStream()));

				while (true) {
				
					String line = is.readLine();
					
					if (line != null && line.substring(0, 3).equals("lat")) {
						
						String lat;
						String log;
						String weather = null;
						String temp = null;
						String name= "";
						os.write(line);
						os.newLine();
						os.flush();

						lat = line.substring(4, line.indexOf("&"));
						log = line.substring(line.indexOf("&")+6, line.length());
						String key = "4aa2822308c5d4b1b3ab39b0bc0cb2b5";
						String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + log
								+ "&appid=" + key;

					
						JSONObject json = readJsonFromUrl(url);
						
						
					
						JSONArray js = json.getJSONArray("weather");						
						for (int i = 0; i < js.length(); i++) {
						    JSONObject jsonobject = js.getJSONObject(i);
						    weather = jsonobject.getString("main");
						}
					
						Double min =  Double.parseDouble(json.getJSONObject("main").get("temp_min").toString())-273  ;
						Double max =  Double.parseDouble(json.getJSONObject("main").get("temp_max").toString())-273  ;
						String minTemp = ShortNum(min);
						String maxTemp = ShortNum(max);
					
						temp =minTemp + "*C ~ " + maxTemp + "*C";
						os.write(url);
						os.newLine();
						os.flush();
		
						name = json.get("name").toString().isEmpty()?"Unidentified":json.get("name").toString();
				
						os.write("Name:" + name);
						os.newLine();
						os.flush();
						os.write("Weather:"+ weather);
						os.newLine();
						os.flush();
						os.write("Temp:"+ temp);
						os.newLine();
						os.flush();
					}

//					QUIT
					if (line != null && line.equals("QUIT")) {
						os.write(">> QUIT");
						os.newLine();
						os.flush();
						break;
					}

				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} 
		}
	}
	private static String ShortNum(Double db){
		DecimalFormat numberFormat = new DecimalFormat("#.00");
		return numberFormat.format(db);
	}
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		}
		finally {
			is.close();
		}
	}
}