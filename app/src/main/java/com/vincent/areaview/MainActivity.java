package com.vincent.areaview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<View> viewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        viewList = new ArrayList<>();
        viewList.add(getOneItem());
        viewList.add(getOneItem());
        viewList.add(getOneItem());

        ViewPager vp = (ViewPager) findViewById(R.id.vp);
        vp.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewList.get(position));
                return viewList.get(position);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(viewList.get(position));
            }
        });
    }

    private View getOneItem() {
        View v = this.getLayoutInflater().inflate(R.layout.vp_item, null);
        final AreaView iv = (AreaView) v.findViewById(R.id.area_view);

        InputStream is = this.getResources().openRawResource(R.raw.img_paper);
        iv.setImageBitmap(BitmapFactory.decodeStream(is));

        iv.addPoly(new float[]{0, 152, 125, 152, 125, 419, 265, 419, 265, 736, 405, 736, 405, 417, 538, 417, 538, 834, 0, 834},
                new AreaView.OnAreaViewClickListener() {
                    @Override
                    public void onAreaViewClick(AreaView.Shape shape) {
                        Toast.makeText(MainActivity.this, "Open Document", Toast.LENGTH_SHORT).show();
                    }
                });

        iv.addCircle(new float[]{630, 700}, 51f, new AreaView.OnAreaViewClickListener() {
            @Override
            public void onAreaViewClick(AreaView.Shape shape) {
                Toast.makeText(MainActivity.this, "Open Circle", Toast.LENGTH_SHORT).show();
            }
        });

//        Glide.with(MainActivity.this)
//                .load("file://" + Environment.getExternalStorageDirectory() + "/img_paper.jpg")
//                .into(iv);

//        Glide.with(MainActivity.this)
//                .load("http://img.blog.csdn.net/20161213103222714?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMDEyODIyMA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast")
//                .into(iv);

//        Glide.with(MainActivity.this)
//                .load("http://img.blog.csdn.net/20161213103222714?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdTAxMDEyODIyMA==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast")
//                .asBitmap()
//                .into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                        iv.setImageBitmap(resource);
//                    }
//                });
//
//        iv.addPoly(new float[]{0,
//                100,
//                300,
//                100,
//                300,
//                220,
//                0,
//                220}, new AreaView.OnAreaViewClickListener() {
//            @Override
//            public void onAreaViewClick(AreaView.Shape shape) {
//                Toast.makeText(MainActivity.this, "Open Picture", Toast.LENGTH_SHORT).show();
//            }
//        });

        return v;
    }
}
