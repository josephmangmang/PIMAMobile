package com.pimamobile.pima.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.pimamobile.pima.activities.ItemsActivity;
import com.pimamobile.pima.adapter.ItemsRecyclerAdapter;
import com.pimamobile.pima.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnItemsInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment {

    private OnItemsInteractionListener mListener;
    private Toolbar toolbar;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {

    }


    public static ItemFragment newInstance() {
        ItemFragment fragment = new ItemFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            String[] itemArray = getResources().getStringArray(R.array.items);
            List<String> items = new ArrayList<>();
            for (int i = 0; i < itemArray.length; i++) {
                items.add(itemArray[i]);
            }
            recyclerView.setAdapter(new ItemsRecyclerAdapter(items, mListener));
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((ItemsActivity) getActivity()).getSupportActionBar().setTitle(R.string.item_activity);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemsInteractionListener) {
            mListener = (OnItemsInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListItemInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnItemsInteractionListener {

        void onItemsFragmentInteraction(int position);
    }
}
