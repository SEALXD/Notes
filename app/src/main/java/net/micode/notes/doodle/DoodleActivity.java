package net.micode.notes.doodle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import net.micode.notes.R;
import net.micode.notes.ui.NoteEditActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.content.ContentValues.TAG;

public class DoodleActivity extends Activity {
    private ImageView iv;
    private Bitmap baseBitmap;
    private Canvas canvas;
    private Paint paint;
    int xx, yy, yyyy, xxxx;
    int startX;
    int startY;
    public String savedPath = null;

    private String[] wideList = {"5", "10", "15", "20", "25", "30"};//单选列表
    private String[] colorList = {"红", "黄", "绿", "蓝", "青", "灰", "黑"};//单选列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doodle);

        init();

        //触摸方法
        touch();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_HOME){
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent=new Intent();
            //Log.i("doodle","============="+savedPath);
            intent.putExtra("doodlepath",savedPath);
            setResult(RESULT_OK,intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    //触摸方法
    private void touch() {
        iv.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 获取手按下时的坐标
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 获取手移动后的坐标
                        int stopX = (int) event.getX();
                        int stopY = (int) event.getY();
                        // 在开始和结束坐标间画一条线
                        xx = startX;
                        yy = startY;
                        xxxx = stopX;
                        yyyy = stopY;
                        Drows(event);
                        break;
                }
                return true;
            }
        });
    }

    private void Drows(MotionEvent event) {
        canvas.drawLine(xx, yy, xxxx, yyyy, paint);
        startX = (int) event.getX();
        startY = (int) event.getY();
        iv.setImageBitmap(baseBitmap);
    }

    private void init() {
        iv = (ImageView) findViewById(R.id.iv);
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int high = outMetrics.heightPixels;

        // 创建一张空白图片
        baseBitmap = Bitmap.createBitmap(width, high, Bitmap.Config.ARGB_8888);
        // 创建一张画布
        canvas = new Canvas(baseBitmap);
        // 画布背景为白色
        canvas.drawColor(Color.WHITE);
        // 创建画笔
        paint = new Paint();
        // 画笔颜色为红色
        paint.setColor(Color.RED);
        // 宽度5个像素
        paint.setStrokeWidth(5);
        // 先将白色背景画上
        canvas.drawBitmap(baseBitmap, new Matrix(), paint);
        iv.setImageBitmap(baseBitmap);

        //定义路径类，用于保存
        class DrawPath {
            public Path mpath;// 路径
            public Paint mpaint;// 画笔
        }
    }

    public void clickBt(View view) {
        switch (view.getId()) {
            case R.id.btSave:
                save();  //保存
                //Log.i("doodle","============"+savedPath);
                break;
            case R.id.btClean:
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(baseBitmap, new Matrix(), paint);
                iv.setImageBitmap(baseBitmap);
                break;
            case R.id.btChangeColor:
                changeColor();
                break;
            case R.id.btChangeWide:
                changeWide();
                break;
        }
    }

    private void changeColor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(colorList, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int i) {
                String color = colorList[i];
                switch (color) {
                    case "红":
                        paint.setColor(Color.RED);
                        break;
                    case "黄":
                        paint.setColor(Color.YELLOW);
                        break;
                    case "绿":
                        paint.setColor(Color.GREEN);
                        break;
                    case "蓝":
                        paint.setColor(Color.BLUE);
                        break;
                    case "青":
                        paint.setColor(Color.CYAN);
                        break;
                    case "灰":
                        paint.setColor(Color.GRAY);
                        break;
                    case "黑":
                        paint.setColor(Color.BLACK);
                        break;
                }
                dialog.dismiss();//关闭对话框
            }
        });
        /*添加对话框中取消按钮点击事件*/
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();//关闭对话框
            }
        });
        AlertDialog dialog = builder.create();//获取dialog
        dialog.show();//显示对话框  
    }

    private void changeWide() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(wideList, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int i) {
                String wide = wideList[i];
                paint.setStrokeWidth(Integer.parseInt(wide));
                dialog.dismiss();//关闭对话框
            }
        });
        /*添加对话框中取消按钮点击事件*/
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();//关闭对话框
            }
        });
        AlertDialog dialog = builder.create();//获取dialog
        dialog.show();//显示对话框  
    }

    public String save() {
        /*******************保存图片到系统文件夹****************/
        String picName = System.currentTimeMillis()+ ".jpg";
        String newPath = getExternalFilesDir(null) + "/" + picName;
        Log.i(TAG,"directory_pictures="+newPath);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(newPath);
            baseBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            //Toast.makeText(this, "保存图片成功", Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "保存图片失败", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "保存图片失败", Toast.LENGTH_LONG).show();
        }

        /****************保存图片到相册*****************/
        String galleryPath= Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                +File.separator+"Camera"+File.separator;
        // 声明文件对象
        File file = null;
        // 声明输出流
        FileOutputStream outStream = null;
        try {
            // 如果有目标文件，直接获得文件对象，否则创建一个以filename为名称的文件
            file = new File(galleryPath, picName+ ".jpg");

            // 获得文件相对路径
            String fileName = file.toString();
            // 获得输出流，如果文件中有内容，追加内容
            outStream = new FileOutputStream(fileName);
            if (null != outStream) {
                baseBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            }

        } catch (Exception e) {
            e.getStackTrace();
        }finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //通知相册更新
        MediaStore.Images.Media.insertImage(this.getContentResolver(), baseBitmap, file.getAbsolutePath(), null);
        Toast.makeText(this, "保存成功", Toast.LENGTH_LONG).show();


        /********保存成功后如果不扫描SD卡，保存的文件不会显示********/
        //  Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED); //这是刷新SD卡
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);  // 这是刷新单个文件
        Uri uri = Uri.fromFile(new File(newPath));
        intent.setData(uri);
        sendBroadcast(intent);
        Log.i(TAG,"扫描完成"+newPath);
       // Toast.makeText(this, "扫描完成", Toast.LENGTH_LONG).show();

        savedPath = newPath;
        return savedPath;  //存储涂鸦路径
    }

}
