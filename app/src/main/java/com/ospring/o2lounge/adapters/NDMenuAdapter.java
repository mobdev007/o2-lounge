package com.ospring.o2lounge.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ospring.o2lounge.R;
import com.ospring.o2lounge.activities.MainActivity;
import com.ospring.o2lounge.others.DBAdapter;
import com.ospring.o2lounge.others.MenuItemObj;

import java.util.List;

/**
 * Created by Vetero on 17-12-2015.
 */
public class NDMenuAdapter extends RecyclerView.Adapter<NDMenuAdapter.ViewHolder> {

    List<MenuItemObj> menuItemObjList;
    LayoutInflater inflater;
    DBAdapter myDb;
    Context c;

    public NDMenuAdapter(Context context, List<MenuItemObj> menuItemObjList) {
        this.menuItemObjList = menuItemObjList;
        this.inflater = LayoutInflater.from(context);
        this.c = context;
    }

    public void addToCart(int position) {
        MenuItemObj menuItemObj = menuItemObjList.get(position);
        int i = Integer.parseInt(menuItemObj.getCartCount());
        String itemName = menuItemObj.getName();
        int itemPrice = menuItemObj.getPrice();
        int itemImage = menuItemObj.getThumbnail();
        openDB();
        if (i > 0) {
            myDb.updateRow(itemName, String.valueOf(i + 1));
        } else {
            myDb.insertRow(itemName, itemPrice, 1, itemImage);
        }
        closeDb();
        menuItemObj.setCartCount(String.valueOf(i + 1));
        upDateCartCount();
        notifyItemChanged(position);
    }

    private void upDateCartCount() {
        ((Activity) c).invalidateOptionsMenu();
    }

    private void closeDb() {
        myDb.close();
    }

    private void openDB() {
        myDb = new DBAdapter(c);
        myDb.open();
    }

    public void removeFromCart(int position) {
        MenuItemObj menuItemObj = menuItemObjList.get(position);
        int i = Integer.parseInt(menuItemObj.getCartCount());
        if (i > 0) {
            menuItemObj.setCartCount(String.valueOf(i - 1));
            String itemName = menuItemObj.getName();
            if (i > 1) {
                openDB();
                myDb.updateRow(itemName, String.valueOf(i - 1));
                upDateCartCount();
                closeDb();
            } else {
                openDB();
                myDb.deleteProduct(itemName);
                upDateCartCount();
                closeDb();
            }
            notifyItemChanged(position);
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = inflater.inflate(R.layout.card_view_non_description, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        MenuItemObj nature = menuItemObjList.get(i);
        viewHolder.tvItemName.setText(nature.getName());
        viewHolder.imgThumbnail.setImageResource(nature.getThumbnail());
        viewHolder.tvItemCount.setText(nature.getCartCount());
        String s = c.getString(R.string.Rs) + String.valueOf(nature.getPrice());
        viewHolder.tvItemMRP.setText(s);
    }

    @Override
    public int getItemCount() {
        return menuItemObjList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imgThumbnail;
        public TextView tvItemName;
        public TextView tvItemMRP;
        public TextView tvItemCount;
        public Button bt_add, bt_remove;

        public ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.nd_img_thumbnail);
            tvItemName = (TextView) itemView.findViewById(R.id.nd_tv_name);
            tvItemMRP = (TextView) itemView.findViewById(R.id.nd_tv_mrp);
            bt_add = (Button) itemView.findViewById(R.id.nd_button_add_cart);
            bt_remove = (Button) itemView.findViewById(R.id.nd_button_remove_cart);
            tvItemCount = (TextView) itemView.findViewById(R.id.nd_tv_item_count);

            bt_add.setOnClickListener(this);
            bt_remove.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.nd_button_add_cart:
                    addToCart(getAdapterPosition());
                    break;

                case R.id.nd_button_remove_cart:
                    removeFromCart(getAdapterPosition());
                    break;
            }
        }
    }
}