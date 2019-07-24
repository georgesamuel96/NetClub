package com.egcoders.technologysolution.netclub.Activities;


import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.egcoders.technologysolution.netclub.R;


/**
 * A simple {@link Fragment} subclass.
 */
public abstract class ViewstupFragment extends Fragment {

    private Bundle mSavedInstanceState;
    private boolean mHasInflated = false;
    private ViewStub mViewStub;

    public ViewstupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_viewstup, container, false);

        mViewStub = (ViewStub) view.findViewById(R.id.fragmentViewStub);
        mViewStub.setLayoutResource(getViewStubLayoutResource());
        mSavedInstanceState = savedInstanceState;
        if (getUserVisibleHint() && !mHasInflated) {
            View inflatedView = mViewStub.inflate();
            onCreateViewAfterViewStubInflated(inflatedView, mSavedInstanceState);
            afterViewStubInflated(view);
        }

        return view;
    }

    protected abstract int getViewStubLayoutResource();

    protected abstract void onCreateViewAfterViewStubInflated(View inflatedView, Bundle savedInstanceState);

    @CallSuper
    protected void afterViewStubInflated(View originalViewContainerWithViewStub) {
        mHasInflated = true;
        if (originalViewContainerWithViewStub != null) {
            View pb = originalViewContainerWithViewStub.findViewById(R.id.inflateProgressbar);
            pb.setVisibility(View.GONE);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && mViewStub != null && !mHasInflated) {
            View inflatedView = mViewStub.inflate();
            onCreateViewAfterViewStubInflated(inflatedView, mSavedInstanceState);
            afterViewStubInflated(getView());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHasInflated = false;
    }
}
