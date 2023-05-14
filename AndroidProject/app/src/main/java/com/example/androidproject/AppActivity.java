package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AppActivity extends AppCompatActivity implements FragmentNav {

    public List<String> numeTabs = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    private FirebaseAuth pAuth;
    private FirebaseUser user;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;

    public Uri profilePic = null;

    public List<TaskItem> allTasks = new ArrayList<TaskItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        Objects.requireNonNull(getSupportActionBar()).hide();
        sharedPreferences = getSharedPreferences(Constants.userFileName, Context.MODE_PRIVATE);
        pAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        user = FirebaseAuth.getInstance().getCurrentUser();

        //TaskItem task = new TaskItem(1, 12, 2022, "testare");
        //allTasks.add(task);

        /*getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.container, HomeFragment.class, null)
                .commit();*/

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();

        /*getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.tab_container, TabsFragment.class, null)
                .commit();*/
        getSupportFragmentManager().beginTransaction().replace(R.id.tab_container, new TabsFragment()).commit();

        Intent intent = new Intent(this, TimerService.class);
        startService(intent);
        numeTabs.add("Tasks");
        numeTabs.add("Profile");
    }

    public void navigateFrag(Fragment fragment, Boolean addToStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment);

        if(addToStack)
        {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(TimerService.timer!=null) {
            TimerService.timer.cancel();
        }

        if(!TimerService.running)
        {
            if(user!=null){
                pAuth.signOut();
                Toast.makeText(this,"Successfully logged out", Toast.LENGTH_SHORT).show();
            }
            else
            {
                gsc.signOut()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(),"Successfully logged out", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        TimerService.timer.start();
        TimerService.notified = false;
        TimerService.running = true;
    }

    public void createTask(int day, int month, int year, String title){
        TaskItem task = new TaskItem(day, month, year, title);
        allTasks.add(task);
    }

    public void removeTask(int id){
        for(TaskItem task : allTasks){
            if(task.getId() == id)
            {
                allTasks.remove(task);
                break;
            }
        }
    }

    public void editTask(int id, int day, int month, int year, String title){
        for(TaskItem task : allTasks){
            if(task.getId() == id)
            {
                task.setDate(day, month, year);
                task.setTitle(title);
                break;
            }
        }
    }
}