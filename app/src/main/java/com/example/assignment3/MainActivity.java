package com.example.assignment3;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;

    Bitmap image = null;

    ScrollView scrollView;
    EditText tags;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = openOrCreateDatabase("MyDatabase", Context.MODE_PRIVATE, null);
        db.execSQL("DROP TABLE IF EXISTS Images");
        db.execSQL("DROP TABLE IF EXISTS Tags");
        db.execSQL("CREATE TABLE Images( Image BLOB )");
        db.execSQL("CREATE TABLE Tags( Tag Text, ImageId INT )");

        scrollView = findViewById(R.id.scrollView);
        tags = findViewById(R.id.tags);
    }

    public void buttonClicked (View v) {
        int id = v.getId();

        // Capture is clicked
        if (id == R.id.captureButton) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 1);
        }

        // Save is clicked
        if (id == R.id.saveButton && image != null) {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, bos);
            ContentValues contentValues = new ContentValues();
            contentValues.put("Image", bos.toByteArray());

            long entryId = db.insert("Images", null, contentValues);

            String[] tagList = tags.getText().toString().split(";");
            for (String t : tagList) {
                contentValues = new ContentValues();
                contentValues.put("Tag", t);
                contentValues.put("ImageId", entryId);
                db.insert("Tags", null, contentValues);
            }
            printDbContents();
        }

        // Load is clicked
        if (id == R.id.loadButton) {
            String[] tagList = tags.getText().toString().split(";");

            // Constructs the WHERE clause for the query
            String whereQuery = "";
            for (int i = 0; i < tagList.length; i++) {
                whereQuery += String.format("Tag == \"%s\"", tagList[i]);
                if (i < tagList.length - 1) whereQuery += " or ";
            }

            Cursor c = db.rawQuery(String.format("SELECT ImageId from Tags WHERE %s", whereQuery), null);
            //Log.v("MyTag Num Results", ""+c.getCount());
            c.moveToFirst();

            ArrayList<Integer> results = new ArrayList<>();

            // Gets all unique ImageIds matching the tags
            for(int i = 0; i < c.getCount(); i++){
                for(int j = 0; j < c.getColumnCount(); j++) {
                    if (!results.contains(c.getInt(j))) results.add(c.getInt(j));
                }
                c.moveToNext();
            }
            //Log.v("MyTag Num Results Part2", ""+ results.size());

            

        }

    }

    public void printDbContents() {
        Cursor c = db.rawQuery("SELECT * from Images", null);
        c.moveToFirst();
        for(int i = 0; i < c.getCount(); i++){

            for(int j = 0; j < c.getColumnCount(); j++) {
                Log.v("MyTag", c.getBlob(j).toString());
            }
            c.moveToNext();

        }

        c = db.rawQuery("SELECT * from Tags", null);
        c.moveToFirst();
        for(int i = 0; i < c.getCount(); i++){
            String row = "";
            for(int j = 0; j < c.getColumnCount(); j++) {
                row += "\t" + c.getString(j);
            }
            Log.v("MyTag", row);
            c.moveToNext();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        image = (Bitmap) data.getExtras().get("data");
    }






}