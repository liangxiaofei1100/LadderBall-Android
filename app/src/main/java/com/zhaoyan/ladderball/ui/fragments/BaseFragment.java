package com.zhaoyan.ladderball.ui.fragments;


import android.support.v4.app.Fragment;

import com.zhaoyan.ladderball.http.LadderBallApi;
import com.zhaoyan.ladderball.http.LadderBallFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {

    public static final LadderBallApi mLadderBallApi = LadderBallFactory.getInstance();

}
