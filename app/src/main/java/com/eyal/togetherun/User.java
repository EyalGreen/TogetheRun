package com.eyal.togetherun;

import com.eyal.togetherun.Adapter.ScoreboardRecycleAdapter;
import com.eyal.togetherun.Request.FriendRequest;
import com.eyal.togetherun.Request.Request;
import com.eyal.togetherun.Request.RunRequest;
import com.eyal.togetherun.Run.Runner;
import com.eyal.togetherun.Run.Target.Time;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String username, teamName;
    private int rank;
//    private byte[] bitmapImage;
    private boolean isInGame = false;
    private boolean Online = false;
    private String runUid;
    private int runningTime = 0; //seconds
    private double totalKm = 0;
    private Pace averagePace = new Pace();

    public long getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(int seconds) {
        this.runningTime = seconds;
    }

    public double getTotalKm() {
        return totalKm;
    }
    public void addTime(Time timeToAdd){
        this.runningTime += timeToAdd.getSecondsFromStart();
    }
    public void addDistance(double distance){
        this.totalKm += distance;
    }

    public void setTotalKm(double totalKm) {
        this.totalKm = totalKm;
    }

    @Exclude
    public Pace getAveragePace() {
        return Runner.calculateAndReturnPace(totalKm, runningTime);
    }

    public void setAveragePace(Pace averagePace) {
        this.averagePace = averagePace;
    }

    public List<String> allRunsUid = new ArrayList<>();


    public User(User other) {
        this.username = other.username;
        this.teamName = other.teamName;
        this.rank = other.rank;
        this.isInGame = other.isInGame;
        this.Online = other.isOnline();
        this.runUid = other.runUid;
        this.uid = other.uid;
    }

    public String getRunUid() {
        return runUid;
    }

    public void setRunUid(String runUid) {
        this.runUid = runUid;
    }

    public boolean isOnline() {
        return Online;
    }

    public void setOnline(boolean online) {
        Online = online;

    }

    private String uid;
    private FriendMapDataType friends = new FriendMapDataType();
    private Map<String, Request> requests = new HashMap<>();
    private String email;



//    public void setBitmap(Bitmap bitmapImage){
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//        byte[] img = bos.toByteArray();
//        this.bitmapImage = img;
//    }
//    public Bitmap getBitmap(){
//        return BitmapFactory.decodeByteArray(bitmapImage, 0, bitmapImage.length);
//
//    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isThereRequest(String tUid){
        return requests.containsKey(FriendRequest.FRIEND_REQUEST_TYPE + tUid);

    }
    public User(String username, String uid) {
        this.username = username;
        this.uid = uid;

    }

    public void addFriend(String username, String uid){
        this.friends.put(username, uid);
    }


    public boolean isFriend(String user){
        return this.friends.containsKey(user);
    }




    public Map<String, Request> getRequests() {
        return requests;
    }

    public void setRequestsFromList(Map<String, Request> requests) {
        this.requests = requests;
    }

    public void addRequest(Request request){
        this.requests.put(request.getSenderUid(), request);
    }
    public Map<String, String> getFriends() {
        return friends.getFriends();
    }

    public void setFriends(Map<String, String> friends) {
        this.friends = new FriendMapDataType(friends);
    }

    public User(String uid) {
        this.uid = uid;
    }


    public User(String username, String teamName, int rank, byte[] bitmapImage, boolean isInGame, String uid) {
        this.username = username;
        this.teamName = teamName;
        this.rank = rank;
//        this.bitmapImage = bitmapImage;
        this.isInGame = isInGame;
        this.uid = uid;

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public User() {
    }

    public User(String username, String teamName, int rank, byte[] bitmapImage, boolean isInGame) {
        this.username = username;
        this.teamName = teamName;
        this.rank = rank;
//        this.bitmapImage = bitmapImage;
        this.isInGame = isInGame;
    }

    public boolean isInGame() {
        return isInGame;
    }

    public void setInGame(boolean inGame) {
        isInGame = inGame;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }


//    public byte[] getBitmapImage() {
//        return bitmapImage;
//    }
//
//    public void setBitmapImage(byte[] bitmapImage) {
//        this.bitmapImage = bitmapImage;
//    }

    public User(String username, String teamName, int rank, byte[] bitmapImage) {
        this.username = username;
        this.teamName = teamName;
        this.rank = rank;
//        this.bitmapImage = bitmapImage;
    }


    public boolean removeRequestRunRequest(String runUid) {
        for (Map.Entry<String, Request> set : requests.entrySet()) {
            Request request = set.getValue();
            if (request.getTypeOfRequest().equals(RunRequest.RUN_REQUEST_TYPE)){
                //run request
                if(((RunRequest)request).getRunUid().equals(runUid)) //run uid match
                {
                    requests.remove(set.getKey());
                    return true;
                }
            }
        }
        return false;
    }

    public void setRequestsFromList(List<Request> requests) {
        this.requests = new HashMap<String, Request>();
        for (int i = 0; i < requests.size(); i++) {
//            equest.getTypeOfRequest() + "_" + request.getSenderUid()
            Request request = requests.get(i);
            this.requests.put(request.getTypeOfRequest() + "_" + request.getSenderUid(), requests.get(i));
        }
    }

    public void updateRank(int rankToAdd) {
        this.rank += rankToAdd;

    }

    public String getTotalKmFormatted() {
        return String.format("%.2f", totalKm);
    }

    public String getRunningTimeInMinutes() {
        return String.valueOf(runningTime / 60);
    }

    public String getRunningTimeInSeconds() {
        return String.valueOf(runningTime % 60);
    }
}
