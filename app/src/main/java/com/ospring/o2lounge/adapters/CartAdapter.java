package com.ospring.o2lounge.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ospring.o2lounge.R;
import com.ospring.o2lounge.others.DBAdapter;
import com.ospring.o2lounge.others.MenuItemObj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vetero on 19-12-2015.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    public static final String KEY_NAME = "item_name";
    public static final String KEY_PRICE = "item_price";
    public static final String KEY_COUNT = "item_count";
    public static final String KEY_IMG = "item_image";
    public static final int COL_NAME = 1;
    public static final int COL_PRICE = 2;
    public static final int COL_COUNT = 3;
    public static final int COL_IMG = 4;
    Context context;
    DBAdapter myDb;
    List<MenuItemObj> menuItemObjList;
    LayoutInflater inflater;
    String s;

    public CartAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        openDb();
        Cursor cursor = myDb.getAllRows();
        menuItemObjList = new ArrayList<MenuItemObj>();
        if (cursor.moveToFirst()) {
            do {
                MenuItemObj menuItemObj = new MenuItemObj();
                menuItemObj.setName(cursor.getString(COL_NAME));
                int s = Integer.parseInt(cursor.getString(COL_PRICE));
                int i = Integer.parseInt(cursor.getString(COL_COUNT));
                menuItemObj.setPrice(s * i);
                menuItemObj.setThumbnail(cursor.getInt(COL_IMG));
                menuItemObjList.add(menuItemObj);
            } while (cursor.moveToNext());
        }
        closeDb();
    }

    private void closeDb() {
        myDb.close();
    }

    private void openDb() {
        myDb = new DBAdapter(context);
        myDb.open();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.card_view_cart, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MenuItemObj nature = menuItemObjList.get(position);
        holder.tvItemName.setText(nature.getName());
        holder.tvItemMRP.setText(String.valueOf(nature.getPrice()));
        holder.relativeLayout.setBackgroundResource(nature.getThumbnail());
    }

    @Override
    public int getItemCount() {
        return menuItemObjList.size();
    }

    private void removeProduct(int adapterPosition) {
        MenuItemObj menuItemObj = menuItemObjList.get(adapterPosition);
        s = menuItemObj.getName();
        openDb();
        boolean b = myDb.removeProduct(s);
        closeDb();
        menuItemObjList.remove(adapterPosition);
        if (b) {
            Toast.makeText(context, s + " deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Something wrong happened!", Toast.LENGTH_SHORT).show();
        }
        setGrandTotal(getGrandTotal());
    }

    public String getGrandTotal() {
        openDb();
        String s = context.getString(R.string.Rs) + myDb.getGrandTotal();
        closeDb();
        return s;
    }

    private void setGrandTotal(String grandTotal) {
        TextView textView = (TextView) ((Activity)context).findViewById(R.id.tv_cart_grand_total);
        textView.setText(grandTotal);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvItemName;
        public TextView tvItemMRP;
        public RelativeLayout relativeLayout;
        public Button button;

        public ViewHolder(View itemView) {
            super(itemView);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rl_cart_bgImg);
            tvItemName = (TextView) itemView.findViewById(R.id.tv_cart_item_name);
            tvItemMRP = (TextView) itemView.findViewById(R.id.tv_cart_item_price);
            button = (Button) itemView.findViewById(R.id.bt_cart_remove_item);

            button.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            removeProduct(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());
        }
    }
}