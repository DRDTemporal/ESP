package com.david.esp32.controlls;

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
import com.david.esp32.models.Datos;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView txtEstadoRele, txtDato1, txtDato2, txtDato3, txtDato4;
    Button btnCambio;
    LineChart grafica;
    FloatingActionButton btnExportar;
    File archivo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        inicializar();
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
