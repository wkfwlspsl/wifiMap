package com.example.wifimap;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.widget.Toast;

//DB를 총괄관리
public class DBManager {

	// DB관련 상수 선언
	private static final String dbName = "ViewWifiInfo.db";
	private static final String tableName = "ViewWifiInfo info";
	public static final int dbVersion = 1;

	// DB관련 객체 선언
	private OpenHelper opener; // DB opener
	private SQLiteDatabase db; // DB controller

	// 부가적인 객체들
	private Context context;

	// 생성자
	public DBManager(Context context) {
		this.context = context;
		this.opener = new OpenHelper(context, dbName, null, dbVersion);
		db = opener.getWritableDatabase();
	}

	// Opener of DB and Table
	private class OpenHelper extends SQLiteOpenHelper {

		public OpenHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, null, version);
		}

		// 생성된 DB가 없을 경우에 한번만 호출됨
		@Override
		public void onCreate(SQLiteDatabase arg0) {
			// String dropSql = "drop table if exists " + tableName;
			// db.execSQL(dropSql);

			String createSql = "create table " + tableName + " ("
					+ "index integer, " + "ssid text, " + "passwd text, "
					+ "isOpen text, " + "latitude text, " + "longitude text, "
					+ "signalLength text, " + "apTelecom text, "
					+ "primary key ( ssid, latitude, longitude));";
			arg0.execSQL(createSql);
			// Toast.makeText(context, "DB is opened", 0).show();
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		}
	}

	// 데이터 추가
	public void insertData(int index, ViewWifiInfo info) {
		String sql = "insert into " + tableName + " values(" + index + ", '"
				+ info.getData(0) + "', '" + info.getData(1) + "', '"
				+ info.getData(2) + "', '" + info.getData(3) + "', '"
				+ info.getData(4) + "', '" + info.getData(5) + "', '"
				+ info.getData(6) + "');";
		db.execSQL(sql);
	}

	// 데이터 갱신
	public void updateData(ViewWifiInfo info, int index) {
	}

	// 데이터 삭제
	public void removeData(int index) {
		String sql = "delete from " + tableName + " where index = " + index
				+ ";";
		db.execSQL(sql);

	}

	// 데이터 검색
	public ArrayList<ViewWifiInfo> getData(int index) {
		String sql = "select * from " + tableName + " where index = " + index
				+ ";";
		Cursor result = db.rawQuery(sql, null);
		ArrayList<ViewWifiInfo> info = null;

		// result(Cursor 객체)가 비어 있으면 false 리턴
		if (!result.moveToFirst()) {
			result.close();
			return null;
		}

		do {
			info.add(new ViewWifiInfo(result.getString(1), result.getString(2),
					result.getString(3), result.getString(4), result
							.getString(5), result.getString(6), result
							.getString(7),result.getString(8),result.getString(9)));

		} while (result.moveToNext());

		result.close();
		return info;
	}

	public int getDataNum() { // 전체 레코드 갯수 반환하는 함수
		String sql = "select * from " + tableName + ";";
		Cursor result = db.rawQuery(sql, null);

		return result.getCount();
	}
}
