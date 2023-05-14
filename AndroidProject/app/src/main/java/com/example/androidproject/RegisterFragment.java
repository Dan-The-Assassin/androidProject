package com.example.androidproject;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseAuthWebException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentNav fragNav;
    private EditText username;
    private EditText password;
    private EditText c_password;
    FirebaseAuth rAuth;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        rAuth = FirebaseAuth.getInstance();

        username = view.findViewById(R.id.username_reg);
        password = view.findViewById(R.id.password_reg);
        c_password = view.findViewById(R.id.c_password_reg);

      /*  view.findViewById(R.id.btn_login_reg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragNav.navigateFrag(new LoginFragment(), false);
            }
        });*/

        view.findViewById(R.id.btn_register_reg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateForm();
            }
        });

        return view;
    }

    private void validateForm()
    {
        String username_s = username.getText().toString();
        String password_s = password.getText().toString();
        String c_password_s = c_password.getText().toString();

        if(TextUtils.isEmpty(username_s.trim())) {
            username.setError("Enter an email!");
            return;
        }

        if(TextUtils.isEmpty(password_s.trim())) {
            password.setError("Enter a password!");
            return;
        }

        if(!c_password_s.equals(password_s)){
            c_password.setError("Password doesn't match!");
            return;
        }

        rAuth.createUserWithEmailAndPassword(username_s, password_s).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(),"Account successfully created", Toast.LENGTH_SHORT).show();
                } else {
                    try{
                        throw Objects.requireNonNull(task.getException());
                    } catch (Exception e){
                        if(e instanceof  FirebaseAuthWeakPasswordException) {
                            Toast.makeText(requireActivity().getApplicationContext(), "Password needs at least 6 characters", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Toast.makeText(requireActivity().getApplicationContext(),"Invalid email or password", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
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