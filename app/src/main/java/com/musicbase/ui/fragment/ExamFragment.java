package com.musicbase.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.musicbase.R;
import com.musicbase.ui.activity.BuyRecordActivity;
import com.musicbase.ui.activity.CacheActivity;
import com.musicbase.ui.activity.CardActivity;
import com.musicbase.ui.activity.ContentListActivity;
import com.musicbase.ui.activity.SettingActivity;
import com.musicbase.ui.activity.WorkWebActivity;
import com.musicbase.util.ActivityUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.musicbase.preferences.Preferences.CONTRACT_USER;

/**
 * A simple {@link Fragment} subclass.
 */
@Deprecated
public class ExamFragment extends Fragment {

    @BindView(R.id.tv_tip)
    TextView tv_tip;
    @BindView(R.id.layout_tip)
    LinearLayout layout_tip;
    @BindView(R.id.test0)
    LinearLayout test0;
    @BindView(R.id.test1)
    LinearLayout test1;
    @BindView(R.id.test2)
    LinearLayout test2;
    @BindView(R.id.test3)
    LinearLayout test3;
    @BindView(R.id.test4)
    LinearLayout test4;
    @BindView(R.id.test5)
    LinearLayout test5;
    @BindView(R.id.test6)
    LinearLayout test6;
    Unbinder unbinder;

    public ExamFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exam, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.test0, R.id.test1, R.id.test2, R.id.test3, R.id.test4, R.id.test5, R.id.test6})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.test0:
                Intent intent3 = new Intent(getContext(), WorkWebActivity.class);
                intent3.putExtra("filePath", "https://www.yinyuesuyang.com/testpaper/nengliceshi_1/");
                intent3.putExtra("name", "在线测试");
                intent3.putExtra("fileType", "html");
                startActivity(intent3);
                break;
            case R.id.test1:
                Intent intent = new Intent(getActivity(), ContentListActivity.class);
                intent.putExtra("id", 3);
                intent.putExtra("content", "央院音基模考题");
                startActivity(intent);
                break;
            case R.id.test2:
                ActivityUtils.showToast(getActivity(), "此功能暂未开放，敬请期待！");
                break;
            case R.id.test3:
                ActivityUtils.showToast(getActivity(), "此功能暂未开放，敬请期待！");
//                startActivity(new Intent(getActivity(), TestActivity.class));
                break;
            case R.id.test4:
                ActivityUtils.showToast(getActivity(), "此功能暂未开放，敬请期待！");
                break;
            case R.id.test5:
                ActivityUtils.showToast(getActivity(), "此功能暂未开放，敬请期待！");
                break;
            case R.id.test6:
//                ActivityUtils.showToast(getActivity(), "此功能暂未开放，敬请期待！");
                break;

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
