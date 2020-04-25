package com.eyal.togetherun.Request;

import static com.eyal.togetherun.Request.FriendRequest.FRIEND_REQ_MSG;

public  class Request {
     protected String nameOfSender;
     protected String contentOfRequest;
     protected String senderUid;

     public void setTypeOfRequest(String typeOfRequest) {
          this.typeOfRequest = typeOfRequest;
     }

     private String typeOfRequest;
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

     public String getSenderUid() {
          return senderUid;
     }

     public void setSenderUid(String senderUid) {
          this.senderUid = senderUid;
     }

     public Request(String nameSender, String senderUid) {

          this.nameOfSender = nameSender;
          this.senderUid = senderUid;
     }


     public void onApprove() {

     }

     public String getTypeOfRequest(){
          return typeOfRequest;
     }

     public Request() {
     }

     public void onDenied() {

     }
}

