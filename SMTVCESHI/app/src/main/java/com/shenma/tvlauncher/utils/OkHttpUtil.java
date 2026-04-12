package com.shenma.tvlauncher.utils;

import java.io.File;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Callback;
import okhttp3.FormBody.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OkHttpUtil {
    private final static int CACHE_SIZE_BYTES = 1024 * 1024 * 2;
    public static void post(String address, Callback callback, Map<String, String> map) {
        OkHttpClient client = new OkHttpClient();
        Builder builder = new Builder();
        if (map != null) {
            for (Entry<String, String> entry : map.entrySet()) {
                builder.add((String) entry.getKey(), (String) entry.getValue());
            }
        }
        client.newCall(new Request.Builder().url(address).post(builder.build()).build()).enqueue(callback);
    }
    public static void get(String address, Callback callback) {
        Request.Builder builder=new Request.Builder();

        builder.method("GET",null);
        //Request request=builder.build();
        OkHttpClient Client=new OkHttpClient();
        //Call call=okHttpClient.newCall(request);
        Client.newCall(new Request.Builder().url(address).build()).enqueue(callback);

    }


    public static void Cacheget(File cacheFile, String address, Callback callback) {
        //定义一个信任所有证书的TrustManager
        final X509TrustManager trustAllCert = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        };
        //缓存大小为10M
        int cacheSize = 10 * 1024 * 1024;
        //创建缓存对象
        final Cache cache = new Cache(cacheFile,cacheSize);

        Request.Builder builder=new Request.Builder();

        builder.method("GET",null);
        //Request request=builder.build();
        //OkHttpClient Client=new OkHttpClient.Builder().cache(cache).build();
        //设置OkHttpClient
        OkHttpClient Client = new OkHttpClient.Builder()
                .cache(cache)
                //连接超时
                .connectTimeout(20, TimeUnit.SECONDS)
                //读取超时
                .readTimeout(20, TimeUnit.SECONDS)
                //写入超时
                .writeTimeout(20, TimeUnit.SECONDS)
                //超时重连
                //.retryOnConnectionFailure(true)
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())//配置
                .sslSocketFactory(new SSLSocketFactoryCompat(trustAllCert), trustAllCert).build();
        CacheControl cacheControl = new CacheControl.Builder()
                //有缓存直接用缓存
                //.noStore()
                //缓存60秒
                .maxAge(0, TimeUnit.SECONDS)
                //强制使用缓存
                //.maxStale(Integer.MAX_VALUE, TimeUnit.SECONDS)
                .build();
        Client.newCall(new Request.Builder().url(address).cacheControl(cacheControl).removeHeader("User-Agent").addHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36").build()).enqueue(callback);

    }

    public static void okhttpget(String url, Callback callback) {
        //定义一个信任所有证书的TrustManager
        final X509TrustManager trustAllCert = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        };
        Request.Builder builder=new Request.Builder();
        builder.method("GET",null);
        OkHttpClient Client= new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .hostnameVerifier(SSLSocketClient.getHostnameVerifier())//配置
                .sslSocketFactory(new SSLSocketFactoryCompat(trustAllCert), trustAllCert).build();
        Client.newCall(new Request.Builder().url(url).build()).enqueue(callback);

    }
}
