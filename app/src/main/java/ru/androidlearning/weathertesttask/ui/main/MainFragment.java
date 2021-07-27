package ru.androidlearning.weathertesttask.ui.main;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

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
}
