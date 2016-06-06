package com.mossle.android.fragment;

import android.content.Context;
import android.content.SharedPreferences;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.telephony.TelephonyManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;

import com.mossle.android.AppConstants;
import com.mossle.android.R;
import com.mossle.android.support.ContractAsyncTask;

public class ContractFragment extends Fragment {
    private SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        sp = this.getActivity().getSharedPreferences("userInfo",
                Context.MODE_WORLD_READABLE);

        View view = inflater.inflate(R.layout.fragment_contract, null);

        // 绑定XML中的ListView，作为Item的容器
        ListView listView = (ListView) view
                .findViewById(R.id.fragment_contract_list);
        this.initContract(listView);

        return view;
    }

    public String getImei(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            return tm.getDeviceId();
        } catch (Exception ex) {
            return ex.toString();
        }
    }

    public void initContract(ListView listView) {
        String sessionId = sp.getString("SESSIONID", null);
        new ContractAsyncTask(AppConstants.getBaseUrl()
                + "/rs/android/pim/contract", this.getActivity(), sessionId)
                .execute(listView);
    }
}
