package com.finwin.urbanvikas.custmate.home.balance_enquiry;

import android.app.Application;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.finwin.urbanvikas.custmate.SupportingClass.ConstantClass;
import com.finwin.urbanvikas.custmate.SupportingClass.Enc_Utils;
import com.finwin.urbanvikas.custmate.SupportingClass.Enc_crypter;
import com.finwin.urbanvikas.custmate.home.balance_enquiry.action.BalanceAction;
import com.finwin.urbanvikas.custmate.home.balance_enquiry.pojo.Balance;
import com.finwin.urbanvikas.custmate.retrofit.ApiInterface;
import com.finwin.urbanvikas.custmate.retrofit.RetrofitClient;
import com.finwin.urbanvikas.custmate.utils.Services;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class BalanceViewmodel extends AndroidViewModel {
    public BalanceViewmodel(@NonNull Application application) {
        super(application);

        repository=BalanceRepository.getInstance();
        mAction=new MutableLiveData<>();
        compositeDisposable=new CompositeDisposable();

        repository.setCompositeDisposable(compositeDisposable);
        repository.setmAction(mAction);
    }

    BalanceRepository repository;
    CompositeDisposable compositeDisposable;
    MutableLiveData<BalanceAction> mAction;
    Enc_crypter encr = new Enc_crypter();
    ApiInterface apiInterface;

    SweetAlertDialog loading;
    public ObservableField<String> tvAccountNumber=new ObservableField<>("");
    public ObservableField<String> tvDate=new ObservableField<>("");
    public ObservableField<String> tvTime=new ObservableField<>("");
    public ObservableField<String> tvAccountName=new ObservableField<>("");
    public ObservableField<String> tvBalance=new ObservableField<>("0.00");

    public void initLoading(Context context) {
        loading = Services.showProgressDialog(context);
    }

    public void cancelLoading() {
        if (loading != null) {
            loading.cancel();
            loading = null;
        }
    }

    public MutableLiveData<BalanceAction> getmAction() {
        mAction=repository.getmAction();
        return mAction;
    }

    public void setmAction(MutableLiveData<BalanceAction> mAction) {
        this.mAction = mAction;
    }

    public void balanceEnquiry() {

        Map<String, String> params = new HashMap<>();
        Map<String, String> items = new HashMap<>();
        items.put("account_no", ConstantClass.const_accountNumber);

        params.put("data", encr.conRevString(Enc_Utils.enValues(items)));
        apiInterface = RetrofitClient.RetrofitClient().create(ApiInterface.class);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), (new JSONObject(params)).toString());
        repository.balanceEnquiry(apiInterface, body);
    }


    @Override
    protected void onCleared() {
        super.onCleared();

        (repository.getCompositeDisposable()).dispose();
        mAction.setValue(new BalanceAction(BalanceAction.DEFAULT));
    }

    public void setBalance(Balance balance) {

        tvAccountName.set(balance.getData().getNAME());
        tvAccountNumber.set(balance.getData().getACCNO());
        tvDate.set(balance.getData().getDATE().substring(0,10));
        tvTime.set(balance.getData().getDATE().substring(10,19));
        tvBalance.set(balance.getData().getCURRENTBALANCE());
    }

    public void clickBack(View view)
    {
        mAction.setValue(new BalanceAction(BalanceAction.CLICK_BACK));
    }
}
