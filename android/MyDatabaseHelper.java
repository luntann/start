package com.branchard.android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

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
				"123", "���", "123456", "δ����","δ����","��","0","head01","1","1","1","1"});
		db.execSQL("insert into user_tb values(null,?,?,?,?,?,?,?,?,?,?,?,?)", new String[] {
				"789", "�����", "123456", "δ����","δ����","Ů","0","head03","1","1","1","1"});
		db.execSQL("insert into user_tb values(null,?,?,?,?,?,?,?,?,?,?,?,?)", new String[] {
				"456", "��ɽ��", "123456", "δ����","δ����","��","0","head09","1","1","1","1"});
		db.execSQL("insert into user_tb values(null,?,?,?,?,?,?,?,?,?,?,?,?)", new String[] {
				"330013", "������", "123456", "δ����","δ����","��","0","head04","0","0","1","0"});
		db.execSQL("insert into user_tb values(null,?,?,?,?,?,?,?,?,?,?,?,?)", new String[] {
				"0123703", "���", "123456", "����·��","δ����","��","0","head05","0","0","1","0"});
		db.execSQL("insert into user_tb values(null,?,?,?,?,?,?,?,?,?,?,?,?)", new String[] {
				"0124339", "��ʼ��", "123456", "δ����","δ����","��","0","head06","0","0","1","0"});
		
		db.execSQL("insert into topic_tb values(null,?,?,?,?,?)", new String[] {
				"0124339", "������������", "2015-06-01 16:36:50", "0", "0"});
		db.execSQL("insert into topic_tb values(null,?,?,?,?,?)", new String[] {
				"123", "�簡��", "2015-06-02 06:30:00", "1", "0"});
		db.execSQL("insert into topic_tb values(null,?,?,?,?,?)", new String[] {
				"456", "��������ţ����������ţ�̣�ţ���̸�������������ţ�̡�������˵ţ���̵�����ţ�̲��������̵�����ţ�̣�ţ����˵�����̵�����ţ�̻����̣������������˴���ţ�����������ţ�̲Ż����̡���������Ҳ���������̺�ţ���̵�����ţ�̡�", "2015-06-01 06:30:00", "1", "0"});
		db.execSQL("insert into topic_tb values(null,?,?,?,?,?)", new String[] {
				"789", "��׹�����", "2015-06-03 16:36:50", "1", "1"});
		db.execSQL("insert into topic_tb values(null,?,?,?,?,?)", new String[] {
				"330013", "��������", "2015-06-09 16:36:51", "0", "0"});
		db.execSQL("insert into topic_tb values(null,?,?,?,?,?)", new String[] {
				"0123703", "ȥ����� ����Ϊʲô������ҵ�� ����ɷ�� �� ȥ�꺮�� ����Ϊʲô������ҵ�� ����ħ��Ȣ���ˡ� ������� ����Ϊʲô������ҵ�� ���Ұ������������ˡ� ", "2015-06-10 16:36:52", "0", "0"});
		
		
		db.execSQL("insert into comments_tb values(null,?,?,?,?,?,?)", new String[] {
				"2","456","��ɽ��", "��", "2015-06-02 06:31:00","0" });
		db.execSQL("insert into comments_tb values(null,?,?,?,?,?,?)", new String[] {
				"6","789","�����", "�Ǻ�", "2015-06-01 06:31:00","0" });
		db.execSQL("insert into comments_tb values(null,?,?,?,?,?,?)", new String[] {
				"4","123","���", "����ѽ", "2015-06-03 19:31:00","0" });
		
		db.execSQL("insert into fans_tb values(null,?,?)", new String[] {
				"123","456"});
		db.execSQL("insert into fans_tb values(null,?,?)", new String[] {
				"456","789"});
		db.execSQL("insert into fans_tb values(null,?,?)", new String[] {
				"789","123"});
		
		db.execSQL("insert into zan_tb values(null,?,?)", new String[] {
				"3","456"});
	}
	public void onUpgrade(SQLiteDatabase db, int oldVersion, 
			int newVersion) {
		System.out.println("---------"+oldVersion+"------->"+newVersion);
	}
}