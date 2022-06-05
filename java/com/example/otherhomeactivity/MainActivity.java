package com.branchard.android;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class OtherHomeActivity extends Activity {

    private Button backBtn, topicNumberBtn, focusNumberBtn, fansNumberBtn,
            likeyouBtn, messageBtn;
    private EditText moodEt, otherIdEt, otherNameEt, birthdayEt, sexEt;
    private ImageView head;
    private String topicNumber, focusNumber, fansNumber;
    private String mood, otherId, otherName, birthday, sex;
    private String userId, headName;

    MyDatabaseHelper mydbHelper;

    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otherhome);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        headName = intent.getStringExtra("headName");
        otherId = intent.getStringExtra("user_id");
        otherName = intent.getStringExtra("userName");
        backBtn = (Button) findViewById(R.id.backBtn);
        topicNumberBtn = (Button) findViewById(R.id.postNumber);
        focusNumberBtn = (Button) findViewById(R.id.focusNumber);
        fansNumberBtn = (Button) findViewById(R.id.fansNumber);
        likeyouBtn = (Button) findViewById(R.id.likeyouBut);
        messageBtn = (Button) findViewById(R.id.messageBtn);
        moodEt = (EditText) findViewById(R.id.qianWord);
        otherIdEt = (EditText) findViewById(R.id.otherIdEt);
        otherNameEt = (EditText) findViewById(R.id.otherNameEt);
        birthdayEt = (EditText) findViewById(R.id.birthdayEt);
        sexEt = (EditText) findViewById(R.id.sexEt);
        head = (ImageView) findViewById(R.id.head);

        setAllMessage();

        // 关注
        if (isLike()) {
            likeyouBtn.setText("取消关注");
        }

        likeyouBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (likeyouBtn.getText().equals("加关注")) {
                    addLike();
                    likeyouBtn.setText("取消关注");
                } else {
                    ridLike();
                    likeyouBtn.setText("加关注");
                }
            }
        });

        backBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
    }

    public void setAllMessage() {
        otherIdEt.setText(otherId);
        otherNameEt.setText(otherName);
        // 放置头像
        String path = "/sdcard/head/" + headName + ".jpg";
        File file = new File(path);
        if (file.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(path);
            // 将图片显示到ImageView中
            head.setImageBitmap(bm);
        } else {
            head.setImageResource(R.drawable.head02);
        }

        mydbHelper = new MyDatabaseHelper(OtherHomeActivity.this, "user.db",
                null, 1);// 创建数据库辅助类
        SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库

        Cursor cur = db.query("user_tb", new String[] { "mood", "birthday",
                "sex", "likeNumber", "fansNumber", "topicNumber" }, "userId='"
                + otherId + "'", null, null, null, null);
        while (cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                cur.moveToPosition(i);
                mood = cur.getString(0);
                birthday = cur.getString(1);
                sex = cur.getString(2);
                focusNumber = cur.getString(3);
                fansNumber = cur.getString(4);
                topicNumber = cur.getString(5);
            }
        }
        moodEt.setText(mood);
        birthdayEt.setText(birthday);
        sexEt.setText(sex);
        focusNumberBtn.setText(focusNumber);
        fansNumberBtn.setText(fansNumber);
        topicNumberBtn.setText(topicNumber);
    }

    public Boolean isLike() {
        mydbHelper = new MyDatabaseHelper(OtherHomeActivity.this, "fans.db",
                null, 1);// 创建数据库辅助类
        SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库

        Cursor cur = db.query("fans_tb", new String[] { "userId" }, "fansId='"
                + userId + "'", null, null, null, null);
        while (cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                cur.moveToPosition(i);
                String likeId = cur.getString(0);
                if (likeId.equals(otherId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addLike() {
        mydbHelper = new MyDatabaseHelper(OtherHomeActivity.this, "fans.db",
                null, 1);// 创建数据库辅助类
        SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库

        db.execSQL("insert into fans_tb values(null,?,?)", new String[] {
                otherId, userId });
        fansNumber = String.valueOf(Integer.parseInt(fansNumber) + 1);
        fansNumberBtn.setText(fansNumber);

        mydbHelper = new MyDatabaseHelper(OtherHomeActivity.this, "user.db",
                null, 1);// 创建数据库辅助类
        SQLiteDatabase db2 = mydbHelper.getReadableDatabase();// 获取SQLite数据库
        // 被关注者粉丝数增加
        db2.execSQL("update user_tb set fansNumber=? where userId=?",
                new String[] { fansNumber, otherId });
        // 用户关注数增加
        String number = "";
        Cursor cur = db.query("user_tb", new String[] { "likeNumber" },
                "userId='" + userId + "'", null, null, null, null);
        while (cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                cur.moveToPosition(i);
                number = cur.getString(0);
            }
        }
        number = String.valueOf(Integer.parseInt(number) + 1);
        db2.execSQL("update user_tb set likeNumber=? where userId=?",
                new String[] { number, userId });
    }

    public void ridLike() {
        mydbHelper = new MyDatabaseHelper(OtherHomeActivity.this, "fans.db",
                null, 1);// 创建数据库辅助类
        SQLiteDatabase db = mydbHelper.getReadableDatabase();// 获取SQLite数据库

        db.execSQL("delete from fans_tb where fansId=? and userId=?",
                new String[] { userId, otherId });
        fansNumber = String.valueOf(Integer.parseInt(fansNumber) - 1);
        fansNumberBtn.setText(fansNumber);

        mydbHelper = new MyDatabaseHelper(OtherHomeActivity.this, "user.db",
                null, 1);// 创建数据库辅助类
        SQLiteDatabase db2 = mydbHelper.getReadableDatabase();// 获取SQLite数据库
        // 被关注者粉丝数减少
        db2.execSQL("update user_tb set fansNumber=? where userId=?",
                new String[] { fansNumber, otherId });
        // 用户关注数减少
        String number = "";
        Cursor cur = db.query("user_tb", new String[] { "likeNumber" },
                "userId='" + userId + "'", null, null, null, null);
        while (cur.moveToNext()) {
            for (int i = 0; i < cur.getCount(); i++) {
                cur.moveToPosition(i);
                number = cur.getString(0);
            }
        }
        number = String.valueOf(Integer.parseInt(number) - 1);
        db2.execSQL("update user_tb set likeNumber=? where userId=?",
                new String[] { number, userId });
    }

    protected void onDestroy() {
        if (mydbHelper != null) {
            mydbHelper.close();
        }
        super.onDestroy();
    }

}
