package com.example.nerozen.casloginprocess;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Nerozen on 2016/6/8.
 */
public abstract class CASBroadcastReceiver extends BroadcastReceiver {

    protected boolean isCASErrorReport(Intent intent) {
        return intent.getAction().equals(CASLoginProxy.CAS_ERROR_BROADCAST_ACTION);
    }
    protected boolean isCASperiodlySucceedReport(Intent intent) {
        return intent.getAction().equals(CASLoginProxy.CAS_SUCCESS_BROADCAST_ACTION);
    }
    protected boolean isGardenInfoTransportedReport(Intent intent) {
        return intent.getAction().equals(CASLoginProxy.CAS_DATA_TRANSITION_HOSTINFO_MAP_BROADCAST_ACTION);
    }
    protected boolean isHostJsessionTranportedReport(Intent intent) {
        return intent.getAction().equals(CASLoginProxy.EXTRA_KEY_DATA_TRANSITION_HOST_SESSION);
    }

    protected int getIntentErrorNewsType(Intent intent) {
        return intent.getIntExtra(CASLoginProxy.INTEXTRA_TAG_NAME_ERROR, -1);
    }
    protected int getIntentSuccessfulNewsType(Intent intent) {
        return intent.getIntExtra(CASLoginProxy.INTEXTRA_TAG_NAME_SUCCESS, -1);
    }

    protected abstract void DoWhenAuthenticateFailed(Context context, Intent intent);
    protected abstract void DoWhenConnectTimeout(Context context, Intent intent);
    protected abstract void DoWhenServerException(Context context, Intent intent);
    protected abstract void DoWhenNetworkOutoforder(Context context, Intent intent);

    protected abstract void DoWhenAuthenticateSucceed(Context context, Intent intent);
    protected abstract void DoWhenGardenInfoArrived(Context context, Intent intent);
    protected abstract void DoWhenHostSessionActivated(Context context, Intent intent);
    protected abstract void DoWhenHostSessionArrived(Context context, Intent intent);

    /**
     * 继承这个类，可以不覆盖这个方法，实现上面的抽象方法，直接调用父类onReceive即可
    * */
    @Override
    public void onReceive(Context context, Intent intent) {
        if(isCASErrorReport(intent)) {
            //登陆过程中 出错的情况
            switch(getIntentErrorNewsType(intent)) {
                case CASLoginProxy.CAS_AUTHENTICATION_FAILED_USER_INFO_INVALID:
                    DoWhenAuthenticateFailed(context, intent);
                    break;
                case CASLoginProxy.CAS_REQUEST_ERROR_CONNECT_TIMEOUT:
                    DoWhenConnectTimeout(context, intent);
                    break;
                case CASLoginProxy.CAS_REQUEST_ERROR_NETWORK_ERROR:
                    DoWhenNetworkOutoforder(context, intent);
                    break;
                case CASLoginProxy.CAS_REQUEST_ERROR_SERVER_EXCEPTION:
                    DoWhenServerException(context, intent);
                    break;
                /*case CASLoginProxy.CAS_HOST_LIST_JSON_EXCEPTION:
                    break;*/
                case -1:default:
                    //出bug了，需要修复
                    break;
            }
            return;
        }
        if(isCASperiodlySucceedReport(intent)) {
            //阶段成功反馈
            switch(getIntentSuccessfulNewsType(intent)) {
                //HostJsession获取到并激活成功，但HostJsession在数据传输的广播中才能获取到
                case CASLoginProxy.CAS_ACTIVATE_HOST_SESSION_SUCCESS:
                    DoWhenHostSessionActivated(context, intent);
                    break;
                //身份认证成功
                case CASLoginProxy.CAS_AUTHENTICATION_SUCCESS:
                    DoWhenAuthenticateSucceed(context, intent);
                    break;
            }
            return;
        }
        if(isGardenInfoTransportedReport(intent)) {
            //数据传输
            //此处注意，如果用户选择了"记住我的选择"，先进行匹配(是否在园区列表里面，没有的话还是要再选一次)
            DoWhenGardenInfoArrived(context, intent);
            return;
        }
        if(isHostJsessionTranportedReport(intent)) {
            //该广播将HostJsessionID传来
            DoWhenHostSessionArrived(context, intent);
            return;
        }
    }
}
