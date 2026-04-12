package com.shenma.tvlauncher.view;
/*检测抓包服务*/

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.shenma.tvlauncher.R;
import com.shenma.tvlauncher.utils.Constant;
import com.shenma.tvlauncher.utils.Md5Encoder;
import com.shenma.tvlauncher.utils.Rc4;
import com.shenma.tvlauncher.utils.SharePreferenceDataUtil;
import com.shenma.tvlauncher.utils.Utils;


public class MyServices extends Service {
    private Thread thread;
    private ServiceThread serviceThread;
    private Handler mediaHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Utils.exit();
                    return;
                case 2:
                    Utils.showToast(MyServices.this, R.string.Turn_soff_sVPN, R.drawable.toast_err);
                    /*重置检测状态*/
                    SharePreferenceDataUtil.setSharedIntData(MyServices.this, "Vpn_check",0);
                    mediaHandler.sendEmptyMessageDelayed(1, 1000);
                case 3:
                    Utils.showToast(MyServices.this, R.string.Turn_soff_sXP, R.drawable.toast_err);
                    /*重置检测状态*/
                    SharePreferenceDataUtil.setSharedIntData(MyServices.this, "Xp_check",0);
                    mediaHandler.sendEmptyMessageDelayed(1, 1000);
                    return;
                case 4:
                    /*签名不正确*/
                    Utils.showToast(MyServices.this, getString(R.string.fail) + "error 107", R.drawable.toast_err);
                    /*重置检测状态*/
                    SharePreferenceDataUtil.setSharedIntData(MyServices.this, "Verifysign_check",0);
                    mediaHandler.sendEmptyMessageDelayed(1, 1000);
                    return;
                case 5:
                    /*名字不正确*/
                    Utils.showToast(MyServices.this, getString(R.string.fail) + "error 108", R.drawable.toast_err);
                    /*重置检测状态*/
                    SharePreferenceDataUtil.setSharedIntData(MyServices.this, "Name_check",0);
                    mediaHandler.sendEmptyMessageDelayed(1, 1000);
                    return;
                default:
                    return;
            }
        }
    };

    private int Vpn_check = SharePreferenceDataUtil.getSharedIntData(MyServices.this, "Vpn_check", 0);
    private int Xp_check = SharePreferenceDataUtil.getSharedIntData(MyServices.this, "Xp_check", 0);
    private int Verifysign_check = SharePreferenceDataUtil.getSharedIntData(MyServices.this, "Verifysign_check", 0);
    private String Verifysign = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, "Verifysign", ""), Constant.d);
    private int Name_check = SharePreferenceDataUtil.getSharedIntData(MyServices.this, "Name_check", 0);
    private String Name = Rc4.decry_RC4(SharePreferenceDataUtil.getSharedStringData(this, "Name", ""), Constant.d);

    @Override
    public void onCreate()
    {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        serviceThread=new ServiceThread();
        thread=new Thread(serviceThread);
        thread.start();
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        serviceThread.flag = false;
        thread.interrupt();
        thread = null;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    @Override
    public boolean onUnbind(Intent intent)
    {
        return super.onUnbind(intent);
    }

    class ServiceThread implements Runnable
    {
        volatile boolean flag = true;
        @Override
        public void run() {
            while (flag)
            {
                try{
                    Thread.sleep(1500);
                } catch (InterruptedException exception) {
                }

                if (Vpn_check == 1){
                    /*防抓包*/
                    if (Utils.isVpnConnected() || Utils.isWifiProxy(MyServices.this)) {
                        mediaHandler.sendEmptyMessage(2);//闪退并警告
                    }
                }

                if (Xp_check == 1){
                    /*防XP环境*/
                    if (Utils.checkXpFormMap() || Utils.isHookByStack()) {
                        mediaHandler.sendEmptyMessage(3);//闪退并警告
                    }
                }

                if (Verifysign_check == 1){
                    /*签名检测*/
                    if (!Verifysign.equals(Utils.getMD5(MyServices.this))){
                        mediaHandler.sendEmptyMessage(4);//闪退并警告
                    }
                }

                if (Name_check == 1){
                    /*名字校验*/
                    if (!Name.equals(Md5Encoder.encode(getString(R.string.app_name)))){
                        mediaHandler.sendEmptyMessage(5);//闪退并警告
                    }
                }
            }
        }
    }
}