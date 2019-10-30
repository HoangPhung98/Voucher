package com.kingphung.voucher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;

import com.kingphung.voucher.model.Voucher;

import java.util.ArrayList;

import static android.os.Build.VERSION_CODES.P;

public class SlideVoucherActivity extends FragmentActivity implements VoucherFragment.OnFragmentInteractionListener {
    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    ArrayList<Voucher> listVoucher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_voucher);

        //set layout size
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        getWindow().setLayout(size.x*8/10, size.y*7/10);

        listVoucher =  getIntent().getParcelableArrayListExtra("LIST_VOUCHER");

        viewPager = findViewById(R.id.pagerVoucher);
        pagerAdapter = new ScreenSlidePagerAdaper(getSupportFragmentManager(), listVoucher);
        viewPager.setAdapter(pagerAdapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem()==0){
            super.onBackPressed();
        }else{
            viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
        }
    }
    private class ScreenSlidePagerAdaper extends FragmentStatePagerAdapter{
        ArrayList<Voucher> listVoucher;
        public ScreenSlidePagerAdaper(FragmentManager fm, ArrayList<Voucher> listVoucher){
            super(fm);
            this.listVoucher = listVoucher;
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            Voucher voucher = listVoucher.get(position);
            return VoucherFragment.newInstance(voucher.getTitle(), voucher.getCode(), voucher.getDescription(), voucher.getimg_url());
        }

        @Override
        public int getCount() {
            return listVoucher.size();
        }
    }
}
