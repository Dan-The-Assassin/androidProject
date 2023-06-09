package com.example.androidproject;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //private static final String SAVED_TITLES = "saveTitles";
    //private static final String SAVED_DATES = "saveDates";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentNav fragNav;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    Parcelable state = null;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

       /* if(savedInstanceState!=null)
        {
            ArrayList<String> titles = savedInstanceState.getStringArrayList(SAVED_TITLES);
            ArrayList<String> dates = savedInstanceState.getStringArrayList(SAVED_DATES);
            ((AppActivity) requireActivity()).restoreTasks(titles, dates);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.task_list);
        //recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new RecycleAdapter(((AppActivity) requireActivity()).allTasks, getContext());
        recyclerView.setAdapter(mAdapter);

        view.findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragNav.navigateFrag(new AddFragment(), false);
            }
        });

        return view;
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            fragNav = (FragmentNav) context;
        } catch (ClassCastException castException) {
            /** The activity does not implement the listener. */
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragNav = null;
    }

   /* @Override
    public void onSaveInstanceState(Bundle outState) {
        ArrayList<String> titles = ((AppActivity) requireActivity()).getAllTaskTitles();
        outState.putStringArrayList(SAVED_TITLES, titles);
        ArrayList<String> dates = ((AppActivity) requireActivity()).getAllTaskDates();
        outState.putStringArrayList(SAVED_DATES, dates);
        // putting recyclerview items
       // outState.putParcelableArrayList(SAVED_RECYCLER_VIEW_DATASET_ID, (ArrayList<? extends Parcelable>) ((AppActivity) requireActivity()).allTasks);
        super.onSaveInstanceState(outState);
    }*/
}