package com.ospring.o2lounge.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ospring.o2lounge.R;
import com.ospring.o2lounge.adapters.OrderHistoryAdapter;
import com.ospring.o2lounge.others.OrderHistoryDb;

/**
 * Created by Vetero on 28-12-2015.
 */
public class OrderHistoryFragment extends Fragment {

    ListView listView;
    OrderHistoryDb myOrderHistoryDb;
    OrderHistoryAdapter orderHistoryAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, null, false);
        listView = (ListView) view.findViewById(R.id.lv_order_history);

        myOrderHistoryDb = new OrderHistoryDb(getContext());
        myOrderHistoryDb.open();
        Cursor cursor = myOrderHistoryDb.getAllRows();
        myOrderHistoryDb.close();

        orderHistoryAdapter = new OrderHistoryAdapter(getContext(), cursor, 0);
        listView.setAdapter(orderHistoryAdapter);

        return view;
    }
}