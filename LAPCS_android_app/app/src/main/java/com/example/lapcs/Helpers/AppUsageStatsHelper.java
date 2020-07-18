package com.example.lapcs.Helpers;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.ArrayMap;
import android.util.Log;

import com.example.lapcs.AppConsts;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AppUsageStatsHelper {

    private UsageStatsManager mUsageStatsManager;
    private PackageManager mPm;
    private Context mContext;

    private final ArrayMap<String, String> mAppLabelMap = new ArrayMap<>();
    private final ArrayList<UsageStats> mPackageStats = new ArrayList<>();
        public AppUsageStatsHelper(Context context) {

        mContext = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mUsageStatsManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);

            mPm = mContext.getPackageManager();

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -1);  //-1 means Collect statistics of 1 day

//            // Get the app statistics since one year ago from the current time.
//            Calendar cal = Calendar.getInstance();
//            cal.add(Calendar.YEAR, -1);

            final List<UsageStats> stats =
                    mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                            cal.getTimeInMillis(), System.currentTimeMillis());

            if (stats == null) {
                return;
            }
            else
            {
                ArrayMap<String, UsageStats> map = new ArrayMap<>();
                final int statCount = stats.size();
                for (int i = 0; i < statCount; i++) {
                    final UsageStats pkgStats = stats.get(i);

                    // load application labels for each application
                    try {

                        ApplicationInfo appInfo = mPm.getApplicationInfo(pkgStats.getPackageName(), 0);
                        if(isUserApp(appInfo))
                        {
                            String label = appInfo.loadLabel(mPm).toString();
                            mAppLabelMap.put(pkgStats.getPackageName(), label);
                            UsageStats existingStats = map.get(pkgStats.getPackageName());
                            if (existingStats == null) {
                                map.put(pkgStats.getPackageName(), pkgStats);
                            } else {
                                existingStats.add(pkgStats);
                            }
                        }

                    } catch (PackageManager.NameNotFoundException e) {
                        // This package may be gone.
                    }
                }
                mPackageStats.addAll(map.values());
            }
        }

    }


    public String prepareAppUsageStatsStringForDB()
    {
        String AppUsageStatsString="";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
        {
            if(mPackageStats.size()>0)
            {
                for (int i =0;i<mPackageStats.size();i++)
                {
                    UsageStats pkgStats = mPackageStats.get(i);
                    if (pkgStats != null) {
                        String label = mAppLabelMap.get(pkgStats.getPackageName());

                        CharSequence lastTimeUsed = DateUtils.formatSameDayTime(pkgStats.getLastTimeUsed(),
                                System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM);

                        String usageTime = DateUtils.formatElapsedTime(pkgStats.getTotalTimeInForeground() / 1000);

                        long TimeInforground = 0;
                        TimeInforground=pkgStats.getTotalTimeInForeground();
                        long hours = TimeUnit.MILLISECONDS.toHours(TimeInforground);
                        long minutes = TimeUnit.MILLISECONDS.toMinutes(TimeInforground) % TimeUnit.HOURS.toMinutes(1);
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(TimeInforground) % TimeUnit.MINUTES.toSeconds(1);

                        String minutesStr = String.format("%d", minutes);
                        String secondsStr = String.format("%d", seconds);
                        String hoursStr = String.format("%d", hours);
                        String usageTimeStr = hoursStr+"h  "+minutesStr+"m  "+secondsStr+"s";



                        Log.d(AppConsts.TAG,"label=>"+label+"   getLastTimeUsed=>"+pkgStats.getLastTimeUsed()+"    getTotalTimeInForeground=>"+pkgStats.getTotalTimeInForeground());
                        if(!usageTime.equals("00:00"))
                        {
                            AppUsageStatsString += "label: " + label + "||" + "lastTimeUsed: " + lastTimeUsed + "||" + "usageTime: " +usageTimeStr+ "//";
                        }
                        Log.d(AppConsts.TAG,AppUsageStatsString);

                    } else {
                        Log.d(AppConsts.TAG, "No usage stats info for package:" + i);
                    }
                }
            }
            else
            {
                AppUsageStatsString += "label:NIL"  + "," + "lastTimeUsed:NIL "  + "," + "usageTime:NIL" + "//";
                Log.d(AppConsts.TAG, "No  Package Stats");
            }
        }
        return AppUsageStatsString;
    }

//    public String prepareAppUsageStatsStringForDB()
//    {
//        String AppUsageStatsString="";
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
//        {
//            if(mPackageStats.size()>0)
//            {
//                for (int i =0;i<mPackageStats.size();i++)
//                {
//                    UsageStats pkgStats = mPackageStats.get(i);
//                    if (pkgStats != null) {
//                        String label = mAppLabelMap.get(pkgStats.getPackageName());
//                        CharSequence lastTimeUsed = DateUtils.formatSameDayTime(pkgStats.getLastTimeUsed(),
//                                System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.MEDIUM);
//                        String usageTime = DateUtils.formatElapsedTime(pkgStats.getTotalTimeInForeground() / 1000);
//
//                        Log.d(AppConsts.TAG,"label=>"+label+"   getLastTimeUsed=>"+pkgStats.getLastTimeUsed()+"    getTotalTimeInForeground=>"+pkgStats.getTotalTimeInForeground());
//                        if(!usageTime.equals("00:00"))
//                        {
//                            AppUsageStatsString += "label: " + label + "||" + "lastTimeUsed: " +lastTimeUsed + "||" + "usageTime: " +usageTime+ "//";
//                        }
//                        Log.d(AppConsts.TAG,AppUsageStatsString);
//
//                    } else {
//                        Log.d(AppConsts.TAG, "No usage stats info for package:" + i);
//                    }
//                }
//            }
//            else
//            {
//                AppUsageStatsString += "label:NIL"  + "," + "lastTimeUsed:NIL "  + "," + "usageTime:NIL" + "//";
//                Log.d(AppConsts.TAG, "No  Package Stats");
//            }
//        }
//        return AppUsageStatsString;
//    }

    public boolean isUserApp(ApplicationInfo ai) {
        int mask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
        return (ai.flags & mask) == 0;
    }


}
