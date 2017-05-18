package com.example.mobilesdblack.ejemplo2;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TableLayout;

public class Wizard_Main extends AppCompatActivity {

    Toolbar toolbar;

    TabLayout tabLayout;
    ViewPager viewPager;

    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

//Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//set content view AFTER ABOVE sequence (to avoid crash)
        this.setContentView(R.layout.activity_wizard__main);
        //setContentView(R.layout.activity_wizard__main);

        toolbar = (Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);


        //tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        //viewPager = (ViewPager)findViewById(R.id.viewPager);
        //viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        //viewPagerAdapter.addFragments(new Inicio(), "SELECCIONE SU CUPÓN");
        //viewPagerAdapter.addFragments(new Preguntas(), "RESPONDA LA ENCUESTA");
        //viewPagerAdapter.addFragments(new Finalizar(), "FINALIZAR ENCUESTA");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }
}
