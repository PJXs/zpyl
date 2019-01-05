package com.hankexu.smsfilter;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.hankexu.util.DbHelper;

import java.util.ArrayList;
/*
* 关键字管理
* */
public class KeywordActivity extends AppCompatActivity {
    private ListView lv;
    private EditText ed;
    private Button btnAdd;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> keywordList;
    private DbHelper dbHelper;
    private SQLiteDatabase dbWriter;
    private SQLiteDatabase dbReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyword);
        dbHelper = new DbHelper(KeywordActivity.this);
        initView();
        keywordList = loadKeywordList();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, keywordList);
        lv.setAdapter(adapter);
    }

    /**
     * @return 从数据库中查出所有的关键字，添加到list中并返回
     */
    private ArrayList<String> loadKeywordList() {
        ArrayList<String> list = new ArrayList<String>();
        dbReader = dbHelper.getReadableDatabase();
        Cursor cursor = dbReader.query(DbHelper.TABLE_NAME_KEYWORDS, new String[]{DbHelper.COLUMN_NAME_KEYWORD}, null, null, null, null, null);
        while (cursor.moveToNext())
            list.add(cursor.getString(cursor.getColumnIndex(DbHelper.COLUMN_NAME_KEYWORD)));
        dbReader.close();
        return list;
    }/*查询关键字*/

    private void initView() {
        lv = (ListView) findViewById(R.id.lv_keywords);
        ed = (EditText) findViewById(R.id.ed_keyword);
        btnAdd = (Button) findViewById(R.id.btn_add_keyword);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addKeyword();
            }
        });
        lv.setOnItemLongClickListener(itemLongClickListener);
    }/*初始化视图*/

    private AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            AlertDialog.Builder builder = new AlertDialog.Builder(KeywordActivity.this);
            builder.setTitle("提示").setMessage("确定删除?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (deleteKeyword(position)) refreshListView();
                    else
                        Toast.makeText(KeywordActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
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

    /**
     * @param index 传入被点击条目的索引
     * @return 返回处理结果，成功为true
     */
    private boolean deleteKeyword(int index) {
        dbReader = dbHelper.getReadableDatabase();
        Cursor cursor = dbReader.query(DbHelper.TABLE_NAME_KEYWORDS, new String[]{DbHelper.COLUMN_NAME_ID}, null, null, null, null, null);
        cursor.moveToPosition(index);
        int itemId = cursor.getInt(cursor.getColumnIndex(DbHelper.COLUMN_NAME_ID));
        dbWriter = dbHelper.getWritableDatabase();
        int result = dbWriter.delete(DbHelper.TABLE_NAME_KEYWORDS, DbHelper.COLUMN_NAME_ID + "=?", new String[]{itemId + ""});
        dbReader.close();
        if (result == 1) return true;
        else return false;
    }/*删除关键字*/

    private void addKeyword() {
        String keyword = ed.getText().toString();
        if (keyword.length() > 0)
            if (keyword.length() <= 20) {
                ContentValues cv = new ContentValues();
                cv.put(DbHelper.COLUMN_NAME_KEYWORD, keyword);
                dbWriter = dbHelper.getWritableDatabase();
                long newRowID = dbWriter.insert(DbHelper.TABLE_NAME_KEYWORDS, null, cv);
                dbWriter.close();
                if (newRowID != -1) {
                    refreshListView();
                    ed.setText("");
                } else Toast.makeText(KeywordActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(KeywordActivity.this, "长度超出", Toast.LENGTH_SHORT).show();
        else Toast.makeText(KeywordActivity.this, "请先填写关键字", Toast.LENGTH_SHORT).show();
    }/*添加关键字*/

    private void refreshListView() {
        keywordList.clear();
        keywordList = loadKeywordList();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, keywordList);/*TODO 换用adapter.notifyDataSetChanged()*/
        lv.setAdapter(adapter);
    }/*刷新listview*/
}
