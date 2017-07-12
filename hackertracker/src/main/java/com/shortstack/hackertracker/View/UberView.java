package com.shortstack.hackertracker.View;

import android.content.Context;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import com.shortstack.hackertracker.Adapter.SpinnerAdapter;
import com.shortstack.hackertracker.Common.Constants;
import com.shortstack.hackertracker.R;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UberView extends FrameLayout {

    @BindView(R.id.spinner)
    AppCompatSpinner spinner;

    @BindView(R.id.uber_request_button)
    RideRequestButton button;

    public UberView(Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.alert_uber, null);
        ButterKnife.bind(this, view);

        addView(view);

        SessionConfiguration config = new SessionConfiguration.Builder()
                .setClientId(Constants.UBER_CLIENT_ID)
                .setEnvironment(SessionConfiguration.Environment.PRODUCTION)
                .setScopes(Arrays.asList(Scope.PROFILE, Scope.RIDE_WIDGETS))
                .build();
        UberSdk.initialize(config);


        spinner.setOnItemSelectedListener(new UberOnItemSelectedListener());
        SpinnerAdapter adapter = new SpinnerAdapter(getContext(), R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        adapter.addAll(Constants.UBER_LOCATIONS);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getCount());
    }

    public class UberOnItemSelectedListener implements AdapterView.OnItemSelectedListener {


        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            String nickname = Constants.UBER_LOCATIONS[pos];
            String address = Constants.UBER_ADDRESSES[pos];

            // set up ride request button
            RideParameters rideParams = new RideParameters.Builder()
                    .setDropoffLocation(0.0, 0.0, nickname, address)
                    .build();

            button.setRideParameters(rideParams);
        }

        public void onNothingSelected(AdapterView parent) {
        }
    }
}
