package com.example.androidproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentNav fragNav;
    FirebaseAuth lAuth;
    private EditText username;
    private EditText password;
    private SharedPreferences sharedPreferences;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    ActivityResultLauncher<Intent> someActivityResultLauncher;
    private static final int RC_SIGN_IN = 7;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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

       /* someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // There are no request codes
                            Intent data = result.getData();
                            Toast.makeText(requireActivity().getApplicationContext(),"Google fail", Toast.LENGTH_SHORT).show();
                        }
                        if(result.getResultCode() == 1000)
                        {
                            Toast.makeText(requireActivity().getApplicationContext(),"Google detection", Toast.LENGTH_SHORT).show();
                        }
                        if(result.getResultCode() == Activity.RESULT_CANCELED)
                        {
                            Toast.makeText(requireActivity().getApplicationContext(),"Invalid google", Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

       /* view.findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragNav.navigateFrag(new RegisterFragment(), false);
            }
        });*/

        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        lAuth = FirebaseAuth.getInstance();

        sharedPreferences = requireActivity().getSharedPreferences(Constants.userFileName, Context.MODE_PRIVATE);
        String storedEmail = sharedPreferences.getString(Constants.usernameKey, null);
        String storedPassword = sharedPreferences.getString(Constants.passwordKey, null);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(requireActivity(), gso);

       // GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireActivity().getApplicationContext());
       // if(account!=null)
       // {
       //    startApp();
       // }

        if(storedEmail!= null && storedPassword!= null && TimerService.running)
        {
            if(!storedPassword.equals("google"))
                lAuth.signInWithEmailAndPassword(storedEmail, storedPassword);

            startApp();
        }
        else
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }

        view.findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateForm();
            }
        });

        view.findViewById(R.id.btn_google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

        return view;
    }

    private void googleSignIn()
    {
        Intent signInIntent = gsc.getSignInIntent();
        //someActivityResultLauncher.launch(signInIntent);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                //Navigate to second activity
                //Toast.makeText(requireActivity().getApplicationContext(), "Worked", Toast.LENGTH_SHORT).show();

                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(requireActivity());
                if(acct!=null) {
                    String username_s = acct.getEmail();
                    String password_s = "google";
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constants.usernameKey, username_s);
                    editor.putString(Constants.passwordKey, password_s);
                    editor.apply();
                    startApp();
                }
            } catch (ApiException e) {
                Toast.makeText(requireActivity().getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void validateForm()
    {
        String username_s = username.getText().toString();
        String password_s = password.getText().toString();

        if(TextUtils.isEmpty(username_s.trim())) {
            username.setError("Enter an email!");
            return;
        }

        if(TextUtils.isEmpty(password_s.trim())) {
            password.setError("Enter a password!");
            return;
        }

        lAuth.signInWithEmailAndPassword(username_s, password_s).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(),"Successfully logged in", Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constants.usernameKey, username_s);
                    editor.putString(Constants.passwordKey, password_s);
                    editor.apply();

                    startApp();
                } else {
                    try{
                        throw Objects.requireNonNull(task.getException());
                    } catch (Exception e){
                        Toast.makeText(requireActivity().getApplicationContext(),"Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
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

    private void startApp() {
        Intent intent = new Intent(getActivity(), AppActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}