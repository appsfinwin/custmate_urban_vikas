package com.finwin.urbanvikas.custmate.account_selection_fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.finwin.custmate.R;
import com.finwin.urbanvikas.custmate.SupportingClass.Enc_crypter;
import com.finwin.urbanvikas.custmate.account_selection_fragment.action.AccountSelectionAction;
import com.finwin.custmate.databinding.FragmentAccountSelectionBinding;
import com.finwin.urbanvikas.custmate.activity_main.ActivityMain;
import com.finwin.urbanvikas.custmate.mpin_register.MPINRegisterFragment;
import com.finwin.urbanvikas.custmate.SupportingClass.ConstantClass;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class AccountSelectionFragment extends Fragment {
    TextView name, ac_no, mobile;
    String Name, Mobile, respndMsg, message, demessage;
    SweetAlertDialog dialog;
    CardView cv_ac_details;
    Button ok;

    final Enc_crypter encr = new Enc_crypter();
    RequestQueue queue;
    AccountSelectionViewmodel viewmodel;
    FragmentAccountSelectionBinding binding;

    public AccountSelectionFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static AccountSelectionFragment newInstance(String title) {
        AccountSelectionFragment frag = new AccountSelectionFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        queue = Volley.newRequestQueue(getActivity());
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_account_selection,container,false);
        viewmodel=new ViewModelProvider(getActivity()).get(AccountSelectionViewmodel.class);
        binding.setViewmodel(viewmodel);
        viewmodel.setBinding(binding);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        queue = Volley.newRequestQueue(getActivity());
        name = binding.tvAcName;
        ac_no = binding.tvAcAccNo;
        mobile = binding.tvAcMobile;
        ok = binding.btnContinue;



        viewmodel.getmAction().observe(getViewLifecycleOwner(), new Observer<AccountSelectionAction>() {
            @Override
            public void onChanged(AccountSelectionAction accountSelectionAction) {
                switch (accountSelectionAction.getAction())
                {
                    case AccountSelectionAction.GET_ACCOUNT_HOLDER_SUCCESS:
                        viewmodel.cancelLoading();
                        name.setText(accountSelectionAction.getAccountHolderResponse.getAccount().getData().getName());
                        mobile.setText( accountSelectionAction.getAccountHolderResponse.getAccount().getData().getMobile());
                        break;

                        case AccountSelectionAction.API_ERROR:

                            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops!")
                                    .setContentText(accountSelectionAction.getError())
                                    .show();
                            break;
                }
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConstantClass.mpinStatus) {

                    startActivity(new Intent(getActivity(), ActivityMain.class));
                    getActivity().finish();
                } else {
                    FragmentManager fragmentManager = getFragmentManager();
                    assert fragmentManager != null;
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    MPINRegisterFragment fragment = new MPINRegisterFragment();
                    fragmentTransaction.replace(R.id.frame_layout_login,fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });
    }

}
