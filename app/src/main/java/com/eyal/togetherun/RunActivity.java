package com.eyal.togetherun;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eyal.togetherun.Adapter.ScoreboardRecycleAdapter;
import com.eyal.togetherun.Run.LinesView;
import com.eyal.togetherun.Run.Pair;
import com.eyal.togetherun.Run.PairIndex;
import com.eyal.togetherun.Run.Run;
import com.eyal.togetherun.Run.Runner;
import com.eyal.togetherun.Run.Target.DistanceTarget;
import com.eyal.togetherun.Run.Target.DuratoinTarget;
import com.eyal.togetherun.Run.Target.TargetOfRun;
import com.eyal.togetherun.Run.Target.Time;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;

import java.util.List;
import java.util.Map;


public class RunActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, LocationListener, DatabaseHandler.GetResultLisener, DatabaseHandler.GetUpdatedRunLisener, DatabaseHandler.GetRunnersLisener, DatabaseHandler.RunEndLisener, DatabaseHandler.GetPlacesLisener, GoogleApiClient.ConnectionCallbacks {
    public static final String ARE_YOU_SURE_YOU_WANT_TO_QUIT_THE_RUN = "Are you sure you want to quit the run?";
    public static final double RADIUS = 6378.137;
    public static final int MAP_PREMISSION_REQUEST_CODE = 100;
    public static final int LOCATION_REQUEST_INTERVALS = 3;
    public static final int DELAY_MILLIS_UPDATE_DB = 2000;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    private Context context;
    private Handler handler;
    private Handler handlerUpdateDB;
    private LocationManager locationManager;
    private Run run;
    private Location lastLocation = null;

    Location mLastLocation;
    LocationRequest locationRequest;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;


