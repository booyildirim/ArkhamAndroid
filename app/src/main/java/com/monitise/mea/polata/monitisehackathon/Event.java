package com.monitise.mea.polata.monitisehackathon;

import com.google.auto.value.AutoValue;

/**
 * Created by polata on 21/04/2016.
 */
@AutoValue
@AutoGson
abstract class Event {
    static Event create(boolean status, long time, String deviceId, int branchId) {
        return new AutoValue_Event(status, time, deviceId, branchId);
    }

    abstract boolean status();

    abstract long time();

    abstract String deviceId();

    abstract int branchId();
}
