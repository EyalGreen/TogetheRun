package com.eyal.togetherun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.eyal.togetherun.Fragments.GetUsernameFragment;
import com.eyal.togetherun.Fragments.RequestListFragment;
import com.eyal.togetherun.Fragments.RunLinesFragment;
import com.eyal.togetherun.Run.Run;
import com.eyal.togetherun.RunSettingsViews.DistanceView;
import com.eyal.togetherun.RunSettingsViews.DurationView;
import com.eyal.togetherun.Fragments.PartyModeFragment;
import com.eyal.togetherun.RunSettingsViews.TargetOfRunView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements DatabaseHandler.GetResultLisener, DatabaseHandler.GetUsernameDialogLisener, GetUsernameFragment.ConfirmLisener, OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, DatabaseHandler.FinishHandlingUser {
    public static String DISTANCE_STR = "Target Distance";
    public static String DURATION_STR = "Target Duration";
    public static Bitmap DEAFULT_ICON;
    public static final int MAP_PREMISSION_REQUEST_CODE = 100;
    Context context;
    private GoogleMap mMap;
    private LinearLayout mainLayout;
    private Location location;
    private LocationManager locationManager;
    private String selectedMode;
    private ImageView requestBell;
    private NotificationControl notificationControl;
    private TargetOfRunView targetOfRunView;
    private ProgressBar pb;

    public void changeBell(boolean hasRequest) {
        if (hasRequest){
            this.requestBell.setImageResource(R.drawable.friendsinbox);
        }else{
            this.requestBell.setImageResource(R.drawable.empty_bell);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        mainLayout = findViewById(R.id.mainLayout);
        pb = findViewById(R.id.progress_bar);
        DEAFULT_ICON  = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.defualt_pic);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        findViewById(R.id.logoutbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });
        targetOfRunView = findViewById(R.id.targetOfRunView);
        targetOfRunView.setActivity(this);
        findViewById(R.id.btnParty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openPartyFragment();
            }
        });


        findViewById(R.id.btnSolo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedMode = targetOfRunView.getSelectedMode();
                Intent intent = new Intent(context, RunActivity.class);
                if (selectedMode.equals(DISTANCE_STR)) {
                    DistanceView distanceView = targetOfRunView.findViewWithTag("targetView");
                    intent.putExtra("targetDistance", distanceView.getValue());

                } else if (selectedMode.equals(DURATION_STR)) {
                    DurationView durationView = targetOfRunView.findViewWithTag("targetView");
                    intent.putExtra("targetDuration", durationView.getValue());
                }

                startActivity(intent);
            }
        });


        requestBell = findViewById(R.id.ivNotificationInbox);
        requestBell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInbox();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(NotificationControl.OPEN_REQUEST_LIST))
                openInbox();
        }


    }
    private void openInbox(){
        RequestListFragment dialogFragment = new RequestListFragment(this.getSupportFragmentManager());
        dialogFragment.setCancelable(true);
        dialogFragment.show(getSupportFragmentManager(), "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseHandler.setUserOffline();
        DatabaseHandler.currentRun = null;
        DatabaseHandler.endOfRun();
        DatabaseHandler.quitRun();

        finish();
        finish();
    }



    private void openPartyFragment() {
        selectedMode = targetOfRunView.getSelectedMode();

        PartyModeFragment dialogFragment = new PartyModeFragment();
        dialogFragment.setCancelable(true);
        dialogFragment.setArguments(targetOfRunView.getValue());
        dialogFragment.show(getSupportFragmentManager(), "");

    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        pb.setVisibility(View.VISIBLE);
        DatabaseHandler.create();
        DatabaseHandler.checkIfUserAlreadyRegister(this, this);


    }

    private void checkForPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mapIsReady();
        } else {
            // Show rationale and request permission.
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MAP_PREMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MAP_PREMISSION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                mapIsReady();
            } else {
                checkForPermission();
            }
        }
    }

    private void mapIsReady() {
        mMap.setMyLocationEnabled(true);

        setLocationService();


    }

    private void setLocationService() {
        locationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkForPermission();
            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        centerCamera();
    }

    private void centerCamera() {
        if (location != null) {
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(17).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        } else {
            setLocationService();
        }
    }


    @Override
    public void getUsername() {
        //open dialog username
        GetUsernameFragment dialogFragment = new GetUsernameFragment(this);
        dialogFragment.setCancelable(true);
        dialogFragment.show(getSupportFragmentManager(), "");
    }

    @Override
    public void onConfirm(String username) {
        DatabaseHandler.addUser(username, this);
    }

    @Override
    public void onCancle() {
        getUsername();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

    }

    @Override
    public boolean onMyLocationButtonClick() {

        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onFinish() {

        pb.setVisibility(View.GONE);
        findViewById(R.id.meBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, MeActivity.class));
            }
        });
        notificationControl = new NotificationControl(this, this);
        notificationControl.closeNotifications();

        if (DatabaseHandler.user.isInGame()){
//            DatabaseHandler.setCurrentRun(DatabaseHandler.user.getRunUid(), this);
        }



        checkForPermission();
    }


    /**
     * Finish setting the run
     * @param result
     */
    @Override
    public void onFinish(boolean result) {
        if (result){
            startActivity(new Intent(context, RunActivity.class));
        }else{
            DatabaseHandler.setCurrentRun(DatabaseHandler.user.getRunUid(), this);
        }
    }
}
