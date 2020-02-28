package com.david.esp32.models;

import android.content.Context;
import android.widget.TextView;

import com.david.esp32.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.List;

public class CustomMarkerViewData1 extends MarkerView {
    private TextView txtCustomMarker1, txtCustomMarker2;
    private int colorDelDato = 0;
    private String tipoDelDato = "";
    private List<String> labelsChart;

    private MPPointF mOffset;
    private float sizeList;
    private float getX1;

    public float getSizeList() {
        return sizeList;
    }

    public void setSizeList(float sizeList) {
        this.sizeList = sizeList;
    }

    public void setColorDelDato(int colorDelDato) {
        this.colorDelDato = colorDelDato;
    }

    public void setTipoDelDato(String tipoDelDato) {
        this.tipoDelDato = tipoDelDato;
    }

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */

    public CustomMarkerViewData1(Context context, int layoutResource, List<String> labelsChart) {
        super(context, layoutResource);
        this.labelsChart = labelsChart;
        txtCustomMarker1 = findViewById(R.id.txtCustomMarker1);
        txtCustomMarker2 = findViewById(R.id.txtCustomMarker2);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        super.refreshContent(e, highlight);
        getX1=e.getX();
        txtCustomMarker1.setText(getResources().getString(R.string.dato)+": "+ labelsChart.get((int) e.getX()));
        txtCustomMarker2.setText(tipoDelDato+": " + e.getY());
        txtCustomMarker2.setTextColor(colorDelDato);

    }

    @Override
    public MPPointF getOffset() {
        mOffset = new MPPointF((float) -(getWidth() / 2.2), -getHeight());
        float resta = getSizeList()-getX1;
        float tmp1 = (resta*100)/getSizeList();
        if (tmp1<12) {
            mOffset = new MPPointF((float) -(getWidth() / 1.2), -getHeight());
        }
        return mOffset;
    }


}
