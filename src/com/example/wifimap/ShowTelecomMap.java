package com.example.wifimap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowTelecomMap extends FragmentActivity implements
		LocationListener {
	// ������ ���� ������
	WifiInfo info;
	WifiManager mng;
	WifiInfo wifi;
	String Ip, apTelecom, TelecomIP, cnt, mac, radius;
	int count = 0;
	InputStream is = null;
	String[][] RealrowArr;

	// ���� �������� Wifi�� ����
	String curTime, curUserTelecom, curSsid, curMac, curDeviceName, c_channel,
			curApTelecom, curIp = null;
	double curLat, curLon = 0;
	int curLength = 0;
	boolean curIsOpen = false;

	// ������ ���� ������
	GoogleMap mGoogleMap; // ������ ���� ����
	Geocoder geoCoder; // �ּ�(string)�� �����浵�� �ٲپ��ִ� ����
	protected LocationManager locationManager;
	String userTelecom, ssid, signalLength, longitude, latitude;
	Marker curMarker = null; // ���� ��ġ ��Ŀ
	boolean isGetLocation = false; // GPS �� wife ������ �����ִ��� Ȯ��, GPS ���°�
	String inputPasswd;

	// DB�� ����� Wifi ���3
	ArrayList<ViewWifiInfo> listItem = new ArrayList<ViewWifiInfo>();

	// ���� ��ġ �ֺ��� Wifi ���
	ArrayList<ViewWifiInfo> currentWifi = new ArrayList<ViewWifiInfo>();
	ArrayList<Marker> currentApMarker = new ArrayList<Marker>(); // ap��ġ ��Ŀ
	ArrayList<Circle> currentApCircle = new ArrayList<Circle>(); // ��...

	// ���󺯼�
	int sktColor = Color.argb(204, 255, 87, 26);
	int ktColor = Color.argb(204, 223, 32, 40);
	int lgColor = Color.argb(204, 236, 6, 141);
	int noneColor = Color.argb(204, 194, 207, 249);

	Handler handler = new Handler();

	// DB���� ����
	DBHelper myHelper;
	EditText edtName, edtNameResult;
	SQLiteDatabase sqlDB;

	public boolean mDatainput = false;

	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_telecom_map);

		// �ȵ���̵� ���� 3.0 �̻󿡼� UI Thread���� ���ͳ� ����� runtime ���� �� ���� �ϴ� ��
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// < ������ �ʱ�ȭ >
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mGoogleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap(); // �� �ʱ�ȭ
		geoCoder = new Geocoder(this); // geoCoder �ʱ�ȭ
		mng = (WifiManager) getSystemService(WIFI_SERVICE);
		info = mng.getConnectionInfo();

		Button sktBtn = (Button) findViewById(R.id.sktButton);
		Button ktBtn = (Button) findViewById(R.id.ktButton);
		Button lgBtn = (Button) findViewById(R.id.lgButton);
		Button noneBtn = (Button) findViewById(R.id.noneButton);
		Button returnCureBtn = (Button) findViewById(R.id.returnCurButton);
		currentLocation(); // ���� ��ġ ������ִ� �Լ� ȣ��

		myHelper = new DBHelper(this);

		// ���� ��ġ�� ���ư��� ��ư
		returnCureBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.returnCurButton)
					LocationZoom(new LatLng(curLat, curLon));
			}
		});

		// ī�޶� �̵� Ȯ�� ��Ұ� ������ �̺�Ʈ �߻�
		mGoogleMap.setOnCameraChangeListener(new OnCameraChangeListener() {
			public void onCameraChange(CameraPosition position) {
				// LatLng point = position.target;
				float zoomLevel = position.zoom;

				if (!currentApCircle.isEmpty()) {
					if (zoomLevel < 13.0) // ���� �Ⱥ��̰� �ؾ��ϴ� ���
						for (int i = 0; i < currentApCircle.size(); i++)
							// currentApCircle.get(i).setVisible(false);
							setCircleVisible(currentApCircle.get(i), false);

					else
						// ���� ���̰� �ؾ��ϴ� ���
						for (int i = 0; i < currentApCircle.size(); i++)
							// currentApCircle.get(i).setVisible(true);

							setCircleVisible(currentApCircle.get(i), true);
				}
			}
		});

		// ��Ŀ Ŭ���� �ش� Wifi�� ���� ���
		mGoogleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				if (!marker.equals(curMarker))
					marker.setTitle(marker.getTitle());
				return false;
			}

		});

		// ��Ż� ��ư ������
		View.OnClickListener signalBtnListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Button btn = (Button) findViewById(v.getId());
				int color = 0;

				// ��ư�� ���õ� �������� �ƴ��� �Ǻ��� ����
				if (btn.isSelected())
					btn.setSelected(false);
				else
					btn.setSelected(true);

				// ��ư�� ���� ���� ����
				if (v.getId() == R.id.sktButton)
					color = sktColor;
				else if (v.getId() == R.id.ktButton)
					color = ktColor;
				else if (v.getId() == R.id.lgButton)
					color = lgColor;
				else if (v.getId() == R.id.noneButton)
					color = noneColor;

				// ���� ���� ��ġ�ϴ� ��� �Ⱥ��̰� ��
				if (color != 0) {
					for (int i = 0; i < currentApCircle.size(); i++) {
						if (currentApCircle.get(i).getFillColor() == color)
							if (currentApCircle.get(i).isVisible()) {
								currentApCircle.get(i).setVisible(false);
								currentApMarker.get(i).setVisible(false);
							} else {
								currentApCircle.get(i).setVisible(true);
								currentApMarker.get(i).setVisible(true);
							}
					}
				}
			}
		};
		sktBtn.setOnClickListener(signalBtnListener);
		ktBtn.setOnClickListener(signalBtnListener);
		lgBtn.setOnClickListener(signalBtnListener);
		noneBtn.setOnClickListener(signalBtnListener);
	}

	// ���� ��ġ �˾Ƴ���
	public void currentLocation() {

		Location location = null;
		boolean isGPSEnabled = false; // ���� GPS �������
		boolean isNetworkEnabled = false;// ��Ʈ��ũ �������

		// �ּ� GPS ���� ������Ʈ �Ÿ� 10����
		long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
		// �ּ� GPS ���� ������Ʈ �ð� �и������̹Ƿ� 1��
		long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

		try {
			// GPS ���� ��������
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// ���� ��Ʈ��ũ ���� �� �˾ƿ���
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// GPS �� ��Ʈ��ũ����� �������� ������ �ҽ� ����
				Toast.makeText(ShowTelecomMap.this, "���� ��ġ�� �˼��� �����ϴ�.",
						Toast.LENGTH_LONG).show();
				isGetLocation = false;
			} else {
				isGetLocation = true;

				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) { // ���� �浵 ����
							curLat = location.getLatitude();
							curLon = location.getLongitude();

							findCloseWifi(currentWifi, curLat, curLon);
							setApMarker(currentWifi, currentApMarker,
									currentApCircle);
						}
					}
				}

				else if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								curLat = location.getLatitude();
								curLon = location.getLongitude();

								findCloseWifi(currentWifi, curLat, curLon);
								setApMarker(currentWifi, currentApMarker,
										currentApCircle);
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		setCurrentMarker(curLat, curLon);
	}

	public void stopUsingGPS() { // GPS ����
		if (locationManager != null) {
			locationManager.removeUpdates(ShowTelecomMap.this);
		}
	}

	public void showSettingsAlert() { // GPS ������ �������� �������� ���������� ���� ����� alert â
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

		alertDialog.setTitle("GPS �����������");

		alertDialog.show();
	}

	// AP��Ŀ�� ���� ���� ��½�ų ������ �Ǻ��ϴ� �Լ�
	public void setCircleVisible(Circle setC, boolean mapBool) {
		Button sktBtn = (Button) findViewById(R.id.sktButton);
		Button ktBtn = (Button) findViewById(R.id.ktButton);
		Button lgBtn = (Button) findViewById(R.id.lgButton);
		Button noneBtn = (Button) findViewById(R.id.noneButton);

		boolean bool = true;

		// �� ���� ���� ��ư�� ���ȴ��� �Ǻ�
		if (setC.getFillColor() == sktColor)
			if (sktBtn.isSelected())
				bool = false;
			else
				bool = true;
		else if (setC.getFillColor() == ktColor)
			if (ktBtn.isSelected())
				bool = false;
			else
				bool = true;
		else if (setC.getFillColor() == lgColor)
			if (lgBtn.isSelected())
				bool = false;
			else
				bool = true;
		else if (setC.getFillColor() == noneColor)
			if (noneBtn.isSelected())
				bool = false;
			else
				bool = true;

		// ��ư�� ���õǾ� ���� �ʰ�, ���������� ���� ���̴� ������ ��쿡�� ���
		if (bool == true && mapBool == true)
			setC.setVisible(true);
		else
			setC.setVisible(false);
	}

	public void findCloseWifi(ArrayList<ViewWifiInfo> vw, double lat, double lon)
			throws URISyntaxException, ClientProtocolException, IOException {

		String passwd = null, isOpen = null;
		// ���� ��ġ �ֺ��� WIFI ����� �޾ƿ� ������ ǥ��!

		// ������ ���� ������ �޾ƿ���
		StringBuilder jsonHtml = new StringBuilder();

		URL url = new URL("http://113.198.84.38/dbTOandroid.php?latitude="
				+ lat + "&longitude=" + lon);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		if (conn != null) {
			conn.setConnectTimeout(10000);
			conn.setUseCaches(false);
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						conn.getInputStream(), "EUC-KR"));

				for (;;) {

					String line = br.readLine();

					if (line == null)
						break;
					jsonHtml.append(line + "\n");
				}
				br.close();
			}
			conn.disconnect();
		}

		String Json = jsonHtml.toString();

		String[] rowArr = Json.split("\n");

		if (!vw.isEmpty())
			vw.clear();

		for (int q = 0; q < rowArr.length; q++) {
			int testCnt = 0;
			StringTokenizer stk = new StringTokenizer(rowArr[q], "\"", false);
			while (stk.hasMoreTokens()) {
				String rowStr = stk.nextToken();

				if (testCnt == 1)
					ssid = rowStr;

				else if (testCnt == 3)
					isOpen = rowStr;

				else if (testCnt == 5)
					apTelecom = rowStr;

				else if (testCnt == 7)
					mac = rowStr;

				else if (testCnt == 9)
					latitude = rowStr;

				else if (testCnt == 11)
					longitude = rowStr;

				else if (testCnt == 13)
					signalLength = rowStr;

				else if (testCnt == 15)
					radius = rowStr;

				else if (testCnt == 17)
					cnt = rowStr;

				testCnt++;
			}
			// ������ġ ������ AP�� �޾ƿö� ���� null�� ��츦 ó��
			if (ssid != null && signalLength != null) {
				vw.add(new ViewWifiInfo(ssid, isOpen, apTelecom, mac, latitude,
						longitude, signalLength, radius, cnt));
			}

		}
	}

	public void LocationZoom(LatLng loc) { // ���Լ�
		CameraPosition cp = new CameraPosition.Builder().target((loc)).zoom(16)
				.build(); // ī�޶� �������� �ش� ���� �浵�� ����
		// �� ��ġ�� ī�޶� ���������� ����
		mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
	}

	public void setCurrentMarker(double lat, double lon) { // ���� ��Ŀ ����
		LatLng loc = new LatLng(lat, lon); // ��ġ ����
		LocationZoom(loc);
		MarkerOptions marker = new MarkerOptions().position(loc);

		marker.icon(BitmapDescriptorFactory
				.fromResource(R.drawable.img_marker_myposition));

		if (curMarker != null)
			curMarker.remove();

		curMarker = mGoogleMap.addMarker(marker);
	}

	public void setApMarker(ArrayList<ViewWifiInfo> vw, ArrayList<Marker> m,
			ArrayList<Circle> c) { // ap��Ŀ ����

		if (!m.isEmpty()) { // ������� �ʴٸ� ���� �ʱ�ȭ
			int size = m.size();
			for (int i = 0; i < size; i++) {
				m.get(i).remove();
				c.get(i).remove();
			}
		}

		for (int i = 0; i < vw.size(); i++) {
			LatLng loc = new LatLng(Double.valueOf(vw.get(i).getData(4)),
					Double.valueOf(vw.get(i).getData(5))); // ��ġ
			CircleOptions circleOptions = new CircleOptions().center(loc)
					.radius((Double.valueOf(vw.get(i).getData(7))))
					.strokeWidth(3).strokeColor(0);
			MarkerOptions marker = new MarkerOptions().position(loc); // ��Ŀ�� ����

			int color;
			int apMarker;
			Button signalBtn = null; // ��ư������ �޾ƿ������� ����

			// ��ȣ���⿡ ���� �� ���� ����
			if (vw.get(i).getData(2).equals("SKT")) {
				apMarker = R.drawable.img_marker_skt;
				color = sktColor;
				signalBtn = (Button) findViewById(R.id.sktButton);
			} else if (vw.get(i).getData(2).equals("KT")) {
				apMarker = R.drawable.img_marker_kt;
				color = ktColor;
				signalBtn = (Button) findViewById(R.id.ktButton);
			} else if (vw.get(i).getData(2).equals("LGU+")) {
				apMarker = R.drawable.img_marker_lg;
				color = lgColor;
				signalBtn = (Button) findViewById(R.id.lgButton);
			} else {
				apMarker = R.drawable.img_marker_none;
				color = noneColor;
				signalBtn = (Button) findViewById(R.id.noneButton);
			}

			circleOptions.fillColor(color).strokeColor(color);

			marker.icon(BitmapDescriptorFactory.fromResource(apMarker));

			marker.title(vw.get(i).getData(0));

			// ��ư�� �������ִٸ� ������ �ʰ� ����
			if (signalBtn.isSelected()) {
				marker.visible(false);
				circleOptions.visible(false);
			}

			if (mGoogleMap.getCameraPosition().zoom < 13.0)
				circleOptions.visible(false);

			c.add(mGoogleMap.addCircle(circleOptions));
			m.add(mGoogleMap.addMarker(marker));
		}
	}

	public String getLocalIpAddress() {// �� ��巹��
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public IBinder onBind(Intent arg0) {
		return null;
	}

	public void onLocationChanged(Location location) {

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	public void onProviderEnabled(String provider) {

	}

	public void onProviderDisabled(String provider) {

	}
}