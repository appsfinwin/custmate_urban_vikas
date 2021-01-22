package com.finwin.urbanvikas.custmate.home.neftImps.neft;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.finwin.custmate.R;
import com.finwin.urbanvikas.custmate.SupportingClass.ConstantClass;
import com.finwin.urbanvikas.custmate.SupportingClass.Enc_Utils;
import com.finwin.urbanvikas.custmate.SupportingClass.Enc_crypter;
import com.finwin.custmate.databinding.FrgFundTransOneBinding;
import com.finwin.urbanvikas.custmate.home.neftImps.pojo.neft_transfer.model.ModelSpinner;
import com.finwin.urbanvikas.custmate.home.neftImps.pojo.neft_transfer.model.ModelTransferType;
import com.finwin.urbanvikas.custmate.home.transfer.transfer_account_otp.FundTransferAccOTP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import online.devliving.passcodeview.PasscodeView;

public class NeftImpsFragment extends Fragment {

    Enc_crypter encr = new Enc_crypter();

    RequestQueue requestQueue;
    SweetAlertDialog dialog,beneficiaryLoading,verifyMpinLoading,loadingDialog;
    NeftViewmodel viewmodel;
    FrgFundTransOneBinding binding;
    Dialog verifyMpinDialog;

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        binding= DataBindingUtil.inflate(inflater,R.layout.frg_fund_trans_one, container, false);
        requestQueue = Volley.newRequestQueue(getContext());
        dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);

        viewmodel=new ViewModelProvider((ViewModelStoreOwner) getActivity()).get(NeftViewmodel.class);
        binding.setViewmodel(viewmodel);
        viewmodel.setBinding(binding);
        loadingDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view= view;
        getBeneficiaryList();


        viewmodel.getmAction().observe(getActivity(), new Observer<NeftAction>() {
            @Override
            public void onChanged(NeftAction neftAction) {

                   switch (neftAction.getAction())
                {
                    case NeftAction.BENEFICIARY_LIST_SUCCESS:
                        beneficiaryLoading.dismiss();
                        viewmodel.setBeneficiaryList(neftAction.getBeneficiaryListResponse().getData());
                        break;

                        case NeftAction.API_ERROR:
                        beneficiaryLoading.dismiss();
                            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error!")
                                    .setContentText(neftAction.getError())
                                    .show();
                        break;

                    case NeftAction.VALIDATE_MPIN_SUCCESS:
                        verifyMpinLoading.dismiss();
                        generateOTP();
                        break;

                        case NeftAction.GENERATE_OTP_SUCCESS:

                            loadingDialog.dismiss();

                            Intent intent = new Intent(getActivity(), FundTransferAccOTP.class);
                            intent.putExtra("from", "neft");
                            intent.putExtra("account_number", ConstantClass.const_accountNumber);
                            intent.putExtra("cr_account_no", viewmodel.beneficiary_account.get());//crAcno
                            intent.putExtra("process_amount",viewmodel.transferAmount.get());
                            intent.putExtra("agent_id", ConstantClass.const_cusId);
                            intent.putExtra("ben_id", viewmodel.beneficiary_id.get());
                            intent.putExtra("TXN_PAYMODE", viewmodel.transferType_id.get());

                            intent.putExtra("otp_data", neftAction.genarateOtpResponse.getOtp().getData());
                            intent.putExtra("otp_id", neftAction.genarateOtpResponse.getOtp().getOtpId());
                            startActivity(intent);

                            break;

                            case NeftAction.CLICK_PROCEED:
                                    verifyMpin();

                                break;
                }
            }
        });

    }



    public static NeftImpsFragment newInstance(String text) {
        NeftImpsFragment f = new NeftImpsFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        return f;
    }

    public void getBeneficiaryList()
    {
        beneficiaryLoading = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        beneficiaryLoading.setTitleText("Loading");
        beneficiaryLoading.setCancelable(false);
        beneficiaryLoading.setContentText("Fetching Beneficiary Details..").show();
        Map<String, String> params = new HashMap<>();
        Map<String, String> items = new HashMap<>();
        items.put("customer_id", ConstantClass.const_cusId);
        items.put("ben_name", "");
        items.put("ben_mobile", "");
        items.put("ben_account_no", "");
        items.put("ben_ifscode", "");
        items.put("ben_type", "ob");

        Log.e("getBeneficiary: ", String.valueOf(items));
        params.put("data", encr.conRevString(Enc_Utils.enValues(items)));
        Log.e("getBeneficiary: data", encr.conRevString(Enc_Utils.enValues(items)));
        viewmodel.getBeneficiary(params);
    }


    public void verifyMpin() {
        verifyMpinDialog = new Dialog(view.getContext());
        verifyMpinDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        verifyMpinDialog.setCancelable(true);
        verifyMpinDialog.setTitle("Enter mPIN");
        verifyMpinDialog.setContentView(R.layout.mpin_verification);


        PasscodeView passcodeView = verifyMpinDialog.findViewById(R.id.passcode_view);
        if (passcodeView.requestFocus()) {
            verifyMpinDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        passcodeView.setPasscodeEntryListener(new PasscodeView.PasscodeEntryListener() {
            @Override
            public void onPasscodeEntered(String passcode) {
                verifyMpinDialog.dismiss();
                //validateMpin(passcode, verifyMpinDialog);
                verifyMpin(passcode);
            }
        });
        verifyMpinDialog.show();
    }

    public void verifyMpin(String mpin)
    {
        verifyMpinLoading= new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        verifyMpinLoading.setTitleText("Please wait")
                .setContentText("validating..")
                .show();

        Map<String, String> params = new HashMap<>();
        Map<String, String> items = new HashMap<>();
        items.put("userid", ConstantClass.const_cusId);
        items.put("MPIN", mpin);

        params.put("data", encr.conRevString(Enc_Utils.enValues(items)));
        Log.e("mpin post", encr.conRevString(Enc_Utils.enValues(items)));
        viewmodel.validateMpin(params);
    }


    public void generateOTP()
    {
        loadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        loadingDialog.setTitleText("Transferring..");
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        Map<String, String> params = new HashMap<>();
        Map<String, String> items = new HashMap<>();
        items.put("particular", "mob");
        items.put("account_no", ConstantClass.const_accountNumber);
        items.put("amount", viewmodel.transferAmount.get());
        items.put("agent_id", ConstantClass.const_cusId);
        params.put("data", encr.conRevString(Enc_Utils.enValues(items)));

        Log.e("OTPGenerate: ", String.valueOf(items));
        Log.e("OTPGenerate data", String.valueOf(params));

        viewmodel.generateOTP(params);
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
