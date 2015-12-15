package com.zhaoyan.ladderballmanager.ui.fragment;


import android.support.v4.app.Fragment;

import com.zhaoyan.ladderballmanager.http.LBManagerApi;
import com.zhaoyan.ladderballmanager.http.LBManagerFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {

    public static final LBManagerApi mLadderBallApi = LBManagerFactory.getInstance();

}
