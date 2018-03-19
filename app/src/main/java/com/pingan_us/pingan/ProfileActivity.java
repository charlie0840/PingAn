package com.pingan_us.pingan;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    final int LOGIN = 0, REGISTER = 1, REQUEST_CAMERA = 20, SELECT_FILE = 30, POLICY = 40, PROFILE_PHOTO = 50;

    private boolean hasPolicy = false, resume = false;
    private int counter = 0, ID_or_POLICY;
    private byte[] imageByte;
    private String policyName, userChoosenTask;
    private List<Drawable> policyList = new ArrayList<>();
    private List<String> policyNameList = new ArrayList<>();
    private List<String> policyIDList = new ArrayList<>();

    private CircleImageView profilePhoto;
    private TextView nameText, phoneText, policyText;
    private Button logoutBtn, loginBtn, regBtn, addPolicyBtn, deletePolicyBtn;
    private ImageView policyImg, preBtn, nextBtn;
    private ProgressBar progressBar;
    private ImageSwitcher policyImageSwitcher;
    private RelativeLayout policySection, nameLayer, phoneLayer, profileLogin, profileLogout, background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        ParseUser user = ParseUser.getCurrentUser();

        Drawable p = resize(getResources().getDrawable(R.drawable.insurance), 400, 400);

        policyList.add(p);
        policyNameList.add("Add a Policy");

        progressBar = (ProgressBar) findViewById(R.id.profile_progressBar);
        background = (RelativeLayout) findViewById(R.id.profile_background);

        profilePhoto = (CircleImageView) findViewById(R.id.profile_photo);

        policyText = (TextView) findViewById(R.id.profile_policy_name_text);
        nameText = (TextView) findViewById(R.id.pro_name_text);
        phoneText = (TextView) findViewById(R.id.pro_phone_text);

        policySection = (RelativeLayout) findViewById(R.id.policy_section);
        nameLayer = (RelativeLayout) findViewById(R.id.name_layout);
        phoneLayer = (RelativeLayout) findViewById(R.id.phone_layout);

        policyImageSwitcher = (ImageSwitcher) findViewById(R.id.policy_switch);

        profileLogin = (RelativeLayout) findViewById(R.id.profile_login);
        profileLogout = (RelativeLayout) findViewById(R.id.profile_logout);

        logoutBtn = (Button) findViewById(R.id.profile_logout_button);
        addPolicyBtn = (Button) findViewById(R.id.profile_add_policy_button);
        deletePolicyBtn = (Button) findViewById(R.id.profile_delete_policy_button);
        loginBtn = (Button) findViewById(R.id.login_nav);
        regBtn = (Button) findViewById(R.id.register_nav);
        preBtn = (ImageView) findViewById(R.id.profile_prev_policy_button);
        nextBtn = (ImageView) findViewById(R.id.profile_next_policy_button);

        logoutBtn.setOnClickListener(this);
        addPolicyBtn.setOnClickListener(this);
        deletePolicyBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        regBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        preBtn.setOnClickListener(this);
        profilePhoto.setOnClickListener(this);

        policyImageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                policyImg = new ImageView(getApplicationContext());
                policyImg.setLayoutParams(new ImageSwitcher.LayoutParams(
                        ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT
                ));
                policyImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
                policyImg.setImageDrawable(policyList.get(0));
                return policyImg;
            }
        });


        Animation animationLOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        Animation animationLIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        policyImageSwitcher.setOutAnimation(animationLOut);
        policyImageSwitcher.setInAnimation(animationLIn);


        policySection.setOnTouchListener(new OnSwipeTouchListener(getBaseContext()) {
            int switcherImage = 0;

            @Override
            public void onSwipeRight() {
                switcherImage = policyList.size();
                counter++;
                if (counter == switcherImage)
                    counter = 0;
                policyImageSwitcher.setImageDrawable(policyList.get(counter));
                policyText.setText(policyNameList.get(counter));
            }

            @Override
            public void onSwipeLeft() {
                switcherImage = policyList.size();
                counter--;
                if (counter == -1)
                    counter = switcherImage - 1;
                policyImageSwitcher.setImageDrawable(policyList.get(counter));
                policyText.setText(policyNameList.get(counter));
            }
        });

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
        progressBar.setVisibility(View.GONE);
        background.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        int switcherImage = 0;
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
            case R.id.profile_prev_policy_button:
                switcherImage = policyList.size();
                if(switcherImage == 0) {
                    break;
                }
                counter--;
                if (counter == -1)
                    counter = switcherImage - 1;
                policyImageSwitcher.setImageDrawable(policyList.get(counter));
                policyText.setText(policyNameList.get(counter));
                break;
            case R.id.profile_next_policy_button:
                switcherImage = policyList.size();
                if(switcherImage == 0) {
                    break;
                }
                counter++;
                if (counter == switcherImage)
                    counter = 0;
                policyImageSwitcher.setImageDrawable(policyList.get(counter));
                policyText.setText(policyNameList.get(counter));
                break;
            case R.id.profile_photo:
                ID_or_POLICY = PROFILE_PHOTO;
                selectImage();
                break;
            case R.id.profile_add_policy_button:
                ID_or_POLICY = POLICY;
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);

                LayoutInflater inflater=ProfileActivity.this.getLayoutInflater();
                //this is what I did to added the layout to the alert dialog
                View layout=inflater.inflate(R.layout.popup_window,null);
                builder.setView(layout);
                final EditText editText = (EditText) layout.findViewById(R.id.editTextDialogUserInput);
                Button confirm_btn = (Button) layout.findViewById(R.id.pop_confirm_button);
                Button cancel_btn = (Button) layout.findViewById(R.id.pop_cancel_button);

                final AlertDialog dialog = builder.create();
                dialog.show();

                confirm_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        policyName = editText.getText().toString();
                        dialog.dismiss();
                        selectImage();
                    }
                });
                cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.profile_delete_policy_button:
                deletePolicy();
                break;
        }
    }


    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(ProfileActivity.this);
                if (!result)
                    Toast.makeText(getApplicationContext(), "No permission!", Toast.LENGTH_LONG).show();
                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @TargetApi(23)
    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        PackageManager pm = this.getPackageManager();
        if (ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MyAppConstants.MY_CAMERA_REQUEST_CODE);
        } else {
            if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                startActivityForResult(intent, REQUEST_CAMERA);
            else
                Toast.makeText(this, "No camera detected", Toast.LENGTH_LONG).show();
        }
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MyAppConstants.MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                PackageManager pm = this.getPackageManager();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                    startActivityForResult(intent, REQUEST_CAMERA);
                else
                    Toast.makeText(this, "No camera detected", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        Bitmap resBitmap = null;
        if (data != null) {
            String path = Utility.getPath(this, data.getData());
            Uri uri = Uri.parse(new File(path).toString());
            bm = Bitmap.createBitmap(Utility.compressImageUri(uri, 1024, 768, this));
            if(bm != null) {
                resBitmap = Bitmap.createScaledBitmap(bm, bm.getWidth(), bm.getHeight(), true);
                addToList(resBitmap);
            }
            else {
                Toast.makeText(this, "Failed to load image!", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_LONG).show();
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap resBitmap = Bitmap.createScaledBitmap(thumbnail, thumbnail.getWidth(), thumbnail.getHeight(), true);
        addToList(resBitmap);

    }

    public void addToList(Bitmap bmp) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);
        background.setAlpha((float) 0.5);
        Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 600, 350, true);
        Drawable pic = new BitmapDrawable(getResources(), scaledBmp);
        if (ID_or_POLICY == POLICY){
            if(!hasPolicy) {
                policyList.clear();
                policyNameList.clear();
                hasPolicy = true;
            }
            policyList.add(pic);
            if(policyList.size() > 1)
                counter = policyList.size() - 2;
            policyNameList.add(policyName);
            nextBtn.performClick();
            counter = policyNameList.size() - 1;
            uploadImg(bmp);
        }
        else {
            profilePhoto.setImageDrawable(pic);
            uploadImg(bmp);
        }
    }

    private void uploadImg(final Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        final byte[] byteArray = stream.toByteArray();
        final ParseFile file;
        file = new ParseFile("imageID", byteArray);
        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    uploadData(file);
                    bmp.recycle();
                }
                else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    background.setAlpha((float) 0);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "pic uploading went wrong!!! " + e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void uploadData(ParseFile file) {
        final ParseUser currUser = ParseUser.getCurrentUser();
        if(ID_or_POLICY == POLICY) {
            final ParseObject object = new ParseObject("Policy");
            object.put("image", file);
            object.put("companyPolicy", policyName );
            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null) {
                        final String objectID = object.getObjectId();
                        List<String> idList = new ArrayList<String>();
                        if(currUser.get("policyID") != null) {
                            try {
                                idList = new ArrayList<String>((List<String>) currUser.get("policyID"));
                            }
                            catch (ClassCastException e1) {
                            }
                        }
                        idList.add(objectID);
                        policyIDList.add(objectID);
                        currUser.put("policyID", idList);
                        try {
                            currUser.save();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        background.setAlpha((float) 0);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });

        }
        else {
            currUser.put("idImage", file);
            currUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    background.setAlpha((float) 0);
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    public void loadContent() {
        getData();
        getPolicyList();
        Bitmap profileImage = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        profileImage = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length, options);
        Bitmap finalBmp = Bitmap.createScaledBitmap(profileImage, 320, 240, true);
        profileImage.recycle();
        profilePhoto.setImageBitmap(finalBmp);
    }

    public void getPolicyList() {
        ParseUser currUser = ParseUser.getCurrentUser();
        policyList = new ArrayList<Drawable>();
        if(currUser.get("policyID") != null) {
            try {
                policyIDList = new ArrayList<String>((List<String>) currUser.get("policyID"));
            }
            catch (ClassCastException e) {
            }
        }
        policyList.clear();
        getBitmapList(policyIDList);
    }

    public void getBitmapList(List<String> list) {
        if(list.size() == 0) {
            policyList.add(getResources().getDrawable(R.drawable.insurance));
            policyNameList.add("Add a Policy");
            hasPolicy = false;
            return;
        }
        hasPolicy = true;
        int i = 0;
        while(i < list.size()) {
            String currStr = list.get(i);
            i++;
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Policy");
            query.whereEqualTo("objectId", currStr);
            try {
                ParseObject object = query.getFirst();
                ParseFile file = (ParseFile) object.get("image");
                String name = (String) object.get("companyPolicy");
                byte[] byteArray = new byte[0];
                try {
                    byteArray = file.getData();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 600, 350, false);
                Drawable d = new BitmapDrawable(getResources(), scaledBmp);
                bmp.recycle();
                policyList.add(d);
                policyNameList.add(name);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(policyList.get(0) != null)
            policyImg.setImageDrawable(policyList.get(0));
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
            imageByte = file.getData();
            //byteArray = file.getData();
            //Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            //Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 600, 350, false);
            //Drawable d = new BitmapDrawable(getResources(), scaledBmp);
            //bmp.recycle();
            //profilePhoto.setImageDrawable(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void deletePolicy() {
        if(policyIDList.size() == 0) {
            return;
        }
        policyNameList.remove(counter);
        policyList.remove(counter);
        String id = policyIDList.get(counter);
        policyIDList.remove(counter);
        if(policyIDList.size() == 0)
            hasPolicy = false;
        counter = 0;
        if(policyList.size() == 0) {
            policyList.add(getResources().getDrawable(R.drawable.insurance));
            policyNameList.add("No Policy");
        }
        nextBtn.performClick();
        ParseUser currUser = ParseUser.getCurrentUser();
        currUser.put("policyID", policyIDList);
        try {
            currUser.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Policy");
        query.whereEqualTo("objectId", id);
        try {
            ParseObject obj = query.getFirst();
            obj.delete();
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
        if(resume) {
            loadContent();
            policyText.setText(policyNameList.get(0));
            nextBtn.performClick();
            resume = false;
            doAnimation();
        }
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

    private Drawable resize(Drawable image, int height, int width) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, height, width, true);
        //b.recycle();
        return new BitmapDrawable(getResources(), bitmapResized);
    }
}
