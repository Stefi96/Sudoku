package com.example.sudoku;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentRekord extends Fragment {

    private EditText lako, srednje, tesko;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rekord, container, false);

        lako = view.findViewById(R.id.txtLako);
        srednje = view.findViewById(R.id.txtSrednje);
        tesko = view.findViewById(R.id.txtTesko);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = getActivity().getSharedPreferences("sudoku", Context.MODE_PRIVATE);
        String l = preferences.getString("vremeLako","59:59");
        String s = preferences.getString("vremeSrednje","59:59");
        String t = preferences.getString("vremeTeško","59:59");

        lako.setText(l);
        srednje.setText(s);
        tesko.setText(t);

    }
}
