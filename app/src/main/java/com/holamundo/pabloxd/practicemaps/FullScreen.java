package com.holamundo.pabloxd.practicemaps;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;

public class FullScreen extends AppCompatActivity {

    private ViewPager fullSlider;
    private FullSliderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        fullSlider = findViewById(R.id.full_slider);
        adapter = new FullSliderAdapter(this );
        fullSlider.setAdapter(adapter);

        Intent intent = getIntent();
        int pos = intent.getIntExtra("posisi", 0);

        fullSlider.setCurrentItem(pos);


    }

    private class FullSliderAdapter extends PagerAdapter {
        private Context context;
        private LayoutInflater inflater;

        public FullSliderAdapter(Context context) {
            this.context = context;
        }


        // list
        //int[] list_img = {R.drawable.bano1, R.drawable.bano2, R.drawable.bano3};
        int[] list_img = new int[]{R.drawable.croquis_utp, R.drawable.parking_utp1, R.drawable.parking_utp2,  R.drawable.bano1, R.drawable.bano2, R.drawable.bano3};

        @Override
        public int getCount() {
            return list_img.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_full, container, false );

            PhotoView photoView = view.findViewById(R.id.full_img);
            photoView.setImageResource(list_img[position]);

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((PhotoView) object);

        }
    }
}
