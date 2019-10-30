package com.example.wifimap;

public class SaveWifiInfo {

	private String[] mData;

	public SaveWifiInfo(String[] data) {

		mData = data;
	}

	public SaveWifiInfo(String userTelecom, String ssid, String signalLength,
			String longitude, String latitude, String apTelecom, String Ip,
			String mac, String deviceName, String isOpen) {

		mData = new String[11];
		mData[0] = userTelecom;
		mData[1] = ssid;
		mData[2] = signalLength;
		mData[3] = latitude;
		mData[4] = longitude;
		mData[5] = apTelecom;
		mData[6] = Ip;
		mData[7] = mac;
		mData[8] = apTelecom;
		mData[9] = deviceName;
		mData[10] = isOpen;

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