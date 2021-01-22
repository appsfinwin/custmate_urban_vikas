package com.finwin.urbanvikas.custmate.my_account.update_mpin;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.finwin.urbanvikas.custmate.SupportingClass.Enc_crypter;
import com.finwin.urbanvikas.custmate.my_account.update_mpin.action.UpdateMpinAction;
import com.finwin.urbanvikas.custmate.retrofit.ApiInterface;
import com.finwin.urbanvikas.custmate.retrofit.RetrofitClient;

import org.json.JSONObject;

import java.util.Map;
import io.reactivex.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class UpdateMpinViewmodel extends AndroidViewModel {
    public UpdateMpinViewmodel(@NonNull Application application) {
        super(application);

        repository=UpdateMpinRepository.getInstance();
        compositeDisposable=new CompositeDisposable();
        mAction=new MutableLiveData<>();

        repository.setCompositeDisposable(compositeDisposable);
        repository.setmAction(mAction);
    }

    MutableLiveData<UpdateMpinAction> mAction;
    CompositeDisposable compositeDisposable;
    UpdateMpinRepository repository;
    final Enc_crypter encr = new Enc_crypter();
    ApiInterface apiInterface;

    public MutableLiveData<UpdateMpinAction> getmAction() {
        mAction=repository.getmAction();
        return mAction;
    }

    public void validateUser(Map<String, String> params) {

        apiInterface = RetrofitClient.RetrofitClient().create(ApiInterface.class);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), (new JSONObject(params)).toString());
        repository.validateUser(apiInterface, body);
    }

    public void validateMpin(Map<String, String> params) {

        apiInterface = RetrofitClient.RetrofitClient().create(ApiInterface.class);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), (new JSONObject(params)).toString());
        repository.validateMpin(apiInterface, body);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        (repository.getCompositeDisposable()).dispose();
        mAction.setValue(new UpdateMpinAction(UpdateMpinAction.DEFAULT));
    }
}
