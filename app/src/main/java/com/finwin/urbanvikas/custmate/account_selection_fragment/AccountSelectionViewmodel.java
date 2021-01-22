package com.finwin.urbanvikas.custmate.account_selection_fragment;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.PropertyChangeRegistry;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;


import com.finwin.custmate.BR;

import com.finwin.urbanvikas.custmate.SupportingClass.ConstantClass;
import com.finwin.urbanvikas.custmate.SupportingClass.Enc_Utils;
import com.finwin.urbanvikas.custmate.SupportingClass.Enc_crypter;
import com.finwin.urbanvikas.custmate.account_selection_fragment.action.AccountSelectionAction;
import com.finwin.custmate.databinding.FragmentAccountSelectionBinding;
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

public class AccountSelectionViewmodel extends AndroidViewModel implements Observable {
    public AccountSelectionViewmodel(@NonNull Application application) {
        super(application);

        repository=AccountSelectionRepository.getInstance();
        disposable=new CompositeDisposable();
        mAction=new MutableLiveData<>();

        repository.setDisposable(disposable);
        repository.setmAction(mAction);
        setAccountNumbers();

    }



    MutableLiveData<AccountSelectionAction> mAction;
    CompositeDisposable disposable;
    AccountSelectionRepository repository;
    Enc_crypter encr = new Enc_crypter();
    ApiInterface apiInterface;
    FragmentAccountSelectionBinding binding;

    private PropertyChangeRegistry registry = new PropertyChangeRegistry();
    public ObservableArrayList<String> listAccountNumbers=new ObservableArrayList<>();
    public ObservableField<String> acountNumberSelected=new ObservableField<>("");

    public MutableLiveData<AccountSelectionAction> getmAction() {
        mAction=repository.getmAction();
        return mAction;
    }

    private int selectedAccountNumber;

    @Bindable
    public int getSelectedAccountNumber() {
        return selectedAccountNumber;
    }

    public void setSelectedAccountNumber(int selectedState) {
        this.selectedAccountNumber = selectedState;
        registry.notifyChange(this, BR.selectedAccountNumber);
    }
//    private void setAccountNumbers() {
////        for (int i=0; i<ConstantClass.listAccountNumbers.size(); i++)
////        {
////            String scheme;
////            if ( ConstantClass.listScheme.get(i).contains("[FD]")){
////                listAccountNumbers.add(ConstantClass.listAccountNumbers.get(i)+ "(FD)");
////            }else if ( ConstantClass.listScheme.get(i).contains("[RD]")){
////                listAccountNumbers.add(ConstantClass.listAccountNumbers.get(i)+ "(RD)");
////            }else if ( ConstantClass.listScheme.get(i).contains("[SB]")){
////                listAccountNumbers.add(ConstantClass.listAccountNumbers.get(i)+ "(SB)");
////            }
////
////        }
////
////
////    }

    private void setAccountNumbers() {
        for (String a : ConstantClass.listAccountNumbers) {
            listAccountNumbers.add(a);
        }
    }
    public void onSelectedState(AdapterView<?> parent, View view, int position, long id)
    {
        binding.tvAcAccNo.setText(listAccountNumbers.get(position));
        acountNumberSelected.set(listAccountNumbers.get(position));
        ConstantClass.const_accountNumber=listAccountNumbers.get(position);
        initLoading(view.getContext());
        getAccountHolder();
    }


    public void getAccountHolder() {
        Map<String, String> params = new HashMap<>();
        Map<String, String> items = new HashMap<>();
        items.put("account_no", acountNumberSelected.get());

        params.put("data", encr.conRevString(Enc_Utils.enValues(items)));

        params.put("data", encr.conRevString(Enc_Utils.enValues(items)));
        apiInterface = RetrofitClient.RetrofitClient().create(ApiInterface.class);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), (new JSONObject(params)).toString());
        repository.getAccountHolder(apiInterface, body);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        (repository.getDisposable()).dispose();
        mAction.setValue(new AccountSelectionAction(AccountSelectionAction.DEFAULT));
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        registry.add(callback);
    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        registry.remove(callback);
    }

    SweetAlertDialog loading;

    public void initLoading(Context context) {
        loading = Services.showProgressDialog(context);
        loading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        loading.setTitleText("Getting Account info..");
        loading.setCancelable(false);
    }

    public void cancelLoading() {
        if (loading != null) {
            loading.cancel();
            loading = null;
        }
    }

    public void setBinding(FragmentAccountSelectionBinding binding) {
        this.binding=binding;
    }
}