    private double distanceFromLastPaceUpdate = 0;
    private Pace pace;
    private Time time;
    private int afkSecondsTimer = 0;
    private double distance;
    private boolean runTimer = true;
    private int secondsPassed = 0;
    private TargetOfRun targetOfRun;
    private TextView paceTextView, distanceTextView, timeTextView, finishTheRunTextView, yourResultsTextView;
    private RecyclerView scoreboardRecyclerView;
    private ImageView backBtn;
    private ScoreboardRecycleAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Runner runner;
    private View pb, scoreboardPB;
    private ImageView lockBtn, pauseButton;
    private LinearLayout stopButtonsLinearLayout;
    private boolean canClickBtnStop = false;
    private boolean isLinesOpen = false;
    private UpdateScreenLisener runScreenLisener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);
        context = this;


        targetOfRun = getTargetOfRun();


        if (DatabaseHandler.currentRun == null) {

            DatabaseHandler.createRun(this, new Run(targetOfRun, 1));
        } else {
            onFinish(true);
        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseHandler.currentRun = null;
        finish();

    }

    private TargetOfRun getTargetOfRun() {

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("targetDuration")) {
                Time time = new Time((String) extras.get("targetDuration"));
                return new DuratoinTarget(time);
            } else {
                double distance = Double.valueOf((String) extras.get("targetDistance"));
                return new DistanceTarget(distance);
            }
        }
        return null;
    }

    private void checkForPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            setLocationService();
        } else {
            // Show rationale and request permission.
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MAP_PREMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MAP_PREMISSION_REQUEST_CODE) {
            if (permissions.length == 2 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED && permissions[1] == Manifest.permission.ACCESS_COARSE_LOCATION &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                setLocationService();
            } else {
                finish();
            }
        }
    }


    private void setLocationService() {
        locationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkForPermission();
            return;
        }
        startRunTimer();

    }
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }


    @SuppressLint("MissingPermission")
    private void startRunTimer() {
        final Runnable runnable = new Runnable() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {

                if (!runTimer) return;

                if (targetOfRun.isFinish(distance, time)) {
                    runner.setFinish(true);
                    endOfRun();
                    return;
                }


//                locationManager.removeUpdates(RunActivity.this);
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, RunActivity.this);
//                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, RunActivity.this);

                secondsPassed++;

                updateTime();


                handler.postDelayed(this, 1_000);

            }
        };
        if (runner.getTime() != null) {
            secondsPassed = runner.getTime().getSecondsFromStart();
        }
        handler = new Handler();
        handler.postDelayed(runnable, 1_000);
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();

        startUpdateDBTimer();
    }

    private void startUpdateDBTimer() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (!runTimer)
                    return;

                DatabaseHandler.updatePlace(runner.getUser().getUsername(), runner.getDistance());

                handler.postDelayed(this, DELAY_MILLIS_UPDATE_DB);
            }
        };
        handlerUpdateDB = new Handler();
        handlerUpdateDB.postDelayed(runnable, DELAY_MILLIS_UPDATE_DB);
    }

    private double convertChangeToMeters(double lat1, double lon1, double lat2, double lon2) {
        double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = RADIUS * c;
        return d * 1000; // meters

    }


    @Override
    public void onLocationChanged(Location location) {

        double lat = location.getLatitude();
        double lon = location.getLongitude();

        if (lastLocation != null) {
            double meters = convertChangeToMeters(lastLocation.getLatitude(), lastLocation.getLongitude(), lat, lon);
            updateDistance(meters);

            distanceFromLastPaceUpdate += meters;
            if (secondsPassed % LOCATION_REQUEST_INTERVALS == 0) {

                //update pace
                meters = distanceFromLastPaceUpdate;
                distanceFromLastPaceUpdate = 0;
                double elapsedTime = LOCATION_REQUEST_INTERVALS;
                double calculatedSpeed = calculateSpeed(meters, elapsedTime);
                System.out.println("the speed is: " + calculatedSpeed + " distance: " + meters);
                updatePace(calculatedSpeed);
            }

        }
        this.lastLocation = location;


    }

    /**
     * @param distance meters
     * @param time     seconds
     * @return
     */

    private double calculateSpeed(double distance, double time) {
        distance /= time;
        double calculatedSpeed = distance; //meters / 1 second
        calculatedSpeed *= 3.6; // km / 1h
        if (calculatedSpeed != 0)
            calculatedSpeed = 60 / calculatedSpeed; // min/1km
        return calculatedSpeed;
    }

    public void endOfRun() {
        DatabaseHandler.updatePlace(runner.getUser().getUsername(), runner.getDistance());
        locationManager.removeUpdates(RunActivity.this);
        runTimer = false;

        DatabaseHandler.getUpdatedRun(this);
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("You Finished The Run!");
//        builder.setMessage("Your time: " + time.toString() + "\n" +
//                "Your Distance: " + formatDistance() + "km\n" +
//                "Your Pace:  " + pace.toString());
//        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.show();


    }

    private String formatDistance() {
        return String.format("%.2f", distance);
    }

    private void updateDistance(double newMeters) {
        newMeters = Math.round(newMeters);
        newMeters /= 1000; //convert to 0.meters
        distance += newMeters;
//        distance = round(distance, 2);
        runner.setDistance(distance);
        distanceTextView.setText(runner.formatDistance());
//        tvDis.setText(formatDistance());
    }

    private void updatePace(double calculatedSpeed) {
        if (calculatedSpeed != 0) {
            System.out.println("not 0");
        }
        pace.updateTime(calculatedSpeed);
        runner.setPace(pace);
        paceTextView.setText(pace.toString());
//        tvPace.setText(pace.toString());
    }

    private void updateTime() {
        time.addSecond();
//        tvTime.setText(time.toString());
        runner.setTime(time);
        timeTextView.setText(time.toString());

    }

    private void setPointer() {
        context = this;
        paceTextView = findViewById(R.id.tvPace);
        distanceTextView = findViewById(R.id.tvDistance);
        timeTextView = findViewById(R.id.tvTime);
        scoreboardRecyclerView = findViewById(R.id.recycler_view);
        scoreboardPB = findViewById(R.id.scoreboardPB);
        finishTheRunTextView = findViewById(R.id.finishTheRunTextView);
        yourResultsTextView = findViewById(R.id.yourResultsTextView);
        backBtn = findViewById(R.id.btnBack);
        stopButtonsLinearLayout = findViewById(R.id.stopButtonsLinearLayout);


        DatabaseHandler.currentRun.startRun();
        //DatabaseHandler.currentRun.handleIsTargetDistance();
        DatabaseHandler.setPlaces();
        DatabaseHandler.setFinishersCounterLisener();
        runner = new Runner(DatabaseHandler.user);
        time = new Time();
        pace = new Pace();
        distance = 0;
        DatabaseHandler.updateUserInRun();

        Runner currentRunner = run.getRunner(DatabaseHandler.user);
        paceTextView.setText(currentRunner.getPace().toString());
        distanceTextView.setText(currentRunner.formatDistance());
        timeTextView.setText(currentRunner.getTime().toString());


        /* Stop buttons */
        lockBtn = findViewById(R.id.lockBtn);
        lockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!canClickBtnStop) {
                    canClickBtnStop = true;
                    lockBtn.setImageResource(R.drawable.openlock);

                } else {
                    canClickBtnStop = false;
                    lockBtn.setImageResource(R.drawable.lock_btn);
                }
            }
        });
        pauseButton = findViewById(R.id.stopBtn);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canClickBtnStop) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(ARE_YOU_SURE_YOU_WANT_TO_QUIT_THE_RUN);
                    builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            endOfRun();
                        }
                    });
                    builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }


            }
        });


        DatabaseHandler.getRunners(this);
