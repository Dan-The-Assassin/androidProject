package com.example.androidproject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TabsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentNav fragNav;

    public TabsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TabsFragment newInstance(String param1, String param2) {
        TabsFragment fragment = new TabsFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tabs, container, false);

        TabLayout tabs = view.findViewById(R.id.tab_layout);

        Activity activity = getActivity();

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(activity instanceof MainActivity) {
                    if (tab.getPosition() == 0) {
                        fragNav.navigateFrag(new LoginFragment(), false);
                    }
                    else
                        fragNav.navigateFrag(new RegisterFragment(), false);
                }
                if(activity instanceof AppActivity){
                    if (tab.getPosition() == 0) {
                        fragNav.navigateFrag(new HomeFragment(), false);
                    }
                    else
                        fragNav.navigateFrag(new ProfileFragment(), false);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        for(int i=0; i <tabs.getTabCount();i++)
        {
            if(activity instanceof MainActivity)
            {
                (Objects.requireNonNull(tabs.getTabAt(i))).setText(((MainActivity) activity).numeTabs.get(i));
            }
            if(activity instanceof AppActivity)
            {
                (Objects.requireNonNull(tabs.getTabAt(i))).setText(((AppActivity) activity).numeTabs.get(i));
            }
        }

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
}