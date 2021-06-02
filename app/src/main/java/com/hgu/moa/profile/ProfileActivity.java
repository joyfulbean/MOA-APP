package com.hgu.moa.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hgu.moa.R;
import com.hgu.moa.data.MyData;
import com.hgu.moa.init.LoginActivity;
import com.hgu.moa.main.MainActivity;
import com.hgu.moa.main.SearchActivity;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
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
    private TextView policy;

    private ChipNavigationBar chipNavigationBar;

    // for google sign in
    private GoogleSignInClient mGoogleSignInClient;

    private int REQUEST_TEST = 777;
    private int REQUEST_BANK = 888;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //* under tab bar (store & profile)
        chipNavigationBar = findViewById(R.id.bottom_navi);
        chipNavigationBar.setItemSelected(R.id.profile, true);
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                switch (id) {
                    case R.id.profile:
//                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
//                        overridePendingTransition(0, 0);
//                        finish();
                    case R.id.store:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        break;
                    case R.id.search:
                        startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        break;
                    case R.id.myroom:
                        startActivity(new Intent(getApplicationContext(), MyRoomActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        break;
                }
            }
        });

        logout = findViewById(R.id.logout_button);
        policy = findViewById(R.id.policy_button);
        name = findViewById(R.id.profileinfo_name_textview2);
        mail = findViewById(R.id.profileinfo_email_textview2);
        phone = findViewById(R.id.profileinfo_phonenumber_textview2);
        bankName = findViewById(R.id.profileinfo_bank_textview2);
        bankAccount = findViewById(R.id.profileinfo_banknumber_textview2);
        bankPeopleName = findViewById(R.id.profileinfo_bank_people_name_textview2);

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(signInAccount != null){
            name.setText(signInAccount.getDisplayName());
            mail.setText(signInAccount.getEmail());
            phone.setText(MyData.phoneNumber);

            if(MyData.bankName != null && MyData.accountNumber != null && MyData.accountName != null) {
                bankName.setText(MyData.bankName);
                bankAccount.setText(MyData.accountNumber);
                bankPeopleName.setText(MyData.accountName);
            }
        }

        //* phone number intent action
        phoneNumberButton = findViewById(R.id.profileinfo_phonenumber_button);
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
                Intent intent = new Intent(getApplication(), PhoneNumberActivity.class);
                startActivityForResult(intent, REQUEST_TEST);
                // startActivityForResult(intent);
            }
        });


        //* bank intent action
        bankButton = findViewById(R.id.profileinfo_bank_button);
        bankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (getApplication(), BankActivity.class);
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
                    Toast.makeText(getApplication() , "User Sign out!", Toast.LENGTH_SHORT).show();
                }catch (Exception e) {
                    Toast.makeText(getApplication() , "Error Sign out!", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(getApplication(), LoginActivity.class);
                startActivity(intent);
            }
        });

        //* show policy
        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://3.19.66.183:5000/privacy"));
                startActivity(browserIntent);
            }
        });
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments = new ArrayList<>();
        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }
        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }
        @Override
        public int getCount() {
            return fragments.size();
        }
    }


    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getApplication(), gso);
        // Build a GoogleSignInClient with the options specified by gso.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            MyData.setName(user.getDisplayName());
            MyData.setMail(user.getEmail());
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TEST) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplication(), "Result: " + MyData.phoneNumber, Toast.LENGTH_SHORT).show();
                phone.setText(MyData.phoneNumber);
            } else {   // RESULT_CANCEL
                //Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getActivity(), "Success: " + MyData.phoneNumber, Toast.LENGTH_SHORT).show();
                phone.setText(MyData.phoneNumber);
            }
        }
        else if(requestCode == REQUEST_BANK) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplication(), "Result: " + MyData.account, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }
}