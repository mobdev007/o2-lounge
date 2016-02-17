package com.ospring.o2lounge.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ospring.o2lounge.R;
import com.ospring.o2lounge.activities.PhoneVerification;

/**
 * Created by Vetero on 21-01-2016.
 */
public class PhoneNumberVerfFragment extends Fragment implements View.OnClickListener {

    Button button;
    EditText editText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone_verification, null, false);

        button = (Button) view.findViewById(R.id.btn_request_sms);
        editText = (EditText) view.findViewById(R.id.inputMobile);

        button.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (isValidPhone(editText.getText().toString())) {
            Intent intent = new Intent(getContext(), PhoneVerification.class);
            intent.putExtra("Phone", editText.getText().toString());
            startActivity(intent);
            getActivity().finish();
        } else {
            editText.setError("Invalid Phone number");
        }
    }

    private boolean isValidPhone(String phone) {
        return phone.length() == 10 && Patterns.PHONE.matcher(phone).matches();
    }
}