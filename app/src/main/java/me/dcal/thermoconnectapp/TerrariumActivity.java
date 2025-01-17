package me.dcal.thermoconnectapp;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.EntryXComparator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import me.dcal.thermoconnectapp.Modeles.BodyAnimal;
import me.dcal.thermoconnectapp.Modeles.BodyTerrarium;
import me.dcal.thermoconnectapp.Modeles.BodyTerrariumData;
import me.dcal.thermoconnectapp.Services.API;
import me.dcal.thermoconnectapp.Services.BodyConnexion;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TerrariumActivity extends AppCompatActivity {

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private LineChart chartTemperature;
    private LineChart chart;
    private BodyTerrarium bt;
    private TimePickerDialog timeMinPickerDialog;
    private TimePickerDialog timeMaxPickerDialog;
    private TextView TitleTerrarium;
    private EditText TitleTerrariumEdit;
    private TextView temperatureMin;
    private NumberPicker minPicker;
    private TextView temperatureMax;
    private NumberPicker maxPicker;
    private TextView humidite;
    private NumberPicker humiditePicker;
    private TextView heureMinTerrarium;
    private TextView heureMaxTerrarium;
    private Button save_button;
    private ScrollView infoView;
    private LinearLayout graphView;
    private LinearLayout infoButton;
    private LinearLayout graphButton;
    private TextView textInfoButton;
    private TextView textGraphButton;
    private ImageView imageInfoButton;
    private ImageView imageGraphButton;
    private ArrayList<Entry> dataGraphTemperature;
    private ArrayList<Entry> dataGraphHumidite;
    private TextView textGraphHumidite;
    private TextView textGraphTemperature;
    private BodyTerrariumData lastdata;
    private TextView updateTemperature;
    private TextView updateHumidite;
    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.bt = (BodyTerrarium) getIntent().getSerializableExtra("Terrarium");
        setContentView(R.layout.activity_terrarium);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BodyConnexion body=API.getBodyConnexion(getApplicationContext());
        // toast = Toast.makeText(getApplicationContext(), body.login , Toast.LENGTH_LONG);
        //toast.show();

        //Init
        TitleTerrarium = (TextView)findViewById(R.id.TitleTerrarium);
        TitleTerrariumEdit = (EditText)findViewById(R.id.TitleTerrariumEdit);
        chartTemperature = (LineChart)findViewById(R.id.barchartTemperature);
        chart = (LineChart)findViewById(R.id.barchart);
        temperatureMin = (TextView)findViewById(R.id.TemperatureFroidTerrarium);
        minPicker = (NumberPicker)findViewById(R.id.TemperatureFroidTerrariumEdit);
        temperatureMax = (TextView)findViewById(R.id.TemperatureChaudTerrarium);
        maxPicker = (NumberPicker)findViewById(R.id.TemperatureChaudTerrariumEdit);
        humidite = (TextView)findViewById(R.id.HumiditéTerrarium);
        humiditePicker = (NumberPicker)findViewById(R.id.HumiditéTerrariumEdit);
        heureMinTerrarium = (TextView)findViewById(R.id.HeureMinTerrarium);
        heureMaxTerrarium = (TextView)findViewById(R.id.HeureMaxTerrarium);
        save_button = (Button)findViewById(R.id.save_button);
        infoView = (ScrollView)findViewById(R.id.infoView);
        infoButton = (LinearLayout)findViewById(R.id.InfoButton);
        textInfoButton =(TextView)findViewById(R.id.textInfoButton);
        imageInfoButton = (ImageView)findViewById(R.id.imageInfoButton);
        graphView = (LinearLayout)findViewById(R.id.graphView);
        graphButton = (LinearLayout)findViewById(R.id.GraphButton);
        textGraphButton =(TextView)findViewById(R.id.textGraphButton);
        imageGraphButton = (ImageView)findViewById(R.id.imageGraphButton);
        textGraphHumidite = (TextView)findViewById(R.id.textGraphHumidite);
        textGraphTemperature = (TextView)findViewById(R.id.textGraphTemperature);
        updateTemperature = (TextView)findViewById(R.id.updateTemperature);
        updateHumidite = (TextView)findViewById(R.id.updateHumidite);
        errorMessage = (TextView)findViewById(R.id.erreurMessage);

        //Setup Value
        TitleTerrariumEdit.setText(bt.getNameTerrarium());
        TitleTerrarium.setText(bt.getNameTerrarium());
        temperatureMin.setText(bt.getTemperatureMin().toString());
        minPicker.setMinValue(0);
        minPicker.setMaxValue(100);
        int minTemperatureValue = (int)Math.round(bt.getTemperatureMin());
        minPicker.setValue(minTemperatureValue);
        temperatureMax.setText(bt.getTemperatureMax().toString());
        maxPicker.setMaxValue(100);
        maxPicker.setMinValue(0);
        int maxTemperatureValue = (int)Math.round(bt.getTemperatureMax());
        maxPicker.setValue(maxTemperatureValue);
        humidite.setText(bt.getHumidityTerrarium().toString());
        humiditePicker.setMaxValue(100);
        humiditePicker.setMinValue(0);
        int humiditeValue = (int)Math.round(bt.getHumidityTerrarium());
        humiditePicker.setValue(humiditeValue);
        heureMinTerrarium.setText(bt.getStartLightTime());
        heureMaxTerrarium.setText(bt.getStopLightTime());
        bt.setBodyConnexion(body);
        chart.getDescription().setEnabled(false);

        //On resume et liste
        generationAffichage();
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

    public void ajouterAnimal(View v){
        Intent i=new Intent(this, AddAnimalActivity.class);
        i.putExtra("idterra", this.bt.getIdTerrarium());
        startActivity(i);

    }

    @Override
    protected void onResume() {
        super.onResume();
        generationAffichage();
    }

    //Modification des valeurs

    public void updateInfoVue(View v){
        TitleTerrarium.setVisibility(View.VISIBLE);
        TitleTerrariumEdit.setVisibility(View.GONE);
        TitleTerrarium.setText(TitleTerrariumEdit.getText().toString());
        temperatureMax.setVisibility(View.VISIBLE);
        maxPicker.setVisibility(View.GONE);
        temperatureMax.setText(maxPicker.getValue() + ".0");
        temperatureMin.setVisibility(View.VISIBLE);
        minPicker.setVisibility(View.GONE);
        temperatureMin.setText(minPicker.getValue() + ".0");
        humiditePicker.setVisibility(View.GONE);
        humidite.setVisibility(View.VISIBLE);
        humidite.setText(humiditePicker.getValue()+".0");
        verification();
    }

    public void modiftemperaturemin(View v){
        TitleTerrarium.setVisibility(View.VISIBLE);
        TitleTerrariumEdit.setVisibility(View.GONE);
        TitleTerrarium.setText(TitleTerrariumEdit.getText().toString());
        temperatureMax.setVisibility(View.VISIBLE);
        maxPicker.setVisibility(View.GONE);
        temperatureMax.setText(maxPicker.getValue() + ".0");
        temperatureMin.setVisibility(View.GONE);
        minPicker.setVisibility(View.VISIBLE);
        humiditePicker.setVisibility(View.GONE);
        humidite.setVisibility(View.VISIBLE);
        humidite.setText(humiditePicker.getValue()+".0");
        verification();

    }

    public void modiftemperaturemax(View v){
        TitleTerrarium.setVisibility(View.VISIBLE);
        TitleTerrariumEdit.setVisibility(View.GONE);
        TitleTerrarium.setText(TitleTerrariumEdit.getText().toString());
        temperatureMax.setVisibility(View.GONE);
        maxPicker.setVisibility(View.VISIBLE);
        temperatureMin.setVisibility(View.VISIBLE);
        minPicker.setVisibility(View.GONE);
        temperatureMin.setText(minPicker.getValue() + ".0");
        humiditePicker.setVisibility(View.GONE);
        humidite.setVisibility(View.VISIBLE);
        humidite.setText(humiditePicker.getValue()+".0");
        verification();

    }

    public void modifhumidite(View v){
        TitleTerrarium.setVisibility(View.VISIBLE);
        TitleTerrariumEdit.setVisibility(View.GONE);
        TitleTerrarium.setText(TitleTerrariumEdit.getText().toString());
        temperatureMax.setVisibility(View.VISIBLE);
        maxPicker.setVisibility(View.GONE);
        temperatureMax.setText(maxPicker.getValue() + ".0");
        temperatureMin.setVisibility(View.VISIBLE);
        minPicker.setVisibility(View.GONE);
        temperatureMin.setText(minPicker.getValue() + ".0");
        humiditePicker.setVisibility(View.VISIBLE);
        humidite.setVisibility(View.GONE);
        verification();
    }

    public void ChangerTitre(View v){
        TitleTerrarium.setVisibility(View.GONE);
        TitleTerrariumEdit.setVisibility(View.VISIBLE);
        temperatureMax.setVisibility(View.VISIBLE);
        maxPicker.setVisibility(View.GONE);
        temperatureMax.setText(maxPicker.getValue() + ".0");
        temperatureMin.setVisibility(View.VISIBLE);
        minPicker.setVisibility(View.GONE);
        temperatureMin.setText(minPicker.getValue() + ".0");
        humiditePicker.setVisibility(View.GONE);
        humidite.setVisibility(View.VISIBLE);
        humidite.setText(humiditePicker.getValue()+".0");
        verification();
    }

    public void clickTimeMin(View v){
        TitleTerrarium.setVisibility(View.VISIBLE);
        TitleTerrariumEdit.setVisibility(View.GONE);
        TitleTerrarium.setText(TitleTerrariumEdit.getText().toString());
        temperatureMax.setVisibility(View.VISIBLE);
        maxPicker.setVisibility(View.GONE);
        temperatureMax.setText(maxPicker.getValue() + ".0");
        temperatureMin.setVisibility(View.VISIBLE);
        minPicker.setVisibility(View.GONE);
        temperatureMin.setText(minPicker.getValue() + ".0");
        humiditePicker.setVisibility(View.GONE);
        humidite.setVisibility(View.VISIBLE);
        humidite.setText(humiditePicker.getValue()+".0");
        timeMinPickerDialog=new TimePickerDialog(TerrariumActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
                if(minutes<=9){
                    heureMinTerrarium.setText(hour+":0"+minutes+":00");
                }else{
                    heureMinTerrarium.setText(hour+":"+minutes+":00");
                }
                if(heureMinTerrarium.getText().toString().length() == 7)
                    heureMinTerrarium.setText("0" + heureMinTerrarium.getText().toString());
                verification();
            }
        },Integer.parseInt(heureMinTerrarium.getText().toString().split(":")[0]),Integer.parseInt(heureMinTerrarium.getText().toString().split(":")[1]),true);
        timeMinPickerDialog.show();

    }

    public void clickTimeMax(View v){
        TitleTerrarium.setVisibility(View.VISIBLE);
        TitleTerrariumEdit.setVisibility(View.GONE);
        TitleTerrarium.setText(TitleTerrariumEdit.getText().toString());
        temperatureMax.setVisibility(View.VISIBLE);
        maxPicker.setVisibility(View.GONE);
        temperatureMax.setText(maxPicker.getValue() + ".0");
        temperatureMin.setVisibility(View.VISIBLE);
        minPicker.setVisibility(View.GONE);
        temperatureMin.setText(minPicker.getValue() + ".0");
        humiditePicker.setVisibility(View.GONE);
        humidite.setVisibility(View.VISIBLE);
        humidite.setText(humiditePicker.getValue()+".0");
        timeMaxPickerDialog=new TimePickerDialog(TerrariumActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
                if(minutes<=9){
                    heureMaxTerrarium.setText(hour+":0"+minutes+":00");
                }else{
                    heureMaxTerrarium.setText(hour+":"+minutes+":00");
                }
                if(heureMaxTerrarium.getText().toString().length() == 7)
                    heureMaxTerrarium.setText("0" + heureMaxTerrarium.getText().toString());
                verification();
            }
        },Integer.parseInt(heureMaxTerrarium.getText().toString().split(":")[0]),Integer.parseInt(heureMaxTerrarium.getText().toString().split(":")[1]),true);
        timeMaxPickerDialog.show();
        verification();
    }

    //Affichage du bouton de sauvegarde

    public void verification(){
        if(!TitleTerrarium.getText().toString().equals(bt.getNameTerrarium()) || !Double.valueOf(temperatureMax.getText().toString()).equals(bt.getTemperatureMax()) || !Double.valueOf(temperatureMin.getText().toString()).equals(bt.getTemperatureMin()) || !Double.valueOf(humidite.getText().toString()).equals(bt.getHumidityTerrarium()) || !heureMinTerrarium.getText().toString().equals(bt.getStartLightTime()) || !heureMaxTerrarium.getText().toString().equals(bt.getStopLightTime()))
            save_button.setVisibility(View.VISIBLE);
        else
            save_button.setVisibility(View.INVISIBLE);
    }

    //Sauvegarde dans la base
    public void SaveModif(View v){
        updateInfoVue(v);
        bt.setNameTerrarium(TitleTerrarium.getText().toString());
        bt.setTemperatureMax(Double.valueOf(temperatureMax.getText().toString()));
        bt.setTemperatureMin(Double.valueOf(temperatureMin.getText().toString()));
        bt.setHumidityTerrarium(Double.valueOf(humidite.getText().toString()));
        bt.setStartLightTime(heureMinTerrarium.getText().toString());
        bt.setStopLightTime(heureMaxTerrarium.getText().toString());
        Call<Integer> retour = API.getInstance().simpleService.modifTerrarium(bt);
        retour.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                API.launchShortToast(getApplicationContext(), "Modification faite!");
                errorMessage.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                errorMessage.setVisibility(View.VISIBLE);
            }
        });

    }

    //Suppression du terrarium
    public void deleteTerrarium(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(TerrariumActivity.this);
        builder.setMessage("Voulez vous vraiment supprimer le terrarium : "+bt.getNameTerrarium()+" ?\n Ceci entrainera la suppression de toutes les données associés ainsi que les animaux.")//R.string.confirm_dialog_message
                .setTitle("Suppression de la fiche de " + bt.getNameTerrarium())//R.string.confirm_dialog_title
                .setPositiveButton("Confimer", new DialogInterface.OnClickListener() { //R.string.confirm
                    public void onClick(DialogInterface dialog, int id) {
                        updateInfoVue(v);
                        Call<Integer> retour = API.getInstance().simpleService.deleteTerrarium(bt);
                        retour.enqueue(new Callback<Integer>() {
                            @Override
                            public void onResponse(Call<Integer> call, Response<Integer> response) {
                                API.launchShortToast(getApplicationContext(), "Suppression faite!");
                                finish();
                                errorMessage.setVisibility(View.GONE);
                            }

                            @Override
                            public void onFailure(Call<Integer> call, Throwable t) {
                                updateInfoVue(v);
                                errorMessage.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {//R.string.cancel
                    public void onClick(DialogInterface dialog, int id) {
                        updateInfoVue(v);
                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();


    }

    //chart
    public long timestampToFloat(Timestamp tm){
        long time =  tm.getTime();
        return time;
    }

    //Generation affichage
    public void generationAffichage() {
        Call<List<BodyAnimal>> list = API.getInstance().simpleService.listAnimal(bt);
        ListView AnimalList = (ListView)findViewById(R.id.AnimalList);
        ArrayAdapter<BodyAnimal> arrayAdapter = new ArrayAdapter<BodyAnimal>(this, android.R.layout.simple_list_item_1);

        AnimalList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BodyAnimal ba = (BodyAnimal)parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), AnimalActivity.class);
                intent.putExtra("Animal", ba);
                startActivity(intent);
            }
        });
        list.enqueue(new Callback<List<BodyAnimal>>() {
            @Override
            public void onResponse(Call<List<BodyAnimal>> call, Response<List<BodyAnimal>> response) {

                if (response.body() != null){
                    for(BodyAnimal ba : response.body())
                        arrayAdapter.add(ba);

                    if (arrayAdapter.getCount()>0){
                        View item = arrayAdapter.getView(0, null, AnimalList);
                        item.measure(0, 0);
                        int nbitem = arrayAdapter.getCount()>4 ? 4 : arrayAdapter.getCount();
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (nbitem * item.getMeasuredHeight()));
                        AnimalList.setLayoutParams(params);
                        AnimalList.setVisibility(View.VISIBLE);
                    }else if (arrayAdapter.getCount()==0){
                        AnimalList.setVisibility(View.GONE);
                    }
                    errorMessage.setVisibility(View.GONE);
                    AnimalList.setAdapter(arrayAdapter);
                }

            }

            @Override
            public void onFailure(Call<List<BodyAnimal>> call, Throwable t) {
                errorMessage.setVisibility(View.VISIBLE);
            }
        });

        Call<BodyTerrariumData> info = API.getInstance().simpleService.getLastTerrariumData(bt);
        info.enqueue(new Callback<BodyTerrariumData>() {
            @Override
            public void onResponse(Call<BodyTerrariumData> call, Response<BodyTerrariumData> response) {
                errorMessage.setVisibility(View.GONE);
                lastdata = response.body();
                textGraphHumidite.setText("Humidité: " + lastdata.getHumidity().toString() + "%");
                textGraphTemperature.setText("Temperature: " + lastdata.getTemperature().toString() + "°C");
            }

            @Override
            public void onFailure(Call<BodyTerrariumData> call, Throwable t) {
                errorMessage.setVisibility(View.VISIBLE);
            }
        });

        XAxis xAxisHumidite = chart.getXAxis();
        xAxisHumidite.setDrawGridLines(true);
        xAxisHumidite.setGranularity(1);
        xAxisHumidite.setGranularityEnabled(true);
        xAxisHumidite.setAvoidFirstLastClipping(true);
        xAxisHumidite.setValueFormatter(new ValueFormatter(){
            private final SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm:ss", Locale.FRANCE);
            @Override
            public String getFormattedValue(float value) {
                long millis = TimeUnit.MINUTES.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });
        update_graph();

    }

    public void update_graph(){
        final Runnable background = new Runnable() {
            @Override
            public void run() {
                dataGraphTemperature = new ArrayList<Entry>();
                dataGraphHumidite = new ArrayList<Entry>();
                Call<List<BodyTerrariumData>> data = API.getInstance().simpleService.getAllTerrariumdata(bt);
                data.enqueue(new Callback<List<BodyTerrariumData>>() {
                    @Override
                    public void onResponse(Call<List<BodyTerrariumData>> call, Response<List<BodyTerrariumData>> response) {
                        for(BodyTerrariumData bd : response.body()){
                            long now = TimeUnit.MILLISECONDS.toMinutes(timestampToFloat(Timestamp.valueOf(bd.getDate())));
                            dataGraphTemperature.add(new Entry(now, Float.parseFloat(bd.getTemperature().toString())));
                            dataGraphHumidite.add(new Entry(now, Float.parseFloat(bd.getHumidity().toString())));
                        }
                        dataGraphHumidite.sort(new EntryXComparator());
                        dataGraphTemperature.sort(new EntryXComparator());
                        LineDataSet dataSetTemperature = new LineDataSet(dataGraphTemperature, "Evolution de la temperature(°C)");
                        LineDataSet datasetHumidite = new LineDataSet(dataGraphHumidite, "Evolution de l'humidité(%)");
                        datasetHumidite.setColor(Color.BLUE);
                        dataSetTemperature.setColors(Color.RED);
                        chart.invalidate();
                        LineData dataHumidite = new LineData(datasetHumidite, dataSetTemperature);
                        chart.setData(dataHumidite);
                        chart.animateXY(0, 0);
                        errorMessage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<List<BodyTerrariumData>> call, Throwable t) {
                        errorMessage.setVisibility(View.VISIBLE);
                    }
                });
            }
        };
        final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(background, 1, 60, TimeUnit.SECONDS);
        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                beeperHandle.cancel(true);
            }
        }, 10 * 10, TimeUnit.DAYS);
    }
}