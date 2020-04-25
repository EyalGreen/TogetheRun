package com.eyal.togetherun.Request;

public class FriendRequest extends Request {

    public static final String FRIEND_REQ_MSG = "Hello, I want to be your friend!";
    public static final String FRIEND_REQUEST_TYPE = "Friend Request";
    private String nameOfSender;
    private String contentOfRequest;
    private String senderUid;

    public static String getFriendReqMsg() {
        return FRIEND_REQ_MSG;
    }

    public String getNameOfSender() {
        return nameOfSender;
    }

    public void setNameOfSender(String nameOfSender) {
        this.nameOfSender = nameOfSender;
    }

    public String getContentOfRequest() {
        return contentOfRequest;
    }

    public void setContentOfRequest(String contentOfRequest) {
        this.contentOfRequest = contentOfRequest;
    }
    public FriendRequest(){

    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public FriendRequest(String nameSender, String senderUid) {
        super(nameSender, senderUid);
        this.contentOfRequest = FRIEND_REQ_MSG;
        this.nameOfSender = nameSender;
        this.senderUid = senderUid;
    }

    @Override
    public String getTypeOfRequest(){
        return FRIEND_REQUEST_TYPE;
    }

    @Override
    public void onApprove() {

    }

    @Override
    public void onDenied() {

    }
}
