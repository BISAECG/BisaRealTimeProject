package com.bisa.health.cust;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bisa.health.R;
import com.bisa.health.cache.SharedPersistor;
import com.bisa.health.model.enumerate.ActionEnum;
import com.bisa.health.utils.ActivityUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActionBarBackFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ActionBarBackFragment";
    private View view;
    private TextView tvTitle;
    private ImageButton ibtn_back;
    private SharedPersistor sharedPersistor;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        sharedPersistor=new SharedPersistor(getActivity());
        view = inflater.inflate(R.layout.fragment_action_bar, container, false);
        Log.i(TAG, "onCreateView: "+view.getId());
        ibtn_back=(ImageButton) view.findViewById(R.id.ibtn_back);
        ibtn_back.setOnClickListener(this);
        tvTitle=(TextView) view.findViewById(R.id.tv_title);
        tvTitle.setText( this.getTag());
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v==ibtn_back){
                ActivityUtil.finishAnim(getActivity(),ActionEnum.BACK);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i(TAG, "onResume: "+ this.getTag());
    }
}
