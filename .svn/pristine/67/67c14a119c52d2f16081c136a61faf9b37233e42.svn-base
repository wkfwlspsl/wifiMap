package com.example.wifimap;

import android.app.TabActivity;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
	// slide menu
	private DisplayMetrics metrics;
	private LinearLayout slidingPanel;
	private LinearLayout leftMenuPanel;
	private FrameLayout.LayoutParams slidingPanelParameters;
	private FrameLayout.LayoutParams leftMenuPanelParameters;
	private int panelWidth;
	private static boolean isLeftExpanded;
	private Button leftBtn;

	// wifi_channel
	int strength, count = 0;
	WifiManager mng;
	WifiInfo info;
	protected LocationManager locationManager;
	boolean isGetLocation = false;
	Button channelBtn;
	TextView wifiName;
	int[] channel2_count = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	int[] channel5_count = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0 };
	int[] channel2 = { 2412, 2417, 2422, 2427, 2432, 2437, 2442, 2447, 2452,
			2457, 2462, 2467, 2472, 2484 };
	int[] channel5 = { 5180, 5200, 5220, 5240, 5260, 5280, 5300, 5320, 5745,
			5765, 5785, 5805, 5825, 5500, 5520, 5540, 5560, 5580, 5600, 5620,
			5640, 5660, 5680, 5700 };
	int mCurrentFrequency = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		// 안드로이드 버전 3.0 이상에서 UI Thread에서 인터넷 연결시 runtime 에러 안 나게 하는 법
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// Tab menu start
		TabHost tab_host = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		// first tab ======> ShowMap
		intent = new Intent().setClass(this, ShowWifiMap.class);
		spec = tab_host.newTabSpec("WIFI 지도");
		spec.setIndicator("WIFI 지도");
		spec.setContent(intent);
		tab_host.addTab(spec);

		// second tab =====> Statistics

		intent = new Intent().setClass(this, ShowTelecomMap.class);
		spec = tab_host.newTabSpec("통신사 지도");
		spec.setIndicator("통신사 지도");
		spec.setContent(intent);
		tab_host.addTab(spec);

		tab_host.setCurrentTab(0);
		// Tab menu end

		// 도움말 버튼
		ImageButton helpBtn = (ImageButton) findViewById(R.id.helpButton);
		helpBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						HelpActivity.class);
				startActivity(intent);
			}
		});

		// sliding menu
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		panelWidth = (int) ((metrics.widthPixels) * 0.6);

		leftBtn = (Button) findViewById(R.id.myWifiButton);
		leftBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.myWifiButton) {
					info = mng.getConnectionInfo();
					setWifiName();
					setSignalLength();
					setChannel();
					menuLeftSlideAnimationToggle();
				}
			}
		});

		// sliding view 설정
		slidingPanel = (LinearLayout) findViewById(R.id.slidingPanel);
		slidingPanelParameters = (FrameLayout.LayoutParams) slidingPanel
				.getLayoutParams();
		slidingPanelParameters.width = metrics.widthPixels;
		slidingPanel.setLayoutParams(slidingPanelParameters);

		// left slide menu 설정
		leftMenuPanel = (LinearLayout) findViewById(R.id.leftMenuPanel);
		leftMenuPanelParameters = (FrameLayout.LayoutParams) leftMenuPanel
				.getLayoutParams();
		leftMenuPanelParameters.width = panelWidth;
		leftMenuPanel.setLayoutParams(leftMenuPanelParameters);

		// wifi_channel
		mng = (WifiManager) getSystemService(WIFI_SERVICE);

		final int goodch5 = MinA(channel5_count);
		final int goodch2 = MinA(channel2_count);

		int testch; // 채널
		testch = convertFrequencyToChannel(mCurrentFrequency);// 채널 받기

		channelBtn = (Button) findViewById(R.id.channelButton);
		channelBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setSignalLength();
				int changech = 0;
				try {
					for (ScanResult scanResult : mng.getScanResults()) {
						if (info != null && info.getBSSID() != null
								&& info.getBSSID().equals(scanResult.BSSID)) {

							mCurrentFrequency = scanResult.frequency;

							if (mCurrentFrequency >= 5000
									&& mCurrentFrequency <= 6000) {
								scanResult.frequency = channel5[goodch5];
								changech = convertFrequencyToChannel(scanResult.frequency);
								Toast.makeText(MainActivity.this,
										"신호가 최적화 되었습니다.", Toast.LENGTH_LONG)
										.show();
							} else if (mCurrentFrequency >= 2000
									&& mCurrentFrequency <= 3000) {
								scanResult.frequency = channel2[goodch2];
								changech = convertFrequencyToChannel(scanResult.frequency);
								Toast.makeText(MainActivity.this,
										"신호가 최적화 되었습니다.", Toast.LENGTH_LONG)
										.show();
							}
							break;
						}
					}
				} catch (Exception e) {
				}
			}

		});

	}

	void setWifiName() {
		wifiName = (TextView) findViewById(R.id.wifi_name);
		wifiName.setText("SSID : " + info.getSSID());
	}

	void setSignalLength() { // 온도계 신호세기 출력하는 함수
		LinearLayout chartLngImg = (LinearLayout) findViewById(R.id.chartLength);
		LayoutParams params = (LayoutParams) chartLngImg.getLayoutParams();
		int n = info.getRssi() + 120;
		params.height = (int) (730 - ((80 - n) * 9.125));
		chartLngImg.setLayoutParams(params);
	}

	void setChannel() { // 채널을 설정해주는 함수
		try {
			for (ScanResult scanResult : mng.getScanResults()) {
				if (info != null && info.getBSSID() != null
						&& info.getBSSID().equals(scanResult.BSSID)) {
					mCurrentFrequency = scanResult.frequency;
					break;
				}
			}
		} catch (Exception e) {
		}

		if (mCurrentFrequency >= 5000 && mCurrentFrequency <= 6000) { // 5GHz
			try {
				for (ScanResult scanResult : mng.getScanResults()) {

					if (count == 100)
						break;

					switch (scanResult.frequency) {
					case 5180:
						channel5_count[0]++;
						break;
					case 5200:
						channel5_count[1]++;
						break;
					case 5220:
						channel5_count[2]++;
						break;
					case 5240:
						channel5_count[3]++;
						break;
					case 5260:
						channel5_count[4]++;
						break;
					case 5280:
						channel5_count[5]++;
						break;
					case 5300:
						channel5_count[6]++;
						break;
					case 5320:
						channel5_count[7]++;
						break;
					case 5745:
						channel5_count[8]++;
						break;
					case 5765:
						channel5_count[9]++;
						break;
					case 5785:
						channel5_count[10]++;
						break;
					case 5805:
						channel5_count[11]++;
						break;
					case 5825:
						channel5_count[12]++;
						break;
					case 5500:
						channel5_count[13]++;
						break;
					case 5520:
						channel5_count[14]++;
						break;
					case 5540:
						channel5_count[15]++;
						break;
					case 5560:
						channel5_count[16]++;
						break;
					case 5580:
						channel5_count[17]++;
						break;
					case 5600:
						channel5_count[18]++;
						break;
					case 5620:
						channel5_count[19]++;
						break;
					case 5640:
						channel5_count[20]++;
						break;
					case 5660:
						channel5_count[21]++;
						break;
					case 5680:
						channel5_count[22]++;
						break;
					case 5700:
						channel5_count[23]++;
						break;
					default:
						break;
					}
					count++;

				}
			} catch (Exception e) {
			}
		}

		else if (mCurrentFrequency >= 2000 && mCurrentFrequency <= 3000) { // 2.4GHz
			try {
				for (ScanResult scanResult : mng.getScanResults()) {

					if (count == 100)
						break;
					switch (scanResult.frequency) {
					case 2412:
						channel2_count[0]++;
						break;
					case 2417:
						channel2_count[1]++;
						break;
					case 2422:
						channel2_count[2]++;
						break;
					case 2427:
						channel2_count[3]++;
						break;
					case 2432:
						channel2_count[4]++;
						break;
					case 2437:
						channel2_count[5]++;
						break;
					case 2442:
						channel2_count[6]++;
						break;
					case 2447:
						channel2_count[7]++;
						break;
					case 2452:
						channel2_count[8]++;
						break;
					case 2457:
						channel2_count[9]++;
						break;
					case 2462:
						channel2_count[10]++;
						break;
					case 2467:
						channel2_count[11]++;
						break;
					case 2472:
						channel2_count[12]++;
						break;
					case 2484:
						channel2_count[13]++;
						break;
					default:
						break;
					}
					count++;
				}
			} catch (Exception e) {
			}
		}
	}

	void menuLeftSlideAnimationToggle() {
		if (!isLeftExpanded) {
			isLeftExpanded = true;
			leftMenuPanel.setVisibility(View.VISIBLE);
			// Expand
			new ExpandAnimation(slidingPanel, panelWidth, "left",
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.6f, 0, 0.0f, 0, 0.0f);

			// disable all of main view
			// LinearLayout viewGroup = (LinearLayout) findViewById(
			LinearLayout viewGroup = (LinearLayout) findViewById(
					R.id.ll_fragment).getParent();
			enableDisableViewGroup(viewGroup, false);

			// enable empty view
			((LinearLayout) findViewById(R.id.ll_empty))
					.setVisibility(View.VISIBLE);

			findViewById(R.id.ll_empty).setEnabled(true);
			findViewById(R.id.ll_empty).setOnTouchListener(
					new OnTouchListener() {

						@Override
						public boolean onTouch(View arg0, MotionEvent arg1) {
							menuLeftSlideAnimationToggle();
							return true;
						}
					});
		} else {
			isLeftExpanded = false;

			// Collapse
			new CloseAnimation(slidingPanel, panelWidth,
					TranslateAnimation.RELATIVE_TO_SELF, 0.6f,
					TranslateAnimation.RELATIVE_TO_SELF, 0.0f, 0, 0.0f, 0, 0.0f);

			// enable all of main view
			LinearLayout viewGroup = (LinearLayout) findViewById(
					R.id.ll_fragment).getParent();
			enableDisableViewGroup(viewGroup, true);

			// disable empty view
			((LinearLayout) findViewById(R.id.ll_empty))
					.setVisibility(View.GONE);
			findViewById(R.id.ll_empty).setEnabled(false);
		}
	}

	public static void enableDisableViewGroup(ViewGroup viewGroup,
			boolean enabled) {
		int childCount = viewGroup.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View view = viewGroup.getChildAt(i);

			if (view.getId() != R.id.myWifiButton) {
				view.setEnabled(enabled);
				if (view instanceof ViewGroup) {
					enableDisableViewGroup((ViewGroup) view, enabled);
				}
			}
		}
	}

	public static int convertFrequencyToChannel(int freq) {
		if (freq >= 2412 && freq <= 2484) {
			if (freq == 2484)
				return (freq - 2412) / 5;
			return (freq - 2412) / 5 + 1;
		} else if (freq >= 5170 && freq <= 5825) {
			return (freq - 5170) / 5 + 34;
		} else {
			return -1;
		}
	}

	public int MinA(int[] x) {
		int min = 999;
		int index = 0;

		for (int i = 0; i < x.length; i++) {
			if (min > x[i]) {
				min = x[i];
				index = i;
			}
		}
		return index;
	}

}
