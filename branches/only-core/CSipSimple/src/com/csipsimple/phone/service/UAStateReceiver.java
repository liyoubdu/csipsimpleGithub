/**
 * Copyright (C) 2010 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CSipSimple.
 *
 *  CSipSimple is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CSipSimple is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CSipSimple.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.csipsimple.phone.service;

import org.pjsip.pjsua.Callback;
import org.pjsip.pjsua.SWIGTYPE_p_p_pjmedia_port;
import org.pjsip.pjsua.SWIGTYPE_p_pjmedia_session;
import org.pjsip.pjsua.SWIGTYPE_p_pjsip_rx_data;
import org.pjsip.pjsua.pjsip_event;
import org.pjsip.pjsua.pjsip_inv_state;
import org.pjsip.pjsua.pjsua;
import org.pjsip.pjsua.pjsua_call_media_status;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.Settings;

import com.csipsimple.phone.R;
import com.csipsimple.phone.models.CallInfo;
import com.csipsimple.phone.utils.Log;
import com.csipsimple.phone.utils.Ringer;

public class UAStateReceiver extends Callback {
	static String THIS_FILE = "SIP UA Receiver";

	
	private int savedVibrateRing;
	private int savedVibradeNotif;
	private int savedWifiPolicy;
	private int savedVolume;
	private boolean savedSpeakerPhone;
	private boolean savedMicrophoneMute;
	private boolean autoAcceptCurrent = false;

	private NotificationManager notificationManager;
	private SipService service;
	private int savedMode;

	private boolean savedBluetooth;

	private Ringer ringer;
	private boolean isSavedAudioState = false;
	private Object treatCallStateMutex = new Object();


	private Intent regStateChangedIntent;


	private Notification inCallNotification;


	private Intent inCallNotificationIntent;


	private Intent callHandlerIntent;


	private ContentResolver ctntResolver;


	private static AudioManager am;
	


	@Override
	public void on_incoming_call(int acc_id, int callId, SWIGTYPE_p_pjsip_rx_data rdata) {
		Log.d(THIS_FILE, "Has incoming call " + callId);
		final int fCallId = callId; 
		Thread t = new Thread() {
			@Override
			public void run() {
				
				// Automatically answer incoming calls with 180/RINGING
				service.callAnswer(fCallId, 180);
				
				saveAudioState();
				String ringtoneUri = service.getPrefs().getRingtone();
				ringer.setCustomRingtoneUri(Uri.parse(ringtoneUri));
				ringer.ring();
				
				CallInfo incomingCall = new CallInfo(fCallId);
			//	Log.d(THIS_FILE, "Show notif for"+incomingCall.getCallId());
				showNotificationForCall(incomingCall);
				if (autoAcceptCurrent) {
					// Automatically answer incoming calls with 200/OK
					service.callAnswer(fCallId, 200);
					autoAcceptCurrent = false;
				} else {
					launchCallHandler(incomingCall);
				}
			}
		};
		t.start();
	}
	
	
	@Override
	public void on_call_state(final int callId, pjsip_event e) {
		final CallInfo callInfo = new CallInfo(callId);
		Thread t = new Thread() {
			public void run() {
				synchronized (treatCallStateMutex) {
					//Get current infos
					final pjsip_inv_state call_state = callInfo.getCallState();
					Log.i(THIS_FILE, "State of call " + callId + " :: " + callInfo.getStringCallState());
					
					if (call_state.equals(pjsip_inv_state.PJSIP_INV_STATE_INCOMING) || call_state.equals(pjsip_inv_state.PJSIP_INV_STATE_CALLING)) {
						showNotificationForCall(callInfo);
						launchCallHandler(callInfo);
	
					} else if (call_state.equals(pjsip_inv_state.PJSIP_INV_STATE_EARLY)) {
						
					} else {
						// Call is now ended
						if (call_state.equals(pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED)) {
							ringer.stopRing();
							service.stopDialtoneGenerator();
							notificationManager.cancel(SipService.CALL_NOTIF_ID);
							Log.d(THIS_FILE, "Finish call2");
							unsetAudioInCall();
						}
					}
					onBroadcastCallState(callInfo);
				}
			};
		};
		t.start();
		
	}

	
	
	
	@Override
	public void on_reg_state(int accountId) {
		Log.d(THIS_FILE, "New reg state for : " + accountId);
		
		onRegisterState(accountId);
	}

	@Override
	public void on_stream_created(int call_id, SWIGTYPE_p_pjmedia_session sess, long stream_idx, SWIGTYPE_p_p_pjmedia_port p_port) {
		Log.d(THIS_FILE, "Stream created");
		//setAudioInCall();
	}
	
	@Override
	public void on_stream_destroyed(int callId, SWIGTYPE_p_pjmedia_session sess, long streamIdx) {
		Log.d(THIS_FILE, "Stream destroyed");
		//unsetAudioInCall();
	}

	
	
	@Override
	public void on_call_media_state(final int callId) {
		final CallInfo callInfo = new CallInfo(callId);
		Thread t = new Thread() {
			public void run() {
				if (callInfo.pjInfo.getMedia_status() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE) {
		
					// Should maybe done under media thread instead of this one
					//android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
					ringer.stopRing();
					
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// When media is active, connect call to sound device.
					pjsua.conf_connect(callInfo.pjInfo.getConf_slot(), 0);
					pjsua.conf_connect(0, callInfo.pjInfo.getConf_slot());
					
					setAudioInCall();
					
				} else if (callInfo.pjInfo.getMedia_status() == pjsua_call_media_status.PJSUA_CALL_MEDIA_NONE || 
						callInfo.pjInfo.getMedia_status() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ERROR) {
					//
				}else {
					//
				}
//				Log.d(THIS_FILE, "Media state has changed<<<<");
//				Log.d(THIS_FILE, info.getMedia_dir().name());
//				Log.d(THIS_FILE, info.getMedia_status().name());
//				Log.d(THIS_FILE, info.getState_text().getPtr());
//				Log.d(THIS_FILE, info.getState().name());
//				Log.d(THIS_FILE, info.getLast_status_text().getPtr());
//				Log.d(THIS_FILE, info.getLast_status().name());
//				Log.d(THIS_FILE, info.getRemote_info().getPtr());
//				Log.d(THIS_FILE, info.getLocal_info().getPtr());
//				Log.d(THIS_FILE, "------------------------");
			}
		};
		t.start();
	}
	
	
	




	// -------
	// Public configuration for receiver
	// -------
	public void setAutoAnswerNext(boolean auto_response) {
		autoAcceptCurrent = auto_response;
	}

	public void initService(SipService srv) {
		service = srv;
		notificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
		ringer = new Ringer(service);
		
		//About registration notification
		regStateChangedIntent = new Intent(SipService.ACTION_SIP_REGISTRATION_CHANGED);
		
		//About inCall notification
		int icon = R.drawable.ic_incall_ongoing;
		CharSequence tickerText =  service.getText(R.string.ongoing_call);
		long when = System.currentTimeMillis();
		inCallNotification = new Notification(icon, tickerText, when);
		inCallNotificationIntent = new Intent(SipService.ACTION_SIP_CALL_UI);
		inCallNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		inCallNotification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
		
		
		
		//About audio management
		am = (AudioManager) service.getSystemService(Context.AUDIO_SERVICE);
		ctntResolver = service.getContentResolver();
	}

	
	
	// --------
	// Private methods
	// --------
	/**
	 * Register state for an account
	 * 
	 * @param info
	 */
	private void onRegisterState(final int accountId) {
		//This has to be threaded to avoid deadlocks
		Thread t = new Thread() {
			@Override
			public void run() {
				
				// Update sip service (for notifications
				((SipService) service).updateRegistrationsState();
				
				// Send a broadcast message that for an account
				// registration state has changed
				
				service.sendBroadcast(regStateChangedIntent);
			}
		};
		t.start();
	}

	private void onBroadcastCallState(final CallInfo callInfo) {	
		Intent callStateChangedIntent = new Intent(SipService.ACTION_SIP_CALL_CHANGED);
		callStateChangedIntent.putExtra("call_info", callInfo);
		service.sendBroadcast(callStateChangedIntent);	
	}

	
	
	private void showNotificationForCall(CallInfo callInfo) {
		inCallNotificationIntent.putExtra("call_info", callInfo);
		PendingIntent contentIntent = PendingIntent.getActivity(service, 0, inCallNotificationIntent, 0);
		inCallNotification.setLatestEventInfo(service.getApplicationContext(), service.getText(R.string.ongoing_call), 
				callInfo.getRemoteContact(), contentIntent);
		notificationManager.notify(SipService.CALL_NOTIF_ID, inCallNotification);
	}
	
	
	
	/**
	 * 
	 * @param callInfo
	 */
	private void launchCallHandler(CallInfo callInfo) {
		//About in inCallActivity 
		
		callHandlerIntent = new Intent(SipService.ACTION_SIP_CALL_UI); //new Intent(service, getInCallClass());
		// Launch activity to choose what to do with this call
		callHandlerIntent.putExtra("call_info", callInfo);
		callHandlerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Log.i(THIS_FILE, "Anounce call activity please");
		service.startActivity(callHandlerIntent);

	}

	
	private void saveAudioState() {
		savedVibrateRing = am.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);
		savedVibradeNotif = am.getVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION);
		savedWifiPolicy = android.provider.Settings.System.getInt(ctntResolver, android.provider.Settings.System.WIFI_SLEEP_POLICY, Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
		savedVolume = am.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
		savedSpeakerPhone = am.isSpeakerphoneOn();
		savedMicrophoneMute = am.isMicrophoneMute();
	//	savedBluetooth = am.isBluetoothA2dpOn();
		savedMode = am.getMode();
		
		isSavedAudioState = true;
	}
	
	
	/**
	 * Set the audio mode as in call
	 */
	private void setAudioInCall() {
		
		if(!isSavedAudioState) {
			saveAudioState();
		}
		
		am.setStreamSolo(AudioManager.STREAM_VOICE_CALL, true);

		// Settings.System.putInt(ctntResolver,
		// Settings.System.WIFI_SLEEP_POLICY,
		// Settings.System.WIFI_SLEEP_POLICY_NEVER);
		am.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
		am.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);
		//For android 1.5
		//TODO : save / restore it
		am.setRouting(AudioManager.MODE_NORMAL,
					   AudioManager.ROUTE_EARPIECE,
					   AudioManager.ROUTE_ALL);

		am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, am.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)*2/3, 0);
		am.setSpeakerphoneOn(false);
		am.setMicrophoneMute(false);
	
		setAudioMode(service,  AudioManager.MODE_IN_CALL);
	}

	/**
	 * Reset the audio mode
	 */
	private void unsetAudioInCall() {
		
		if(!isSavedAudioState) {
			return;
		}
		
		Settings.System.putInt(ctntResolver, Settings.System.WIFI_SLEEP_POLICY, savedWifiPolicy);
		am.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, savedVibrateRing);
		am.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, savedVibradeNotif);
		am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, savedVolume, 0);
		am.setSpeakerphoneOn(savedSpeakerPhone);
		am.setMicrophoneMute(savedMicrophoneMute);
	//	am.setBluetoothA2dpOn(savedBluetooth);
		am.setMode(savedMode);
		
		am.setStreamSolo(AudioManager.STREAM_VOICE_CALL, false);
		
		isSavedAudioState = false;
	}

	
	public static void setAudioMode(Context context, int mode) {
        am.setMode(mode);
	}
}
