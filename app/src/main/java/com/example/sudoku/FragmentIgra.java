package com.example.sudoku;

import android.app.AlertDialog;
import android.content.Context;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class FragmentIgra extends Fragment implements View.OnClickListener {

    private SharedPreferences preferences;
    private TextView textTime, textMod;
    private CountDownTimer timer;
    private Button[][] tabla = new Button[9][9];
    private RelativeLayout layout;//tabla
    private Button selected = null;
    private ProgressBar bar;
    private Spinner difficulty;
    private String[] levels = new String[]{"Lako", "Srednje", "Teško"};
    private int level = 1;
    private Baza baza;
    private boolean win = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_igra, container, false);

        preferences = this.getActivity().getSharedPreferences("sudoku", Context.MODE_PRIVATE);
        textTime = view.findViewById(R.id.textTime);
        textMod = view.findViewById(R.id.txtMod);
        baza = new Baza(view.getContext());
        bar = view.findViewById(R.id.progressBar);
        bar.setVisibility(View.INVISIBLE);
        difficulty = view.findViewById(R.id.difficulty);
        layout = view.findViewById(R.id.tablaLayout);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, levels);
        difficulty.setAdapter(adapter);

        layout.post(new Runnable() {//nakon sto je layout postavljen
            public void run() {
                int width = layout.getWidth();
                postavi(width);
                loadTable();//ucitava zapamcenu igru ako postoji
            }
        });

        Button nova = view.findViewById(R.id.butNova);
        nova.setOnClickListener(this);
        Button obrisi = view.findViewById(R.id.butClear);
        obrisi.setOnClickListener(this);
        Button provera = view.findViewById(R.id.butProvera);
        provera.setOnClickListener(this);

        for(int i=1; i<=9; i++) {
            Log.e("numeric",i+"");
            Button numeric = view.findViewWithTag(""+i);
            numeric.setOnClickListener(this);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.butNova)
            novaIgra();

        if(v.getId() == R.id.butClear && selected != null)
            selected.setText("");

        if(v.getId() == R.id.butProvera && !win)
            provera();

        if(v.getTag() != null && selected != null)//numericki dugmici
            selected.setText(((Button) v).getText().toString());
    }

    public void postavi(int width) {//generise tablu

        int sirina = width / 9;

        for (int x = 0; x < 9; x++)
            for (int y = 0; y < 9; y++) {
                Button but = new Button(layout.getContext());
                tabla[x][y] = but;
                layout.addView(but);
                ViewGroup.LayoutParams param = but.getLayoutParams();
                param.width = sirina - 5;
                param.height = sirina - 5;
                but.setTextSize(sirina / 4);
                but.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                but.setPadding(0, 0, 0, 0);
                but.setBackgroundColor(Color.WHITE);


                //pravi razmake na svaka tri polja
                if (x < 3)
                    but.setX(x * sirina);
                if (x >= 3 && x < 6)
                    but.setX(x * sirina + 5);
                if (x >= 6)
                    but.setX(x * sirina + 10);


                if (y < 3)
                    but.setY(y * sirina);
                if (y >= 3 && y < 6)
                    but.setY(y * sirina + 5);
                if (y >= 6)
                    but.setY(y * sirina + 10);


                but.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (selected != null)
                            selected.setBackgroundColor(Color.WHITE);

                        selected = (Button) v;
                        selected.setBackgroundColor(Color.YELLOW);
                    }
                });

            }
    }

    public void novaIgra() {

        win = false;
        level = difficulty.getSelectedItemPosition()+1;
        textMod.setText((CharSequence) difficulty.getSelectedItem());
        bar.setVisibility(View.VISIBLE);
        GetSudoku gs = new GetSudoku();
        gs.execute();

    }

    public class GetSudoku extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {
            try {

                URL url = new URL("http://www.cs.utep.edu/cheon/ws/sudoku/new/?size=9?level=" + level);
                HttpURLConnection konekcija = (HttpURLConnection) url.openConnection();
                konekcija.setRequestMethod("GET");

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(konekcija.getInputStream()));

                if (konekcija.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    //String response = "{\"response\":true,\"size\":\"9?level=3\",\"squares\":[{\"x\":0,\"y\":3,\"value\":1},{\"x\":0,\"y\":4,\"value\":2},{\"x\":0,\"y\":5,\"value\":5},{\"x\":0,\"y\":7,\"value\":7},{\"x\":0,\"y\":8,\"value\":9},{\"x\":1,\"y\":1,\"value\":7},{\"x\":1,\"y\":2,\"value\":1},{\"x\":1,\"y\":5,\"value\":4},{\"x\":1,\"y\":6,\"value\":5},{\"x\":1,\"y\":7,\"value\":8},{\"x\":2,\"y\":0,\"value\":5},{\"x\":2,\"y\":1,\"value\":2},{\"x\":2,\"y\":5,\"value\":7},{\"x\":2,\"y\":6,\"value\":1},{\"x\":3,\"y\":0,\"value\":3},{\"x\":3,\"y\":3,\"value\":7},{\"x\":3,\"y\":6,\"value\":2},{\"x\":3,\"y\":7,\"value\":9},{\"x\":3,\"y\":8,\"value\":6},{\"x\":4,\"y\":1,\"value\":9},{\"x\":4,\"y\":3,\"value\":6},{\"x\":4,\"y\":4,\"value\":4},{\"x\":4,\"y\":5,\"value\":1},{\"x\":5,\"y\":0,\"value\":7},{\"x\":5,\"y\":1,\"value\":5},{\"x\":5,\"y\":2,\"value\":6},{\"x\":5,\"y\":8,\"value\":8},{\"x\":6,\"y\":0,\"value\":2},{\"x\":6,\"y\":7,\"value\":3},{\"x\":6,\"y\":8,\"value\":7},{\"x\":7,\"y\":1,\"value\":8},{\"x\":7,\"y\":2,\"value\":7},{\"x\":7,\"y\":3,\"value\":4},{\"x\":7,\"y\":6,\"value\":6},{\"x\":7,\"y\":7,\"value\":2},{\"x\":8,\"y\":2,\"value\":5},{\"x\":8,\"y\":3,\"value\":8},{\"x\":8,\"y\":4,\"value\":7},{\"x\":8,\"y\":5,\"value\":2},{\"x\":8,\"y\":6,\"value\":9}]}";
                    String response = bufferedReader.readLine();
                    JSONObject object = new JSONObject(response);

                    publishProgress(100);
                    return object;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            if (values[0] == 100)
                bar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            super.onPostExecute(object);

            clearTable();

            try {
                JSONArray niz = new JSONArray(object.getString("squares"));

                for (int i = 0; i < niz.length(); i++) {
                    JSONObject polje = niz.getJSONObject(i);
                    int x = polje.getInt("x");
                    int y = polje.getInt("y");
                    int value = polje.getInt("value");

                    tabla[x][y].setText(value + "");
                    tabla[x][y].setClickable(false);
                    tabla[x][y].setBackgroundColor(Color.rgb(224, 224, 224));//siva boja

                    setTimer(0);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


    public void provera() {
        removeError();

        for (int x = 0; x < 9; x++)//proverava da li ima praznih polja
            for (int y = 0; y < 9; y++) {
                if (tabla[x][y].getText().toString().equals("")) {
                    Toast.makeText(this.getActivity(), "Nisu popunjena sva polja", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        for (int x = 0; x < 9; x++) //proverava kolone
            for (int y = 0; y < 8; y++) {
                String broj = tabla[x][y].getText().toString();

                for (int i = y + 1; i < 9; i++)
                    if (tabla[x][i].getText().toString().equals(broj)) {
                        tabla[x][i].setTextColor(Color.RED);
                        tabla[x][y].setTextColor(Color.RED);
                        return;
                    }
            }


        for (int y = 0; y < 9; y++) //proverava redove
            for (int x = 0; x < 8; x++) {
                String broj = tabla[x][y].getText().toString();

                for (int i = x + 1; i < 9; i++)
                    if (tabla[i][y].getText().toString().equals(broj)) {
                        tabla[x][i].setTextColor(Color.RED);
                        tabla[x][y].setTextColor(Color.RED);
                        return;
                    }
            }


        //provera kvadrata 3x3
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)//prolazak kroz 9 kvadrata
            {
                for (int x = 0; x < 3; x++)
                    for (int y = 0; y < 3; y++)//prolazak unutar kvadrata 3x3
                    {
                        String broj = tabla[x + 3 * i][y + 3 * j].getText().toString();

                        for (int xp = 0; xp < 3; xp++)//provera izabranog broja sa svim brojevima unutar tog kvadrata
                            for (int yp = 0; yp < 3; yp++)
                                if ((x != xp || y != yp) && tabla[xp + 3 * i][yp + 3 * j].getText().toString().equals(broj)) {
                                    tabla[xp + 3 * i][yp + 3 * j].setTextColor(Color.RED);
                                    tabla[x + 3 * i][y + 3 * j].setTextColor(Color.RED);
                                    return;
                                }
                    }

            }
        pobeda();

    }

    private void pobeda() {
        timer.cancel();
        win = true;

        String vreme = textTime.getText().toString();
        String mod = textMod.getText().toString();
        String rekord = preferences.getString("vreme"+mod, "59:59");

        try {//provera rekorda
            Date dateVreme = new SimpleDateFormat("mm:ss").parse(vreme);
            Date dateRekord = new SimpleDateFormat("mm:ss").parse(rekord);

            if(dateVreme.before(dateRekord)) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("vreme"+mod,vreme);
                editor.apply();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (int x = 0; x < 9; x++)
            for (int y = 0; y < 9; y++)
                tabla[x][y].setClickable(false);

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage("Pobeda\nTežina: "+mod+"\nVreme: "+vreme);
        dialog.setCancelable(true);
        dialog.setPositiveButton("OK",null);
        dialog.show();

    }

    private void removeError(){
        for (int x = 0; x < 9; x++)
            for (int y = 0; y < 9; y++)
                tabla[x][y].setTextColor(Color.BLACK);
    }



    public void clearTable() {

        for (int x = 0; x < 9; x++)
            for (int y = 0; y < 9; y++) {
                tabla[x][y].setText("");
                tabla[x][y].setClickable(true);
                tabla[x][y].setBackgroundColor(Color.WHITE);
                tabla[x][y].setTextColor(Color.BLACK);
            }

    }


    public void saveTable() {

        baza.clear();

        for (int x = 0; x < 9; x++)
            for (int y = 0; y < 9; y++) {
                String broj = tabla[x][y].getText().toString();
                if (!broj.equals("")) {
                    Polje polje = new Polje(x, y, broj, !tabla[x][y].isClickable());
                    baza.dodajPolje(polje);
                }
            }
    }

    public void loadTable() {
        LinkedList<Polje> polja = baza.ucitajPolja();

        if (polja.isEmpty())
            return;

        for (Polje p : polja) {

            tabla[p.getX()][p.getY()].setText(p.getBroj());
            if (p.isFixed()) {
                tabla[p.getX()][p.getY()].setClickable(false);
                tabla[p.getX()][p.getY()].setBackgroundColor(Color.rgb(224, 224, 224));
            } else {
                tabla[p.getX()][p.getY()].setClickable(true);
                tabla[p.getX()][p.getY()].setBackgroundColor(Color.WHITE);
            }
        }
    }


    private void setTimer(long time){
        if(timer != null)
            timer.cancel();

        timer = new CountDownTimer(3600000-time, 1000) {//tajmer broji sat vremena max

            public void onTick(long millisUntilFinished) {
                long time = (3600000-millisUntilFinished);
                Date vreme = new Date(time);
                textTime.setText(new SimpleDateFormat("mm:ss").format(vreme));
            }

            public void onFinish() {
                textTime.setText("59:59");
            }


        }.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        long time = preferences.getLong("time",0);
        String mod = preferences.getString("mod","Lako");
        win = preferences.getBoolean("win",false);
        textMod.setText(mod);

        if(!win)
            setTimer(time);
    }

    @Override
    public void onPause() {
        super.onPause();
        //saveTable();

        long time = 0;
        SharedPreferences.Editor editor = preferences.edit();
        try {
            time = new SimpleDateFormat("mm:ss").parse(textTime.getText().toString()).getTime();

        } catch (ParseException e) { }

        String mod = textMod.getText().toString();

        editor.putLong("time", time);
        editor.putString("mod",mod);
        editor.putBoolean("win",win);
        editor.apply();
    }



}



