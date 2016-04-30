package com.monitise.mea.polata.monitisehackathon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements BeaconManager.RangingListener {

    private BeaconManager beaconManager;

    private static final Region ENTRY_REGION = new Region(
            "Entry Region",
            UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),
            9203,
            37037);

    private boolean sendEnterRequest = true;
    private boolean sendExitRequest;

    private static RetrofitService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new AutoValueAdapterFactory())
                .create();

        final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Timber.tag("Network Log").e(message);
            }
        });

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.58.4.135:8080/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        service = retrofit.create(RetrofitService.class);

        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
            startScanning();
        }
    }

    @Override
    protected void onDestroy() {
        beaconManager.disconnect();

        super.onDestroy();
    }

    private void startScanning() {
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(ENTRY_REGION);
            }
        });
    }

    @Override
    public void onBeaconsDiscovered(Region region, final List<Beacon> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Beacon beacon : list) {
                    Timber.e("%s has distance:%s", beacon.getProximityUUID(), Utils.computeAccuracy(beacon));

                    if (Utils.computeProximity(beacon) == Utils.Proximity.IMMEDIATE
                            || Utils.computeProximity(beacon) == Utils.Proximity.NEAR) {

                        if (sendEnterRequest) {

                            Event event = Event.create(true,
                                    System.currentTimeMillis(),
                                    MockGenerator.generateDeviceId(),
                                    MockGenerator.generateBranchId());

                            service.test(event).enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Timber.e(t.getMessage());
                                }
                            });

                            sendEnterRequest = false;
                            sendExitRequest = true;
                        }

                        Timber.e("Beacon Enter");
                    } else if (Utils.computeProximity(beacon) == Utils.Proximity.FAR) {

                        if (sendExitRequest) {
                            Event event = Event.create(false,
                                    System.currentTimeMillis(),
                                    MockGenerator.generateDeviceId(),
                                    MockGenerator.generateBranchId());

                            service.test(event).enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Timber.e(t.getMessage());
                                }
                            });
                        }

                        sendExitRequest = false;
                        Timber.e("Beacon exit");
                    }
                }
            }
        });
    }
}
