package com.branchard.android;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class PublishActivity extends Activity {

    private EditText topic;//标题
    private Button sure, back;
    private String userId, user_id, userName, date, body, userComments;
    private String topicId, headName;
    private String sign, isBackLook;

    MyDatabaseHelper mydbHelper;

    public void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_publish);

        topic = (EditText) findViewById(R.id.publishWord);
        sure = (Button) findViewById(R.id.publish_sure);
        back = (Button) findViewById(R.id.publish_cancel);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        sign = intent.getStringExtra("sign");
        topicId = intent.getStringExtra("topicId");
        user_id = intent.getStringExtra("user_id");
        userName = intent.getStringExtra("userName");
        userComments = intent.getStringExtra("userComments");
        date = intent.getStringExtra("date");
        body = intent.getStringExtra("body");
        isBackLook = intent.getStringExtra("isBackLook");
        headName = intent.getStringExtra("headName");

        MyOnClickListerner myOnClickListerner = new MyOnClickListerner();
        sure.setOnClickListener(myOnClickListerner);
        back.setOnClickListener(myOnClickListerner);
    }

    private class MyOnClickListerner implements OnClickListener {
        public void onClick(View v) {

            mydbHelper = new MyDatabaseHelper(PublishActivity.this, "topic.db",
                    null, 1);// 创建数据库辅助类
            SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库
            String content = topic.getText().toString();

            mydbHelper = new MyDatabaseHelper(PublishActivity.this,
                    "comments.db", null, 1);// 创建数据库辅助类
            SQLiteDatabase db2 = mydbHelper.getReadableDatabase();// 获取SQLite数据库
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault());
            String dateStr = sdf.format(new Date());

            if (sign.equals("0")) {
                switch (v.getId()) {
                    case R.id.publish_sure:
                        addTopic(db, userId, content, dateStr);
                        System.out.println("!!!!!!!!!!!!!!!!!");

                        if (isBackLook.equals("yes")) {
                            Intent intent2 = new Intent(PublishActivity.this,
                                    LookActivity.class);
                            intent2.putExtra("userId", userId);
                            startActivity(intent2);
                            System.exit(0);
                        } else {
                            Intent intent2 = new Intent(PublishActivity.this,
                                    HomeActivity.class);
                            intent2.putExtra("userId", userId);
                            startActivity(intent2);
                            System.exit(0);
                        }
                        break;
                    case R.id.publish_cancel:
                        topic.setText("");
                        if (isBackLook.equals("yes")) {
                            Intent intent3 = new Intent(PublishActivity.this,
                                    LookActivity.class);
                            intent3.putExtra("userId", userId);
                            startActivity(intent3);
                            System.exit(0);
                        } else {
                            Intent intent3 = new Intent(PublishActivity.this,
                                    HomeActivity.class);
                            intent3.putExtra("userId", userId);
                            startActivity(intent3);
                            System.exit(0);
                        }
                        break;
                    default:
                        break;

                }
            } else {
                switch (v.getId()) {
                    case R.id.publish_sure:
                        addComments(db2, userId, topicId, userComments, content,
                                dateStr);
                        System.out.println("!!!!!!!!!!!!!!!!!");

                        Intent intent2 = new Intent(PublishActivity.this,
                                CommentsActivity.class);
                        intent2.putExtra("userId", userId);
                        intent2.putExtra("user_id", user_id);
                        intent2.putExtra("userName", userName);
                        intent2.putExtra("userComments", userComments);
                        intent2.putExtra("body", body);
                        intent2.putExtra("date", date);
                        intent2.putExtra("headName", headName);
                        startActivity(intent2);
                        System.exit(0);
                        break;
                    case R.id.publish_cancel:
                        topic.setText("");
                        Intent intent3 = new Intent(PublishActivity.this,
                                CommentsActivity.class);
                        intent3.putExtra("userId", userId);
                        intent3.putExtra("user_id", user_id);
                        intent3.putExtra("userName", userName);
                        intent3.putExtra("userComments", userComments);
                        intent3.putExtra("body", body);
                        intent3.putExtra("date", date);
                        intent3.putExtra("headName", headName);
                        startActivity(intent3);
                        System.exit(0);
                        break;
                    default:
                        break;

                }
            }

        }
    }

    public void addTopic(SQLiteDatabase db, String userId, String body,
                         String date) {
        db.execSQL("insert into topic_tb values(null,?,?,?,?,?)", new String[] {
                userId, body, date, "0", "0" });
        addTopicNumber();
        this.topic.setText("");
    }

    public void addComments(SQLiteDatabase db, String userId, String topicId,
                            String userName, String body, String date) {
        db.execSQL("insert into comments_tb values(null,?,?,?,?,?,?)",
                new String[] { topicId, userId, userName, body, date, "0" });
        this.topic.setText("");
        addCommNumber();
    }

    public void addTopicNumber() {
        String number = "";
        mydbHelper = new MyDatabaseHelper(PublishActivity.this, "user.db",
                null, 1);// 创建数据库辅助类
        SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库

        Cursor cur = db.query("user_tb", new String[] { "topicNumber" },
                "userId='" + userId + "'", null, null, null, null);
        while (cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                cur.moveToPosition(i);
                number = cur.getString(0);
            }
        }
        number = String.valueOf(Integer.parseInt(number) + 1);
        db.execSQL("update user_tb set topicNumber=? where userId=?",
                new String[] { number, userId });
    }

    public void addCommNumber() {
        String number = "";
        int id = Integer.parseInt(topicId);
        mydbHelper = new MyDatabaseHelper(PublishActivity.this, "topic.db",
                null, 1);// 创建数据库辅助类
        SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库

        Cursor cur = db.query("topic_tb", new String[] { "commNumber" }, "_id="
                + id, null, null, null, null);
        while (cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                cur.moveToPosition(i);
                number = cur.getString(0);
            }
        }
        number = String.valueOf(Integer.parseInt(number) + 1);
        db.execSQL("update topic_tb set commNumber=? where _id=" + id,
                new String[] { number });
    }

    protected void onDestroy() {
        if (mydbHelper != null) {
            mydbHelper.close();
        }
        super.onDestroy();
    }

}