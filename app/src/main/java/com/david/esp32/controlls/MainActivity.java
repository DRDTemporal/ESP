package com.david.esp32.controlls;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.david.esp32.R;
import com.github.mikephil.charting.charts.LineChart;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView txtEstadoRele, txtDato1, txtDato2, txtDato3, txtDato4;
    Button btnCambio;
    LineChart grafica;

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
        grafica =findViewById (R.id.grafica);
        btnCambio.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnCambiar:
                Toast.makeText(this, "Cambio", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