//        if (!run.getTarget().isByDistance())
//            DatabaseHandler.getUpdateRunEnd(this);
        // we build google api client

        checkForPermission();

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onFinish(boolean result) {

        if (result) {
            if (DatabaseHandler.currentRun != null) {
                run = DatabaseHandler.currentRun;
                setPointer();
            }
        } else {
            DatabaseHandler.createRun(this, new Run(targetOfRun, 1));
        }


    }

    public void setRunScreenLisener(UpdateScreenLisener runScreenLisener) {
        this.runScreenLisener = runScreenLisener;
    }

    @Override
    public void onFinish(List<Runner> runners) {
        scoreboardPB.setVisibility(View.GONE);
        if (adapter == null) {
            run.setNewRunners(run.getMap(runners));
            scoreboardRecyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(context);
            scoreboardRecyclerView.setLayoutManager(layoutManager);
            adapter = new ScoreboardRecycleAdapter(context, run.getScoreboard(), run);
            scoreboardRecyclerView.setAdapter(adapter);
            DatabaseHandler.getPlacesUpdates(this);
        } else {
            handleAdapter(runners);
        }

    }

    @Override
    public void onEnd(Boolean result) {
        if (result == this.run.isEndOfTime())
            return;

        adapter.refreshData(result);

    }

    @Override
    public void onChange(Map<String, Double> places) {

        PairIndex[] changedOrderPlaces = run.getChangeInOrder(places);
        swapItems(changedOrderPlaces);
        run.setNewPlaces(places);
        updateItems(run);

//        int[] itemsChanged = run.getPlacesThatChanged(places);
//        if (run.isChangeInOrder(places)) {
//            run.setNewPlaces(places);
//            adapter.setRun(run);
//            adapter.notifyDataSetChanged();
//        } else {
//            run.setNewPlaces(places);
//            adapter.setRun(run);
//        }
//        if (itemsChanged != null) {
//            updateLinePosition(run.getPlacesSorted(), itemsChanged, null);
//
////                adapter.refreshItem(run.getScoreboard(), run, itemsChanged);
//        }


    }
    private void swapItems(PairIndex[] changeOrderPlaces){
        if (changeOrderPlaces == null)
            return;
        for (int i = 0; i < changeOrderPlaces.length; i++) {
            adapter.notifyItemMoved(changeOrderPlaces[i].before, changeOrderPlaces[i].after);
        }
    }
    private void updateItems(Run run){
        adapter.setRun(run);
        Pair[] places = run.getPlacesSorted();
        for (int i = 0; i < places.length; i++) {
            if(adapter.getItemViewType(i) == ScoreboardRecycleAdapter.NOT_FINISH_TYPE)
                updateLinePosition(i, places[i]);
            else
                adapter.notifyItemChanged(i);
        }
    }

    private void updateLinePosition(int index, Pair place) {
        View view = layoutManager.findViewByPosition(index);
        if (view != null) {
            LinesView linesView = view.findViewWithTag("LinesView");
            linesView.updatePosition(run, place.getDistance());
            TextView usernameTextView = view.findViewById(R.id.usernameTextView);
            usernameTextView.setText(place.getUsername());
        }
    }

    private void handleAdapter(List<Runner> runners) {
        run.setNewRunners(run.getMap(runners));
        updateItems(run);;


    }

    private void updateLinePosition(Pair[] places, int[] itemsChanged, Runner[] runners) {
        if (adapter == null) return;
        for (int i = 0; i < itemsChanged.length; i++) {
            int index = itemsChanged[i];
            View view = layoutManager.findViewByPosition(index);
            assert view != null && layoutManager != null;
            if (layoutManager.getItemViewType(view) != adapter.getItemViewType(index)) {
                if (runners != null){

                    adapter.notifyItemChanged(index);
                }
            } else {
                if (adapter.getItemViewType(index) == ScoreboardRecycleAdapter.NOT_FINISH_TYPE) {
                    LinesView linesView = view.findViewWithTag("LinesView");
                    linesView.updatePosition(run, places[index].getDistance());
                }
            }
        }
    }

    @Override
    public void onGetRun() {

        runner.setDistance(distance);
        runner.setTime(time);
        runner.calculatePace(distance, time);
        runner.updateUserTotals();
        if (!targetOfRun.isByDistance()) {
            if (targetOfRun.isFinish(distance, time)) {
                adapter.refreshData(true);
            }
        }
        run.setRunner(runner);
        run.finishersCounter += 1;
        run.places.put(runner.getUser().getUsername(), runner.generatePlace(run));
        DatabaseHandler.currentRun = run;
//        handleAdapter();
//        DatabaseHandler.updatePlace(runner.getUser().getUsername(), runner.getDistance());
        DatabaseHandler.updateAtEndRun();
//        DatabaseHandler.endOfRun();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                finish();
            }
        });

        stopButtonsLinearLayout.setVisibility(View.GONE);
        backBtn.setVisibility(View.VISIBLE);
        finishTheRunTextView.setVisibility(View.VISIBLE);
        yourResultsTextView.setVisibility(View.VISIBLE);
        DatabaseHandler.exitRun();
//
    }

    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(LOCATION_REQUEST_INTERVALS);
        locationRequest.setFastestInterval(LOCATION_REQUEST_INTERVALS);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) this);
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public interface UpdateScreenLisener {
        //        void onUpdate(Pace pace, Time time, String distance);

        void updateTime(Time time);

        void updatePace(Pace pace);
        void updateDistance(String distance);

    }

}

//