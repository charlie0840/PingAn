package com.pingan_us.pingan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageSwitcher;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.io.File;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        LinearLayout carrierBtn;
        GridLayout carrierGrid;

        ImageSwitcher imageSwitcher;

        imageSwitcher = (ImageSwitcher) findViewById(R.id.home_switcher);
        ImageSwitcherPicasso mImageSwitcherPicasso = new ImageSwitcherPicasso(getApplicationContext(), imageSwitcher);
        //Picasso.with(getApplicationContext()).load(new File()).into(mImageSwitcherPicasso);
        Picasso.with(getApplicationContext()).load(R.drawable.ambassador).into(mImageSwitcherPicasso);

        carrierGrid = (GridLayout) findViewById(R.id.carrier_grid);
        carrierBtn = (LinearLayout) findViewById(R.id.carrier_btn);

        carrierBtn.setOnClickListener(this);
        carrierGrid.setVisibility(View.GONE);
        //imageSwitcher.
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
        }
    }
}
