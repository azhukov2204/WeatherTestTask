package ru.androidlearning.weathertesttask.ui.main;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import ru.androidlearning.weathertesttask.R;
import ru.androidlearning.weathertesttask.databinding.MainFragmentBinding;
import ru.androidlearning.weathertesttask.model.DataLoadState;
import ru.androidlearning.weathertesttask.model.web.data_sources.api.WeatherDTO;

public class MainFragment extends Fragment {
    private MainViewModel mainViewModel;
    private MainFragmentBinding binding;
    private LocationManager locationManager;

    private final View.OnClickListener onEnterCityNameClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String cityName = Objects.requireNonNull(binding.enterCityNameEditText.getText()).toString();
            if (!cityName.trim().isEmpty()) {
                hideKeyboard();
                mainViewModel.getWeatherByCityNameFromServer(cityName, getString(R.string.units_df_measure), getString(R.string.language));
            }
        }
    };

    private final AdapterView.OnItemSelectedListener onChoseSearchTypeListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    binding.enterCityNameLayout.setVisibility(View.VISIBLE);
                    binding.getWeatherByCoordinatesButton.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    binding.enterCityNameLayout.setVisibility(View.INVISIBLE);
                    binding.getWeatherByCoordinatesButton.setVisibility(View.VISIBLE);
                    checkPermissionAndFineLocation();
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private final ActivityResultLauncher<String> permissionToFineLocationResult = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            getSelfLocation();
        } else {
            new AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.access_to_fine_location_text))
                    .setMessage(getString(R.string.explanation_of_fine_location_permission))
                    .setNegativeButton(getString(R.string.close_button_text), (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        }
    });

    private final LocationListener onLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            System.out.printf("\n Coordinates: lat: %f, lon = %f\n", lat, lon);
            locationManager.removeUpdates(this);
            mainViewModel.getWeatherByCoordinatesFromServer(getString(R.string.units_df_measure), getString(R.string.language), lat, lon);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
        }
    };

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = MainFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mainViewModel.getWeatherLiveData().observe(getViewLifecycleOwner(), this::renderData);
        binding.enterCityNameLayout.setEndIconOnClickListener(onEnterCityNameClickListener);

        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.get_weather_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        binding.choseSearchTypeSpinner.setAdapter(adapter);
        binding.choseSearchTypeSpinner.setOnItemSelectedListener(onChoseSearchTypeListener);
        binding.getWeatherByCoordinatesButton.setOnClickListener(v -> checkPermissionAndFineLocation());

    }

    private void renderData(DataLoadState<WeatherDTO> dataLoadState) {
        if (dataLoadState instanceof DataLoadState.Loading) {
            noWeatherLayoutIsVisible(false);
            loadingLayoutIsVisible(true);
        } else if (dataLoadState instanceof DataLoadState.Success) {
            fillData((DataLoadState.Success<WeatherDTO>) dataLoadState);
            noWeatherLayoutIsVisible(false);
            loadingLayoutIsVisible(false);
        } else if (dataLoadState instanceof DataLoadState.Error) {
            loadingLayoutIsVisible(false);
            noWeatherLayoutIsVisible(true);
            Snackbar.make(binding.mainFragmentLayout, getString(R.string.weather_not_found_message), Snackbar.LENGTH_LONG).show();
        }
    }

    private void fillData(DataLoadState.Success<WeatherDTO> dataLoadState) {
        double temp = dataLoadState.getResponseData().getMainParams().getTemp();
        double windSpeed = dataLoadState.getResponseData().getWind().getSpeed();
        double humidity = dataLoadState.getResponseData().getMainParams().getHumidity();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        long sunriseUTCTimeWithOffset = (dataLoadState.getResponseData().getSystemParams().getSunrise() + dataLoadState.getResponseData().getTimezone()) * 1000;
        long sunsetUTCTimeWithOffset = (dataLoadState.getResponseData().getSystemParams().getSunset() + dataLoadState.getResponseData().getTimezone()) * 1000;

        binding.locationTextView.setText(dataLoadState.getResponseData().getLocationName());
        binding.temperatureTextView.setText(String.format(Locale.getDefault(), "%.1f %s", temp, getString(R.string.metric_degrees_symbols)));
        binding.windSpeedTextView.setText(String.format(Locale.getDefault(), "%.1f %s", windSpeed, getString(R.string.wind_speed_metric)));
        binding.humidityTextView.setText(String.format(Locale.getDefault(), "%.1f %s", humidity, getString(R.string.humidity_metric)));
        binding.visibilityTextView.setText(dataLoadState.getResponseData().getWeather().get(0).getDescription());
        binding.timeOfSunriseTextView.setText(dateFormat.format(sunriseUTCTimeWithOffset));
        binding.timeOfSunsetTextView.setText(dateFormat.format(sunsetUTCTimeWithOffset));
    }

    private void loadingLayoutIsVisible(boolean isShow) {
        if (isShow) {
            binding.loadingLayout.setVisibility(View.VISIBLE);
        } else {
            binding.loadingLayout.setVisibility(View.GONE);
        }
    }

    private void noWeatherLayoutIsVisible(boolean isShow) {
        if (isShow) {
            binding.noWeatherLayout.setVisibility(View.VISIBLE);
        } else {
            binding.noWeatherLayout.setVisibility(View.GONE);
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(binding.enterCityNameLayout.getWindowToken(), 0);
    }

    private void checkPermissionAndFineLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getSelfLocation();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestAccessToFineLocationWithDialog();
        } else {
            permissionToFineLocationResult.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void requestAccessToFineLocationWithDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.access_to_fine_location_title))
                .setMessage(getString(R.string.explanation_of_fine_location_permission))
                .setPositiveButton(getString(R.string.grant_access_button_text), (dialog, which) ->
                        permissionToFineLocationResult.launch(Manifest.permission.ACCESS_FINE_LOCATION))
                .setNegativeButton(getString(R.string.negative_button_text), (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void getSelfLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);

            String provider = locationManager.getBestProvider(criteria, true);
            if (provider != null) {
                locationManager.requestLocationUpdates(provider, 0, 0, onLocationListener);
            }
        } else {
            requestAccessToFineLocationWithDialog();
        }
    }
}
