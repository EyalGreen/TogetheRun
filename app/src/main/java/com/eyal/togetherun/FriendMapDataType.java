package com.eyal.togetherun;

import java.util.HashMap;
import java.util.Map;

public class FriendMapDataType {
    private Map<String, String> friends = new HashMap<>();

    public FriendMapDataType() {
    }

    public Map<String, String> getFriends() {
        return friends;
    }

    public void setFriends(Map<String, String> friends) {
        this.friends = friends;
    }
    public void put(String key, String value){
        this.friends.put(key, value);
    }
    public boolean containsKey(String user){
        return this.friends.containsKey(user);
    }

    public FriendMapDataType(Map<String, String> friends) {
        this.friends = friends;
    }
}
