package com.example.sudoku;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {


    FragmentIgra igra;
    FragmentRekord rekord;
    FragmentManager manager;
    BottomNavigationView meni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        igra = new FragmentIgra();
        rekord = new FragmentRekord();
        manager = getSupportFragmentManager();

        manager.beginTransaction().add(R.id.frame, igra, "Igra").commit();
        manager.beginTransaction().add(R.id.frame, rekord, "Rekord").hide(rekord).commit();

        setMeni();
    }

    private void setMeni(){
        meni = findViewById(R.id.bottomMenu);
        meni.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.igra:
                        igra.onResume();
                        manager.beginTransaction().show(igra).hide(rekord).commit();
                        return true;

                    case R.id.score:
                        igra.onPause();
                        rekord.onResume();
                        manager.beginTransaction().show(rekord).hide(igra).commit();
                        return true;

                }
                return false;
            }
        });
    }

    @Override
    protected void onPause () {
        super.onPause();

        if (igra != null)
            igra.saveTable();

    }

}
