package com.fastbuy.fastbuyempresas.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTabHost;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.fastbuy.fastbuyempresas.Adapters.TabPagerAdapter;
import com.fastbuy.fastbuyempresas.EntregaFragment;
import com.fastbuy.fastbuyempresas.PrincipalActivity;
import com.fastbuy.fastbuyempresas.R;
import com.fastbuy.fastbuyempresas.RecepcionFragment;
import com.google.android.material.tabs.TabLayout;

public class HomeFragment extends Fragment {
    //private HomeViewModel homeViewModel;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        final ViewPager viewPager = (ViewPager) root.findViewById(R.id.pager);
        final TabLayout tabLayout = (TabLayout) root.findViewById(R.id.realtabcontent);
        tabLayout.addTab(tabLayout.newTab().setText("RECEPCIÓN"));
        tabLayout.addTab(tabLayout.newTab().setText("ENTREGAS"));

        TabPagerAdapter viewPagerAdapter = new TabPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new RecepcionFragment(), "RECEPCIÓN");
        viewPagerAdapter.addFragment(new EntregaFragment(),"ENTREGAS");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return root;
    }
}
