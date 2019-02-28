package com.dyc.factorymode;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import static android.content.Context.MODE_PRIVATE;
import com.dyc.factorymode.TestReport;

public class StartUtil {

    public static void StartNextActivity(String str , int result ,int testmode, Activity paramActivity , Class<?> cls){
        SharedPreferences.Editor editor = paramActivity.getSharedPreferences("result", MODE_PRIVATE).edit();
        editor.putInt(str, result);
        editor.commit();
        if (testmode == 0) {
            paramActivity.startActivity(new Intent(paramActivity, cls));
        }else if (testmode == 3){
            paramActivity.startActivity(new Intent(paramActivity, TestReport.class));
        }
        paramActivity.overridePendingTransition(0, 0);
        paramActivity.finish();

    }
}
