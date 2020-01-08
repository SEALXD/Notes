package net.micode.notes.todo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.SimpleCursorAdapter;

import net.micode.notes.R;

public class Modify extends Activity implements View.OnClickListener {
    private EditText et_show;
    private Button updateButton, deleteButton, backButton;
    private net.micode.notes.todo.DBHelper dbHelper;
    private Cursor cursor;
    private int id;

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.modify);
        init();
    }

    private void init() {
        et_show = (EditText) findViewById(R.id.et_modify);
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        id = intent.getIntExtra("id", id);
        et_show.setText(data);
        dbHelper = new net.micode.notes.todo.DBHelper(this);
        cursor = dbHelper.select();
        updateButton = (Button) findViewById(R.id.btn_update);
        deleteButton = (Button) findViewById(R.id.btn_delete);
        backButton = (Button) findViewById(R.id.btn_back);
        updateButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    private class RefreshList extends AsyncTask<Void, Void ,Cursor> {
        //步骤1.1：在后台线程中从数据库读取，返回新的游标newCursor
        protected Cursor doInBackground(Void... params) {
            Cursor newCursor = dbHelper.select();
            //Cursor newCursor = database.rawQuery("SELECT _id,Name,Weight from mytable ORDER BY Weight", null);
            return newCursor;
        }

        //步骤1.2：线程最后执行步骤，更换adapter的游标，并将原游标关闭，释放资源 
        protected void onPostExecute(Cursor newCursor) {
            SimpleCursorAdapter adapter=new SimpleCursorAdapter(Modify.this,R.layout.modify,cursor, new String[]{"content"},new int[]{R.id.content},0);
            adapter.changeCursor(newCursor);
            cursor.close();
            cursor = newCursor;
        }
    }

    public void updateData() {
        if (id == 0) {
            Toast.makeText(Modify.this, "内容不能为空", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.update(id, et_show.getText().toString());
            new RefreshList().execute();
            id = 0;
        }
    }
    public void deleteData(){
        if (id==0){
            Toast.makeText(Modify.this, "内容不能为空", Toast.LENGTH_SHORT).show();
        }else{
            dbHelper.delete(id);

            id=0;
        }
    }
    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btn_update:
                updateData();
                Intent intent1 = new Intent(this, net.micode.notes.todo.TodoListActivity.class);
                startActivity(intent1);

                finish();
                break;

            case R.id.btn_delete:
                deleteData();
                Intent intent = new Intent(this, net.micode.notes.todo.TodoListActivity.class);
                startActivity(intent);

                finish();
                break;
            case R.id.btn_back:
                Intent intent2 = new Intent(this, net.micode.notes.todo.TodoListActivity.class);
                startActivity(intent2);

                finish();
                break;
        }

    }
}
