package org.gautham.f22.events;

public enum EventCode {
    //Attendance entry
    EF_ATTLOG(1), //Pressed finger
    EF_FINGER(2), //Enrolled user
    EF_ENROLLUSER(4), //Enrolled user
    EF_ENROLLFINGER(8), //Pressed keyboard key
    EF_BUTTON(16), EF_UNLOCK(32), //Registered user placed finger
    EF_VERIFY(128), //Fingerprint score in enroll procedure
    EF_FPFTR(256), //Triggered alarm
    EF_ALARM(512);

    private final int code;

    EventCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
