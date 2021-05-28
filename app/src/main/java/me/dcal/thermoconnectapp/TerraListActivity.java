package me.dcal.thermoconnectapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import java.util.List;

import me.dcal.thermoconnectapp.Modeles.BodyTerrarium;
import me.dcal.thermoconnectapp.Modeles.BodyTerrariumData;
import me.dcal.thermoconnectapp.Modeles.TerraListData;
import me.dcal.thermoconnectapp.Services.API;
import me.dcal.thermoconnectapp.Services.BodyConnexion;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TerraListActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terra_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BodyConnexion body=API.getBodyConnexion(getApplicationContext());
        Toast toast = Toast.makeText(getApplicationContext(), body.login , Toast.LENGTH_LONG);
        toast.show();


        Call<List<BodyTerrarium>> list = API.getInstance().simpleService.listTerrarium(API.getBodyConnexion(getApplicationContext()));
        ListView terraList = (ListView)findViewById(R.id.TerraList);
        TerraListAdapter<TerraListData> arrayAdapter = new TerraListAdapter<TerraListData>(this, R.layout.terra_list_customlist);
        terraList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BodyTerrarium bt = (BodyTerrarium)parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), TerrariumActivity.class);
                intent.putExtra("Terrarium", bt);
                startActivity(intent);
            }
        });
        list.enqueue(new Callback<List<BodyTerrarium>>() {
            @Override
            public void onResponse(Call<List<BodyTerrarium>> call, Response<List<BodyTerrarium>> response) {
                for(BodyTerrarium bt : response.body()){
                    bt.setBodyConnexion(API.getBodyConnexion(getApplicationContext()));
                    Call<BodyTerrariumData> bd = API.getInstance().simpleService.getLastTerrariumData(bt);
                    bd.enqueue(new Callback<BodyTerrariumData>() {
                        @Override
                        public void onResponse(Call<BodyTerrariumData> call, Response<BodyTerrariumData> response) {
                            arrayAdapter.add(new TerraListData(bt, response.body()));
                        }

                        @Override
                        public void onFailure(Call<BodyTerrariumData> call, Throwable t) {
                            API.launchShortToast(getApplicationContext(), "KO");
                        }
                    });
                }
                terraList.setAdapter(arrayAdapter);
            }

            @Override
            public void onFailure(Call<List<BodyTerrarium>> call, Throwable t) {
                API.launchShortToast(getApplicationContext(), "KO");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.deconnexion:
                API.setBodyConnexion(getApplicationContext(),null);
                Intent i=new Intent(this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
        //noinspection SimplifiableIfStatement

    }
    public void ajouterTerrarium(View v){
        Intent i=new Intent(this, AddTerraActivity.class);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Call<List<BodyTerrarium>> list = API.getInstance().simpleService.listTerrarium(API.getBodyConnexion(getApplicationContext()));
        ListView terraList = (ListView)findViewById(R.id.TerraList);
        TerraListAdapter<TerraListData> arrayAdapter = new TerraListAdapter<TerraListData>(this, R.layout.terra_list_customlist);
        terraList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BodyTerrarium bt = (BodyTerrarium)parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), TerrariumActivity.class);
                intent.putExtra("Terrarium", bt);
                startActivity(intent);
            }
        });
        list.enqueue(new Callback<List<BodyTerrarium>>() {
            @Override
            public void onResponse(Call<List<BodyTerrarium>> call, Response<List<BodyTerrarium>> response) {
                for(BodyTerrarium bt : response.body()){
                    bt.setBodyConnexion(API.getBodyConnexion(getApplicationContext()));
                    Call<BodyTerrariumData> bd = API.getInstance().simpleService.getLastTerrariumData(bt);
                    bd.enqueue(new Callback<BodyTerrariumData>() {
                        @Override
                        public void onResponse(Call<BodyTerrariumData> call, Response<BodyTerrariumData> response) {
                            arrayAdapter.add(new TerraListData(bt, response.body()));
                        }

                        @Override
                        public void onFailure(Call<BodyTerrariumData> call, Throwable t) {
                            API.launchShortToast(getApplicationContext(), "KO");
                        }
                    });
                }
                terraList.setAdapter(arrayAdapter);
            }

            @Override
            public void onFailure(Call<List<BodyTerrarium>> call, Throwable t) {
                API.launchShortToast(getApplicationContext(), "KO");
            }
        });
    }
}