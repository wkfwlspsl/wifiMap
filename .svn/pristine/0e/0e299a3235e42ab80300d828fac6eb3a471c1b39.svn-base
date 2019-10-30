package com.example.wifimap;

public class ViewWifiInfo {
	private String[] mData;

	public ViewWifiInfo(String[] data) {

		mData = data;
	}

	public ViewWifiInfo(String ssid, String isOpen, String apTelecom,
			String mac, String latitude, String longitude,
			String signalLength, String radius , String cnt ) {

		mData = new String[9];
		mData[0] = ssid;
		mData[1] = isOpen;
		mData[2] = apTelecom;
		mData[3] = mac;
		mData[4] = latitude;
		mData[5] = longitude;
		mData[6] = signalLength;
		mData[7] = radius;
		mData[8] = cnt;

	}

	public String[] getData() {
		return mData;
	}

	public String getData(int index) {
		return mData[index];
	}

	public void setData(int index, String data) {
		mData[index] = data;
	}
}
