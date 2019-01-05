package com.hankexu.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.hankexu.bean.Sms;
import com.hankexu.util.DbHelper;
import com.hankexu.util.Filter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by hanke on 2015-10-22.
 */
public class SmsReceiver extends BroadcastReceiver {
    private boolean filted = false;
    private static FilterListerner filterListerner;

    public SmsReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) return;
        Object[] pdus = (Object[]) extras.get("pdus");
        String fromaddress = null;
        String body = null;
        for (int i = 0; i < pdus.length; i++) {
            SmsMessage message = SmsMessage.createFromPdu((byte[]) pdus[i]);
            fromaddress = message.getOriginatingAddress();
            body = message.getMessageBody();

        }/*接收消息*/


        /*
        * 从数据库中查询出需要屏蔽的关键字和号码
        * */
        ArrayList<String> keywordList = new ArrayList<String>();
        ArrayList<String> fromaddressList = new ArrayList<String>();
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db;
        db = dbHelper.getReadableDatabase();
        Cursor cKeywords = db.query(DbHelper.TABLE_NAME_KEYWORDS, new String[]{DbHelper.COLUMN_NAME_KEYWORD}, null, null, null, null, null);
        while (cKeywords.moveToNext()) {
            keywordList.add(cKeywords.getString(cKeywords.getColumnIndex(DbHelper.COLUMN_NAME_KEYWORD)));
        }
        Cursor cFromaddress = db.query(DbHelper.TABLE_NAME_FROMADDRESS, new String[]{DbHelper.COLUMN_NAME_FROMADDRESS}, null, null, null, null, null);
        while (cFromaddress.moveToNext()) {
            fromaddressList.add(cFromaddress.getString(cFromaddress.getColumnIndex(DbHelper.COLUMN_NAME_FROMADDRESS)));
        }
        db.close();


        if (Filter.keywordsFilter(body, keywordList) || Filter.fromaddressFilter(fromaddress, fromaddressList)) {
            abortBroadcast();
            Toast.makeText(context, "帮你拦截到垃圾信息", Toast.LENGTH_SHORT).show();
            filted = saveSms(fromaddress, body, context);
            if (filterListerner != null) {
                filterListerner.onFilted(filted);
            }
        }/*匹配*/

    }

    /**
     * @param fromaddress 信息发送者号码
     * @param body 信息内容
     * @param context
     * @return 保存成功，返回true
     */
    private boolean saveSms(String fromaddress, String body, Context context) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date curDate = new Date(System.currentTimeMillis());
        String datetime = formater.format(curDate);
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.COLUMN_NAME_FROMADDRESS, fromaddress);
        cv.put(DbHelper.COLUMN_NAME_BODY, body);
        cv.put(DbHelper.COLUMN_NAME_RECEIVER_TIME, datetime);
        long newRowId = db.insert(DbHelper.TABLE_NAME_SMS, null, cv);
        db.close();
        if (newRowId != -1) return true;
        else
            return false;
    }

    public interface FilterListerner {
        void onFilted(boolean filted);
    }/*回调接口，用于通知MainActivity刷新界面*/

    public static void setOnFilterListerner(FilterListerner filterListerner) {
        SmsReceiver.filterListerner = filterListerner;
    }
}
