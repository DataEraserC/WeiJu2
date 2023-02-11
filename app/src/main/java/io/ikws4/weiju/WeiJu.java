package io.ikws4.weiju;

import android.app.Application;
import android.content.Context;

import java.lang.ref.WeakReference;

import io.ikws4.weiju.api.API;
import io.ikws4.weiju.utils.MigrateTool;
import io.ikws4.weiju.editor.Editor;
import io.ikws4.weiju.sdk.FirebaseSdk;

public class WeiJu extends Application {
    // For xposed to hook this variable to indicate
    // that xposed works.
    public static boolean XPOSED_ENABLED = false;

    private static WeakReference<Context> sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = new WeakReference<>(this);

        API.initialize(this);
        MigrateTool.migrate(this);
        Editor.initialize(this);
        FirebaseSdk.initialize(this);
    }

    public static <T> T getService(Class<T> serviceClass) {
        return sContext.get().getSystemService(serviceClass);
    }
}
