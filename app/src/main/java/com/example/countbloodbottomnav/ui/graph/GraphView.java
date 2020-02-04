package com.example.countbloodbottomnav.ui.graph;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.example.countbloodbottomnav.R;
import com.example.countbloodbottomnav.models.ModelGraphData;
import com.example.countbloodbottomnav.models.ModelGraph;

@SuppressLint("ViewConstructor")
public class GraphView extends View {

    private ModelGraph graph;
    private int xGrid, xDiv, yGrid, yDiv;
    private Paint[] paints;

    public GraphView(Context context, ModelGraph graph) {
        super(context);
        this.graph = graph;
        initUI();
    }

    private boolean isInt(int n, int d) { //noinspection IntegerDivisionInFloatingPointContext
        return (float) n / d == n / d;
    }

    private void initUI() {
        xGrid = graph.getxGrid();
        xDiv = graph.getxDiv();
        yGrid = graph.getyGrid();
        yDiv = graph.getyDiv();

        Paint paint_blood, paint_fast, paint_slow, paint1, paint2, textPaint;

        paint_blood = new Paint();
        paint_blood.setColor(getResources().getColor(R.color.colorGraphBlood));
        paint_blood.setStyle(Paint.Style.FILL);

        paint_fast = new Paint();
        paint_fast.setColor(getResources().getColor(R.color.colorGraphFast));
        paint_fast.setStrokeWidth(2);

        paint_slow = new Paint();
        paint_slow.setColor(getResources().getColor(R.color.colorGraphSlow));
        paint_fast.setStrokeWidth(2);

        paint1 = new Paint();
        paint1.setColor(getResources().getColor(R.color.colorPrimaryDark));
        paint1.setStrokeWidth(1);

        paint2 = new Paint();
        paint2.setColor(getResources().getColor(R.color.colorPrimary));
        paint2.setStrokeWidth(4);

        float sp = getResources().getDisplayMetrics().scaledDensity;
        textPaint = new Paint();
        textPaint.setColor(Color.GRAY);
        textPaint.setTextSize(20 * sp);
        textPaint.setTextAlign(Paint.Align.CENTER);

        paints = new Paint[]{paint_blood, paint_fast, paint_slow, paint1, paint2, textPaint};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int x0 = 0, x1 = getWidth() - 200, y0 = 0, y1 = -getHeight() + 200;
        canvas.translate(100, getHeight() - 100);

        //Add number of days and amount of data as text on top
        long diff =  graph.getEnd().getTime() - graph.getStart().getTime();
        int numDays = (int) java.util.concurrent.TimeUnit.DAYS.convert(diff, java.util.concurrent.TimeUnit.MILLISECONDS);
        String s = "# Days : " + numDays + " # Data : " + graph.getList().size();
        canvas.drawText(s, x1/2, y1-10, paints[5]);

        //Draws grid
        for (int x = 0; x <= xGrid; x++) {
            int xPos = x * x1 / xGrid;
            if (isInt(x, xDiv)) {
                canvas.drawText(graph.getxText()[x / xDiv], xPos, y0 + 75, paints[5]);
                canvas.drawLine(xPos, y0 + 25, xPos, y1, paints[4]);
            } else canvas.drawLine(xPos, y0, xPos, y1, paints[3]);

            for (int y = 0; y <= yGrid; y++) {
                int yPos = y * y1 / yGrid + y0;
                if (isInt(y, yDiv)) {
                    canvas.drawText(y + "", x0 - 50, yPos - 10, paints[5]);
                    canvas.drawText(y * 2 + "", x1 + 50, yPos - 10, paints[5]);
                    canvas.drawLine(x0 - 50, yPos, x1 + 50, yPos, paints[4]);
                } else canvas.drawLine(x0, yPos, x1, yPos, paints[3]);
            }
        }

        //Draw circles for bloodsample and lines for insulin
        for (ModelGraphData d : graph.getList()){
            if (d.getP() == 0) canvas.drawCircle(d.getX() * x1, d.getY() * y1 / graph.getyGrid(), 10, paints[d.getP()]);
            else canvas.drawLine(d.getX() * x1, d.getY() * y1 / graph.getyGrid(), d.getX() * x1, y0, paints[d.getP()]);
        }

    }
}
