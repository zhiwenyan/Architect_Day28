package com.darren.architect_day28.download;

import java.io.File;
import java.io.IOException;

/**
 * Created by hcDarren on 2017/11/26.
 */

public interface DownloadCallback {
    void onFailure(IOException e);

    void onSucceed(File file);
}
