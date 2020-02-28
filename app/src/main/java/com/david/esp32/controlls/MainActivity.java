package com.david.esp32.controlls;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.david.esp32.R;
import com.david.esp32.models.CustomMarkerViewData1;
import com.david.esp32.models.Datos;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView txtEstadoRele, txtDato1, txtDato2, txtDato3, txtDato4;
    Button btnCambio;
    LineChart grafica;
    private DatabaseReference reference;
    List<Entry> entradas = new ArrayList<>();
    FloatingActionButton btnExportar;
    private List<String> labelsChart = new ArrayList<>();
    float valorMaximo, valorMinimo;
    private XAxis xAxis;
    private String tipoDeDato;
    File archivo;
    private List<Datos> datosList = new ArrayList();
    @Override
    //prueba
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        FirebaseApp.initializeApp(this);
        inicializar();
        inizialiteFirebaseApp();
    }

    private void inizialiteFirebaseApp() {
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(false);}catch (Exception e){}
        reference= FirebaseDatabase.getInstance().getReference();
        obtenerDatosTiempoReal();

    }

    private void inicializar() {
        txtEstadoRele = findViewById(R.id.txtEstadoRele);
        txtDato1 = findViewById(R.id.txtDato1);
        txtDato2 = findViewById(R.id.txtDato2);
        txtDato3 = findViewById(R.id.txtDato3);
        txtDato4 = findViewById(R.id.txtDato4);
        btnCambio = findViewById(R.id.btnCambiar);
        btnExportar = findViewById(R.id.btnExportar);
        grafica =findViewById (R.id.grafica);
        btnCambio.setOnClickListener(this);
        btnExportar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnCambiar:
                Toast.makeText(this, "Cambio", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnExportar:
                exportarACSV();
                break;
        }
    }

    private void obtenerDatos(String anio, String mes, String dia){
        final DatabaseReference datos = reference.child("ESP32").child("datos").child("y"+anio).child("m"+mes).child("d"+dia);
        datos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnap: dataSnapshot.getChildren()){
                    datosList.set(datosList.size(), postSnap.getValue(Datos.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void obtenerDatosTiempoReal(){
        DatabaseReference datosTiempoReal = reference.child("ESP32").child("tiempoReal");
        datosTiempoReal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<Datos>>  t =  new GenericTypeIndicator<ArrayList<Datos>>() {};
                try {
                    showChartRealTime(dataSnapshot.getValue(t));
                }catch (Exception ignored){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showChartRealTime(List<Datos> realTimeData) {
        entradas.clear();
        labelsChart.clear();

        final Date[] date1= {new Date(), new Date()};
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd--MM-yyyy HH:mm:ss");
        Collections.sort(realTimeData, (new Comparator<Datos>() {
            @Override
            public int compare(Datos o1, Datos o2) {

                try {
                    date1[0] =dateFormat.parse(o1.getFecha());
                    date1[1] =dateFormat.parse(o2.getFecha());
                    if (date1[0].getTime() < date1[1].getTime()) {
                        return -1;
                    }
                    if (date1[0].getTime() > date1[1].getTime()) {
                        return 1;
                    }
                    return 0;

                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        }));
        for (int i=0; i<realTimeData.size();i++){
            labelsChart.add(realTimeData.get(i).getHora());
            float dato=0;
            try {
                dato = Float.parseFloat(realTimeData.get(i).getAc());
                if (dato>valorMaximo){
                    valorMaximo = dato;
                }

                if (valorMinimo==0){
                    valorMinimo=dato;
                }
                if (dato<valorMinimo){
                    valorMinimo = dato;
                }
            }catch (Exception ignored){

            }
            Log.e("Datos",String.valueOf(dato));
            entradas.add(new Entry(i,dato));

        }

        if (entradas.size()>0){
            LineDataSet lineDataSet = new LineDataSet(entradas, tipoDeDato);
            // lineDataSet.setColor(colorGrafica);
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineDataSet.setDrawCircles(false);
            lineDataSet.setLineWidth(1.5f);
            LineData data = new LineData(lineDataSet);
            data.setDrawValues(false);
            Description description = new Description();
            grafica.setData(data);
            try {
                description.setText(getString(R.string.fecha_datos_tomados) + " " + realTimeData.get(0).getFecha());
            }catch (Exception ignored){ }

            xAxis = grafica.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labelsChart));
            xAxis.setLabelRotationAngle(-10f);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            YAxis yAxisLeft = grafica.getAxisLeft();
            YAxis yAxisRight = grafica.getAxisRight();
        }
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelsChart));

        YAxis yAxisLeft = grafica.getAxisLeft();
        YAxis yAxisRight = grafica.getAxisRight();
        Description description = new Description();
        if (valorMinimo > 10) {
            valorMinimo -= 0.9f;
        }


        yAxisLeft.setAxisMaximum(valorMaximo + 0.9f);
        yAxisRight.setAxisMaximum(valorMaximo + 0.9f);
        yAxisLeft.setAxisMinimum(valorMinimo);
        yAxisRight.setAxisMinimum(valorMinimo);
        valorMaximo = 0;
        valorMinimo = 0;

        grafica.setDescription(description);
        grafica.setDrawMarkers(true);
        CustomMarkerViewData1 customMarkerView = new CustomMarkerViewData1(this, R.layout.item_custom_marker, labelsChart);
        customMarkerView.setTipoDelDato(tipoDeDato);
        customMarkerView.setSizeList(labelsChart.size());
        // customMarkerView.setColorDelDato(colorGrafica);
        grafica.setMarker(customMarkerView);
        grafica.setTouchEnabled(true);
        grafica.setVisibility(View.VISIBLE);
        grafica.invalidate();
    }


    public void exportarACSV(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.item_busqueda);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final DatePicker datePicker = dialog.findViewById(R.id.calendario);
        final Button btnAceptar =  dialog.findViewById(R.id.btnExportar);
        Button btnCancelar =  dialog.findViewById(R.id.btnCancelar);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int mes = datePicker.getMonth()+1;
                String fecha1 = datePicker.getDayOfMonth()+"/"+mes+"/"+datePicker.getYear();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date();
                try {
                    date =dateFormat.parse(fecha1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat dateFormat1 = new SimpleDateFormat("MM/dd/yyyy");
                String fecha = dateFormat1.format(date);

                // Obtener la informacioón de base de datos.
                List<Datos> tmpListDatos = new ArrayList<>();

                Iterator<Datos> it = tmpListDatos.iterator();
                List<Datos> tmpDatos = new ArrayList<>();
                try {
                    while (it.hasNext()) {
                        Datos current = it.next();
                        if (!current.getFecha().equals(fecha)) {
                            it.remove();
                        }else {
                            tmpDatos.add(current);
                        }
                    }
                    File exportDir = new File(Environment.getExternalStorageDirectory(), "DatosESP");
                    if (!exportDir.exists())
                    {
                        exportDir.mkdirs();
                    }
                    archivo = new File(exportDir, "DatosESP "+fecha.substring(0,2)+"-"+fecha.substring(3,5)+"-"+fecha.substring(6,8)+".csv");
                    archivo.createNewFile();
                    CSVWriter csvWrite = new CSVWriter(new FileWriter(archivo));
                    // Primera linea del excel
                    String titulos [] ={
                            "Fecha","AC","DC","VD","VA","H","T"
                    };
                    csvWrite.writeNext(titulos);
                    // Escritura de los datos del ESP
                    for (int i=0; i<tmpDatos.size();i++) {
                        Datos datosI = tmpDatos.get(i);
                        String arrStr[] = {datosI.getFecha(),datosI.getAc(),datosI.getDc(),
                                datosI.getVd(), datosI.getVa(), datosI.getHumedad(), datosI.getTemperatura()
                        };
                        csvWrite.writeNext(arrStr);
                    }
                    csvWrite.close();

                    if (tmpDatos.size()<1){
                        Toast.makeText(MainActivity.this, "No hay datos registrados en la fecha seleccionada", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MainActivity.this, "El archivo está en la carpeta DatosESP", Toast.LENGTH_SHORT).show();
                    }

                    dialog.cancel();

                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "No hay datos registrados en la fecha seleccionada", Toast.LENGTH_SHORT).show();
                    Log.e("Error",e.getMessage());
                }
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
}
