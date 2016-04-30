package com.monitise.mea.polata.monitisehackathon;

/**
 * Created by polata on 29/04/2016.
 */
public final class MockGenerator {

    private MockGenerator() {
    }

    public static int generateBranchId() {
        return 1;
    }

    public static String generateDeviceId() {
        return String.valueOf(1);
    }
}
