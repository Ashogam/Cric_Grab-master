package com.Navication_selection_class.cric_grap;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.ashok.android.cric_grap.R;
import com.sqlite.cric_grap.Add_player_SqliteManagement;
import com.utility.cric_grap.Player_Info;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ANDROID on 15-11-2015.
 */
public class Add_player extends AppCompatActivity {

    AutoCompleteTextView mName, mNumber;
    Button btnSave;
    ArrayList<Player_Info> player_infos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_player);
        initialize();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mName.length() > 0 && mNumber.length() >= 10) {
                    System.out.println(mName.length());
                    System.out.println(mNumber.length());
                    String name = mName.getText().toString();
                    String number = mNumber.getText().toString();

                    new Save().execute(name, number);
                } else {
                    Toast.makeText(Add_player.this, "Please check the Mobile Number", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initialize() {
        mName = (AutoCompleteTextView) findViewById(R.id.playerName);
        mNumber = (AutoCompleteTextView) findViewById(R.id.playerNumber);
        btnSave = (Button) findViewById(R.id.btnSave);
        player_infos=new ArrayList<>();
    }

    private class Save extends AsyncTask<String, Void, Long> {
        Add_player_SqliteManagement add_player_sqliteManagement = null;
        private ProgressDialog myProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myProgressDialog = new ProgressDialog(
                    Add_player.this);
            myProgressDialog.setMessage("Saving Data");
            myProgressDialog.setCancelable(false);
            myProgressDialog.show();
        }

        @Override
        protected Long doInBackground(String... params) {
            long result = 0;
            add_player_sqliteManagement = new Add_player_SqliteManagement(Add_player.this);
            try {
                Thread.sleep(2000);
                add_player_sqliteManagement.open();
                result = add_player_sqliteManagement.registration(params[0], params[1]);
                Log.d("Result Database", "result :" + result);
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("DOInbackGround", "Exception came");
            } finally {
                if (add_player_sqliteManagement != null) {
                    add_player_sqliteManagement.close();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Long aVoid) {
            super.onPostExecute(aVoid);
            myProgressDialog.dismiss();

            Log.d("OnPostExecuteMethod", "aVoid" + aVoid);
            if (aVoid > 0) {
                Toast.makeText(Add_player.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                Add_player_SqliteManagement management = new Add_player_SqliteManagement(Add_player.this);
                management.open();
                try {
                    JSONArray jsonArray = management.getdetails();
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Player_Info player_info=new Player_Info();
                            player_info.setPlayer_name(jsonObject.getString("player_name"));
                            player_info.setPlayer_mobile_number(jsonObject.getString("player_number"));
                            Log.i("Json feed",jsonObject.getString("player_name")+"_____"+jsonObject.getString("player_number"));
                            player_infos.add(player_info);
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                management.close();
            } else if (aVoid == -1) {
                Toast.makeText(Add_player.this, "Already Registered", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Add_player.this, "Save Denied! DataBase Error", Toast.LENGTH_SHORT).show();
            }
        }


    }
}
