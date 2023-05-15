package com.example.androidproject;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseAuth pAuth;
    private GoogleSignInClient gsc;
    private GoogleSignInOptions gso;

    private FirebaseUser user;

    private ImageView profilePic;

    private SharedPreferences sharedPreferences;

    //private Uri imageUri = null;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sharedPreferences = requireActivity().getSharedPreferences(Constants.userFileName, Context.MODE_PRIVATE);
        pAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(requireActivity(), gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(requireActivity());
        TextView title = view.findViewById(R.id.profile_title);
        user = FirebaseAuth.getInstance().getCurrentUser();
        String strng = null;
        if(user!=null)
        {
            strng = user.getEmail() + "'s Profile";
        }
        else
        {
            if(acct!=null)
                strng = acct.getDisplayName() + "'s Profile";
        }

        title.setText(strng);

        view.findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

        view.findViewById(R.id.btn_delete_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAcc();
            }
        });

        profilePic = view.findViewById(R.id.profile_pic);

        if(((AppActivity) getActivity()).profilePic!=null)
            profilePic.setImageURI(((AppActivity) getActivity()).profilePic);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 3);
            }
        });

        return view;
    }

    void logOut() {
        if(user!=null){
            pAuth.signOut();
            Toast.makeText(getActivity(),"Successfully logged out", Toast.LENGTH_SHORT).show();
        }
        else
        {
            gsc.signOut()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getActivity(),"Successfully logged out", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        exitApp();
    }

    void exitApp(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        requireActivity().finish();
    }

    void deleteAcc() {
        if(user!=null) {
            user.delete();
            Toast.makeText(getActivity(),"Successfully deleted account", Toast.LENGTH_SHORT).show();
        }
        else
        {
            deleteGoogle();
        }

        exitApp();
    }

    void deleteGoogle(){
        //delete user data, not account
        gsc.signOut()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(),"Successfully deleted data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && data!=null)
        {
            ((AppActivity) getActivity()).profilePic = data.getData();
           // imageUri = data.getData();
            profilePic.setImageURI(((AppActivity) getActivity()).profilePic);
        }
    }

  /*  @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("imageUri", imageUri);
    }*/
}