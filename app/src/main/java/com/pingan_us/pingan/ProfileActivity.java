package com.pingan_us.pingan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    final int LOGIN = 0, REGISTER = 1;

    private CircleImageView profilePhoto;
    private TextView nameText, phoneText;
    private Button logoutBtn, loginBtn, regBtn, addPolicyBtn;
    private ImageSwitcher policyImageSwitcher;
    private RelativeLayout policySection, nameLayer, phoneLayer, profileLogin, profileLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        ParseUser user = ParseUser.getCurrentUser();

        profilePhoto = (CircleImageView) findViewById(R.id.profile_photo);

        nameText = (TextView) findViewById(R.id.pro_name_text);
        phoneText = (TextView) findViewById(R.id.pro_phone_text);

        policySection = (RelativeLayout) findViewById(R.id.policy_section);
        nameLayer = (RelativeLayout) findViewById(R.id.name_layout);
        phoneLayer = (RelativeLayout) findViewById(R.id.phone_layout);

        policyImageSwitcher = (ImageSwitcher) findViewById(R.id.policy_switch);

        profileLogin = (RelativeLayout) findViewById(R.id.profile_login);
        profileLogout = (RelativeLayout) findViewById(R.id.profile_logout);

        logoutBtn = (Button) findViewById(R.id.profile_logout_button);
        addPolicyBtn = (Button) findViewById(R.id.policy_add_button);
        loginBtn = (Button) findViewById(R.id.login_nav);
        regBtn = (Button) findViewById(R.id.register_nav);

        logoutBtn.setOnClickListener(this);
        addPolicyBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        regBtn.setOnClickListener(this);


        if(user == null) {
            profileLogin.setVisibility(View.INVISIBLE);
            profileLogout.setVisibility(View.VISIBLE);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN);
        }
        else {
            Toast.makeText(getApplicationContext(), "User exists", Toast.LENGTH_LONG).show();
            profileLogin.setVisibility(View.VISIBLE);
            profileLogout.setVisibility(View.INVISIBLE);
        }

        profilePhoto.setVisibility(View.INVISIBLE);
        logoutBtn.setVisibility(View.INVISIBLE);
        policySection.setVisibility(View.INVISIBLE);
        nameLayer.setVisibility(View.INVISIBLE);
        phoneLayer.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_logout_button:
                ParseUser currUser = ParseUser.getCurrentUser();
                currUser.logOut();
                profileLogin.setVisibility(View.GONE);
                profileLogout.setVisibility(View.VISIBLE);
                break;
            case R.id.login_nav:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, LOGIN);
                break;
            case R.id.register_nav:
                Intent intent1 = new Intent(this, RegisterActivity.class);
                startActivityForResult(intent1, REGISTER);
                break;
            case R.id.policy_add_button:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LOGIN) {
            if(resultCode == RESULT_OK) {
                profileLogin.setVisibility(View.VISIBLE);
                profileLogout.setVisibility(View.INVISIBLE);
                getData();
            }
            else if(resultCode == RESULT_CANCELED) {
                profileLogin.setVisibility(View.INVISIBLE);
                profileLogout.setVisibility(View.VISIBLE);
            }
        }
    }

    public void getData() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        String full_name = (String)currentUser.get("lastName") + "," + (String)currentUser.get("firstName");
        String phone_no = (String)currentUser.get("phoneNo");

        nameText.setText(full_name);
        phoneText.setText(phone_no);

        ParseFile file = null;
        byte[] byteArray;
        try {
            file = currentUser.getParseFile("idImage");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            byteArray = file.getData();
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 600, 350, false);
            Drawable d = new BitmapDrawable(getResources(), scaledBmp);
            bmp.recycle();
            profilePhoto.setImageDrawable(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void doAnimation() {
        ScrollView scrollView = findViewById(R.id.profile_scrollview);

        final Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up_in);
        final Animation alpha = AnimationUtils.loadAnimation(this, R.anim.alpha);
        final Animation scale = AnimationUtils.loadAnimation(this, R.anim.scale);
        scrollView.startAnimation(slideUp);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                profilePhoto.setVisibility(View.VISIBLE);
                logoutBtn.setVisibility(View.VISIBLE);
                policySection.setVisibility(View.VISIBLE);
                phoneLayer.setVisibility(View.VISIBLE);
                nameLayer.setVisibility(View.VISIBLE);
                profilePhoto.startAnimation(scale);
                logoutBtn.startAnimation(alpha);
                nameLayer.startAnimation(alpha);
                phoneLayer.startAnimation(alpha);
                policySection.startAnimation(alpha);
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ParseUser user = ParseUser.getCurrentUser();

        profilePhoto.setVisibility(View.INVISIBLE);
        logoutBtn.setVisibility(View.INVISIBLE);
        policySection.setVisibility(View.INVISIBLE);
        nameLayer.setVisibility(View.INVISIBLE);
        phoneLayer.setVisibility(View.INVISIBLE);

        if(user == null) {
            profileLogin.setVisibility(View.INVISIBLE);
            profileLogout.setVisibility(View.VISIBLE);
        }
        else {
            getData();
            profileLogin.setVisibility(View.VISIBLE);
            profileLogout.setVisibility(View.INVISIBLE);
            doAnimation();
        }
    }




}
