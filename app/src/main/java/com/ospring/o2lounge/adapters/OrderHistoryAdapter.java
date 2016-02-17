package com.ospring.o2lounge.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ospring.o2lounge.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Vetero on 28-12-2015.
 */
public class OrderHistoryAdapter extends CursorAdapter {

    public OrderHistoryAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_view_order_history, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvNames = (TextView) view.findViewById(R.id.tv_item_names_list_view);
        TextView tvDate = (TextView) view.findViewById(R.id.tv_order_date_list_view);
        TextView tvTotalPrce = (TextView) view.findViewById(R.id.tv_order_total_price_list_view);
        TextView tvTime = (TextView) view.findViewById(R.id.tv_order_time_list_view);

        String names = cursor.getString(cursor.getColumnIndexOrThrow("order_items"));
        String time = cursor.getString(cursor.getColumnIndexOrThrow("order_time"));
        String total = cursor.getString(cursor.getColumnIndexOrThrow("order_total"));

        try {
            DateFormat f = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            Date d = f.parse(time);
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            DateFormat timeDateFormat = new SimpleDateFormat("HH:mm:ss");

            tvNames.setText(names);
            tvDate.setText(dateFormat.format(d));
            tvTime.setText(timeDateFormat.format(d));
            tvTotalPrce.setText(total);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}