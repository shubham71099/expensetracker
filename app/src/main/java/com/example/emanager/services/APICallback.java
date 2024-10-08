package com.example.emanager.services;

public interface APICallback<T> {
    void onSuccess(T result);
    void onError(Throwable error);
}
