package com.bisa.health.rest;

import android.support.annotation.Nullable;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Created by Administrator on 2018/8/14.
 */

public class TokenAuthenticator implements Authenticator {
    private static final String TAG = "TokenAuthenticator";

    @Nullable
    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        return null;
    }
}
