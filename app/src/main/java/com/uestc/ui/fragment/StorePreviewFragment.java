package com.uestc.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.uestc.domain.ADInfo;
import com.uestc.domain.TempStaticInstanceCollection;
import com.uestc.ui.activity.R;
import com.uestc.ui.view.viewpager.CycleViewPager;
import com.uestc.utils.ViewFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacob Long(Nerozen) on 2016/4/5.
 */
public class StorePreviewFragment extends Fragment {

    private View fragmentView;
    private CycleViewPager storeInfoPreviewGallary; //

    private List<ImageView> storePreviewPicsList = new ArrayList<ImageView>();
    private List<ADInfo> adiInfoList = TempStaticInstanceCollection.storePreviewInfo;


    private class onSinglePictureClickedListener implements CycleViewPager.ImageCycleViewListener {

        @Override
        public void onImageClick(ADInfo info, int postion, View imageView) {
            //跳转进入商品详细页面webview
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        StorePreviewFragment.this.fragmentView = inflater.inflate(R.layout.fragment_store_preview, container, false);
        initStorePreviewGallary();
        return fragmentView;
    }

    private void initStorePreviewGallary() { //初始化商店预览画廊，并设置循环播放，载入图片，设定切换时间
        StorePreviewFragment.this.storeInfoPreviewGallary =
                (CycleViewPager)StorePreviewFragment.this.getFragmentManager().findFragmentById(R.id.gallary_store_preview);

        storePreviewPicsList.add(ViewFactory.getImageView(getActivity(), adiInfoList.get(adiInfoList.size() - 1).getUrl()));
        for (int i = 0; i < adiInfoList.size(); i++) {
            storePreviewPicsList.add(ViewFactory.getImageView(getActivity(), adiInfoList.get(i).getUrl()));
        }
        storePreviewPicsList.add(ViewFactory.getImageView(getActivity(), adiInfoList.get(0).getUrl()));

        storeInfoPreviewGallary.setCycle(true);
        storeInfoPreviewGallary.setData(storePreviewPicsList, adiInfoList, new onSinglePictureClickedListener());
        storeInfoPreviewGallary.setWheel(true);
        storeInfoPreviewGallary.setTime(2000);
        storeInfoPreviewGallary.setIndicatorCenter();
    }
}
