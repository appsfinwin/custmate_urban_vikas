package com.finwin.urbanvikas.custmate.home.transfer.view_recent_transfers;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.finwin.urbanvikas.custmate.SupportingClass.Enc_crypter;
import com.finwin.urbanvikas.custmate.home.transfer.view_recent_transfers.action.RecentTransactionsAction;
import com.finwin.urbanvikas.custmate.home.transfer.view_recent_transfers.pojo.transaction_list.TransactionListResponse;
import com.finwin.urbanvikas.custmate.pojo.Response;
import com.finwin.urbanvikas.custmate.retrofit.ApiInterface;
import com.finwin.urbanvikas.custmate.SupportingClass.Enc_Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class RecentTransfersRepository {

    public static RecentTransfersRepository instance;

    public static RecentTransfersRepository getInstance() {
        if (instance == null) {
            instance = new RecentTransfersRepository();
        }
        return instance;
    }

    private static final String TAG = "RecentTransfersReposito";
    final Enc_crypter encr = new Enc_crypter();
    CompositeDisposable disposable;
    MutableLiveData<RecentTransactionsAction> mAction;
    TransactionListResponse transactionListResponse;
    Runnable mRunnable;
    Executor mExecutor = Executors.newSingleThreadExecutor();

    public CompositeDisposable getDisposable() {
        return disposable;
    }

    public void setDisposable(CompositeDisposable disposable) {
        this.disposable = disposable;
    }

    public MutableLiveData<RecentTransactionsAction> getmAction() {
        return mAction;
    }

    public void setmAction(MutableLiveData<RecentTransactionsAction> mAction) {
        this.mAction = mAction;
    }

    public void getTransactionList(ApiInterface apiInterface, RequestBody body) {
        Single<Response> call = apiInterface.getRecentTransactionList(body);
        call.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }


                    @Override
                    public void onSuccess(final Response response) {

                        fetchData(response.getData());


                    }

                    @Override
                    public void onError(Throwable e) {

                        if (e instanceof SocketTimeoutException) {
                            mAction.setValue(new RecentTransactionsAction(RecentTransactionsAction.API_ERROR, "Timeout! Please try again later"));
                        } else if (e instanceof UnknownHostException) {
                            mAction.setValue(new RecentTransactionsAction(RecentTransactionsAction.API_ERROR, "No Internet"));
                        } else {
                            mAction.setValue(new RecentTransactionsAction(RecentTransactionsAction.API_ERROR, e.getMessage()));
                        }
                    }
                });
    }

    public void fetchData(final String response)
    {
        mRunnable = new Runnable() {
            @Override
            public void run() {

                try {
                    String data = Enc_Utils.decValues(encr.revDecString(response));
                    Gson gson = new GsonBuilder().create();
                    transactionListResponse = gson.fromJson(data, TransactionListResponse.class);

                } catch (Exception e) {
                    Log.d(TAG, "transaction_list: " + e.getLocalizedMessage() + "  " + e.getMessage());
                    e.printStackTrace();
                }
                setValue(transactionListResponse);
            }
        };

        mExecutor.execute(mRunnable);
    }

    private void setValue(TransactionListResponse transactionListResponse) {
        if (transactionListResponse.getBen().getData().size() > 0) {
            mAction.postValue(new RecentTransactionsAction(RecentTransactionsAction.TRANSACTIONLIST_SUCCESS, transactionListResponse));
        } else {
            mAction.postValue(new RecentTransactionsAction(RecentTransactionsAction.TRANSACTIONLIST_EMPTY));
        }
    }
}



