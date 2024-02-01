package com.applisto.appcloner.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import com.applisto.appcloner.classes.DefaultProvider;
import com.applisto.appcloner.classes.FileAccessMonitor;
import com.applisto.appcloner.classes.HostsBlocker;
import com.applisto.appcloner.classes.PreferenceEditor;
import com.applisto.appcloner.classes.Utils;
import com.applisto.appcloner.classes.util.Log;
import com.applisto.appcloner.service.IRemoteService;
import java.util.List;
import java.util.Map;

/* loaded from: classes2.dex */
public class RemoteService extends Service {
    public static final int INTERFACE_VERSION = 5;
    private static final String TAG = RemoteService.class.getSimpleName();
    private final IRemoteService.Stub mBinder = new IRemoteService.Stub() { // from class: com.applisto.appcloner.service.RemoteService.1
        private void checkCaller() {
            if (!Utils.checkCaller(RemoteService.this)) {
                throw new SecurityException();
            }
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public int getAppClonerInterfaceVersion() {
            checkCaller();
            return 5;
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public void killAppProcesses() {
            try {
                DefaultProvider.invokeSecondaryInstance("util.Utils", "killAppProcesses", RemoteService.this.getApplicationContext());
            } catch (Throwable th) {
                Log.m21w(RemoteService.TAG, th);
            }
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public String[] getPreferenceFiles() {
            checkCaller();
            return PreferenceEditor.getPreferenceFiles(RemoteService.this);
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public Map getPreferences(String str) {
            checkCaller();
            return PreferenceEditor.getPreferences(RemoteService.this, str);
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public void setPreference(String str, String str2, String str3) {
            checkCaller();
            PreferenceEditor.setPreference(RemoteService.this, str, str2, str3);
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public Map getAllowedBlockedHosts() {
            checkCaller();
            return HostsBlocker.getAllowedBlockedHosts();
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public void setAllowedBlockedHosts(Map map) {
            checkCaller();
            HostsBlocker.setAllowedBlockedHosts(map);
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public Map getFileAccessMonitorEntries(long j) {
            checkCaller();
            return FileAccessMonitor.getFileAccessMonitorEntries(j);
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public List inspectLayout() throws RemoteException {
            checkCaller();
            try {
                return (List) DefaultProvider.invokeSecondaryStatic("LayoutInspector", "inspectLayout", new Object[0]);
            } catch (Throwable th) {
                Log.m21w(RemoteService.TAG, th);
                throw new RemoteException(th.toString());
            }
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public boolean performViewAction(int i, String str, String str2) throws RemoteException {
            try {
                return ((Boolean) DefaultProvider.invokeSecondaryStatic("LayoutInspector", "performViewAction", Integer.valueOf(i), str, str2)).booleanValue();
            } catch (Throwable th) {
                Log.m21w(RemoteService.TAG, th);
                throw new RemoteException(th.toString());
            }
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public void clearAppDataAndExit() {
            try {
                DefaultProvider.invokeSecondaryStatic("util.Utils", "clearAppDataAndExit", RemoteService.this.getApplicationContext());
            } catch (Throwable th) {
                Log.m21w(RemoteService.TAG, th);
            }
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public void clearCache() {
            try {
                DefaultProvider.invokeSecondaryStatic("util.Utils", "clearCache", RemoteService.this.getApplicationContext());
            } catch (Throwable th) {
                Log.m21w(RemoteService.TAG, th);
            }
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public Map getActivityMonitorEntries(long j) throws RemoteException {
            checkCaller();
            try {
                return (Map) DefaultProvider.invokeSecondaryStatic("ActivityMonitor", "getActivityMonitorEntries", Long.valueOf(j));
            } catch (Exception e) {
                throw new RemoteException(e.toString());
            }
        }
    };

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }
}
