package com.hankexu.smsfilter;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.hankexu.adapter.SmsAdapter;
import com.hankexu.bean.Sms;
import com.hankexu.receiver.SmsReceiver;
import com.hankexu.util.DbHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView lv;
    private SmsAdapter adapter;
    private ArrayList<Sms> smsList;
    private DbHelper dbHelper;
    private SQLiteDatabase dbWriter;
    private SQLiteDatabase dbReader;
    private SmsReceiver smsReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        dbHelper = new DbHelper(this);
        smsList = loadSmsList();
        adapter = new SmsAdapter(smsList, MainActivity.this);
        dbHelper = new DbHelper(this);
        lv.setAdapter(adapter);

    }


    private ArrayList<Sms> loadSmsList() {
        ArrayList<Sms> list = new ArrayList<Sms>();
        dbReader = dbHelper.getReadableDatabase();
        Cursor cursor = dbReader.query(DbHelper.TABLE_NAME_SMS, new String[]{DbHelper.COLUMN_NAME_FROMADDRESS, DbHelper.COLUMN_NAME_BODY, DbHelper.COLUMN_NAME_RECEIVER_TIME}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String fromaddress = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_NAME_FROMADDRESS));
            String body = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_NAME_BODY));
            String datetime = cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_NAME_RECEIVER_TIME));
            Sms sms = new Sms(fromaddress, body, datetime);
            list.add(sms);
            dbReader.close();
        }
        return list;
    }/*加载拦截历史*/

    private void initView() {
        lv = (ListView) findViewById(R.id.lv);
        lv.setOnItemLongClickListener(itemLongClickListener);
        smsReceiver.setOnFilterListerner(filterListerner);
    }
    SmsReceiver.FilterListerner filterListerner = new SmsReceiver.FilterListerner() {
        @Override
        public void onFilted(boolean filted) {
            if (filted) refreshListView();
        }
    };/*收信监听器*/

    private AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("提示").setMessage("确定删除?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (deleteSms(position)) refreshListView();
                    else
                        Toast.makeText(MainActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                }
            }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            }).show();
            return true;
        }
    };/*删除提示对话框*/

    private void refreshListView() {
        smsList.clear();
        smsList = loadSmsList();
        adapter = new SmsAdapter(smsList, MainActivity.this);/*TODO 换用adapter.notifyDataSetChanged()*/
        lv.setAdapter(adapter);
    }/*刷新listview*/

    /**
     * @param index 传入被点击条目的索引
     * @return 返回处理结果，成功为true
     */
    private boolean deleteSms(int index) {
        dbReader = dbHelper.getReadableDatabase();
        Cursor cursor = dbReader.query(DbHelper.TABLE_NAME_SMS, new String[]{DbHelper.COLUMN_NAME_ID}, null, null, null, null, null);
        cursor.moveToPosition(index);
        int itemId = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_NAME_ID));
        dbWriter = dbHelper.getWritableDatabase();
        int result = dbWriter.delete(DbHelper.TABLE_NAME_SMS, DbHelper.COLUMN_NAME_ID + "=?", new String[]{itemId + ""});
        dbReader.close();
        if (result == 1) return true;
        else return false;
    }/*删除信息*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_item_add_fromaddress:
                intent = new Intent(MainActivity.this, FromaddressActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_item_add_keyword:
                intent = new Intent(MainActivity.this, KeywordActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }/*菜单处理*/


}
