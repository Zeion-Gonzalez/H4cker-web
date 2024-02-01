package com.applisto.appcloner.service;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.util.List;
import java.util.Map;

/* loaded from: classes2.dex */
public interface IRemoteService extends IInterface {

    /* loaded from: classes2.dex */
    public static class Default implements IRemoteService {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public void clearAppDataAndExit() throws RemoteException {
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public void clearCache() throws RemoteException {
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public Map getActivityMonitorEntries(long j) throws RemoteException {
            return null;
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public Map getAllowedBlockedHosts() throws RemoteException {
            return null;
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public int getAppClonerInterfaceVersion() throws RemoteException {
            return 0;
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public Map getFileAccessMonitorEntries(long j) throws RemoteException {
            return null;
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public String[] getPreferenceFiles() throws RemoteException {
            return null;
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public Map getPreferences(String str) throws RemoteException {
            return null;
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public List inspectLayout() throws RemoteException {
            return null;
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public void killAppProcesses() throws RemoteException {
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public boolean performViewAction(int i, String str, String str2) throws RemoteException {
            return false;
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public void setAllowedBlockedHosts(Map map) throws RemoteException {
        }

        @Override // com.applisto.appcloner.service.IRemoteService
        public void setPreference(String str, String str2, String str3) throws RemoteException {
        }
    }

    void clearAppDataAndExit() throws RemoteException;

    void clearCache() throws RemoteException;

    Map getActivityMonitorEntries(long j) throws RemoteException;

    Map getAllowedBlockedHosts() throws RemoteException;

    int getAppClonerInterfaceVersion() throws RemoteException;

    Map getFileAccessMonitorEntries(long j) throws RemoteException;

    String[] getPreferenceFiles() throws RemoteException;

    Map getPreferences(String str) throws RemoteException;

    List inspectLayout() throws RemoteException;

    void killAppProcesses() throws RemoteException;

    boolean performViewAction(int i, String str, String str2) throws RemoteException;

    void setAllowedBlockedHosts(Map map) throws RemoteException;

    void setPreference(String str, String str2, String str3) throws RemoteException;

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRemoteService {
        private static final String DESCRIPTOR = "com.applisto.appcloner.service.IRemoteService";
        static final int TRANSACTION_clearAppDataAndExit = 11;
        static final int TRANSACTION_clearCache = 12;
        static final int TRANSACTION_getActivityMonitorEntries = 13;
        static final int TRANSACTION_getAllowedBlockedHosts = 6;
        static final int TRANSACTION_getAppClonerInterfaceVersion = 1;
        static final int TRANSACTION_getFileAccessMonitorEntries = 8;
        static final int TRANSACTION_getPreferenceFiles = 3;
        static final int TRANSACTION_getPreferences = 4;
        static final int TRANSACTION_inspectLayout = 9;
        static final int TRANSACTION_killAppProcesses = 2;
        static final int TRANSACTION_performViewAction = 10;
        static final int TRANSACTION_setAllowedBlockedHosts = 7;
        static final int TRANSACTION_setPreference = 5;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IRemoteService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (queryLocalInterface != null && (queryLocalInterface instanceof IRemoteService)) {
                return (IRemoteService) queryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i == 1598968902) {
                parcel2.writeString(DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    parcel.enforceInterface(DESCRIPTOR);
                    int appClonerInterfaceVersion = getAppClonerInterfaceVersion();
                    parcel2.writeNoException();
                    parcel2.writeInt(appClonerInterfaceVersion);
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    killAppProcesses();
                    parcel2.writeNoException();
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    String[] preferenceFiles = getPreferenceFiles();
                    parcel2.writeNoException();
                    parcel2.writeStringArray(preferenceFiles);
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    Map preferences = getPreferences(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeMap(preferences);
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    setPreference(parcel.readString(), parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case 6:
                    parcel.enforceInterface(DESCRIPTOR);
                    Map allowedBlockedHosts = getAllowedBlockedHosts();
                    parcel2.writeNoException();
                    parcel2.writeMap(allowedBlockedHosts);
                    return true;
                case 7:
                    parcel.enforceInterface(DESCRIPTOR);
                    setAllowedBlockedHosts(parcel.readHashMap(getClass().getClassLoader()));
                    parcel2.writeNoException();
                    return true;
                case 8:
                    parcel.enforceInterface(DESCRIPTOR);
                    Map fileAccessMonitorEntries = getFileAccessMonitorEntries(parcel.readLong());
                    parcel2.writeNoException();
                    parcel2.writeMap(fileAccessMonitorEntries);
                    return true;
                case 9:
                    parcel.enforceInterface(DESCRIPTOR);
                    List inspectLayout = inspectLayout();
                    parcel2.writeNoException();
                    parcel2.writeList(inspectLayout);
                    return true;
                case 10:
                    parcel.enforceInterface(DESCRIPTOR);
                    boolean performViewAction = performViewAction(parcel.readInt(), parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(performViewAction ? 1 : 0);
                    return true;
                case 11:
                    parcel.enforceInterface(DESCRIPTOR);
                    clearAppDataAndExit();
                    parcel2.writeNoException();
                    return true;
                case 12:
                    parcel.enforceInterface(DESCRIPTOR);
                    clearCache();
                    parcel2.writeNoException();
                    return true;
                case 13:
                    parcel.enforceInterface(DESCRIPTOR);
                    Map activityMonitorEntries = getActivityMonitorEntries(parcel.readLong());
                    parcel2.writeNoException();
                    parcel2.writeMap(activityMonitorEntries);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IRemoteService {
            public static IRemoteService sDefaultImpl;
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.applisto.appcloner.service.IRemoteService
            public int getAppClonerInterfaceVersion() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(1, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getAppClonerInterfaceVersion();
                    }
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.applisto.appcloner.service.IRemoteService
            public void killAppProcesses() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(2, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().killAppProcesses();
                    } else {
                        obtain2.readException();
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.applisto.appcloner.service.IRemoteService
            public String[] getPreferenceFiles() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(3, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getPreferenceFiles();
                    }
                    obtain2.readException();
                    return obtain2.createStringArray();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.applisto.appcloner.service.IRemoteService
            public Map getPreferences(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (!this.mRemote.transact(4, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getPreferences(str);
                    }
                    obtain2.readException();
                    return obtain2.readHashMap(getClass().getClassLoader());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.applisto.appcloner.service.IRemoteService
            public void setPreference(String str, String str2, String str3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    if (!this.mRemote.transact(5, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().setPreference(str, str2, str3);
                    } else {
                        obtain2.readException();
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.applisto.appcloner.service.IRemoteService
            public Map getAllowedBlockedHosts() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(6, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getAllowedBlockedHosts();
                    }
                    obtain2.readException();
                    return obtain2.readHashMap(getClass().getClassLoader());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.applisto.appcloner.service.IRemoteService
            public void setAllowedBlockedHosts(Map map) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeMap(map);
                    if (!this.mRemote.transact(7, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().setAllowedBlockedHosts(map);
                    } else {
                        obtain2.readException();
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.applisto.appcloner.service.IRemoteService
            public Map getFileAccessMonitorEntries(long j) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeLong(j);
                    if (!this.mRemote.transact(8, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getFileAccessMonitorEntries(j);
                    }
                    obtain2.readException();
                    return obtain2.readHashMap(getClass().getClassLoader());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.applisto.appcloner.service.IRemoteService
            public List inspectLayout() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(9, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().inspectLayout();
                    }
                    obtain2.readException();
                    return obtain2.readArrayList(getClass().getClassLoader());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.applisto.appcloner.service.IRemoteService
            public boolean performViewAction(int i, String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeInt(i);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    if (!this.mRemote.transact(10, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().performViewAction(i, str, str2);
                    }
                    obtain2.readException();
                    return obtain2.readInt() != 0;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.applisto.appcloner.service.IRemoteService
            public void clearAppDataAndExit() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(11, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().clearAppDataAndExit();
                    } else {
                        obtain2.readException();
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.applisto.appcloner.service.IRemoteService
            public void clearCache() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (!this.mRemote.transact(12, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().clearCache();
                    } else {
                        obtain2.readException();
                    }
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.applisto.appcloner.service.IRemoteService
            public Map getActivityMonitorEntries(long j) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeLong(j);
                    if (!this.mRemote.transact(13, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getActivityMonitorEntries(j);
                    }
                    obtain2.readException();
                    return obtain2.readHashMap(getClass().getClassLoader());
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IRemoteService iRemoteService) {
            if (Proxy.sDefaultImpl != null || iRemoteService == null) {
                return false;
            }
            Proxy.sDefaultImpl = iRemoteService;
            return true;
        }

        public static IRemoteService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
