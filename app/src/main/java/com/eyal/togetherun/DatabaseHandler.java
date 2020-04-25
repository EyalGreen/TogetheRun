package com.eyal.togetherun;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eyal.togetherun.Request.FriendRequest;
import com.eyal.togetherun.Request.Request;
import com.eyal.togetherun.Request.RunRequest;
import com.eyal.togetherun.Run.Run;
import com.eyal.togetherun.Run.Runner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DatabaseHandler {
    static FirebaseUser currentUser;
    static FirebaseDatabase database;
    static FirebaseAuth mAuth;
    public static User user;
    public static Run currentRun;
    static ValueEventListener valueEventListenerCheckIfUsernameUniq;
    static ValueEventListener valueEventListenerGetListOfFriends;
    static ValueEventListener valueEventListenerSearchUsername;
    static ValueEventListener lisenerValue;
    public static boolean joinRun = false;

    public static void create() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    public static void addUser(String username, FinishHandlingUser finishHandlingUser) {
        user.setUsername(username);
//        if (user.getBitmapImage() == null) {
//            user.setBitmap(MainActivity.DEAFULT_ICON);
//        }
        final DatabaseReference myRef = database.getReference().child("users");
        myRef.child(user.getUid()).setValue(user);
        DatabaseHandler.setUserOnline();
        if (finishHandlingUser != null) {
            finishHandlingUser.onFinish();
        }
    }

    public static void addUser(String username) {
        addUser(username, null);
    }

    public static void addFriend(String username, String uid) {
        user.addFriend(username, uid);
        addUser(user);
    }

    public static void addCurrentUserAsFriend(String uid) {
        final DatabaseReference ref = database.getReference().child("users").child(uid).child("friends").child(user.getUsername());
        ref.setValue(user.getUid());
    }

    public static void addUser(User user) {
//        if (user.getBitmapImage() == null) {
//            user.setBitmap(MainActivity.DEAFULT_ICON);
//        }
        final DatabaseReference myRef = database.getReference().child("users");
        myRef.child(user.getUid()).setValue(user);
//        updateFriends();
    }

    //    public static void sendFriendRequest(final String friendUid){
//        final DatabaseReference ref = database.getReference().child("users").child( friendUid);
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User friendUser = dataSnapshot.getValue(User.class);
//                assert friendUser != null;
//                FriendRequest friendRequest = new FriendRequest(user.getUsername(), user.getUid());
//                friendUser.addRequest(friendRequest);
//                try{
//                    ref.setValue(friendUser);
//
//                }catch (Exception e){
//                    System.out.println(e.getMessage());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }
//    public static void sendRunRequest(User friendTarget, RunRequest request) {
//        DatabaseReference ref = database.getReference().child("users").child(friendTarget.getUid()).child("requests").child(request.getTypeOfRequest() + request.getSenderUid());
//        ref.setValue(request);
//    }
    public static void sendRunRequest(RunRequest request, String uIdTarget) {
        DatabaseReference ref = database.getReference().child("users").child(uIdTarget).child("requests").child(request.getTypeOfRequest() + "_" + request.getSenderUid());
        try {
            ref.setValue(request);
            currentRun.addRunRequestUid(uIdTarget);
            updateRun(null);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static void sendRequest(Request request, String uIdTarget) {
        DatabaseReference ref = database.getReference().child("users").child(uIdTarget).child("requests").child(request.getTypeOfRequest() + "_" + request.getSenderUid());
        try {
            ref.setValue(request);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static void checkIfUserAlreadyRegister(final GetUsernameDialogLisener lisener, final FinishHandlingUser finishHandlingUser) {
        user = new User(currentUser.getUid());
        user.setEmail(currentUser.getEmail());


        final DatabaseReference myRef = database.getReference().child("users");

//        myRef.child(currentUser.getUid()).setValue(user);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (currentUser == null) {
                    create();
                    return;
                }

                if (snapshot.hasChild(currentUser.getUid())) {
                    // run some code
                    DatabaseHandler.user = snapshot.child(currentUser.getUid()).getValue(User.class);
                    DatabaseHandler.setUserOnline();
                    updateFriends();
                    finishHandlingUser.onFinish();
//                    Toast.makeText(context, user.getUsername(), Toast.LENGTH_SHORT).show();
                } else {
//                    setUser();
//                    addUser(user);
//                    Toast.makeText(context, "added", Toast.LENGTH_SHORT).show();
                    if (lisener != null) {
                        lisener.getUsername();
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public static void isUsernameExist(final SearchFriendLisener lisener, final String tUsername) {
        if (lisener == null) return;


        valueEventListenerSearchUsername = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    if (user != null) {
                        if (user.getUsername().equals(tUsername)) {
                            if (DatabaseHandler.user.isFriend(user.getUsername()) || DatabaseHandler.user.isThereRequest(user.getUid())) {
                                //already your friend or already sent you a request
                                lisener.onFinish(false, null);
                                return;
                            }
                            lisener.onFinish(true, user.getUid());
                            return;
                        }
                    }

                }
                lisener.onFinish(false, null);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                lisener.onFinish(false, null);
            }
        };
        DatabaseReference mRef = database.getReference("users");
        mRef.addValueEventListener(valueEventListenerSearchUsername);
    }

    public static void getListOfAllFriends(final GetListOfFriendsLisener lisener) {
        if (lisener == null) return;


        valueEventListenerGetListOfFriends = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> friends = new ArrayList<>();
                DataSnapshot friendsSnapshot = dataSnapshot.child(user.getUid()).child("friends");
                for (DataSnapshot postSnapshot : friendsSnapshot.getChildren()) {
                    String friendUid = postSnapshot.getValue(String.class);
                    if (friendUid != null) {
                        User friend = dataSnapshot.child(friendUid).getValue(User.class);
                        friends.add(friend);
                    }

                }
                lisener.onFinish(friends);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                lisener.onFinish(null);
            }
        };
        DatabaseReference mRef = database.getReference("users");
        mRef.addValueEventListener(valueEventListenerGetListOfFriends);
    }

    public static void getListOfAllRequests(final GetListOfRequestsLisener lisener) {
        final DatabaseReference ref = database.getReference().child("users").child(user.getUid()).child("requests");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<Request> requests = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String typeOfRequest = Objects.requireNonNull(snapshot.getKey()).split("_")[0];
                    if (typeOfRequest.equals(RunRequest.RUN_REQUEST_TYPE)) {
                        RunRequest request = snapshot.getValue(RunRequest.class);
                        if (request != null) {
//                            request.handleIsTargetDistance();
                            requests.add(request);
                        }
                    } else {
                        FriendRequest request = snapshot.getValue(FriendRequest.class);
                        requests.add(request);
                    }
                }
                user.setRequestsFromList(requests);
                if (lisener != null) {
                    lisener.onFinish(requests);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (lisener != null) {
                    lisener.onFinish(null);
                }
            }
        });
    }

    public static void checkIsUniqUsername(final String usernameValue, final CheckIsUniqUsernameLisener lisener) {
        if (lisener == null) return;


        valueEventListenerCheckIfUsernameUniq = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User cUser = postSnapshot.getValue(User.class);
                    if (cUser != null) {
                        if (cUser.getUsername().equals(usernameValue)) {
                            lisener.onFinish(false);
                            return;
                        }

                    }

                }
                lisener.onFinish(true); //result
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                lisener.onFinish(false); //result
            }
        };
        DatabaseReference ref = database.getReference("users");
        ref.addValueEventListener(valueEventListenerCheckIfUsernameUniq);

    }

    public static void removeEvenedListener() {
        if (valueEventListenerCheckIfUsernameUniq != null) {
            DatabaseReference ref = database.getReference("users");
            ref.addValueEventListener(valueEventListenerCheckIfUsernameUniq);
        }

    }


    private static void updateFriends() {
        if (user == null) return;
        DatabaseReference ref = database.getReference().child("users").child(user.getUid()).child("friends");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, String> friends = new HashMap<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    String value = snapshot.getValue(String.class);
                    friends.put(key, value);

                }
                user.setFriends(friends);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void deleteRequest(final Request request, final DeleteRequestLisener lisener) {
        if (currentRun != null && request.getTypeOfRequest().equals(RunRequest.RUN_REQUEST_TYPE)) {
            RunRequest runRequest = (RunRequest) request;
            currentRun.deleteRunRequestUid(runRequest.getRunUid());
            updateRun(null);
        }
        Map<String, String> value = new HashMap<>();
        value.put(request.getTypeOfRequest() + request.getSenderUid(), null);
        DatabaseReference ref = database.getReference().child("users").child(user.getUid())
                .child("requests").child(request.getTypeOfRequest() + "_" + request.getSenderUid());

        ref.setValue(value, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (lisener != null) {

                    lisener.onFinish(request);
                }
            }
        });

    }

    public static void deleteRequestThatThisUserSent(final Request request, String targetUid) {
        DatabaseReference ref = database.getReference().child("users").child(targetUid)
                .child("requests")
                .child(request.getTypeOfRequest() + user.getUid());
        if (ref != null) {
            ref.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                }
            });

        }
    }

    public static void checkIfEmailExist(final String email, final GetResultLisener lisener) {
        DatabaseReference ref = database.getReference().child("users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        if (user.getEmail().equals(email)) {
                            lisener.onFinish(true);
                            return;
                        }

                    }
                }
                lisener.onFinish(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                lisener.onFinish(false);
            }
        });
    }

    public static void sendEmailToChangePassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            System.out.println("email send");
                        }
                    }
                });
    }


    public static void setUserOnline() {
        if (currentUser != null) {
            user.setOnline(true);
            DatabaseReference ref = database.getReference().child("users").child(currentUser.getUid()).child("online");
            ref.setValue(true);

        } else {
            create();
        }
    }

    public static void setUserOffline() {
        if (currentUser != null) {
            DatabaseReference ref = database.getReference().child("users").child(currentUser.getUid()).child("online");
            ref.setValue(false);
        } else {
            create();
        }
    }

    public static void createRun(final GetResultLisener lisener, final Run run) {
        if (currentRun != null) {
            return;
        }
        currentRun = run;
        final DatabaseReference ref = database.getReference().child("runs").child(run.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Run run = dataSnapshot.getValue(Run.class);
                    if (currentRun.equals(run)) //the same run.
                    {
                        currentRun.addRunners(run.getRunners());
                        return;
                    }
                    //Run already exists, generate new UID;
                    DatabaseHandler.currentRun.generateUid();
                    createRun(lisener, currentRun);
                    return;
                }

                currentRun.addRunner(new Runner(user));
                ref.setValue(currentRun, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        lisener.onFinish(true);
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                lisener.onFinish(false);
            }
            //More auto generated code
        });


    }

    //todo: Delete run requests if they are expired
    public static void getRunners(final GetRunnersLisener lisener) {
        DatabaseReference ref = database.getReference().child("runs").child(currentRun.getUid()).child("runners");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Runner> runners = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Runner runner = snapshot.getValue(Runner.class);
                    if (runner != null) {
                        runners.add(runner);
                    }
                }
                if (lisener != null) {
                    lisener.onFinish(runners);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (lisener != null) {
                    lisener.onFinish(null);
                }
            }
        });


    }


    //join by request
    public static void addToRun(final RunRequest request, final Runner runner, final AddToRunLisener lisener) {

        /*
        The algorithm:
        1. check if the run is full
        2. add the runner to the run in db
        3. get the new run from the db
        4. delete the request and update the run in db again.
         */
        DatabaseReference runRef = database.getReference().child("runs").child(request.getRunUid());
        runRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Run run = dataSnapshot.getValue(Run.class);
                if (run.getRunners().size() >= run.getMaxPlayers()) {
                    if (lisener != null) {
                        lisener.runIsFull();
                    }
                    DatabaseHandler.removeRunRequestFromInbox(run.getUid());
                    return;
                }
                DatabaseReference ref = database.getReference().child("runs").child(request.getRunUid()).child("runners").child(runner.getUser().getUsername());
                ref.setValue(runner, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        DatabaseReference mref = database.getReference().child("runs").child(request.getRunUid());
                        mref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                currentRun = dataSnapshot.getValue(Run.class);
                                if (currentRun.deleteRunRequestUid(runner.getUser().getUid()))
                                    updateRun(null);
                                if (joinRun) {
                                    if (lisener != null) {
                                        lisener.onFinish(request, true);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                if (lisener != null) {
                                    lisener.onFinish(request, false);
                                }
                            }
                        });


                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public static void setRunnerReady() {
        DatabaseReference ref = database.getReference().child("runs").child(currentRun.getUid()).child("runners").child(user.getUsername()).child("ready");
        ref.setValue(true);
    }

    public static void getPlacesUpdates(final GetPlacesLisener lisener) {
        DatabaseReference ref = database.getReference().child("runs").child(currentRun.getUid()).child("places");
        lisenerValue = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Double> places = new HashMap<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    places.put(snapshot.getKey(), snapshot.getValue(Double.class));
                }
//                currentRun.setNewPlaces(places);
                if (lisener != null) {
                    lisener.onChange(places);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.removeEventListener(lisenerValue);
        ref.addValueEventListener(lisenerValue);
    }

    public static void setPlaces() {
        if (currentRun == null)
            return;
        DatabaseReference ref = database.getReference().child("runs").child(currentRun.getUid()).child("places");
        ref.setValue(currentRun.places);
    }

    public static void updatePlace(String username, Double distance) {
        if (currentRun == null)
            return;
        DatabaseReference ref = database.getReference().child("runs").child(currentRun.getUid()).child("places").child(username);
        ref.setValue(distance);
    }

    public static void updateRun(Runner runner) {
        if (currentRun == null) return;
        if (runner != null) {
            if (!currentRun.setRunnerDistance(runner))
                return;
        }
        DatabaseReference ref = database.getReference().child("runs").child(currentRun.getUid());
        ref.setValue(currentRun);
    }


    public static void updateRunLisener(final GetResultLisener lisener) {
        if (currentRun == null) {
            lisener.onFinish(true);
            return;
        }
        DatabaseReference ref = database.getReference().child("runs").child(currentRun.getUid());
        ref.setValue(currentRun, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (lisener != null) {
                    lisener.onFinish(true);
                }
            }
        });
    }


    //join by pin
    public static void joinGame(final String gamePin, final JoinRunResultLisener lisener, final Runner runner) {
        final DatabaseReference ref = database.getReference().child("runs").child(gamePin);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //Run  exists
                    currentRun = dataSnapshot.getValue(Run.class);

                    if (currentRun.isRunnerExists(runner)) {

                        if (lisener != null) {
                            lisener.onFinish(true);
                        }
                        return;

                    }
                    if (joinRun) {
                        if (currentRun.getRunners().size() >= currentRun.getMaxPlayers()) {
                            if (lisener != null) {
                                lisener.runIsFull();
                            }
                            return;
                        }
                        currentRun.addRunner(runner);

                        DatabaseReference mref = database.getReference().child("runs").child(gamePin).child("runners").child(runner.getUser().getUsername());
                        mref.setValue(runner, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if (lisener != null) {
                                    lisener.onFinish(true);
                                }
                                DatabaseHandler.removeRunRequestFromInbox(currentRun.getUid());
                            }
                        });
                    }
                } else {
                    if (lisener != null) {
                        lisener.onFinish(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (lisener != null) {
                    lisener.onFinish(false);
                }
            }
        });


    }

    public static void getUpdatesRun(Run run, final GetRunUpdatesLisener lisener) {
        DatabaseReference ref = database.getReference().child("runs").child(currentRun.getUid()).child("runners");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Runner> runners = new HashMap<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Runner runner = snapshot.getValue(Runner.class);
                    runners.put(runner.getUser().getUsername(), runner);
                }
                if (lisener != null) {
                    lisener.onChange(runners);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private static void removeRunRequestFromInbox(String runUid) {
        if (user.removeRequestRunRequest(runUid))
            updateUser();
    }

    public static void updateUser() {
        DatabaseReference ref = database.getReference().child("users").child(user.getUid());
        ref.setValue(user);
    }

    public static void quitRun() {
        currentRun.deleteRunner(user);
//        deleteRequest(new RunRequest());
        updateRun(null);
    }

    public static void setStartRunLisener(final StartRunLisener lisener) {
        DatabaseReference ref = database.getReference().child("runs").child(currentRun.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Run run = dataSnapshot.getValue(Run.class);
                if (run.allReady()) {

                    lisener.onStartRun();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void setCurrentRun(String uid, final GetResultLisener lisener) {
        DatabaseReference ref = database.getReference("runs").child(uid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentRun = dataSnapshot.getValue(Run.class);
                    if (lisener != null) {
                        lisener.onFinish(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if (lisener != null) {
                    lisener.onFinish(false);
                }
            }
        });
    }

    public static void updateUserInRun() {
        user.setInGame(true);
        user.setRunUid(currentRun.getUid());
        updateUser();
    }
    public static void endOfRun(){
        DatabaseHandler.updateAtEndRun();
        DatabaseHandler.updateUser();
    }
    public static void exitRun() {
        DatabaseHandler.user.setInGame(false);
        DatabaseHandler.user.setRunUid(null);
        user.allRunsUid.add(DatabaseHandler.currentRun.getUid());
        updateUser();
    }


    public static void getAllRuns(final GetAllRuns lisener) {
        DatabaseReference ref = database.getReference().child("runs");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Run> runs = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Run run = snapshot.getValue(Run.class);
                    if (user.allRunsUid.contains(run.getUid()))
                        runs.add(run);
                }
                if (lisener != null) {
                    lisener.onFinish(runs);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                lisener.onFinish(null);
            }
        });
    }



    /**
     * update the current finishersCounter when changed.
     */
    public static void setFinishersCounterLisener() {
        DatabaseReference ref = database.getReference().child("runs").child(currentRun.getUid()).child("finishersCounter");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    currentRun.finishersCounter = dataSnapshot.getValue(Integer.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void updateAtEndRun() {
        DatabaseReference ref = database.getReference().child("runs").child(currentRun.getUid());
        ref.setValue(currentRun);
    }



    public static void getUpdatedRun() {
        final DatabaseReference ref = database.getReference().child("runs").child(currentRun.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentRun = dataSnapshot.getValue(Run.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void getUpdatedRun(final GetUpdatedRunLisener lisener) {
        final DatabaseReference ref = database.getReference().child("runs").child(currentRun.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentRun = dataSnapshot.getValue(Run.class);
                if (lisener != null) {
                    lisener.onGetRun();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void getUpdateRunEnd(final RunEndLisener lisener) {
        DatabaseReference ref = database.getReference().child("runs").child(currentRun.getUid()).child("endOfTime");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean result = dataSnapshot.getValue(Boolean.class);
                if (lisener != null) {
                    lisener.onEnd(result);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public interface GetUpdatedRunLisener {

        void onGetRun();
    }


    public interface RunEndLisener {
        void onEnd(Boolean result);
    }

    public interface GetPlacesLisener {
        void onChange(Map<String, Double> places);
    }

    public interface GetAllRuns {
        void onFinish(List<Run> runs);
    }

    public interface GetRunUpdatesLisener {
        void onChange(Map<String, Runner> runners);
    }

    public interface StartRunLisener {
        void onStartRun();
    }

    public interface GetRunnersLisener {
        void onFinish(List<Runner> runners);
    }

    public interface FinishHandlingUser {
        void onFinish();
    }

    public interface AddToRunLisener {
        void onFinish(RunRequest request, boolean result);

        void runIsFull();
    }


    public interface JoinRunResultLisener {
        void onFinish(boolean result);

        void runIsFull();
    }

    public interface GetResultLisener {
        void onFinish(boolean result);
    }

    public interface GetListOfRequestsLisener {
        void onFinish(List<Request> requests);
    }

    public interface DeleteRequestLisener {
        void onFinish(Request deleted);
    }

    public interface CheckIsUniqUsernameLisener {
        void onFinish(boolean result);
    }

    public interface GetUsernameDialogLisener {
        void getUsername();
    }

    public interface GetListOfFriendsLisener {
        void onFinish(List<User> friends);
    }

    public interface SearchFriendLisener {
        void onFinish(boolean result, String uId);
    }

}
