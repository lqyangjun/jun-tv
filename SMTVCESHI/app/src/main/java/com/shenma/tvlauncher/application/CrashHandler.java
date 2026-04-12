package com.shenma.tvlauncher.application;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @author joychang
 * @Description UncaughtException处理类, 当程序发生Uncaught异常的时候, 由该类来接管程序, 并记录发送错误报告.
 */
public class CrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private static CrashHandler INSTANCE = new CrashHandler();// CrashHandler实例
    private Thread.UncaughtExceptionHandler mDefaultHandler;// 系统默认的UncaughtException处理类
    private Context mContext;// 程序的Context对象   
    private Map<String, String> info = new HashMap<String, String>();// 用来存储设备信息和异常信息   
    private SimpleDateFormat format = new SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss");// 用于格式化日期,作为日志文件名的一部分

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {

    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();// 获取系统默认的UncaughtException处理器   
        Thread.setDefaultUncaughtExceptionHandler(this);// 设置该CrashHandler为程序的默认处理器   
    }

    /**
     * 当UncaughtException发生时会转入该重写的方法来处理
     */
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果自定义的没有处理则让系统默认的异常处理器来处理   
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);// 如果处理了，让程序继续运行3秒再退出，保证文件保存并上传到服务器   
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 退出程序   
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex 异常信息
     * @return true 如果处理了该异常信息;否则返回false.
     */
    public boolean handleException(Throwable ex) {
        if (ex == null)
            return false;
        new Thread() {
            public void run() {
                Looper.prepare();
                //Toast.makeText(mContext, mContext.getResources().getString(R.string.str_exit_exception), 0).show();  
                Looper.loop();
            }
        }.start();
        // 收集设备参数信息   
        collectDeviceInfo(mContext);
        // 保存日志文件   
        saveCrashInfo2File(ex);
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param context
     */
    public void collectDeviceInfo(Context context) {
        try {
            PackageManager pm = context.getPackageManager();// 获得包管理器   
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_ACTIVITIES);// 得到该应用的信息，即主Activity
            if (pi != null) {
                String versionName = pi.versionName == null ? "null"
                        : pi.versionName;
                String versionCode = pi.versionCode + "";
                info.put("versionName", versionName);
                info.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        Field[] fields = Build.class.getDeclaredFields();// 反射机制   
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                info.put(field.getName(), field.get("").toString());
                Log.d(TAG, field.getName() + ":" + field.get(""));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private String saveCrashInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        final String time = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss").format(new Date());

        for (Map.Entry<String, String> entry : info.entrySet()) {
            String versionName = "unknown";
            long versionCode = 0;
            String fullStackTrace;
            {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                fullStackTrace = sw.toString();
                pw.close();
            }
            sb.append("************* Crash Head ****************\n");
            sb.append("Time Of Crash	  : ").append(time).append("\n");
            sb.append("Device Manufacturer: ").append(Build.MANUFACTURER).append("\n");
            sb.append("Device Model	   : ").append(Build.MODEL).append("\n");
            sb.append("Android Version	: ").append(Build.VERSION.RELEASE).append("\n");
            sb.append("Android SDK		: ").append(Build.VERSION.SDK_INT).append("\n");
            sb.append("App VersionName	: ").append(versionName).append("\n");
            sb.append("App VersionCode	: ").append(versionCode).append("\n");
            sb.append("************* Crash Head ****************\n");
            sb.append("\n").append(fullStackTrace);


        }

        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        Throwable cause = ex.getCause();
        // 循环着把所有的异常信息写入writer中   
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.close();// 记得关闭   
        String result = writer.toString();
        sb.append(result);
        // 保存文件   
        long timetamp = System.currentTimeMillis();
        //在填写文件路径时，一定要写上具体的文件名称（xx.txt），否则会出现拒绝访问。
        File file = new File("crash-" + time + "-" + timetamp + ".log");
        if (!file.exists()) {
            //先得到文件的上级目录，并创建上级目录，在创建文件
            file.getParentFile().mkdir();
            try {
                //创建文件
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File fileName = new File("crash-" + time + "-" + timetamp + ".log");
        if (!fileName.exists()) {
            //先得到文件的上级目录，并创建上级目录，在创建文件
            file.getParentFile().mkdir();
            try {
                //创建文件
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}  

