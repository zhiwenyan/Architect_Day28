package com.darren.architect_day28;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import com.darren.architect_day28.download.DownloadCallback;
import com.darren.architect_day28.download.DownloadFacade;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 有三个类需要用户去关注，后面我们有可能会自己去更新代码，用户就需要换调用方式
        // 调用的方式 门面
        DownloadFacade.getFacade().init(this);

        DownloadFacade.getFacade()
                .startDownload("http://acj3.pc6.com/pc6_soure/2017-11/com.ss.android.essay.joke_664.apk", new DownloadCallback() {
            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onSucceed(File file) {
                installFile(file);
            }
        });


        // 多线断点下载，只要客户端做一下处理就可以了
        // 什么叫做断点续传，逻辑是什么？
        // 如果下载中断（网络断开，程序退出），下次可以接着上次的地方下载
        // 多线程的逻辑是什么？多个线程读后台文件每个线程只负责读取单独的内容

        // 文件更新 ，专门下载apk软件（应用宝，迅雷，百度云）

        // 文件更新，1. 可以直接跳转到浏览器更新，2.直接下载不断点，也不多线程（OkHttp）3.多线程 4. 多线程加断点

        // 专门下载apk软件：多线程 + 断点，最多只能同时下载几个文件，一些正在下载，一些暂停，一些准备，参考 OKHttp 源码 Dispatch 的逻辑

        // 4. 多线程加断点
        /*OkHttpManager okHttpManager = new OkHttpManager();
        Call call = okHttpManager.asyncCall("http://acj3.pc6.com/pc6_soure/2017-11/com.ss.android.essay.joke_664.apk");

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 不断的读写文件，单线程
                InputStream inputStream = response.body().byteStream();

                File file = new File(getCacheDir(),"x02345.apk");

                OutputStream outputStream = new FileOutputStream(file);

                int len = 0;
                byte[] buffer = new byte[1024*10];

                while ((len = inputStream.read(buffer))!=-1){
                    outputStream.write(buffer,0,len);
                }

                inputStream.close();
                outputStream.close();

                installFile(file);
            }
        });*/

        // 断点续传，需要服务器配合，思路跟断点下载类似
    }

    private void installFile(File  file) {
        // 核心是下面几句代码
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", file );
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);
    }
}
