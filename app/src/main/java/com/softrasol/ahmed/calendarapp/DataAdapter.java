package com.softrasol.ahmed.calendarapp;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private List<DataModel> list;
    private Context context;

    public DataAdapter(List<DataModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.data_item_list, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel model = list.get(position);
        holder.mTxtTitle.setText(model.getTitle());
        holder.mTxtDescription.setText(model.getDescription());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
        Date date = new Date(Long.parseLong(model.getDate()));
        String mDate = dateFormat.format(date);
        holder.mTxtDate.setText(mDate+"");



        if (model.getPriority().equals("1")){
            holder.cardView.setCardBackgroundColor(Color.RED);
        }
        if (model.getPriority().equals("2")){
            holder.cardView.setCardBackgroundColor(Color.parseColor("#1094F6"));
        }
        if (model.getPriority().equals("3")){
            holder.cardView.setCardBackgroundColor(Color.parseColor("#F5A601"));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View mView;
        private TextView mTxtTitle, mTxtDescription, mTxtDate;
        private CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            cardView = mView.findViewById(R.id.cardView);
            mTxtTitle = mView.findViewById(R.id.txtTitle);
            mTxtDescription = mView.findViewById(R.id.txtDescription);
            mTxtDate = mView.findViewById(R.id.txtDate);
        }
    }
}
