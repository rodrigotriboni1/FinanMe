package com.rodrigotriboni.budget.pojos;

public interface ResponseCallback {
    void onResponse(String response);
    void onError(Throwable throwable);
}