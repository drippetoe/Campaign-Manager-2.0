package com.valutext.service;

import java.util.List;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.valutext.api.ValuTextListener;
import com.valutext.api.ValuTextServiceWrapper;

public class ValuTextServiceConnection implements ServiceConnection {
	private static final String TAG = ValuTextService.class.getSimpleName();
	private ValuTextServiceWrapper api;
	private List<ValuTextListener.Stub> listeners;

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		api = ValuTextServiceWrapper.Stub.asInterface(service);
		try {
			if (listeners != null && listeners.size() > 0) {
				for (ValuTextListener.Stub listener : listeners) {
					api.addListener(listener);
				}
			}
		} catch (RemoteException e) {
			Log.e(TAG, "Failed to add listener", e);
			return;
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		try {
			if (listeners != null && listeners.size() > 0) {
				for (ValuTextListener.Stub listener : listeners) {
					api.removeListener(listener);
				}
			} else {
			}
		} catch (Throwable t) {
			Log.w(TAG, "Failed to unbind the service", t);
		}

	}
	
	public void stopGPS() {
	    try {
            api.stopGPS();
        } catch (RemoteException e) {
            Log.e(TAG, "Problem stopping GPS", e);
        }
	    
	}
	
	public void startGPS() {
	    try {
	        api.startGPS();
	    } catch (RemoteException e) {
	        Log.e(TAG, "Problem starting GPS", e);
	    }
	}

	/**
	 * @param listeners
	 */
	public ValuTextServiceConnection(List<ValuTextListener.Stub> listeners) {
		super();
		this.listeners = listeners;
	}

}
