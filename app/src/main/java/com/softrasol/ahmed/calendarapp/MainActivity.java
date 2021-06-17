package com.softrasol.ahmed.calendarapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper databaseHelper;
    private List<DataModel> list;
    private RecyclerView mRecyclerView;
    private com.github.sundeepk.compactcalendarview.CompactCalendarView calendarView;
    private String date;
    private Date currentDate;
    private FloatingActionButton fab;
    private TextView mCalendarTitle;
    private RelativeLayout mTxtEvents;//lklkl
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseHelper = new DatabaseHelper(this);
        mRecyclerView = findViewById(R.id.recyclerView);
        calendarView = findViewById(R.id.calendarView);
        mCalendarTitle = findViewById(R.id.calenderTitle);
        mTxtEvents = findViewById(R.id.txtNoEventsFound);
        fab = findViewById(R.id.fab);
        currentDate = new Date();
        date = currentDate.getTime()+"";
        calendarView.setUseThreeLetterAbbreviation(true);
        DateFormat format = new SimpleDateFormat("MMM");
        mCalendarTitle.setText(format.format(calendarView.getFirstDayOfCurrentMonth()));
        calendarView.shouldSelectFirstDayOfMonthOnScroll(false);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), InsertDataActivity.class));
            }
        });

        list = new ArrayList<>();

        Cursor res = databaseHelper.getAllData();

        if (res.getCount() == 0){
            showMsg("No events added yet.");
            return;
        }else {
            while (res.moveToNext()){
                DataModel model = new DataModel(
                        res.getString(0),
                        res.getString(1),
                        res.getString(2),
                        res.getString(3),
                        res.getString(4)
                );
                list.add(model);
            }

            List<DataModel> temp = new ArrayList<>();
            for (int i=0; i< list.size(); i++){
                for (int j=i+1; j<list.size(); j++){
                    temp.clear();
                    if (Long.parseLong(list.get(i).getDate()) > Long.parseLong(list.get(j).getDate())){
                        temp.add(list.get(i));
                        list.set(i, list.get(j));
                        list.set(j, temp.get(0));
                    }
                }

            }

            List<DataModel> temp2 = new ArrayList<>();
            for (int i=0; i< list.size(); i++){
                for (int j=i+1; j<list.size(); j++){
                    temp2.clear();

                    Date d1 = new Date(Long.parseLong(list.get(i).getDate()));
                    Date d2 = new Date(Long.parseLong(list.get(j).getDate()));
                    String date1 = d1.toGMTString();
                    String date2 = d2.toGMTString();
                    Log.d("dxd ", date1 + " = "+ date2);

                    if(date1.substring(0,2).equalsIgnoreCase(date2.substring(0,2))
                            && Integer.parseInt(list.get(i).getPriority()) > Integer.parseInt(list.get(j).getPriority())
                    ){
                        temp2.add(list.get(i));
                        list.set(i, list.get(j));
                        list.set(j, temp2.get(0));
                    }
                }

            }

            filterDate();
            deleteDatesPassedData();

        }

        onDateSelectedChanged();


    }

    private void sortData() {

    }

    private void deleteDatesPassedData() {
        for (DataModel model : list){
            if (Long.parseLong(model.getDate()) < Long.parseLong(date)){
                Log.d("dxdiag", "Delete : " + model.getTitle());
                databaseHelper.deleteData(model.getId());
            }
        }
    }

    private void filterDate() {

        List<DataModel> dataModelList = new ArrayList<>();
        Date mDate = new Date(Long.parseLong(date));
        Calendar calendar = new GregorianCalendar(/* remember about timezone! */);
        calendar.setTime(mDate);
        calendar.add(Calendar.DATE, 30);
        mDate = calendar.getTime();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
        String d = dateFormat.format(mDate);
        long startDate = Long.parseLong(date);
        long endDate = mDate.getTime();

        for (DataModel model : list){
            long i = Long.parseLong(model.getDate());
            if (i>=startDate && i<=endDate){
                dataModelList.add(model);
                Calendar cal = Calendar.getInstance();
                Event event = new Event(Color.WHITE, Long.parseLong(model.getDate()));
                calendarView.addEvent(event);
                cal.setTimeInMillis(Long.parseLong(model.getDate()));
                Log.d("dxdiag", model.getTitle());
            }
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DataAdapter adapter = new DataAdapter(dataModelList, this);
        mRecyclerView.setAdapter(adapter);
        if (adapter.getItemCount() == 0){
            mTxtEvents.setVisibility(View.VISIBLE);
        }else {
            mTxtEvents.setVisibility(View.GONE);
        }

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Scrolling up
                    fab.setVisibility(View.GONE);
                } else {
                    // Scrolling down
                    fab.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    private void onDateSelectedChanged() {
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                //showMsg(dateClicked.getTime()+"");
                date = dateClicked.getTime() + "";
                filterDate();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                date = firstDayOfNewMonth.getTime() + "";
                filterDate();
                DateFormat format = new SimpleDateFormat("MMM");
                mCalendarTitle.setText(format.format(firstDayOfNewMonth));
            }
        });
    }

    private void showMsg(String msg){
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
    }
}