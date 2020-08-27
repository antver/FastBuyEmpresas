package com.fastbuy.fastbuyempresas.ui.home;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.fastbuy.fastbuyempresas.Adapters.TabPagerAdapter;
import com.fastbuy.fastbuyempresas.PrincipalActivity;
import com.fastbuy.fastbuyempresas.R;
import com.google.android.material.tabs.TabLayout;

public class HomeViewModel extends ViewModel {
    MutableLiveData listapedidos;

    public HomeViewModel() {
        listapedidos = new MutableLiveData<>();
        listapedidos.setValue("");
        //mText.setValue("This is home fragment");


    }

    public LiveData<String> getDatos() {
        return listapedidos;
    }
}