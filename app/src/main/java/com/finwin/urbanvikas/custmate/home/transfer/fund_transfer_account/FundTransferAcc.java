package com.finwin.urbanvikas.custmate.home.transfer.fund_transfer_account;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.finwin.custmate.R;
import com.finwin.custmate.databinding.FrgFundTransferAccBinding;
import com.finwin.urbanvikas.custmate.home.transfer.search_beneficiary.SearchBeneficiary;
import com.finwin.urbanvikas.custmate.SupportingClass.ConstantClass;
import com.finwin.urbanvikas.custmate.SupportingClass.Enc_Utils;
import com.finwin.urbanvikas.custmate.SupportingClass.Enc_crypter;
import com.finwin.urbanvikas.custmate.home.transfer.transfer_account_otp.FundTransferAccOTP;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import online.devliving.passcodeview.PasscodeView;

public class FundTransferAcc extends Fragment {

    Enc_crypter encr = new Enc_crypter();
    String StrOTP_data, StrOTP_id;

    SweetAlertDialog sweetDialog;
    FundTransferAccountViewmodel viewmodel;
    Dialog mpinDialog;
    SweetAlertDialog sweetAlertDialog;
    FrgFundTransferAccBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding= DataBindingUtil.inflate(inflater,R.layout.frg_fund_transfer_acc, container, false);
        sweetDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);

        viewmodel=new ViewModelProvider(this).get(FundTransferAccountViewmodel.class);
        binding.setViewmodel(viewmodel);

        viewmodel.setBinding(binding);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String type = bundle.getString("TRANS_TYP", "");
            if (type.equals("QR")) {
                String accno = bundle.getString("TRANS_ACCNO", "");
                viewmodel.etAmount.set(accno);
                viewmodel.getCreditAccountHolder(accno);
            }
        }

        viewmodel.getAccountHolder();


//        BtnVerify.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (EDTaccno.getText().toString().equals("")) {
//                    Toast.makeText(getActivity(), "Please Enter Credit Account Number", Toast.LENGTH_LONG).show();
//                } else {
//                    getCreditAccountHolderr(EDTaccno.getText().toString());
//                }
//            }
//        });

        binding.ibtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert getFragmentManager() != null;
                getFragmentManager().popBackStack();
            }
        });

//        BtnProceed = rootview.findViewById(R.id.btn_proceed);
//        BtnProceed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (EDTamount.getText().toString().equals("")) {
//                    Toast.makeText(getActivity(), "Please Enter Amount", Toast.LENGTH_LONG).show();
//                } else {
//                    verifyMpin(getActivity());
//                }
//            }
//        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewmodel.getmAction().observe(getActivity(), new Observer<FundTransferAccountAction>() {
            @Override
            public void onChanged(FundTransferAccountAction fundTransferAccountAction) {
                switch (fundTransferAccountAction.getAction())
                {
                    case FundTransferAccountAction.VALIDATE_MPIN_SUCCESS:
                        sweetAlertDialog.dismissWithAnimation();
                        if (fundTransferAccountAction.getValidateMpinResponse().getValue()) {
                            mpinDialog.dismiss();
                            viewmodel.generateOtp();
                        }else {
                            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Invalid!")
                                    .setContentText("Invalid MPIN")
                                    .show();
                        }
                        break;

                        case FundTransferAccountAction.GENERATE_OTP_SUCCESS:
                            StrOTP_data = fundTransferAccountAction.getGenarateOtpResponse().getOtp().getData();
                            StrOTP_id =  fundTransferAccountAction.getGenarateOtpResponse().getOtp().getOtpId();

                            Intent intent = new Intent(getActivity(), FundTransferAccOTP.class);
                            intent.putExtra("from", "account");
                            intent.putExtra("account_number", ConstantClass.const_accountNumber);
                            intent.putExtra("cr_account_no", viewmodel.etAccountNumber.get());//crAcno
                            intent.putExtra("process_amount", viewmodel.etAmount.get());
                            intent.putExtra("agent_id", ConstantClass.const_cusId);
                            intent.putExtra("ben_id", "");
                            intent.putExtra("TXN_PAYMODE", "");

                            intent.putExtra("otp_data", StrOTP_data);
                            intent.putExtra("otp_id", StrOTP_id);
                            startActivity(intent);
                            break;

                    case FundTransferAccountAction.GET_ACCOUNT_HOLDER_SUCCESS:

                        binding.tvTrnAccno.setText(fundTransferAccountAction.getAccountHolderResponse.getAccount().getData().getAccountNumber());
                        binding.tvTrnName.setText(fundTransferAccountAction.getAccountHolderResponse.getAccount().getData().getName());
                        binding.tvTrnMob.setText(fundTransferAccountAction.getAccountHolderResponse.getAccount().getData().getMobile());
                        break;

                        case FundTransferAccountAction.GET_CREDIT_ACCOUNT_HOLDER_SUCCESS:

                            viewmodel.isAcccountVerified.set(true);
                            binding.btnVerify.setText("Account Number verified!!");
                            binding.btnVerify.setTextColor(getResources().getColor(R.color.green));
                            binding.btnVerify.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verified_true, 0);
                            break;

                    case FundTransferAccountAction.CLICK_PROCEED:
                        verifyMpin(getActivity());

                        break;

                        case FundTransferAccountAction.CLICK_SEARCH_BENEFICIARY:
                            Intent searchIntent = new Intent(getActivity(), SearchBeneficiary.class);
                            startActivityForResult(searchIntent, 1044);

                        break;
                }
            }
        });



    }

    @Override
    public void onStop() {
        super.onStop();
        viewmodel.getmAction().setValue(new FundTransferAccountAction(FundTransferAccountAction.DEFAULT));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1044) {
            try {

                String result_ben_name = data.getStringExtra("result_ben_name");
                String result_acc_no = data.getStringExtra("result_ben_account_no");
                viewmodel.etAccountNumber.set(result_acc_no);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    public void verifyMpin(Activity activity) {
        mpinDialog = new Dialog(activity);
        mpinDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mpinDialog.setCancelable(true);
        mpinDialog.setTitle("Enter mPIN");
        mpinDialog.setContentView(R.layout.mpin_verification);

        PasscodeView passcodeView = mpinDialog.findViewById(R.id.passcode_view);
        if (passcodeView.requestFocus()) {
            mpinDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        passcodeView.setPasscodeEntryListener(new PasscodeView.PasscodeEntryListener() {
            @Override
            public void onPasscodeEntered(String passcode) {
                mpinDialog.dismiss();
                validateMpin(passcode);
            }
        });
        mpinDialog.show();
    }

    public void validateMpin(String _mpin)
    {
        final String mpin = _mpin;
        sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setTitleText("Please wait")
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


}
