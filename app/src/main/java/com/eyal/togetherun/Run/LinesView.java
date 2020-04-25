package com.eyal.togetherun.Run;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.eyal.togetherun.DatabaseHandler;
import com.eyal.togetherun.R;


import java.util.HashMap;

import java.util.Map;


public class LinesView extends View implements DatabaseHandler.GetPlacesLisener {
    public static int WIDTH_IMAGE = 90;

    private Run run;
    Map<String, Runner> runners;
    Pair[] places;
    int numOfRunners;
    private Paint paint;
    private Map<String, com.eyal.togetherun.Run.Point> runnersPositionOnLine = new HashMap<>(); //username / point
    private Canvas canvas;
    private String showOnlyOneRunnerUsername = null;


    public LinesView(Context context, Run run) {
        super(context);
        this.run = run;
//        this.runners = run.getRunners();
        this.places = run.getPlacesSorted();
        setPointer();

    }

    public LinesView(Context context, Run run, String runnerUsername) {
        super(context);
        this.run = run;
//        this.runners = run.getRunners();
        this.places = run.getPlacesSorted();
        this.showOnlyOneRunnerUsername = runnerUsername;
        setPointer();
    }

    private Pair getIndex(String runnerUsername) {
        for (int i = 0; i < places.length; i++) {
            if (places[i].getUsername().equals(runnerUsername))
                return new Pair(showOnlyOneRunnerUsername, places[i].getDistance());
        }
        return null;
    }

