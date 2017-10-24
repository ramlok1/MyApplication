
package com.example.mobilesdblack.ejemplo2;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentParent extends Fragment {

    public int posicion = 0;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    public ViewPagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent, container, false);
        getIDs(view);
        setEvents();

        return view;
    }


    public View ObtenerVistaActual(int posi){

        return viewPager.findViewWithTag("fin" + viewPager.getCurrentItem());
    }

    public View Obtenervistainicial(int posi){

        return viewPager.findViewWithTag("medio" + 1);

    }

    private void getIDs(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.my_viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.my_tab_layout);
        adapter = new ViewPagerAdapter(getFragmentManager(), getActivity(), viewPager, tabLayout);
        viewPager.setAdapter(adapter);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#ee7d00"));
        tabLayout.setSelectedTabIndicatorHeight(7);


    }

    public void focusFirst(){
        viewPager.setCurrentItem(0);
        // :'D !!
    }

    int selectedTabPosition;

   private void setEvents() {

        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                viewPager.setCurrentItem(tab.getPosition());
                selectedTabPosition = viewPager.getCurrentItem();
                posicion =  tab.getPosition();
                //ViewFragment = adapter.getTabView(posicion);


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                Log.d("Unselected", "Unselected " + tab.getPosition());
            }
        });
    }

    public void addPage(String pagename, int tipo) {
        Bundle bundle = new Bundle();
        bundle.putString("data", pagename);
        bundle.putInt("tipo",tipo);
        //posicion =  tab.getPosition();
        bundle.putInt("posicion",posicion+1);
        FragmentChild fragmentChild = new FragmentChild();
        fragmentChild.setArguments(bundle);
        adapter.addFrag(fragmentChild, pagename);

        adapter.notifyDataSetChanged();
        if (adapter.getCount() > 0) {
            tabLayout.setupWithViewPager(viewPager);
            LinearLayout tabStrip = ((LinearLayout)tabLayout.getChildAt(0));
            for(int i = 0; i < tabStrip.getChildCount(); i++) {
                tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        return true;
                    }
                });


            }
        }

        viewPager.setCurrentItem(adapter.getCount() - 1);
    }

}
