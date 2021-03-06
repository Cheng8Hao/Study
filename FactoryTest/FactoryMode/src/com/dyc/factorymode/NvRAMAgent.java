/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: NvRAMAgent.aidl
 */

package com.dyc.factorymode;

import android.os.IBinder;

public interface NvRAMAgent extends android.os.IInterface
{
    /** Local-side IPC implementation stub class. */
    public static abstract class Stub extends android.os.Binder implements NvRAMAgent
    {
        private static final java.lang.String DESCRIPTOR = "NvRAMAgent";

        /** Construct the stub at attach it to the interface. */
        public Stub()
        {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an NvRAMAgent interface, generating a proxy if needed.
         */
        public static NvRAMAgent asInterface(android.os.IBinder obj)
        {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = (android.os.IInterface) obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof NvRAMAgent))) {
                return ((NvRAMAgent) iin);
            }
            return new NvRAMAgent.Stub.Proxy(obj);
        }

        public android.os.IBinder asBinder()
        {
            return this;
        }

        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply,
                int flags) throws android.os.RemoteException
        {
            switch (code)
            {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_readFile: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0;
                    _arg0 = data.readInt();
                    byte[] _result = this.readFile(_arg0);
                    reply.writeNoException();
                    reply.writeByteArray(_result);
                    return true;
                }
                case TRANSACTION_writeFile: {
                    data.enforceInterface(DESCRIPTOR);
                    int _arg0;
                    _arg0 = data.readInt();
                    byte[] _arg1;
                    _arg1 = data.createByteArray();
                    int _result = this.writeFile(_arg0, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                case TRANSACTION_readFileByName: {
                    data.enforceInterface(DESCRIPTOR);
                    System.out.println("NVRAM TRANSACTION_readFile=" + data.readString());
                    String _arg0;
                    _arg0 = data.readString();
                    byte[] _result = this.readFileByName(_arg0);
                    reply.writeNoException();
                    reply.writeByteArray(_result);
                    return true;
                }
                case TRANSACTION_writeFileByName: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();

                    System.out.println("NVRAM TRANSACTION_writeFile=" + data.readString());
                    byte[] _arg1;
                    _arg1 = data.createByteArray();
                    int _result = this.writeFileByName(_arg0, _arg1);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements NvRAMAgent
        {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote)
            {
                mRemote = remote;
            }

            public android.os.IBinder asBinder()
            {
                return mRemote;
            }

            public java.lang.String getInterfaceDescriptor()
            {
                return DESCRIPTOR;
            }

            public byte[] readFile(int file_lid) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                byte[] _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(file_lid);
                    mRemote.transact(Stub.TRANSACTION_readFile, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.createByteArray();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            public int writeFile(int file_lid, byte[] buff) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(file_lid);
                    _data.writeByteArray(buff);
                    mRemote.transact(Stub.TRANSACTION_writeFile, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readInt();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            public byte[] readFileByName(String filename) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                byte[] _result;

                try
                {
                    _data.writeInterfaceToken(DESCRIPTOR);

                    _data.writeString(filename);
                    mRemote.transact(Stub.TRANSACTION_readFileByName, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.createByteArray();
                } finally
                {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

            public int writeFileByName(String filename, byte[] buff)
                    throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                int _result;

                System.out.println("writeFile" + filename);

                try
                {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(filename);

                    _data.writeByteArray(buff);
                    mRemote.transact(Stub.TRANSACTION_writeFileByName, _data, _reply, 0);
                    _reply.readException();
                    _result = _reply.readInt();
                } finally
                {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }

        }

        static final int TRANSACTION_readFile = (IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_writeFile = (IBinder.FIRST_CALL_TRANSACTION + 1);
        static final int TRANSACTION_readFileByName = (IBinder.FIRST_CALL_TRANSACTION + 2);
        static final int TRANSACTION_writeFileByName = (IBinder.FIRST_CALL_TRANSACTION + 3);

    }

    public byte[] readFile(int file_lid) throws android.os.RemoteException;

    public int writeFile(int file_lid, byte[] buff) throws android.os.RemoteException;

    public byte[] readFileByName(String filename) throws android.os.RemoteException;

    public int writeFileByName(String filename, byte[] buff) throws android.os.RemoteException;
}
