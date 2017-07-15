package josh.android.coastercollection.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import josh.android.coastercollection.R;
import josh.android.coastercollection.bo.CollectionHistoryMatrix;
import josh.android.coastercollection.utils.Util;
import josh.android.coastercollection.utils.YearAsXAxisLabelFormatter;

public class GraphsActivity extends AppCompatActivity {

    private final static String LOG_TAG = "GRAPHS_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        initGraph(graph);
    }

    public void initGraph(GraphView graph) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");

        CollectionHistoryMatrix stats = Util.createHistoryMatrix();

        int yearMax = stats.getMaxYear();
        int yearMin = stats.getMinYear();

        int nYears = yearMax - yearMin + 1;

        DataPoint[] dataPoints = new DataPoint[nYears];

        String[] strYearArray = new String[nYears];

        for (int i=0; i<nYears; i++) {
            int year = yearMin + i;

            Date d = new Date();

            try {
                d = sdf.parse(String.valueOf(year));
                strYearArray[i] = String.valueOf(year);
            } catch (ParseException e) {
            }

            DataPoint dp = new DataPoint(d, stats.getCount(year));
//            DataPoint dp = new DataPoint(year, stats.getCount(year));

            dataPoints[i] = dp;
        }

//        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dataPoints);

//        series.setDrawBackground(true);
//        series.setBackgroundColor(Color.rgb(255,136,0)); // = R.color.colorAccent
//
//        series.setThickness(15);
        series.setColor(Color.rgb(255,136,0)); // = R.color.colorAccent

        series.setTitle("Collected/Year");
        series.setAnimated(true);
//        series.setDrawAsPath(true);

        // draw values on top
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.RED);
        series.setValuesOnTopSize(30);

        series.setSpacing(10);

        ValueDependentColor<DataPoint> vdc = new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                if (data.getY() >= 240) {
                    return Color.rgb(255,80,0);
                } else if (data.getY() >= 120) {
                    return Color.rgb(255,136,0); // = @color/colorOrangeDark
                } else {
                    return Color.RED;
                }
            }
        };

        series.setValueDependentColor(vdc);

        graph.addSeries(series);
        graph.setTitle("Coastercollection");
        graph.setTitleTextSize(56f);


        // activate horizontal zooming and scrolling
        graph.getViewport().setScalable(true);

        // activate horizontal scrolling
        graph.getViewport().setScrollable(true);

//        // activate horizontal and vertical zooming and scrolling
//        graph.getViewport().setScalableY(true);
//
//        // activate vertical scrolling
//        graph.getViewport().setScrollableY(true);

//        // set manual X bounds
//        graph.getViewport().setXAxisBoundsManual(true);
//        graph.getViewport().setMinX(yearMin);
//        graph.getViewport().setMaxX(yearMax+1);

        // set manual Y bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);

        long maxY = ((long) ((stats.getMaxYearCount()+200)/100))*100;

        graph.getViewport().setMaxY(maxY);

        graph.getViewport().calcCompleteRange();

        graph.getGridLabelRenderer().setHumanRounding(false);

        DateAsXAxisLabelFormatter formatter = new YearAsXAxisLabelFormatter(this);

        graph.getGridLabelRenderer().setLabelFormatter(formatter);

        graph.getGridLabelRenderer().setHorizontalLabelsAngle(135);
        graph.getGridLabelRenderer().setNumHorizontalLabels(nYears);

        graph.getGridLabelRenderer().setPadding(50);

        // use static labels for horizontal and vertical labels
//        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
//        staticLabelsFormatter.setHorizontalLabels(strYearArray);
//
//        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        graph.getGridLabelRenderer().setHorizontalAxisTitle("Year");
        graph.getGridLabelRenderer().setVerticalAxisTitle("#Coasters");

//        graph.getGridLabelRenderer().reloadStyles();

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setMargin(25);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }
}
