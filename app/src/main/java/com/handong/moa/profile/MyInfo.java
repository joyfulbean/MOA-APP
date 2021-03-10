package com.handong.moa.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.handong.moa.R;
import com.handong.moa.data.MyData;
import com.handong.moa.init.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyInfo extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // for screen info
    private TextView name;
    private TextView mail;
    private TextView phone;
    private TextView bankName;
    private TextView bankAccount;
    private TextView bankPeopleName;

    // for intent
    private ImageButton phoneNumberButton;
    private ImageButton bankButton;
    private TextView logout;

    private int REQUEST_TEST = 777;
    private int REQUEST_BANK = 888;

    // for google sign in
    private GoogleSignInClient mGoogleSignInClient;

    public MyInfo() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyInfo.
     */
    // TODO: Rename and change types and number of parameters
    public static MyInfo newInstance(String param1, String param2) {
        MyInfo fragment = new MyInfo();
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

        //* setting screen
        View v=inflater.inflate(R.layout.fragment_my_info, container, false);

        logout = v.findViewById(R.id.logout_button);
        name = v.findViewById(R.id.profileinfo_name_textview2);
        mail = v.findViewById(R.id.profileinfo_email_textview2);
        phone = v.findViewById(R.id.profileinfo_phonenumber_textview2);
        bankName = v.findViewById(R.id.profileinfo_bank_textview2);
        bankAccount = v.findViewById(R.id.profileinfo_banknumber_textview2);
        bankPeopleName = v.findViewById(R.id.profileinfo_bank_people_name_textview2);

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getContext());
        if(signInAccount != null){
            name.setText(signInAccount.getDisplayName());
            mail.setText(signInAccount.getEmail());
            phone.setText(MyData.phoneNumber);

            if(MyData.account != null) {
                String str = MyData.account;
                String[] array = str.split(" ");
                bankName.setText(array[0]);
                bankAccount.setText(array[1]);
                bankPeopleName.setText(array[2]);
            }

        }

        //* phone number intent action
        phoneNumberButton = v.findViewById(R.id.profileinfo_phonenumber_button);
//        phoneNumberButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent (getActivity(), PhoneNumberActivity.class);
//                startActivity(intent);
//                refresh();
//            }
//        });

        phoneNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PhoneNumberActivity.class);
                startActivityForResult(intent, REQUEST_TEST);
                // startActivityForResult(intent);
            }
        });


        //* bank intent action
        bankButton = v.findViewById(R.id.profileinfo_bank_button);
        bankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getActivity(), BankActivity.class);
                startActivityForResult(intent, REQUEST_BANK);
//                startActivity(intent);
//                refresh();

            }
        });

        //* logout
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                try {
                    createRequest();
                    mAuth.signOut();
                    mGoogleSignInClient.signOut();
                    Toast.makeText(getContext() , "User Sign out!", Toast.LENGTH_SHORT).show();
                }catch (Exception e) {
                    Toast.makeText(getContext() , "Error Sign out!", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);

            }
        });

        return v;
    }

    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        // Build a GoogleSignInClient with the options specified by gso.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            MyData.setName(user.getDisplayName());
            MyData.setMail(user.getEmail());
        }
    }

    private void refresh(){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.detach(this).attach(this).commit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TEST) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getActivity(), "Result: " + MyData.phoneNumber, Toast.LENGTH_SHORT).show();
                phone.setText(MyData.phoneNumber);
            } else {   // RESULT_CANCEL
                //Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), "Success: " + MyData.phoneNumber, Toast.LENGTH_SHORT).show();
                phone.setText(MyData.phoneNumber);
            }
        }
        else if(requestCode == REQUEST_BANK) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getActivity(), "Result: " + MyData.account, Toast.LENGTH_SHORT).show();
                bankName.setText(MyData.bankName);
                bankAccount.setText(MyData.accountNumber);
                bankPeopleName.setText(MyData.accountName);
            } else {   // RESULT_CANCEL
                bankName.setText(MyData.bankName);
                bankAccount.setText(MyData.accountNumber);
                bankPeopleName.setText(MyData.accountName);
            }
        }
    }


}