    public LinesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setPointer();
    }

    public LinesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setPointer();

    }

    private void setPointer() {
        if (showOnlyOneRunnerUsername != null) {
            numOfRunners = 1;
            Pair pair = getIndex(showOnlyOneRunnerUsername);
            places = new Pair[]{pair};
        } else {
            numOfRunners = run.getRunners().size();
        }
        paint = new Paint();
//        updateCanvas(canvas, run.getRunners());
//        DatabaseHandler.getPlacesUpdates(this);
    }
    public void refreshData(Map<String, Double> places) {
        run.setNewPlaces(places);
        this.places = this.run.getPlacesSorted();
        if (showOnlyOneRunnerUsername != null) {
            this.places = run.removeAllExcept(showOnlyOneRunnerUsername);
        }
        invalidate();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (places == null || places.length == 0)
            return;

        this.canvas = canvas;
        //verticalLines(canvas);
        horizontalLines(canvas);

    }

    private void horizontalLines(Canvas canvas) {
        float offset = getHeight() / (float) (numOfRunners + 1);
        float yPos = offset;
        float startXPos = getWidth() * 0.1f;
        float endXPos = getWidth() * 0.9f;
        float lengthOfLines = endXPos - startXPos;
        paint.setStrokeWidth(20);
        for (int i = 0; i < places.length; i++) {

            double distance = places[i].getDistance();
            if (run.isFinish(distance)) {
                paint.setColor(getResources().getColor(R.color.finish_run));
            } else if (run.isDropped(distance)){
                paint.setColor(getResources().getColor(R.color.dropped_from_run));
            }else {
                paint.setColor(getResources().getColor(R.color.in_run));
            }
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.com_facebook_profile_picture_blank_portrait);
            Point imagePosPoint = calculateHorizontalPosition(distance, yPos, startXPos, lengthOfLines);

            this.runnersPositionOnLine.put(places[i].getUsername(), imagePosPoint);

            if (showOnlyOneRunnerUsername == null){
                Paint textPaint = new Paint();
                textPaint.setTextAlign(Paint.Align.LEFT);
                textPaint.setTextSize(100);
                Rect bounds = new Rect();
                String mText = places[i].getUsername();
                textPaint.getTextBounds(mText, 0, mText.length(), bounds);
                int height = bounds.height();
                float xPosText = startXPos + lengthOfLines / 2f - bounds.width() / 2f;
                float yPosText = yPos - height;
                canvas.drawText(mText, xPosText, yPosText, textPaint);
            }
            canvas.drawLine(startXPos, yPos, endXPos, yPos, paint);
            canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, WIDTH_IMAGE, WIDTH_IMAGE, false), imagePosPoint.getX(), imagePosPoint.getY(), null);


            yPos += offset;
        }
    }


    private void verticalLines(Canvas canvas) {

        float offset = getWidth() / (float) (numOfRunners + 1);
        float xPos = offset;
        float startYPos = getHeight() * 0.1f;
        float endYPos = getHeight() * 0.8f;
        float heightOfLines = endYPos - startYPos;
        paint.setStrokeWidth(20);

        for (String username : runners.keySet()) {

            Runner runner = runners.get(username);

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.com_facebook_profile_picture_blank_portrait);
            Point imagePosPoint = calculateVerticalPosition(runner, xPos, startYPos, heightOfLines);
            if (this.runnersPositionOnLine.containsKey(runner.getUser().getUid())) {
//                clearImage(this.runnersPositionOnLine.get(runner.getUser().getUid()));
            }
            this.runnersPositionOnLine.put(runner.getUser().getUid(), imagePosPoint);
            canvas.drawLine(xPos, startYPos, xPos, endYPos, paint);
            canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, WIDTH_IMAGE, WIDTH_IMAGE, false), imagePosPoint.getX(), imagePosPoint.getY(), null);


            xPos += offset;
        }
    }

    private Point calculateHorizontalPosition(double distance, float yPos, float startXPos, float lengthOfLines) {
        float passedDistance = (float) (distance) / (float) (run.getTarget().getDistance()); // passed / target
        passedDistance = passedDistance > 1 ? 1 : passedDistance;
        float yPosImage = yPos - (float) WIDTH_IMAGE / 2f;
        float xPosImage = startXPos + (lengthOfLines * passedDistance) - ((float) WIDTH_IMAGE / 2f);
        return new Point(xPosImage, yPosImage);
    }

    private Point calculateVerticalPosition(Runner runner, float xPos, float startYPos, float heightOfLines) {
        float passedDistance = (float) (runner.getDistance()) / (float) (run.getTarget().getDistance()); // passed / target
        passedDistance = passedDistance > 1 ? 1 : passedDistance;
        float xPosImage = xPos - (float) WIDTH_IMAGE / 2;
        float yPosImage = startYPos + (heightOfLines * passedDistance) - ((float) WIDTH_IMAGE / 2f);
        return new Point(xPosImage, yPosImage);
    }

    private void clearImage(Point point) {
        Paint clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawRect(point.getX(), point.getY(), WIDTH_IMAGE, WIDTH_IMAGE, clearPaint);
    }


//    @Override
//    public void onChange(Map<String, Runner> runners) {
////        Map<String, Runner> lastRunners = run.getRunners();
////        Map<String, Runner> newRunners = new HashMap<>();
////
////        //find the runners that moved
////        for (String username : lastRunners.keySet()) {
////            Runner runner = runners.get(username);
////            if (runner != null) {
////                if (lastRunners.get(username).getDistance() != runner.getDistance()) { //change in distance
////                    newRunners.put(username, runner);
////                }
////            }
////        }
//        this.run.setNewRunners(runners);
//        this.runners = runners;
//        if (showOnlyOneRunnerUsername != null) {
//            removeAllExcept();
//        }
////        this.runners = newRunners;
////        if (newRunners.size() != 0)
//        invalidate();
//
//
//    }



    @Override
    public void onChange(Map<String, Double> places) {
        run.setNewPlaces(places);
        this.places = this.run.getPlacesSorted();
        if (showOnlyOneRunnerUsername != null) {
            this.places = run.removeAllExcept(showOnlyOneRunnerUsername);
        }
        invalidate();

    }

    public void updatePosition(Run run, double distance) {
        this.run = run;
        if (showOnlyOneRunnerUsername != null) {
            this.places = new Pair[]{new Pair(showOnlyOneRunnerUsername, distance)};
            invalidate();
        }
    }
}
