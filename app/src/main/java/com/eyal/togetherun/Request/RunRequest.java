package com.eyal.togetherun.Request;

import com.eyal.togetherun.Run.Target.TargetOfRun;

public class RunRequest extends Request{
    public static final String RUN_REQ_MSG = "Hello, I want to run with you!";
    public static final String RUN_REQUEST_TYPE = "Run Request";
    private String nameOfSender;
    private String contentOfRequest;
    private String senderUid;
    private TargetOfRun targetOfRun;
    private String runUid;

    public String getRunUid() {
        return runUid;
    }
    public void handleIsTargetDistance(){
//        targetOfRun.handleIsByDistance();
    }

    public void setRunUid(String runUid) {
        this.runUid = runUid;
    }

    public TargetOfRun getTargetOfRun() {
        return targetOfRun;
    }

    public void setTargetOfRun(TargetOfRun targetOfRun) {
        this.targetOfRun = targetOfRun;
    }

    public RunRequest() {
    }

    @Override
    public String getTypeOfRequest(){
        return RUN_REQUEST_TYPE;
    }

    public String getNameOfSender() {
        return nameOfSender;
    }

    public void setNameOfSender(String nameOfSender) {
        this.nameOfSender = nameOfSender;
    }

    public RunRequest(String nameOfSender, String senderUid, TargetOfRun targetOfRun, String runUid) {
        super(nameOfSender, senderUid);
        this.nameOfSender = nameOfSender;
        this.senderUid = senderUid;
        this.targetOfRun = targetOfRun;
        this.runUid = runUid;
        contentOfRequest = RUN_REQ_MSG + "" +
                "\n The target of the run is " + targetOfRun.toString();
    }

    public String getContentOfRequest() {
        contentOfRequest = RUN_REQ_MSG + "" +
                "\n The target of the run is " + targetOfRun.toString();

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
}
