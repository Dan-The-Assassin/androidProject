package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

    private static final String SAVED_TITLES = "saveTitles";
    private static final String SAVED_DATES = "saveDates";

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

        if(savedInstanceState!=null)
        {
            ArrayList<String> titles = savedInstanceState.getStringArrayList(SAVED_TITLES);
            ArrayList<String> dates = savedInstanceState.getStringArrayList(SAVED_DATES);
            restoreTasks(titles, dates);
            FragmentManager manager = getSupportFragmentManager();
        }

        if(savedInstanceState!=null){
            //imageUri = savedInstanceState.getParcelable("imageUri");
            //profilePic.setImageURI(imageUri);
            profilePic = savedInstanceState.getParcelable("imageUri");
        }

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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(fragment instanceof ProfileFragment)
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

    public ArrayList<String> getAllTaskTitles(){

        ArrayList<String> titles = new ArrayList<>();
        for(TaskItem task : allTasks)
        {
            titles.add(task.GetTitle());
        }

        return titles;
    }

    public ArrayList<String> getAllTaskDates(){
        ArrayList<String> dates = new ArrayList<>();
        for(TaskItem task : allTasks)
        {
            dates.add(task.GetDate());
        }

        return dates;
    }

    public void restoreTasks(ArrayList<String> titles, ArrayList<String> dates){
        for(int i=0; i<titles.size(); i++){
            String[] date = dates.get(i).split("[.]", 0);
            int day = Integer.parseInt(date[0]);
            int month = Integer.parseInt(date[1]);
            int year = Integer.parseInt(date[2]);
            TaskItem task = new TaskItem(day,month,year,titles.get(i));
            allTasks.add(task);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        ArrayList<String> titles = getAllTaskTitles();
        outState.putStringArrayList(SAVED_TITLES, titles);
        ArrayList<String> dates = getAllTaskDates();
        outState.putStringArrayList(SAVED_DATES, dates);
        outState.putParcelable("imageUri", profilePic);
        // putting recyclerview items
        // outState.putParcelableArrayList(SAVED_RECYCLER_VIEW_DATASET_ID, (ArrayList<? extends Parcelable>) ((AppActivity) requireActivity()).allTasks);
        super.onSaveInstanceState(outState);
    }
}