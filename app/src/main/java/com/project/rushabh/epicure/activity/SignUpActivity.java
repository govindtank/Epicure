package com.project.rushabh.epicure.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.project.rushabh.epicure.R;
import com.project.rushabh.epicure.adapter.SignUpSectionPagerAdaper;
import com.project.rushabh.epicure.fragment.SignUpPlaceHolderFragment;
import com.project.rushabh.epicure.test.SignUpFragment2;
import com.project.rushabh.epicure.test.SignUpFragment3;
import com.project.rushabh.epicure.util.NonSwipeableViewPager;

public class SignUpActivity extends AppCompatActivity implements SignUpPlaceHolderFragment.OnGetAccountViewListener, SignUpFragment2.OnGetPersonalViewListener, SignUpFragment3.OnGetWelocomeViewListener {

    private SignUpSectionPagerAdaper sectionsPagerAdapter;
    private NonSwipeableViewPager viewPager;

    private TextView subTitleText, nextText, previousText;

    private TextInputEditText nameSignUpText, emailSignUpText, passwordSignUpText, retypeSignUpText,
            contactSignUpText, addressSignUpText;

    private Button locationSignUpBtn, signUpBtn;

    private double latitudeSignUp = -1, longitudeSignUp = -1;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private View accountView, personalView, welcomeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        sectionsPagerAdapter = new SignUpSectionPagerAdaper(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = findViewById(R.id.viewpager_sign_up);
        viewPager.setAdapter(sectionsPagerAdapter);

        //viewPager.setOffscreenPageLimit(0);//to load all the pages together

        subTitleText = findViewById(R.id.text_signup_sub_title);
        nextText = findViewById(R.id.text_signup_next);
        previousText = findViewById(R.id.text_signup_previous);

        sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_file_name), MODE_PRIVATE);

        previousText.setVisibility(View.INVISIBLE);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        subTitleText.setText(getString(R.string.sign_up_subtitle_1));
                        previousText.setVisibility(View.INVISIBLE);
                        nextText.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        subTitleText.setText(getString(R.string.sign_up_subtitle_2));
                        previousText.setVisibility(View.VISIBLE);
                        nextText.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        subTitleText.setText(null);
                        previousText.setVisibility(View.VISIBLE);
                        nextText.setVisibility(View.INVISIBLE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void onSignUpClick(View view) {
        switch (view.getId()) {
            case R.id.text_signup_next:
                if (viewPager.getCurrentItem() == 0) {
                    if (TextUtils.isEmpty(nameSignUpText.getText()) || TextUtils.isEmpty(emailSignUpText.getText()) || TextUtils.isEmpty(passwordSignUpText.getText()) || TextUtils.isEmpty(retypeSignUpText.getText()))
                        Toast.makeText(this, "Empty field", Toast.LENGTH_SHORT).show();
                    else if (passwordSignUpText.getText().length() < 8)
                        Toast.makeText(this, "Password too short", Toast.LENGTH_SHORT).show();
                    else if (!passwordSignUpText.getText().toString().equals(retypeSignUpText.getText().toString()))
                        Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                    else {
                        editor = sharedPreferences.edit();
                        editor.putString(getString(R.string.spk_name), nameSignUpText.getText().toString());
                        editor.putString(getString(R.string.spk_email), emailSignUpText.getText().toString());
                        editor.putString(getString(R.string.spk_password), passwordSignUpText.getText().toString());
                        editor.apply();
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    }
                } else if (viewPager.getCurrentItem() == 1) {
                    if (TextUtils.isEmpty(contactSignUpText.getText()) || TextUtils.isEmpty(addressSignUpText.getText()) || latitudeSignUp == -1 || longitudeSignUp == -1) {
                        Toast.makeText(this, "Empty field", Toast.LENGTH_SHORT).show();
                    } else {
                        editor = sharedPreferences.edit();
                        editor.putString(getString(R.string.spk_contact), contactSignUpText.getText().toString());
                        editor.putString(getString(R.string.spk_address), addressSignUpText.getText().toString());
                        editor.putFloat(getString(R.string.spk_latitude), (float) latitudeSignUp);
                        editor.putFloat(getString(R.string.spk_longitude), (float) longitudeSignUp);
                        editor.apply();
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    }
                }
                break;
            case R.id.text_signup_previous:
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                break;
            case R.id.text_to_login:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
        }
    }

    @Override
    public void getAccountView(View view) {
        accountView = view;
        nameSignUpText = accountView.findViewById(R.id.textInputEditText_signup_name); //0
        emailSignUpText = accountView.findViewById(R.id.textInputEditText_signup_email);
        passwordSignUpText = accountView.findViewById(R.id.textInputEditText_signup_password);
        retypeSignUpText = accountView.findViewById(R.id.textInputEditText_signup_retype_password);
    }

    @Override
    public void getPersonalView(View view) {
        personalView = view;
        contactSignUpText = personalView.findViewById(R.id.textInputEditText_signup_contact); //1
        addressSignUpText = personalView.findViewById(R.id.textInputEditText_signup_address);
        locationSignUpBtn = personalView.findViewById(R.id.btn_signup_location);
        locationSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getBaseContext(), MapsActivity.class).putExtra("source", "signup"), 5);
            }
        });
    }

    @Override
    public void getWelocomeView(View view) {
        welcomeView = view;
        signUpBtn = welcomeView.findViewById(R.id.btn_sign_up); //2
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String allData = sharedPreferences.getString(getString(R.string.spk_name),"") + "\n"
                        + sharedPreferences.getString(getString(R.string.spk_email),"") + "\n"
                        + sharedPreferences.getString(getString(R.string.spk_password),"") + "\n"
                        + sharedPreferences.getString(getString(R.string.spk_contact),"") + "\n"
                        + sharedPreferences.getString(getString(R.string.spk_address),"") + "\n"
                        + sharedPreferences.getFloat(getString(R.string.spk_latitude), 0) + "\n"
                        + sharedPreferences.getFloat(getString(R.string.spk_longitude),0) + "\n";
                Toast.makeText(SignUpActivity.this, allData , Toast.LENGTH_LONG).show();
                Toast.makeText(SignUpActivity.this, allData , Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 5) {
            latitudeSignUp = data.getDoubleExtra("latitude", 0);
            longitudeSignUp = data.getDoubleExtra("longitude", 0);
            //Toast.makeText(this, Double.toString(latitudeSignUp), Toast.LENGTH_SHORT).show();
        }
    }
}
