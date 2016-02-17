package com.ospring.o2lounge.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ospring.o2lounge.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by Vetero on 11-01-2016.
 */
public class OfferFragment extends Fragment {

    TextView textView;
    ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.offer_fragment, null, false);

        textView = (TextView) view.findViewById(R.id.textView_offer_name);
        imageView = (ImageView) view.findViewById(R.id.imageView_offer_bg);

        textView.setText(getArguments().getString("offer"));
        Picasso.with(getContext())
                .load(getArguments().getString("image"))
                .into(imageView);
//        new DownloadImageTask(imageView).execute(getArguments().getString("image"));

        return view;
    }

    public static OfferFragment newInstance(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            String offer = jsonObject.getString("offer");
            String image = jsonObject.getString("image");
            Bundle args = new Bundle();

            args.putString("image", image);
            args.putString("offer", offer);
            OfferFragment fragment = new OfferFragment();
            fragment.setArguments(args);
            return fragment;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}