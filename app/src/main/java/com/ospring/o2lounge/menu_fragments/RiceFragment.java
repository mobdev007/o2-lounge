package com.ospring.o2lounge.menu_fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ospring.o2lounge.R;
import com.ospring.o2lounge.adapters.DMenuAdapter;
import com.ospring.o2lounge.others.DBAdapter;
import com.ospring.o2lounge.others.MenuItemObj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vetero on 12-12-2015.
 */
public class RiceFragment extends Fragment {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    DBAdapter myDb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.menu_fragment, null, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new DMenuAdapter(getContext(), getData(getActivity().getResources()));
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }

    private List<MenuItemObj> getData(Resources resources) {
        List<MenuItemObj> menuItemObjList = new ArrayList<>();
        String[] menu_items = resources.getStringArray(R.array.food_rice_menu);
        String[] menu_price = resources.getStringArray(R.array.food_rice_price);
        String[] cart_count = getCartCount(menu_items);
        String[] menu_items_des = resources.getStringArray(R.array.food_rice_des);
        int[] img_array = new int[]{R.drawable.fried_rice, R.drawable.schezwan_rice, R
                .drawable.indian_combo};
        int i = menu_items.length;
        for (int j = 0; j < i; j++) {
            MenuItemObj menuItemObj = new MenuItemObj();
            menuItemObj.setName(menu_items[j]);
            menuItemObj.setDes(menu_items_des[j]);
            menuItemObj.setThumbnail(img_array[j]);
            menuItemObj.setCartCount(cart_count[j]);
            menuItemObj.setPrice(Integer.parseInt(menu_price[j]));
            menuItemObjList.add(menuItemObj);
        }
        return menuItemObjList;
    }

    private void closeDb() {
        myDb.close();
    }

    private void openDb() {
        myDb = new DBAdapter(getContext());
        myDb.open();
    }

    private String[] getCartCount(String[] s) {
        openDb();
        String[] strings = new String[s.length];
        for(int i = 0; i < s.length; i++){
            strings[i] = myDb.getSingleEntry(s[i]);
        }
        closeDb();
        return strings;
    }
}