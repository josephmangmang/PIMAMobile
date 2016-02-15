package com.pimamobile.pima.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pimamobile.pima.R;
import com.squareup.timessquare.CalendarPickerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatePickerFragment extends Fragment {

    private CalendarPickerView calendar;

    public static DatePickerFragment newInstance() {
        return new DatePickerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.date_picker, container, false);
        Calendar pastYear = Calendar.getInstance();
        pastYear.add(Calendar.YEAR, -1);

        calendar = (CalendarPickerView) view.findViewById(R.id.calendar_view);
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE, 1);

        calendar.init(pastYear.getTime(), today.getTime())
                .inMode(CalendarPickerView.SelectionMode.RANGE)
                .withSelectedDate(new Date());

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        List<Date> selectedDates = calendar.getSelectedDates();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < selectedDates.size(); i++) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
            builder.append(dateFormat.format(selectedDates.get(i)) + " \n");
        }
        Log.i("DatePicker", "Selected Dates: \n" + builder.toString());
    }
}
