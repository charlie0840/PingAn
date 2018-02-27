package com.pingan_us.pingan;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    List<String> idList = new ArrayList<>();
    List<Drawable> picList = new ArrayList<>();
    List<String> picTitleList = new ArrayList<>();

    ImageButton autoBtn, healthBtn, houseBtn, businessBtn;
    ImageSwitcher imageSwitcher;
    ImageView imageSwitcherView;
    TextView slideTitle;
    LinearLayout carrierBtn;
    GridLayout carrierGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);


        Drawable temp = getApplicationContext().getResources().getDrawable(R.drawable.pingan);

        picList.add(temp);
        picTitleList.add("Welcome!");

        slideTitle = (TextView) findViewById(R.id.slide_title);
        imageSwitcher = (ImageSwitcher) findViewById(R.id.home_switcher);
        ImageSwitcherPicasso mImageSwitcherPicasso = new ImageSwitcherPicasso(getApplicationContext(), imageSwitcher);
        //Picasso.with(getApplicationContext()).load(new File()).into(mImageSwitcherPicasso);
        Picasso.with(getApplicationContext()).load(R.drawable.ambassador).into(mImageSwitcherPicasso);

        carrierGrid = (GridLayout) findViewById(R.id.carrier_grid);
        carrierBtn = (LinearLayout) findViewById(R.id.carrier_btn);

        carrierBtn.setOnClickListener(this);
        carrierGrid.setVisibility(View.GONE);

        autoBtn = (ImageButton) findViewById(R.id.auto_learnmore);
        houseBtn = (ImageButton) findViewById(R.id.house_learnmore);
        healthBtn = (ImageButton) findViewById(R.id.health_learnmore);
        businessBtn = (ImageButton) findViewById(R.id.business_learnmore);

        autoBtn.setOnClickListener(this);
        houseBtn.setOnClickListener(this);
        healthBtn.setOnClickListener(this);
        businessBtn.setOnClickListener(this);

        //imageSwitcher.

        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        imageSwitcher.setInAnimation(in);
        imageSwitcher.setOutAnimation(out);

        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                imageSwitcherView = new ImageView(getApplicationContext());
                imageSwitcherView.setLayoutParams(new ImageSwitcher.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT));
                imageSwitcherView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageSwitcherView.setImageDrawable(picList.get(0));
                return imageSwitcherView;
            }
        });

        getBitmapList();

        imageSwitcher.postDelayed(new Runnable() {
            int i = 0;

            @Override
            public void run() {
                Drawable pic = picList.get(i);
                String dis = picTitleList.get(i);
                i++;
                if(i == picList.size())
                    i = 0;
                imageSwitcher.setImageDrawable(pic);
                slideTitle.setText(dis);
                imageSwitcher.postDelayed(this, 3000);
            }
        }, 5000);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.carrier_btn:
                GridLayout grid = (GridLayout) findViewById(R.id.carrier_grid);
                if(grid.getVisibility() == View.GONE)
                    grid.setVisibility(View.VISIBLE);
                else
                    grid.setVisibility(View.GONE);
                break;
            case R.id.auto_learnmore:
                startActivityForResult();
                Utility.showDialog(HomeActivity.this, MyAppConstants.autoLearnMore);
                break;
            case R.id.house_learnmore:
                Utility.showDialog(HomeActivity.this, MyAppConstants.houseLearnMore);
                break;
            case R.id.health_learnmore:
                Utility.showDialog(HomeActivity.this, MyAppConstants.healthLearnMore);
                break;
            case R.id.business_learnmore:
                Utility.showDialog(HomeActivity.this, MyAppConstants.businessLearnMore);
                break;
        }
    }

    public void getBitmapList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Data");
        query.whereEqualTo("name", MyAppConstants.HOME_SLIDE);
        try {
            ParseObject object = query.getFirst();
            idList = new ArrayList<String>((List<String>)object.get("idArray"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(idList.size() == 0) {
            picList.add(getApplicationContext().getResources().getDrawable(R.drawable.cancel));
            picTitleList.add("No Internet Connection");
        }
        for(String curr:idList) {
            ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Pictures");
            query1.whereEqualTo("objectId", curr);
            try {
                ParseObject object1 = query1.getFirst();

                byte[] byteArray = new byte[0];
                String title = "";
                try {
                    byteArray = ((ParseFile) object1.get("pic")).getData();
                    title = (String) object1.get("discription");
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 600, 350, false);
                Drawable d = new BitmapDrawable(getResources(), scaledBmp);
                bmp.recycle();
                picList.add(d);
                picTitleList.add(title);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(picList.size() > 1) {
            picList.remove(0);
            picTitleList.remove(0);
        }

        if(picList.size() != 0 && picList.get(0) != null)
            imageSwitcher.setImageDrawable(picList.get(0));
    }


}
