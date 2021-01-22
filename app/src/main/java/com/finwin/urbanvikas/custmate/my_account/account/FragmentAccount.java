package com.finwin.urbanvikas.custmate.my_account.account;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.finwin.custmate.databinding.FrgMyAccountBinding;
import com.finwin.urbanvikas.custmate.ActivityInitial;
import com.finwin.urbanvikas.custmate.my_account.update_mpin.UpdateMPINFragment;
import com.finwin.custmate.R;
import com.finwin.urbanvikas.custmate.SupportingClass.ConstantClass;
import com.finwin.urbanvikas.custmate.SupportingClass.Enc_Utils;
import com.finwin.urbanvikas.custmate.SupportingClass.Enc_crypter;
import com.finwin.urbanvikas.custmate.SupportingClass.ErrorLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.finwin.urbanvikas.custmate.SupportingClass.ConstantClass.api_getAccountHolder;


public class FragmentAccount extends Fragment {

    SweetAlertDialog dialog;

    final Enc_crypter encr = new Enc_crypter();


    FrgMyAccountBinding binding;
    AccountViewmodel viewmodel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater,R.layout.frg_my_account, container, false);
        viewmodel= new ViewModelProvider(this).get(AccountViewmodel.class);
        viewmodel.setBinding(binding);
        binding.setViewModel(viewmodel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);


        binding.tvAccName.setText(ConstantClass.const_name);
        binding.tvAccNo.setText(ConstantClass.const_accountNumber);
        binding.tvAccMob.setText(ConstantClass.const_phone);

        viewmodel.setSelectedAccountNumber(ConstantClass.listAccountNumbers.indexOf(ConstantClass.const_accountNumber));

        ConstraintLayout linear_changeMpin =binding.linearChangeMpin;
        linear_changeMpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                assert fragmentManager != null;
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                UpdateMPINFragment containerFragment = new UpdateMPINFragment();
                fragmentTransaction.replace(R.id.frame_layout, containerFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        ConstraintLayout linearSignout = binding.linearSignout;
        linearSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertsignout();
            }
        });
    }


    public void alertsignout() {
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(getActivity());
        // Setting Dialog Title
        alertDialog2.setTitle("Confirm SignOut");
        // Setting Dialog Message
        alertDialog2.setMessage("Are you sure you want to Sign out?");
        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        Intent i = new Intent(getActivity(), ActivityInitial.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                });
        // Setting Negative "NO" Btn
        alertDialog2.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        dialog.cancel();
                    }
                });
        // Showing Alert Dialog
        alertDialog2.show();
    }


}
