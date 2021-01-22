package com.finwin.urbanvikas.custmate.home.transfer.view_recent_transfers;

import android.app.Application;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.finwin.urbanvikas.custmate.SupportingClass.ConstantClass;
import com.finwin.urbanvikas.custmate.SupportingClass.Enc_Utils;
import com.finwin.urbanvikas.custmate.SupportingClass.Enc_crypter;
import com.finwin.urbanvikas.custmate.home.transfer.view_recent_transfers.action.RecentTransactionsAction;
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

public class RecentTransfersViewmodel extends AndroidViewModel {
    public RecentTransfersViewmodel(@NonNull Application application) {
        super(application);

        repository=RecentTransfersRepository.getInstance();
        disposable=new CompositeDisposable();
        mAction=new MutableLiveData<>();

        repository.setDisposable(disposable);
        repository.setmAction(mAction);

    }
    Enc_crypter encr = new Enc_crypter();
    ApiInterface apiInterface;
    RecentTransfersRepository repository;
    CompositeDisposable disposable;
    MutableLiveData<RecentTransactionsAction> mAction;

    public LiveData<RecentTransactionsAction> getmAction() {
        mAction=repository.getmAction();
        return mAction;
    }

    SweetAlertDialog loading;
    public void initLoading(Context context) {
        loading = Services.showProgressDialog(context);
    }

    public void cancelLoading() {
        if (loading != null) {
            loading.cancel();
            loading = null;
        }
    }


    public void getRecentTransactions() {
        Map<String, Object> jsonParams = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        jsonParams.put("customer_id", ConstantClass.const_cusId);

        params.put("data", encr.conRevString(Enc_Utils.enValues(jsonParams)));

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), (new JSONObject(params)).toString());

        apiInterface = RetrofitClient.RetrofitClient().create(ApiInterface.class);
        repository.getTransactionList(apiInterface,body);
    }


    @Override
    protected void onCleared() {
        super.onCleared();

        (repository.getDisposable()).dispose();
        mAction.setValue(new RecentTransactionsAction(RecentTransactionsAction.DEFAULT));
    }

    public void backPressed(View view)
    {
        mAction.setValue(new RecentTransactionsAction(RecentTransactionsAction.BACK_PRESSED));
    }
}
