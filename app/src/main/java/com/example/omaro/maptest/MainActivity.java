package com.example.omaro.maptest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

        private BroadcastReceiver receiver;
        private TextView test;
        private SQLhelper SQLhelper;
        private Context mContext;
        private Spinner spin;
        private EditText inputName;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
                if(checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                0);
                }
                mContext = this;
                test = (TextView) findViewById(R.id.tv_test);
                spin = (Spinner) findViewById(R.id.TaskType);
                inputName = (EditText) findViewById(R.id.TaskNameInputMain);
                ArrayList<String> temp = new ArrayList<>();
                temp.add("NAV");
                ArrayAdapter<String> dapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,temp);
                spin.setAdapter(dapter);
                        //Start Service
                Intent service = new Intent(this, GPSService.class);
                startService(service);
        }

        @Override
        protected  void onResume(){
                super.onResume();
                if(receiver == null){
                        receiver = new BroadcastReceiver() {
                                @Override
                                public void onReceive(Context context, Intent intent) {
                                        //RECEIVE Nick

                                        SharedPreferences temp = getSharedPreferences(intent.getStringExtra("nick") , MODE_PRIVATE);
                                        Set<String> t = temp.getStringSet("Tasks", new HashSet<String>());
                                        Log.d("recieved",t.toString());

                                        for (String s: t) {
                                                SharedPreferences TaskPref = getSharedPreferences(s, MODE_PRIVATE);
                                                String type = TaskPref.getString("type", "non");
                                                Log.d("type",t.toString());
                                                switch (type) {
                                                        case "non":
                                                                break;
                                                        case "NAV":
                                                                String Dest = TaskPref.getString("dest", "non");
                                                                if (Dest == "non")
                                                                        break;
                                                                else {
                                                                        DistanceTo(Dest);
                                                                }
                                                                break;
                                                }
                                        }


                                }
                        };
                }
                registerReceiver(receiver,new IntentFilter("update"));
        }

        @Override
        protected void onDestroy() {
                super.onDestroy();
                unregisterReceiver(receiver);
        }

        public void beginMapStart(View view) {
                Intent bms = new Intent(this,MapActivity.class);
                bms.putExtra("NICK","null");
                bms.putExtra("LAT",40.600858);
                bms.putExtra("LONG",-74.2920317);
                startActivity(bms);
        }

        public void listLocationStart(View view) {
                Intent lls = new Intent(this,ListLocation.class);
                startActivity(lls);
        }

        public void AddTaskClick(View view) {

                String selectedtype  = spin.getSelectedItem().toString();
                if(selectedtype == "NAV")
                {
                        Intent Nav = new Intent(this,AddNavTask.class);
                        startActivity(Nav);
                }




        }

        public void SignOutClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent main = new Intent(this,LoginActivity.class);
                startActivity(main);

        }

        public void DistanceTo(String nick)
        {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=37.7749,-122.4194");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
        }


}
