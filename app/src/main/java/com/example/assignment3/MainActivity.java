package com.example.assignment3;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;

    Context context;

    EditText tags;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = openOrCreateDatabase("MyDatabase", Context.MODE_PRIVATE, null);
        db.execSQL("DROP TABLE IF EXISTS Photos");
        db.execSQL("DROP TABLE IF EXISTS Tags");
        db.execSQL("CREATE TABLE Photos( ID INT, Photo TEXT, Size INT )");
        db.execSQL("CREATE TABLE Tags( ID INT, Tag TEXT )");
        seadDb();

        tags = findViewById(R.id.tags);

        context = getApplicationContext();

        printMyTable("Photos");
        printMyTable("Tags");
    }

    public void seadDb() {
        db.execSQL("INSERT INTO Photos VALUES (1, 'p1.jpeg', 100);");
        db.execSQL("INSERT INTO Photos VALUES (2, 'p2.jpeg', 200);");
        db.execSQL("INSERT INTO Photos VALUES (3, 'p3.jpeg', 300);");
        db.execSQL("INSERT INTO Photos VALUES (4, 'p4.jpeg', 200);");
        db.execSQL("INSERT INTO Tags VALUES (1, 'Old Well');");
        db.execSQL("INSERT INTO Tags VALUES (1, 'UNC');");
        db.execSQL("INSERT INTO Tags VALUES (2, 'Sitterson');");
        db.execSQL("INSERT INTO Tags VALUES (2, 'Building');");
        db.execSQL("INSERT INTO Tags VALUES (3, 'Sky');");
        db.execSQL("INSERT INTO Tags VALUES (4, 'Dining');");
        db.execSQL("INSERT INTO Tags VALUES (1, 'Building');");
    }

    public void printMyTable(String tableName) {
        Cursor c = db.rawQuery(String.format("SELECT * FROM %s", tableName), null);

        c.moveToFirst();

        ArrayList<Integer> results = new ArrayList<>();

        // Gets all unique ImageIds matching the tags
        for(int i = 0; i < c.getCount(); i++){
            String row = "";
            for(int j = 0; j < c.getColumnCount(); j++) {
                row += c.getString(j) + "\t";
            }
            Log.v("MyTag", row);
            c.moveToNext();
        }
    }

    public void buttonClicked (View v) {
        int id = v.getId();

        // Load is clicked
        if (id == R.id.loadButton) {
            String[] tagList = tags.getText().toString().split(";");

            // Constructs the WHERE clause for the query
            String whereQuery = "";
            for (int i = 0; i < tagList.length; i++) {
                whereQuery += String.format("Tag == \"%s\"", tagList[i]);
                if (i < tagList.length - 1) whereQuery += " or ";
            }

            Cursor c = db.rawQuery(String.format("SELECT ID from Tags WHERE %s", whereQuery), null);
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

            whereQuery = "";
            for (int i = 0; i < results.size(); i++) {
                whereQuery += String.format("ID == \"%s\"", results.get(i));
                if (i < results.size() - 1) whereQuery += " or ";
            }
            c = db.rawQuery(String.format("SELECT Photo from Photos WHERE %s", whereQuery), null);
            c.moveToFirst();
            //Log.v("MyTag Num Images", ""+c.getCount();
            ArrayList<String> images = new ArrayList<>();
            for(int i = 0; i < c.getCount(); i++){
                for(int j = 0; j < c.getColumnCount(); j++) {
                    images.add(c.getString(j));
                }
                c.moveToNext();
            }

            String photoResults = "";
            for (String i : images) {
                photoResults += i + "\t";
            }
            Log.v("MyTag", photoResults);
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






}