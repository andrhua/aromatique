package com.kekonyan.aromatique.state;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.kekonyan.aromatique.R;
import com.kekonyan.aromatique.UI.Button;
import com.kekonyan.aromatique.core.Assets;
import com.kekonyan.aromatique.util.Const;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

class StatsState extends BasicState {
    private Button back, time, money, deaths;
    private enum State {TIME, MONEY, DEATHS}
    private State state;
    private PieChart timeSpentChart, moneyChart;
    private HorizontalBarChart deathsByLevelsChart, timeByLevelsChart;
    private LinearLayout deathsLL, timeLL, moneyLL;
    private PieData timePieData;

    private void setState(State state){
        time.setIdleState();
        money.setIdleState();
        deaths.setIdleState();
        this.state=state;
        switch (state){
            case TIME: {
                time.setActiveState();
                timeSpentChart.animateY(2000);
                timeByLevelsChart.animateXY(2000, 2000);
            } break;
            case MONEY: {
                money.setActiveState();
                moneyChart.animateY(2000);
            } break;
            case DEATHS:{
                deaths.setActiveState();
                deathsByLevelsChart.animateXY(2000, 2000);
            } break;
        }
    }

    StatsState(Context context, StateManager stateManager) {
        super(context,stateManager);
    }

    @Override
    public void preload() throws IOException {
        int b=Const.BUTTON_BMP;
        back=new Button(Assets.buttons[Assets.Button.BACK.ordinal()], b/2, height-b, b/2, 0, false);
        time=new Button(context.getString(R.string.time), width/4, 7*height/24, Assets.regularPaint, Color.TRANSPARENT, Color.rgb(195,195,195), true);
        money=new Button(context.getString(R.string.money), width/2, 7*height/24, Assets.regularPaint, Color.TRANSPARENT, Color.rgb(195,195,195), true);
        deaths=new Button(context.getString(R.string.deaths), 3*width/4, 7*height/24, Assets.regularPaint, Color.TRANSPARENT, Color.rgb(195,195,195), true);
        //time in game & in menu
        timeSpentChart=new PieChart(context);
        ArrayList<Entry> timeSpentEntries=new ArrayList<>(2);
        if (playerData.timeInGame>0) timeSpentEntries.add(new Entry(playerData.timeInGame, 0));
        timeSpentEntries.add(new Entry(playerData.timeInMenu, 1));
        PieDataSet timePDS=new PieDataSet(timeSpentEntries, context.getString(R.string.of_all_spent_time));
        timePDS.setColors(ColorTemplate.LIBERTY_COLORS);
        ArrayList<String> timeSpentLabels=new ArrayList<>(2);
        if (playerData.timeInGame>0) timeSpentLabels.add(context.getString(R.string.in_game));
        timeSpentLabels.add(context.getString(R.string.look_selection));
        timePieData=new PieData(timeSpentLabels, timePDS);
        timeSpentChart.setDrawSliceText(false);
        timeSpentChart.setData(timePieData);
        timeSpentChart.setMinimumWidth(width/2);
        timeSpentChart.setMinimumHeight(height/2);
        timeSpentChart.setUsePercentValues(true);
        timeSpentChart.setDescription(context.getString(R.string.wasting_life));
        long millis=playerData.timeInGame+playerData.timeInMenu;
        timeSpentChart.setCenterText(context.getString(R.string.total)+
                String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis)- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)))
        );
        //time by levels
        timeByLevelsChart=new HorizontalBarChart(context);
        ArrayList<BarEntry> timeByLevelsEntries=new ArrayList<>(6);
        for (int i=0; i<6; i++) timeByLevelsEntries.add(new BarEntry(TimeUnit.MILLISECONDS.toSeconds(playerData.timeByLevels[i]), i));
        BarDataSet timeBDS=new BarDataSet(timeByLevelsEntries, context.getString(R.string.of_seconds));
        timeBDS.setColors(ColorTemplate.VORDIPLOM_COLORS);
        ArrayList<String> timeByLevelsLabels=new ArrayList<>(6);
        for (int i=0; i<6; i++) timeByLevelsLabels.add(context.getResources().getStringArray(R.array.levelName)[i]);
        BarData timeBarData=new BarData(timeByLevelsLabels, timeBDS);
        timeByLevelsChart.setData(timeBarData);
        timeByLevelsChart.setMinimumWidth(width/2);
        timeByLevelsChart.setMinimumHeight(height/2);
        timeByLevelsChart.setDescription(context.getString(R.string.time_wasted));
        timeByLevelsChart.setDescriptionPosition(9*width/20, 49*height/100);
        timeLL=new LinearLayout(context);
        timeLL.setOrientation(LinearLayout.HORIZONTAL);
        timeLL.addView(timeByLevelsChart);
        timeLL.addView(timeSpentChart);
        timeLL.setGravity(Gravity.CENTER);
        timeLL.measure(width,height);
        timeLL.layout(0,0,width,height);
        //money
        moneyChart=new PieChart(context);
        ArrayList<Entry> moneyEntries=new ArrayList<>(2);
        if (playerData.wardrobeCost>0) moneyEntries.add(new Entry(playerData.wardrobeCost, 0));
        if (playerData.inventoryCost>0) moneyEntries.add(new Entry(playerData.inventoryCost, 0));
        moneyEntries.add(new Entry(playerData.money.longValue(), 0));
        PieDataSet moneyPDS=new PieDataSet(moneyEntries, context.getString(R.string.dollars));
        moneyPDS.setColors(ColorTemplate.PASTEL_COLORS);
        ArrayList<String> moneyLabels=new ArrayList<>(3);
        moneyLabels.add(context.getString(R.string.inventory_title));
        moneyLabels.add(context.getString(R.string.wardrobe_title));
        moneyLabels.add(context.getString(R.string.personal_capital));
        PieData moneyPieData=new PieData(moneyLabels, moneyPDS);
        moneyPieData.setValueFormatter(new LargeValueFormatter());
        moneyChart.setDrawSliceText(false);
        moneyChart.setData(moneyPieData);
        moneyChart.setMinimumWidth(width/2);
        moneyChart.setMinimumHeight(height/2);
        moneyChart.setDescription(context.getString(R.string.cost_of_u));
        moneyLL=new LinearLayout(context);
        moneyLL.addView(moneyChart);
        moneyLL.setOrientation(LinearLayout.HORIZONTAL);
        moneyLL.setGravity(Gravity.CENTER);
        moneyLL.measure(width,height);
        moneyLL.layout(width/2,0,width,height);
        //deaths by levels
        deathsByLevelsChart=new HorizontalBarChart(context);
        ArrayList<BarEntry> deathsByLevelsEntries=new ArrayList<>(6);
        for (int i=0; i<6; i++) deathsByLevelsEntries.add(new BarEntry(playerData.deathsByLevels[i], i));
        BarDataSet bds=new BarDataSet(deathsByLevelsEntries, context.getString(R.string.of_deaths));
        bds.setColors(ColorTemplate.JOYFUL_COLORS);
        ArrayList<String> deathsByLevelsLabels=new ArrayList<>(6);
        for (int i=0; i<6; i++) deathsByLevelsLabels.add(context.getResources().getStringArray(R.array.levelName)[i]);
        BarData barData=new BarData(deathsByLevelsLabels, bds);
        barData.setValueFormatter(new LargeValueFormatter());
        deathsByLevelsChart.setData(barData);
        deathsByLevelsChart.setMinimumWidth(width/2);
        deathsByLevelsChart.setMinimumHeight(height/2);
        deathsByLevelsChart.setDescription(context.getString(R.string.most_difficult_level));
        deathsByLevelsChart.setDescriptionPosition(9*width/20, 49*height/100);
        deathsLL =new LinearLayout(context);
        deathsLL.setOrientation(LinearLayout.HORIZONTAL);
        deathsLL.addView(deathsByLevelsChart);
        deathsLL.setGravity(Gravity.CENTER);
        deathsLL.measure(width,height);
        deathsLL.layout(0,0,width,height);
        setState(State.TIME);
    }

    @Override
    public void render(Canvas canvas) {
        super.render(canvas);
        time.render(canvas);
        money.render(canvas);
        deaths.render(canvas);
        canvas.drawText(context.getString(R.string.stats_title), width/2, height/8, Assets.headerPaint);
        canvas.save();
        canvas.translate(0, height / 10);
        switch (state){
            case TIME: {
                timeLL.draw(canvas);
                canvas.drawLine(width/2, 3*height/10, width/2, 7*height/10, Assets.hintPaint);
            } break;
            case MONEY: {
                moneyLL.draw(canvas);
                canvas.drawText(context.getString(R.string.total_)+playerData.money.longValue()+context.getString(R.string.dollars), 3*width/4, height/3, Assets.hintPaint);
                canvas.drawLine(7*width/10, 40*height/100, 4*width/5, 40*height/100, Assets.hintPaint);
                canvas.drawText(context.getString(R.string.total_gained)+playerData.totalGainedMoney+context.getString(R.string.dollars), 3*width/4, height/2, Assets.hintPaint);
                canvas.drawLine(7*width/10, 56*height/100, 4*width/5, 56*height/100, Assets.hintPaint);
                canvas.drawText(context.getString(R.string.total_spent)+playerData.totalSpentMoney+context.getString(R.string.dollars), 3*width/4, 2*height/3, Assets.hintPaint);
            } break;
            case DEATHS: {
                deathsLL.draw(canvas);
                canvas.drawLine(width/2, 3*height/10, width/2, 7*height/10, Assets.hintPaint);
            } break;
        }
        canvas.restore();
        back.render(canvas);
    }

    @Override
    public void update(float elapsedTime) {
        switch (state){
            case TIME: {
                long millis=playerData.timeInGame+playerData.timeInMenu;
                timeSpentChart.setCenterText(context.getString(R.string.total)+
                        String.format("%02d:%02d:%02d",
                                TimeUnit.MILLISECONDS.toHours(millis),
                                TimeUnit.MILLISECONDS.toMinutes(millis)- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));
                timePieData.removeEntry(1, 0);
                timePieData.addEntry(new Entry(playerData.timeInMenu, 1), 0);
                timeSpentChart.notifyDataSetChanged();
                timeSpentChart.invalidate();
            } break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_UP: {
                if (back.onTouch(view,motionEvent)) getStateManager().setState(StateManager.State.MENU, false);
                if (time.onTouch(view,motionEvent)) setState(State.TIME);
                if (money.onTouch(view,motionEvent)) setState(State.MONEY);
                if (deaths.onTouch(view,motionEvent)) setState(State.DEATHS);
            } break;
            default: {
                back.onTouch(view,motionEvent);
                time.onTouch(view, motionEvent);
                money.onTouch(view, motionEvent);
                deaths.onTouch(view, motionEvent);
            }
        }
        return true;
    }

    @Override
    public boolean onBackPressed() {
        getStateManager().setState(StateManager.State.MENU, false);
        return true;
    }

}
