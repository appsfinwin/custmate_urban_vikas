package com.finwin.urbanvikas.custmate.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.finwin.custmate.BuildConfig;
import com.finwin.custmate.R;
import com.finwin.urbanvikas.custmate.ActivityInitial;
import com.finwin.urbanvikas.custmate.SupportingClass.ConstantClass;
import com.finwin.urbanvikas.custmate.SupportingClass.Enc_crypter;
import com.finwin.urbanvikas.custmate.SupportingClass.ErrorLog;
import com.finwin.urbanvikas.custmate.account_selection_fragment.AccountSelectionFragment;
import com.finwin.custmate.databinding.ActivityLoginBinding;
import com.finwin.urbanvikas.custmate.login.action.LoginAction;
import com.finwin.urbanvikas.custmate.sign_up.sign_up.SignUpActivity;
import com.finwin.urbanvikas.custmate.utils.VersionChecker;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.view.View.GONE;

public class LoginFragment extends Fragment {

    Button btnSignin;
    //EditText EdtUsername, EdtPassword;
    TextView tvSignup;
    String username, password, demessage, message = "Sorry, something went Wrong.",
            DecKey, DecKeyGrdl, respMsgApi, respndMsg = "error", apiKeyStored = "";
   // RequestQueue queue;
    final Enc_crypter encr = new Enc_crypter();
    private ProgressBar progress;

    LoginViewmodel viewmodel;
    //ActivityLoginBinding binding;

    ActivityLoginBinding binding;
    String latestVersion,currentVersion;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.activity_login, container, false);
        viewmodel=new ViewModelProvider(getActivity()).get(LoginViewmodel.class);
        binding.setViewmodel(viewmodel);

//        queue = Volley.newRequestQueue(getActivity());
        progress = (ProgressBar) binding.progress;
//        EdtUsername = rootview.findViewById(R.id.edt_usr);
//        EdtPassword = rootview.findViewById(R.id.edt_pass);
//        btnSignin = rootview.findViewById(R.id.btn_signin);
//        tvSignup = rootview.findViewById(R.id.tv_signup);

        viewmodel.setBinding(binding);
        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();

        checkVersion();
        return binding.getRoot();
    }

    private void checkVersion() {
        VersionChecker versionChecker = new VersionChecker();
        try {

            latestVersion = versionChecker.execute().get();
           // Toast.makeText(getActivity().getApplicationContext(), latestVersion , Toast.LENGTH_SHORT).show();

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        PackageManager manager = getActivity().getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert info != null;
        currentVersion = info.versionName;

        if(Float.parseFloat(currentVersion)<Float.parseFloat(latestVersion)){
            showUpdateDialog();
        }else {

        }
    }

    private void showUpdateDialog() {


            AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(getActivity());
            // Setting Dialog Title
            alertDialog2.setTitle("Update Available");
            // Setting Dialog Message
            alertDialog2.setMessage("There is a newer version of this application is available");
            // Setting Positive "Yes" Btn
            alertDialog2.setPositiveButton("Update",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog
                            Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                            i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.finwin.urbanvikas.custmate"));
                            startActivity(i);
                        }
                    });
            alertDialog2.setCancelable(false);

            alertDialog2.show();

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewmodel.getmAction().observe(getViewLifecycleOwner(), new Observer<LoginAction>() {
            @Override
            public void onChanged(LoginAction loginAction) {

                binding.progress.setVisibility(GONE);
                switch (loginAction.getAction()) {
                    case LoginAction.API_KEY_SUCCESS:
                        apiKeyStored = encr.revDecString(encr.decrypter(BuildConfig.AP_KE));
                        if (apiKeyStored.equals(loginAction.getError())) {
                            viewmodel.login();
                        }
                        break;

                    case LoginAction.CLICK_SIGNUP:
                        Intent i = new Intent(getContext(), SignUpActivity.class);
                        startActivity(i);

//                        Intent launchIntent =getActivity(). getPackageManager().getLaunchIntentForPackage("com.phonepe.app");
//                        if (launchIntent != null) {
//                            startActivity(launchIntent);
//                        } else {
//                            launchIntent = new Intent(Intent.ACTION_VIEW);
//                            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            launchIntent.setData(Uri.parse("market://details?id=" + "com.phonepe.app"));
//                            startActivity(launchIntent);
//                        }
                        break;

                    case LoginAction.API_ERROR:

                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("ERROR")
                                .setContentText(loginAction.getError())
                                .show();
                        break;

                        case LoginAction.LOGIN_SUCCESS:
                            ConstantClass.const_name = loginAction.getLoginResponse().getUser().getData().getUSERNAME();
                            ConstantClass.const_phone = loginAction.getLoginResponse().getUser().getData().getMOBILENO();
                            ConstantClass.const_cusId =loginAction.getLoginResponse().getUser().getData().getUSERID();
                            ConstantClass.mpinStatus = loginAction.getLoginResponse().getUser().getData().getMPINstatus();
                            ConstantClass.listAccountNumbers=new ArrayList<>();
                            ConstantClass.listAccountNumbers.clear();
                            ConstantClass.listScheme=new ArrayList<>();
                            ConstantClass.listScheme.clear();


                            int size=loginAction.getLoginResponse().getUser().getData().getAccNo().size();
                            for (int j=0;j<size;j++)
                            {
                                ConstantClass.listAccountNumbers.add(loginAction.getLoginResponse().getUser().getData().getAccNo().get(j).getAccNo());
                                ConstantClass.listScheme.add(loginAction.getLoginResponse().getUser().getData().getAccNo().get(j).getSchname());
                            }
                            openAccountSelectionFrag();
                            break;

                }
            }
        });

    }


    private void openAccountSelectionFrag() {
        try {
            FragmentManager fragmentManager = getFragmentManager();
            Class fragmentClass = AccountSelectionFragment.class;
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            fragmentManager.beginTransaction().replace(R.id.frame_layout_login, fragment).commit();

        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
            ErrorLog.submitError(getContext(), this.getClass().getSimpleName() + ":" + new Object() {
            }.getClass().getEnclosingMethod().getName(), e.toString());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            ErrorLog.submitError(getContext(), this.getClass().getSimpleName() + ":" + new Object() {
            }.getClass().getEnclosingMethod().getName(), e.toString());
        }
    }

    private void setloginStatus(String user, String pass) {
        try {
            SharedPreferences.Editor editor = Objects.requireNonNull(getContext()).getSharedPreferences(ConstantClass.PREFS_DATA, Context.MODE_PRIVATE).edit();
            editor.putBoolean("login", true);
            editor.putString("ue", user);
            editor.putString("pd", pass);
            editor.apply();
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkVersion();
    }
}
