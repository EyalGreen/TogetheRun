package com.eyal.togetherun.Run;

import com.eyal.togetherun.Adapter.ScoreboardRecycleAdapter;
import com.eyal.togetherun.MaxPlayersView;
import com.eyal.togetherun.Request.Request;
import com.eyal.togetherun.Request.RunRequest;
import com.eyal.togetherun.Run.Target.SortPair;
import com.eyal.togetherun.Run.Target.TargetOfRun;
import com.eyal.togetherun.User;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Run {

    public static final int LOSER_RANK_AMOUNT = 20;
    public static final int WINNER_RANK_FACTOR = 30;
    private TargetOfRun target;
    private Map<String, Runner> runners; //username, runner
    private String uid;
    private boolean isEndOfTime = false;

    public boolean isEndOfTime() {
        return isEndOfTime;
    }

    public void setEndOfTime(boolean endOfTime) {
        isEndOfTime = endOfTime;
    }

    private int maxPlayers = MaxPlayersView.MAX_PLAYERS;
    private List<String> allRunRequestUidForThisRun = new ArrayList<>();
    public Map<String, Double> places = new HashMap<>();
    public int finishersCounter = 0;
    public List<String> getAllRunRequestUidForThisRun() {
        return allRunRequestUidForThisRun;
    }

    public void addRunRequestUid(String uid) {
        allRunRequestUidForThisRun.add(uid);
    }

    public boolean deleteRunRequestUid(String uid) {
        if (allRunRequestUidForThisRun.contains(uid))
            return allRunRequestUidForThisRun.remove(uid);
        return false;
    }

    public void setAllRunRequestUidForThisRun(List<String> allRunRequestUidForThisRun) {
        this.allRunRequestUidForThisRun = allRunRequestUidForThisRun;
    }

    public void startRun() {
        for (String username : runners.keySet()) {
            places.put(username, runners.get(username).getDistance());
        }

    }


    public Run(TargetOfRun target, Map<String, Runner> runners) {
        this.target = target;
        this.runners = runners;

    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setRunner(Runner runner) {
        runners.put(runner.getUser().getUsername(), runner);
    }

    public Run(TargetOfRun target, int maxPlayers) {
        this.target = target;
        this.maxPlayers = maxPlayers;
        this.runners = new HashMap<>();
    }

    public TargetOfRun getTarget() {
//        target.handleIsByDistance();
        return target;
    }


    public String generateUid() {
        Random random = new Random();
        String uid = String.valueOf(random.nextInt(1_000_000));
        this.uid = uid;
        return uid;
    }

    public void setTarget(TargetOfRun target) {
        this.target = target;
    }

    public Map<String, Runner> getRunners() {
        return runners;
    }


    public void setRunners(Map<String, Runner> runners) {
        this.runners = runners;
    }

    public Run() {
        runners = new HashMap<>();
    }

    public Run(Run other) {
        this.runners = new HashMap<>(other.getRunners());
        this.target = new TargetOfRun(other.target);
        this.allRunRequestUidForThisRun = new ArrayList<>(other.allRunRequestUidForThisRun);
        this.places = other.places;
        this.maxPlayers = other.maxPlayers;

    }

    public void addRunner(Runner runner) {
        runners.put(runner.getUser().getUsername(), runner); //need aliasing
    }

    public String getUid() {
        return uid == null ? generateUid() : uid;
    }
    public void handleIsTargetDistance(){
//        target.handleIsByDistance();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Run run = (Run) o;
        return this.uid.equals(run.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, runners, uid, maxPlayers);
    }

    public boolean isRunnerExists(Runner runner) {
        return runners.containsKey(runner.getUser().getUsername());
    }

    public void deleteRunner(User user) {

        runners.remove(user.getUsername());


    }

    public boolean allReady() {
        if (runners.size() == 0)
            return false;
        for (Map.Entry<String, Runner> set : runners.entrySet()) {
            Runner runner = set.getValue();
            if (!runner.isReady()) {
                return false;
            }
        }
        return true;
    }


    public boolean setRunnerDistance(Runner runner) {
        Runner runner1 = runners.get(runner.getUser().getUsername());
        if (runner1.getDistance() != runner.getDistance()) {
            runners.put(runner.getUser().getUsername(), new Runner(runner));
            return true;
        }
        return false;
    }

    public void addRunners(Map<String, Runner> runners) {
        for (String key : runners.keySet()) {
            this.runners.put(key, new Runner(runners.get(key)));
        }
    }

    public Runner getRunner(User user) {
        return runners.get(user.getUsername());
    }


    public void setNewRunners(Map<String, Runner> newRunners) {
        for (String name : newRunners.keySet()) {
            this.runners.put(name, newRunners.get(name));
        }
    }

    public void setNewPlaces(Map<String, Double> newPlaces) {
        for (String name : newPlaces.keySet()) {
            this.places.put(name, newPlaces.get(name));
        }
        if (!target.isByDistance()){
            double maxDistance = getPlacesSorted()[0].getDistance();
            this.target.setDistance(Math.max(maxDistance, target.getDistance()));
        }
    }



    public boolean isFinish(double distance) {
        if (target.isByDistance())
            return target.getDistance() <= distance;
        return isEndOfTime;
    }

    public boolean isDropped(double distance){
        return distance < 0;
    }

    public int getPlace(Runner runner){
        Runner[] runnersArray = getScoreboard();
        for (int i = 0; i < runnersArray.length; i++) {
            if (runnersArray[i].equals(runner))
                return i;
        }
        return 0;
    }

    @Exclude
    public Runner[] getScoreboard() {

        Runner[] runnersArray = new Runner[this.runners.size()];
        Pair[] places = getPlacesSorted();
        for (int i = 0; i < runnersArray.length; i++) {
            runnersArray[i] = this.runners.get(places[i].getUsername());
//            runnersArray[i].setDistance(places[i].getDistance());
        }
        return runnersArray;
    }
    @Exclude
    private Pair[] getPlacesSorted(Map<String, Double> places){
        Pair[] placesArray = new Pair[places.size()];
        int i = 0;
        for(String username: places.keySet()){
            placesArray[i++] = new Pair(username, places.get(username));

        }
        Arrays.sort(placesArray, new SortPair());
        return placesArray;
    }
    @Exclude
    public Pair[] getPlacesSorted() {
       return getPlacesSorted(this.places);
    }
    @Exclude
    public Pair[] removeAllExcept(String username) {
        for (String name: places.keySet()){
            if (name.equals(username))
                return new Pair[]{new Pair(name, places.get(name))};
        }
        return null;
    }

    @Exclude
    public Map<String, Runner> getMap(List<Runner> runners) {
        Map<String, Runner> runnerMap = new HashMap<>();
        for (Runner runner : runners) {
            runnerMap.put(runner.getUser().getUsername(), runner);
        }
        return runnerMap;
    }

    public int generateRank(Runner runner) {
        if (isFinish(runner.getDistance())){
            int place = getPlace(runner);
            return (runners.size() - place)* WINNER_RANK_FACTOR;
        }else if (isDropped(runner.getDistance())){
            return LOSER_RANK_AMOUNT;
        }

        return 0;
    }



    /*Convert List of indexes to array */
    @Exclude
    private int[] convertToArray(List<Integer> data){
        int[] arr = new int[data.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = data.get(i);
        }
        return arr;
    }



    @Exclude
    public PairIndex[] getChangeInOrder(Map<String, Double> places){
        Pair[] oldPlaces = getPlacesSorted();
        Pair[] newPlaces = getPlacesSorted(places);
        List<PairIndex> pairIndices = new ArrayList<>();
        for (int i = 0; i < oldPlaces.length; i++) {
            String username = oldPlaces[i].getUsername();
            if (!username.equals(oldPlaces[i].getUsername())){
                for (int j = 0; j < newPlaces.length; j++) {
                    if (newPlaces[i].getUsername().equals(username)){
                        //the new position of the runner
                        pairIndices.add(new PairIndex(i, j));
                    }
                }
            }

        }
        return pairIndices.size() == 0 ? null : convertToArrayOfPairIndex(pairIndices);
    }

    private PairIndex[] convertToArrayOfPairIndex(List<PairIndex> data) {
        PairIndex[] arr = new PairIndex[data.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = data.get(i);
        }
        return arr;
    }

    @Exclude
    public boolean isChangeInOrder(Map<String, Double> places){
        Pair[] thisPlaces = getPlacesSorted();
        Pair[] otherPlaces = getPlacesSorted(places);
        for (int i = 0; i < thisPlaces.length; i++) {
            if (!otherPlaces[i].getUsername().equals(thisPlaces[i].getUsername()))
                return true;
        }
        return false;
    }

    @Exclude
    public int[] getPlacesThatInRun(){
        Pair[] placesSorted = getPlacesSorted();
        List<Integer> linePlaces = new ArrayList<>();
        for (int i = 0; i < placesSorted.length; i++) {
            Pair place = placesSorted[i];
            if (!isDropped(place.getDistance()) && !isFinish(place.getDistance()))
                linePlaces.add(i);
        }
        return linePlaces.size() == 0? null: convertToArray(linePlaces);

    }
    
    
    @Exclude
    public int[] getPlacesThatChanged(Map<String, Double> places) {
        Pair[] thisPlaces = getPlacesSorted();
        Pair[] otherPlaces = getPlacesSorted(places);

        //assumption thisPlaces.length == otherPlaces.length

        List<Integer> placesChanged = new ArrayList<>();

        for (int i = 0; i < thisPlaces.length; i++) {
            if (thisPlaces[i].getDistance() != otherPlaces[i].getDistance())
                placesChanged.add(i);

        }


        return placesChanged.size() == 0? null: convertToArray(placesChanged);
    }
}
