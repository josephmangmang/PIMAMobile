package com.pimamobile.pima.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pimamobile.pima.R;
import com.pimamobile.pima.models.History;

public class HistoryFragment extends Fragment {

    private OnHistoryFragment mListener;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    public HistoryFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHistoryFragment) {
            mListener = (OnHistoryFragment) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " must implement onHistoryFragment");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnHistoryFragment {
        void onHistoryItemClicked(History history);
    }
}
