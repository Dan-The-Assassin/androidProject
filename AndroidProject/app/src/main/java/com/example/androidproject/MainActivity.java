package com.example.androidproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements FragmentNav {

    public List<String> numeTabs = new ArrayList<>();

   // public List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        /*getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.container, LoginFragment.class, null)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.tab_container, TabsFragment.class, null)
                .commit();*/

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new LoginFragment()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.tab_container, new TabsFragment()).commit();

        numeTabs.add("Login");
        numeTabs.add("Register");

        TimerService.notified = true;
        if(TimerService.timer!=null) {
            TimerService.timer.cancel();
            TimerService.running = true;
        }
    }

    @Override
    public void navigateFrag(Fragment fragment, Boolean addToStack) {
       FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(fragment instanceof RegisterFragment)
            transaction.setCustomAnimations(R.anim.from_right, R.anim.to_left);
        else
            transaction.setCustomAnimations(R.anim.from_left, R.anim.to_right);

        transaction.replace(R.id.container, fragment);

       if(addToStack)
       {
           transaction.addToBackStack(null);
       }
       transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TimerService.notified = true;
        if(TimerService.timer!=null) {
            TimerService.timer.cancel();
            TimerService.running = true;
        }
    }

    protected void onStop() {
        super.onStop();
        TimerService.notified = true;
        if(TimerService.timer!=null) {
            TimerService.timer.cancel();
            TimerService.running = true;
        }
    }
}