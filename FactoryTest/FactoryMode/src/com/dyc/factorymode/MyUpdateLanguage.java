package com.dyc.factorymode;

import android.content.Context;

public class MyUpdateLanguage {

     public static void updateLanguage(Context context , boolean isChinese ) {
        android.content.res.Resources resources = context.getResources();
        android.content.res.Configuration config = resources.getConfiguration();
        android.util.DisplayMetrics dm = resources.getDisplayMetrics();
        if (isChinese) {
            config.locale = java.util.Locale.CHINESE;
            resources.updateConfiguration(config, dm);
        } else {
            config.locale = java.util.Locale.ENGLISH;
            resources.updateConfiguration(config, dm);
        }
    }

}
