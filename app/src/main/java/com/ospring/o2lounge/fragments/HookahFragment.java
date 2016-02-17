package com.ospring.o2lounge.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ospring.o2lounge.R;
import com.ospring.o2lounge.adapters.NDMenuAdapter;
import com.ospring.o2lounge.others.DBAdapter;
import com.ospring.o2lounge.others.MenuItemObj;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vetero on 12-12-2015.
 */
public class HookahFragment extends Fragment implements View.OnClickListener {

    static int count = 0;
    Button button;
    AlertDialog alertDialogzz, alertDialogxx, alertDialogyy;
    String listString = "";
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    DBAdapter myDb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.hookah_pan_combo, null, false);

        button = (Button) v.findViewById(R.id.bt_pan_commbo_pan_combo_frag);
        button.setOnClickListener(this);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_hookah_frag);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new NDMenuAdapter(getContext(), getData(getActivity().getResources()));
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }

    private void closeDb() {
        myDb.close();
    }

    private void openDb() {
        myDb = new DBAdapter(getContext());
        myDb.open();
    }

    @Override
    public void onClick(View v) {
        selectpancombinations(getActivity().getResources());
    }

    private void selectpancombinations(Resources resources) {
        final String[] menu_items = resources.getStringArray(R.array.hookah_pan_combo_menu);
        final String[] pot_base_items = resources.getStringArray(R.array.hookah_pot_base_menu);
        final String[] chillam_heads = resources.getStringArray(R.array.hookah_chillam_heads_menu);
// arraylist to keep the selected items
        final ArrayList seletedItems = new ArrayList();

        AlertDialog.Builder dialogzz = new AlertDialog.Builder(getContext(), R.style
                .MyAlertDialogStyle)
                .setTitle("Select Any three")
                .setMultiChoiceItems(menu_items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            if (count < 3) {
                                seletedItems.add(menu_items[indexSelected]);
                                count++;
                            } else {
                                Toast.makeText(getContext(), "You can select maximum 3.", Toast
                                        .LENGTH_SHORT).show();
                                alertDialogzz.getListView().setItemChecked(indexSelected, false);
                            }
                        } else if (seletedItems.contains(menu_items[indexSelected])) {
                            // Else, if the item is already in the array, remove it
                            seletedItems.remove(menu_items[indexSelected]);
                            count--;
                        }
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (count > 0) {
                            for (Object s : seletedItems) {
                                listString +=
                                        s.toString() + "/";
                            }
                            dialog.dismiss();
                            count = 0;
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext
                                    (), R.style.MyAlertDialogStyle)
                                    .setTitle("Select the pot base:")
                                    .setSingleChoiceItems(pot_base_items, 0, new
                                            DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    seletedItems.add(pot_base_items[which]);
                                                    final int[] i = {300};
                                                    switch (which) {
                                                        case 0:
                                                            listString += "Base : Normal/";
                                                            i[0] = 300;
                                                            break;

                                                        case 1:
                                                            listString += "Base : Ice/";
                                                            i[0] = 350;
                                                            break;

                                                        case 2:
                                                            listString += "Base : Milk/";
                                                            i[0] = 350;
                                                            break;

                                                        case 3:
                                                            listString += "Base : Coke-Thums Up/";
                                                            i[0] = 400;
                                                            break;

                                                        case 4:
                                                            listString += "Base : Red Bull/";
                                                            i[0] = 450;
                                                            break;

                                                        case 5:
                                                            listString += "Base : Pulpy Orange/";
                                                            i[0] = 350;
                                                            break;

                                                        default:
                                                            break;
                                                    }
                                                    alertDialogxx.dismiss();
                                                    final AlertDialog.Builder alertDialog1 = new
                                                            AlertDialog.Builder
                                                            (getContext(), R.style.MyAlertDialogStyle)
                                                            .setTitle("Select Chillam " +
                                                                    "Head:")
                                                            .setSingleChoiceItems
                                                                    (chillam_heads, 0,
                                                                            new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    switch (which) {
                                                                                        case 0:
                                                                                            listString += "Head : Normal";
                                                                                            i[0] = i[0];
                                                                                            break;

                                                                                        case 1:
                                                                                            listString += "Head : Apple-Mosambi";
                                                                                            i[0] = i[0] + 200;
                                                                                            break;

                                                                                        case 2:
                                                                                            listString += "Head : Watermelon-Pineapple";
                                                                                            i[0] = i[0] + 200;
                                                                                            break;

                                                                                        case 3:
                                                                                            listString += "Head : Fresh Coconut";
                                                                                            i[0] = i[0] + 200;
                                                                                            break;

                                                                                        default:
                                                                                            break;
                                                                                    }
                                                                                    Toast
                                                                                            .makeText(getContext(), "Added to cart", Toast.LENGTH_SHORT).show();
                                                                                    seletedItems.add(chillam_heads[which]);
                                                                                    openDb();
                                                                                    myDb.insertRow(listString, i[0], 1, R.drawable.hookah);
                                                                                    closeDb();
                                                                                    ((Activity) getContext()).invalidateOptionsMenu();
                                                                                    alertDialogyy.dismiss();
                                                                                }
                                                                            })
                                                            .setCancelable(false);
                                                    alertDialogyy = alertDialog1.create();
                                                    alertDialogyy.show();
                                                }
                                            })
                                    .setCancelable(false);
                            alertDialogxx = alertDialog.create();
                            alertDialogxx.show();
                        } else {
                            Toast.makeText(getContext(), "Select at least one Flavour", Toast
                                    .LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        count = 0;
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);
        alertDialogzz = dialogzz.create();
        alertDialogzz.show();
    }

    private List<MenuItemObj> getData(Resources resources) {
        List<MenuItemObj> menuItemObjList;
        String[] menu_items = resources.getStringArray(R.array.hookah_special_combos);
        String[] cart_count = getCartCount(menu_items);
        String[] menu_price = resources.getStringArray(R.array.hookah_special_combos_prices);
        int[] img_array = new int[]{R.drawable.hookah, R.drawable.hookah, R
                .drawable.hookah};
        int i = menu_items.length;
        menuItemObjList = new ArrayList<MenuItemObj>();
        for (int j = 0; j < i; j++) {
            MenuItemObj menuItemObj = new MenuItemObj();
            menuItemObj.setName(menu_items[j]);
            menuItemObj.setThumbnail(img_array[j]);
            menuItemObj.setCartCount(cart_count[j]);
            menuItemObj.setPrice(Integer.parseInt(menu_price[j]));
            menuItemObjList.add(menuItemObj);
        }
        return menuItemObjList;
    }

    private String[] getCartCount(String[] s) {
        openDb();
        String[] strings = new String[s.length];
        for (int i = 0; i < s.length; i++) {
            strings[i] = myDb.getSingleEntry(s[i]);
        }
        closeDb();
        return strings;
    }
}