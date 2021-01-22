package com.finwin.urbanvikas.custmate.sign_up.sign_up.action;

import com.finwin.urbanvikas.custmate.home.transfer.fund_transfer_account.pojo.get_account_holder.GetAccountHolderResponse;
import com.finwin.urbanvikas.custmate.pojo.Response;


public class SignupAction {
    public static final int DEFAULT=-1;
    public static final int API_ERROR=1;
    public static final int ACCOUNT_HOLDER_EXIST=2;
    public static final int ACCOUNT_HOLDER_NOT_EXIST=3;
    public static final int API_KEY_SUCCESS=4;
    public static final int CLICK_SIGN_IN=5;
    public int action;
    String error;
    public GetAccountHolderResponse getAccountHolderResponse;

    public Response apiKeyResponse;

    public SignupAction(int action, Response apiKeyResponse) {
        this.action = action;
        this.apiKeyResponse = apiKeyResponse;
    }

    public SignupAction(int action) {
        this.action = action;
    }

    public SignupAction(int action, String error) {
        this.action = action;
        this.error = error;
    }

    public int getAction() {
        return action;
    }

    public SignupAction(int action, GetAccountHolderResponse getAccountHolderResponse) {
        this.action = action;
        this.getAccountHolderResponse = getAccountHolderResponse;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public GetAccountHolderResponse getGetAccountHolderResponse() {
        return getAccountHolderResponse;
    }

    public void setGetAccountHolderResponse(GetAccountHolderResponse getAccountHolderResponse) {
        this.getAccountHolderResponse = getAccountHolderResponse;
    }

    public Response getApiKeyResponse() {
        return apiKeyResponse;
    }

    public void setApiKeyResponse(Response apiKeyResponse) {
        this.apiKeyResponse = apiKeyResponse;
    }
}
