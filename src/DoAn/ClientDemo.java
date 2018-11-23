package DoAn;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;

import java.net.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class ClientDemo extends JFrame {

	public String lat = "";
	public String log = "";
	JButton btn;
	static JLabel  place;
	static JLabel  weather;
	static JLabel  temp;
	Browser browser;
	BrowserView view;
	static Socket socketOfClient;
	static BufferedWriter os;
	static BufferedReader is;
	
	
	public void Giaodien() {

		browser = new Browser();
		view = new BrowserView(browser);

		JPanel main = new JPanel(new FlowLayout());
		JPanel sub = new JPanel(new GridLayout(20, 2));
		JPanel map = new JPanel(new BorderLayout());
		sub.setPreferredSize(new Dimension(150, 480));
		btn = new JButton("Nhap");
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				try {
					Double lati;
					Double longti;
					String url = ClientDemo.this.browser.getURL();

					ClientDemo.this.lat = url.substring(url.indexOf("lat") + 4, url.indexOf("lat") + 10);
					ClientDemo.this.log = url.substring(url.indexOf("lon") + 4, url.indexOf("lon") + 10);
					lati= Double.parseDouble(ClientDemo.this.lat);
					longti= Double.parseDouble(ClientDemo.this.log);
					
					
					String message = "lat=" + lati+ "&long=" + longti;
					os.write(message);
					os.newLine();
					os.flush();
				
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		});
		JLabel placelbl= new JLabel("Place:");
		place= new JLabel();
		JLabel weatherlbl= new JLabel("Weather:");
		weather = new JLabel();
		JLabel templbl= new JLabel("Temperature:");
		temp= new JLabel();

		
		sub.add(placelbl, BorderLayout.CENTER);
		sub.add(place, BorderLayout.CENTER);
		sub.add(weatherlbl, BorderLayout.CENTER);
		sub.add(weather, BorderLayout.CENTER);
		sub.add(templbl, BorderLayout.CENTER);
		sub.add(temp, BorderLayout.CENTER);
		sub.add(btn);
		map.add(view, BorderLayout.CENTER);

		main.add(map, BorderLayout.CENTER);

		JFrame frame = new JFrame();
		frame.add(sub, BorderLayout.EAST);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent e) {
				String message = "QUIT";
				try {
					os.write(message);
					os.newLine();
					os.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}
		});
		frame.add(map, BorderLayout.CENTER);
		frame.setSize(1024, 800);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		browser.loadURL(
				"https://openweathermap.org/weathermap?basemap=map&cities=false&layer=clouds&lat=16.01&lon=108.12&zoom=3");

	}

	public static void main(String[] args) {
		ClientDemo clientDemo = new ClientDemo();

		// Địa chỉ máy chủ.
		final String serverHost = "localhost";

		try {
			// Gửi yêu cầu kết nối tới Server đang lắng nghe
			// trên máy 'localhost' cổng 7777.

			socketOfClient = new Socket(serverHost, 7777);

			// Tạo luồng đầu ra tại client (Gửi dữ liệu tới server)
			os = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));

			// Luồng đầu vào tại Client (Nhận dữ liệu từ server).
			is = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));
			clientDemo.Giaodien();

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + serverHost);
			return;
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + serverHost);
			return;
		}

		// Đọc dữ liệu trả lời từ phía server
		// Bằng cách đọc luồng đầu vào của Socket tại Client.
		while (true) {
			try {

				String responseLine;
				while ((responseLine = is.readLine()) != null) {
					System.out.println("Server: " + responseLine);
					
					if(responseLine.substring(0,4).equals("Name") && responseLine.substring(6, responseLine.length()) != "") {
						place.setText(responseLine.substring(responseLine.indexOf(":")+1, responseLine.length()));
					}
					if(responseLine.substring(0,7).equals("Weather")) {
						weather.setText(responseLine.substring(responseLine.indexOf(":")+1, responseLine.length()));
					}
					
					if(responseLine.substring(0,4).equals("Temp")) {
//						System.out.println(responseLine.substring(0,11));
//						System.out.println(responseLine.substring(12,responseLine.length()));
						temp.setText(responseLine.substring(responseLine.indexOf(":")+1, responseLine.length()));
					}
				}

				os.close();
				is.close();
				socketOfClient.close();
			} catch (UnknownHostException e) {
				System.err.println("Trying to connect to unknown host: " + e);
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
			} 

		}
	}

}