package com.eyal.togetherun;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eyal.togetherun.Adapter.MeRecyclerAdapter;
import com.eyal.togetherun.Fragments.FriendModeListFragment;
import com.eyal.togetherun.Fragments.GetUsernameFragment;
import com.eyal.togetherun.Run.Run;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.UUID;

public class MeActivity extends AppCompatActivity implements DatabaseHandler.GetUsernameDialogLisener, DatabaseHandler.GetAllRuns, GetUsernameFragment.ConfirmLisener, DatabaseHandler.FinishHandlingUser {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Context context;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private TextView userNameTv, rankTv, runningTimeTv, totalKmTv, averagePaceTv;
    private ImageView profilePicImageView, changePictureImageView;
    private ProgressBar progressBarRecords, progressBarUsername, progressbarProfilePic;
    private Button uploadBtn;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;

    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        setPointer();
    }

    private void setPointer() {
        context = this;
        progressBarUsername = findViewById(R.id.progressBarUsername);
        progressBarRecords = findViewById(R.id.progress_bar);
        progressbarProfilePic = findViewById(R.id.profilePicProgressbar);
        progressBarRecords.setVisibility(View.VISIBLE);
        userNameTv = findViewById(R.id.userNameTextView);
        rankTv = findViewById(R.id.rankTextView);
        runningTimeTv = findViewById(R.id.runningTimeTextView);
        totalKmTv = findViewById(R.id.totalKmTextView);
        averagePaceTv = findViewById(R.id.avePaceTextView);
        profilePicImageView = findViewById(R.id.profilePicImageView);
        changePictureImageView = findViewById(R.id.changePictureImageView);
        uploadBtn = findViewById(R.id.uploadBtn);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");


        changePictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openFileChooser();
            }
        });

        findViewById(R.id.friendsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFriendsFragment();
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Save Is Not Working", Toast.LENGTH_SHORT).show();
                changePictureImageView.setVisibility(View.VISIBLE);
                uploadBtn.setVisibility(View.GONE);
//                changePictureImageView.setVisibility(View.GONE);
//                if (mUploadTask != null && mUploadTask.isInProgress()) {
//                    Toast.makeText(context, "Upload in progress", Toast.LENGTH_SHORT).show();
//                } else {
//                    uploadImage();
//                }
            }
        });

        User user = DatabaseHandler.user;
        userNameTv.setText(user.getUsername());
        rankTv.setText(String.valueOf(user.getRank()));
        runningTimeTv.setText(user.getRunningTimeInMinutes() + " Minutes " + user.getRunningTimeInSeconds() + " Seconds");
        totalKmTv.setText(user.getTotalKmFormatted() + " Kilometers");
        averagePaceTv.setText(user.getAveragePace().toString());


        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        DatabaseHandler.getAllRuns(this);

        findViewById(R.id.changeUsername).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUsername();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

    }

    private void openFriendsFragment() {

        FriendModeListFragment dialogFragment = new FriendModeListFragment(false);
        dialogFragment.setCancelable(true);
        dialogFragment.show(getSupportFragmentManager(), "");

    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null & data.getData() != null) {
            mImageUri = data.getData();


            Picasso.with(context).load(mImageUri).into(profilePicImageView);
            uploadBtn.setVisibility(View.VISIBLE);
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
    public void onFinish(List<Run> runs) {
        if (runs != null) {
            progressBarRecords.setVisibility(View.GONE);
            MeRecyclerAdapter adapter = new MeRecyclerAdapter(context, runs);
            recyclerView.setAdapter(adapter);
        } else {
            DatabaseHandler.getAllRuns(this);
        }

    }

    @Override
    public void onConfirm(String username) {
        progressBarUsername.setVisibility(View.VISIBLE);
        userNameTv.setText(username);
        DatabaseHandler.addUser(username, this);
    }

    @Override
    public void onCancle() {

    }

    @Override
    public void onFinish() {
        progressBarUsername.setVisibility(View.GONE);
    }


    //get the extension of the file
    //example: pic.jpeg -> jpeg
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressbarProfilePic.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(context, "Upload successful", Toast.LENGTH_SHORT).show();
                            UploadImage upload = new UploadImage("Profile Pic".trim(),
                                    taskSnapshot.getUploadSessionUri().toString());
                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);
                            uploadBtn.setVisibility(View.GONE);
                            changePictureImageView.setVisibility(View.VISIBLE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressbarProfilePic.setProgress((int) progress);
                        }
                    });

        }
    }

    private void uploadImage() {
        if (mImageUri != null) {

            // Code for showing progressDialog while uploading


            // Defining the child of storageReference
            StorageReference ref
                    = mStorageRef.child("images/" + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressbarProfilePic.setProgress(0);
                        }
                    }, 500);

                    Toast.makeText(context, "Upload successful", Toast.LENGTH_SHORT).show();
                    UploadImage upload = new UploadImage("Profile Pic".trim(),
                            taskSnapshot.getUploadSessionUri().toString());
                    String uploadId = mDatabaseRef.push().getKey();
                    mDatabaseRef.child(uploadId).setValue(upload);
                    uploadBtn.setVisibility(View.GONE);
                    changePictureImageView.setVisibility(View.VISIBLE);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressbarProfilePic.setProgress((int) progress);
                        }
                    });

        }
    }

}
