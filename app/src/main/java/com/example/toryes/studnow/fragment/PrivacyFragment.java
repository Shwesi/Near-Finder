package com.example.toryes.studnow.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.toryes.studnow.R;
import com.example.toryes.studnow.Utils.JustifyTextView;

/**
 * Created by TORYES on 8/12/2017.
 */

public class PrivacyFragment  extends Fragment {
    JustifyTextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.privacy_policy, null, false);
        getActivity().setTitle(getActivity().getResources().getString(R.string.privacy_policy));
        textView = (JustifyTextView) view.findViewById(R.id.textView);
        textView.setText(getActivity().getResources().getString(R.string.privacy_text));
        getActivity().setTitle(getActivity().getResources().getString(R.string.privacy_policy));
        return view;
    }
}
