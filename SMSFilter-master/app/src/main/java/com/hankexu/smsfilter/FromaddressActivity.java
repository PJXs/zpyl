package com.hankexu.smsfilter;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.hankexu.util.DbHelper;

import java.util.ArrayList;

public class FromaddressActivity extends AppCompatActivity {
    private ListView lv;
    private EditText ed;
    private Button btnAdd;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> fromaddressList;
    private DbHelper dbHelper;
    private SQLiteDatabase dbWriter;
    private SQLiteDatabase dbReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fromaddress);
        dbHelper = new DbHelper(FromaddressActivity.this);
        initView();
        fromaddressList = loadFromaddressList();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fromaddressList);
        lv.setAdapter(adapter);
    }

    private void initView() {
        lv = (ListView) findViewById(R.id.lv_fromaddress);
        ed = (EditText) findViewById(R.id.ed_fromaddress);
        btnAdd = (Button) findViewById(R.id.btn_add_fromaddress);
        btnAdd.setOnClickListener(btnClickListener);
        lv.setOnItemLongClickListener(itemLongClickListener);
    }/*初始化视图*/

    private View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addFromaddress();
        }
    };

    private AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(FromaddressActivity.this);
            builder.setTitle("提示").setMessage("确定删除?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (deleteFromaddress(position)) refreshListView();
                    else
                        Toast.makeText(FromaddressActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
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

    private ArrayList<String> loadFromaddressList() {
        ArrayList<String> list = new ArrayList<String>();
        dbReader = dbHelper.getReadableDatabase();
        Cursor cursor = dbReader.query(DbHelper.TABLE_NAME_FROMADDRESS, new String[]{DbHelper.COLUMN_NAME_FROMADDRESS}, null, null, null, null, null);
        while (cursor.moveToNext())
            list.add(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_NAME_FROMADDRESS)));
        dbReader.close();
        return list;
    }/*加载列表关键字*/

    /**
     * @param index 传入被点击条目的索引
     * @return 删除成功返回true
     */
    private boolean deleteFromaddress(int index) {
        dbReader = dbHelper.getReadableDatabase();
        Cursor cursor = dbReader.query(DbHelper.TABLE_NAME_FROMADDRESS, new String[]{DbHelper.COLUMN_NAME_ID}, null, null, null, null, null);
        cursor.moveToPosition(index);
        int itemId = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_NAME_ID));
        dbWriter = dbHelper.getWritableDatabase();
        int result = dbWriter.delete(DbHelper.TABLE_NAME_FROMADDRESS, DbHelper.COLUMN_NAME_ID + "=?", new String[]{itemId + ""});
        dbReader.close();
        if (result == 1) return true;
        else return false;
    }

    private void addFromaddress() {
        String keyword = ed.getText().toString();
        if (keyword.length() > 0)
            if (keyword.length() <= 20) {
                ContentValues cv = new ContentValues();
                cv.put(DbHelper.COLUMN_NAME_FROMADDRESS, keyword);
                dbWriter = dbHelper.getWritableDatabase();
                long newRowID = dbWriter.insert(DbHelper.TABLE_NAME_FROMADDRESS, null, cv);
                dbWriter.close();
                if (newRowID != -1) {
                    refreshListView();
                    ed.setText("");
                } else Toast.makeText(FromaddressActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(FromaddressActivity.this, "长度超出", Toast.LENGTH_SHORT).show();
        else Toast.makeText(FromaddressActivity.this, "请先填写关键字", Toast.LENGTH_SHORT).show();
    }/*添加关键字*/

    private void refreshListView() {
        fromaddressList.clear();
        fromaddressList = loadFromaddressList();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fromaddressList);/*TODO 换用adapter.notifyDataSetChanged()*/
        lv.setAdapter(adapter);
    }/*刷新列表*/
}
