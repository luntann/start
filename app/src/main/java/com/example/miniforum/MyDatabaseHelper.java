package com.example.miniforum;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
	final String CREATE_TABLE_SQL=
		"create table topic_tb(_id integer primary " +
		"key autoincrement,user_id,body,date,commNumber,goodNumber)";//2
	final String table_comments=
			"create table comments_tb(_id integer primary " +
			"key autoincrement,topicId,userId,userName,body,date,goodNumber)";
	final String table_user=
			"create table user_tb(_id integer primary " +
			"key,userId,userName,passWord,mood,birthday,sex,isOnline,head,likeNumber,fansNumber,topicNumber,commNumber)";//5
	final String table_fans=
			"create table fans_tb(_id integer primary " +
			"key,userId,fansId)";
	final String table_zan=
			"create table zan_tb(_id integer primary " +
			"key,topicId,userId)";
	public MyDatabaseHelper(Context context, String name,
                            CursorFactory factory, int version) {
		super(context, name, factory, version);		
	}
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_SQL);
		db.execSQL(table_user);
		db.execSQL(table_comments);
		db.execSQL(table_fans);
		db.execSQL(table_zan);
		
		db.execSQL("insert into user_tb values(null,?,?,?,?,?,?,?,?,?,?,?,?)", new String[] {
				"21906091029", "", "123456", "Î´ÉèÖÃ","Î´ÉèÖÃ","ÄÐ","0","head01","1","1","1","1"});
		db.execSQL("insert into user_tb values(null,?,?,?,?,?,?,?,?,?,?,?,?)", new String[] {
				"21906091031", "»ÆÓÓÊ¤", "123456", "Î´ÉèÖÃ","Î´ÉèÖÃ","ÄÐ","0","head03","1","1","1","1"});
		db.execSQL("insert into user_tb values(null,?,?,?,?,?,?,?,?,?,?,?,?)", new String[] {
				"21906091032", "»ÆÓî", "123456", "Î´ÉèÖÃ","Î´ÉèÖÃ","ÄÐ","0","head09","1","1","1","1"});
		db.execSQL("insert into user_tb values(null,?,?,?,?,?,?,?,?,?,?,?,?)", new String[] {
				"21906091033", "¼ÖÎÄ²Æ", "123456", "Î´ÉèÖÃ","Î´ÉèÖÃ","ÄÐ","0","head04","0","0","1","0"});
		db.execSQL("insert into user_tb values(null,?,?,?,?,?,?,?,?,?,?,?,?)", new String[] {
				"21906091036", "ãÛ¿ªµä", "123456", "ÓÖÃÔÂ·ÁË","Î´ÉèÖÃ","ÄÐ","0","head05","0","0","1","0"});
		db.execSQL("insert into user_tb values(null,?,?,?,?,?,?,?,?,?,?,?,?)", new String[] {
				"21906091037", "¿µµØ", "123456", "Î´ÉèÖÃ","Î´ÉèÖÃ","ÄÐ","0","head06","0","0","1","0"});
		db.execSQL("insert into user_tb values(null,?,?,?,?,?,?,?,?,?,?,?,?)", new String[] {
				"21906091038", "Àî±ó", "123456", "Î´ÉèÖÃ","Î´ÉèÖÃ","ÄÐ","0","head06","0","0","1","0"});
		db.execSQL("insert into user_tb values(null,?,?,?,?,?,?,?,?,?,?,?,?)", new String[] {
				"21906091039", "Àî·²", "123456", "Î´ÉèÖÃ","Î´ÉèÖÃ","ÄÐ","0","head06","0","0","1","0"});
		
		db.execSQL("insert into topic_tb values(null,?,?,?,?,?)", new String[] {
				"21906091029", "ºúÔË³¬", "2022-06-01 16:36:50", "0", "0"});
		db.execSQL("insert into topic_tb values(null,?,?,?,?,?)", new String[] {
				"21906091031", "»ÆÓÓÊ¤", "2012-06-02 06:30:00", "1", "0"});
		db.execSQL("insert into topic_tb values(null,?,?,?,?,?)", new String[] {
				"21906091032", "»ÆÓî", "2022-06-01 06:30:00", "1", "0"});
		db.execSQL("insert into topic_tb values(null,?,?,?,?,?)", new String[] {
				"21906091033", "¼ÖÎÄ²Æ", "2022-06-03 16:36:50", "1", "1"});
		db.execSQL("insert into topic_tb values(null,?,?,?,?,?)", new String[] {
				"21906091036", "ãÛ¿ªµä", "2022-06-01 16:36:51", "0", "0"});
		db.execSQL("insert into topic_tb values(null,?,?,?,?,?)", new String[] {
				"21906091037", "¿µµØ", "2022-06-1 16:36:52", "0", "0"});
		db.execSQL("insert into topic_tb values(null,?,?,?,?,?)", new String[] {
				"21906091038", "Àî±ó", "2022-06-1 16:36:52", "0", "0"});
		db.execSQL("insert into topic_tb values(null,?,?,?,?,?)", new String[] {
				"21906091039", "Àî·²", "2022-06-1 16:36:52", "0", "0"});
		
		
		db.execSQL("insert into comments_tb values(null,?,?,?,?,?,?)", new String[] {
				"2","21906091036","ãÛ¿ªµä", "Ôç", "2022-06-02 06:31:00","0" });
		db.execSQL("insert into comments_tb values(null,?,?,?,?,?,?)", new String[] {
				"6","21906091037","¿µµØ", "ºÇºÇ", "2022-06-01 06:31:00","0" });
		db.execSQL("insert into comments_tb values(null,?,?,?,?,?,?)", new String[] {
				"4","21906091029","ºúÔË³¬", "¹þ¹þ", "2022-06-03 19:31:00","0" });
		
		db.execSQL("insert into fans_tb values(null,?,?)", new String[] {
				"21906091029","21906091031"});
		db.execSQL("insert into fans_tb values(null,?,?)", new String[] {
				"21906091031","21906091032"});
		db.execSQL("insert into fans_tb values(null,?,?)", new String[] {
				"21906091033","21906091037"});
		
		db.execSQL("insert into zan_tb values(null,?,?)", new String[] {
				"3","21906091031"});
	}
	public void onUpgrade(SQLiteDatabase db, int oldVersion, 
			int newVersion) {
		System.out.println("---------"+oldVersion+"------->"+newVersion);
	}
}