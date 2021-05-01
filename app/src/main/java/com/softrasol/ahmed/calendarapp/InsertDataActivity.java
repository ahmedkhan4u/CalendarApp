package com.softrasol.ahmed.calendarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class InsertDataActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private DatabaseHelper databaseHelper;
    private Spinner mSpinner;
    private EditText mTxtTitle, mTxtDescription;
    private Button mBtnSave;
    private String date, priority, title, description;

    private String[] items = {"Choose Priority","1","2","3"};
    private String[] itemsData = {"Choose Priority","High","Medium","Low"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_data);

        databaseHelper = new DatabaseHelper(this);
        calendarView = findViewById(R.id.calendarView);
        date = calendarView.getDate()+"";
        mSpinner = findViewById(R.id.spinner);
        mTxtTitle = findViewById(R.id.txtTitle);
        mTxtDescription = findViewById(R.id.txtDescription);
        mBtnSave = findViewById(R.id.btnSave);

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDataToSqliteDB();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemsData);
        mSpinner.setAdapter(adapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                priority = items[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                Calendar c = Calendar.getInstance();
                c.set(i, i1, i2);
                date = c.getTimeInMillis() + "";
//                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
//                Date date1 = new Date(Long.parseLong(date));
//                String mDate = dateFormat.format(date1);
//                showMsg(mDate);
            }
        });


    }

    private void saveDataToSqliteDB() {

        title = mTxtTitle.getText().toString().trim();
        description = mTxtDescription.getText().toString().trim();

        if (priority.equalsIgnoreCase("Choose Priority")){
            showMsg("Choose Priority");
            return;
        }

        if (title.isEmpty()){
            mTxtTitle.setError("Required");
            mTxtTitle.requestFocus();
            return;
        }

        if (description.isEmpty()){
            mTxtDescription.setError("Required");
            mTxtDescription.requestFocus();
            return;
        }

        boolean result = databaseHelper.insertData(title, description, priority, date);
        if (result == true){
            showMsg("Data Save Successfully");
            startActivity(new Intent(InsertDataActivity.this, MainActivity.class));
            finish();
        }else {
            showMsg("Error Occurred While Saving Data To Database");
        }

    }

    private void showMsg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}