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
package com.csipsimple.models;

import org.pjsip.pjsua.pj_time_val;
import org.pjsip.pjsua.pjsip_inv_state;
import org.pjsip.pjsua.pjsua;
import org.pjsip.pjsua.pjsuaConstants;
import org.pjsip.pjsua.pjsua_call_info;
import org.pjsip.pjsua.pjsua_call_media_status;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;

import com.csipsimple.R;

public class CallInfo implements Parcelable {

	public int PrimaryKey = -1;
	private int callId;
	private pjsip_inv_state callState;
	private String remoteContact;
	private boolean isIncoming;
	private int confPort = -1;
	private int accId = -1;
	
	public long callStart = 0;
	private pjsua_call_media_status mediaStatus;
	private boolean mediaSecure = false;
	private long connectStart = 0;
	
	@SuppressWarnings("serial")
	public class UnavailableException extends Exception {

		public UnavailableException() {
			super("Unable to find call infos from stack");
		}
	}
	  

	public static final Parcelable.Creator<CallInfo> CREATOR = new Parcelable.Creator<CallInfo>() {
		public CallInfo createFromParcel(Parcel in) {
			return new CallInfo(in);
		}

		public CallInfo[] newArray(int size) {
			return new CallInfo[size];
		}
	};
	//private static final String THIS_FILE = "CallInfo";

	public CallInfo(Parcel in) {
		readFromParcel(in);
	}

	public CallInfo(pjsua_call_info pj_callinfo) {
		fillFromPj(pj_callinfo);
	}

	public CallInfo(int aCallId) throws UnavailableException {
		callId = aCallId;
		updateFromPj();
		
		
	}

	private void fillFromPj(pjsua_call_info pjCallInfo) {
		callId = pjCallInfo.getId();
		callState = pjCallInfo.getState();
		mediaStatus = pjCallInfo.getMedia_status();
		remoteContact = pjCallInfo.getRemote_info().getPtr();
		confPort = pjCallInfo.getConf_slot();
		accId = pjCallInfo.getAcc_id();
		pj_time_val duration = pjCallInfo.getConnect_duration();
		connectStart  = SystemClock.elapsedRealtime () - duration.getSec() * 1000 - duration.getMsec(); 
		
	}
	
	public void updateFromPj() throws UnavailableException {
		pjsua_call_info pj_info = new pjsua_call_info();
		int status = pjsua.call_get_info(callId, pj_info);
		if(status != pjsua.PJ_SUCCESS) {
			//Log.e(THIS_FILE, "Error while getting Call info from stack");
			throw new UnavailableException();
		}
		fillFromPj(pj_info);
		mediaSecure = (pjsua.is_call_secure(callId) == pjsuaConstants.PJ_TRUE);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(PrimaryKey);
		dest.writeInt(callId);
		dest.writeInt(callState.swigValue());
		dest.writeInt(mediaStatus.swigValue());
		dest.writeString(remoteContact);
		dest.writeInt(isIncoming()?1:0);
		dest.writeInt(confPort);
		dest.writeInt(accId);
	}

	public void readFromParcel(Parcel in) {
		PrimaryKey = in.readInt();
		callId = in.readInt();
		callState = pjsip_inv_state.swigToEnum(in.readInt());
		mediaStatus = pjsua_call_media_status.swigToEnum(in.readInt());
		remoteContact = in.readString();
		setIncoming((in.readInt() == 1));
		confPort = in.readInt();
		accId = in.readInt();
	}

	// Getters / Setters
	/**
	 * Get the call id of this call info
	 * @return id of this call
	 */
	public int getCallId() {
		return callId;
	}

	/**
	 * Get the call state of this call info
	 * @return the pjsip invitation state
	 */
	public pjsip_inv_state getCallState() {
		return callState;
	}
	
	public pjsua_call_media_status getMediaStatus() {
		return mediaStatus;
	}

	/**
	 * Get the corresponding string for a given state
	 * Can be used to translate or debug current state
	 * @return the string reprensenting this call info state
	 */
	public String getStringCallState(Context context) {
		String state = "";
		if (callState.equals(pjsip_inv_state.PJSIP_INV_STATE_CALLING)) {
			state = context.getString(R.string.call_state_calling);
		} else if (callState.equals(pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED)) {
			state = context.getString(R.string.call_state_confirmed);
		} else if (callState.equals(pjsip_inv_state.PJSIP_INV_STATE_CONNECTING)) {
			state = context.getString(R.string.call_state_connecting);
		} else if (callState.equals(pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED)) {
			state = context.getString(R.string.call_state_disconnected);
		} else if (callState.equals(pjsip_inv_state.PJSIP_INV_STATE_EARLY)) {
			state = context.getString(R.string.call_state_early);
		} else if (callState.equals(pjsip_inv_state.PJSIP_INV_STATE_INCOMING)) {
			state = context.getString(R.string.call_state_incoming);
		} else if (callState.equals(pjsip_inv_state.PJSIP_INV_STATE_NULL)) {
			state = context.getString(R.string.call_state_null);
		}
		return state;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof CallInfo)) {
			return false;
		}
		CallInfo ci = (CallInfo) o;
		if (ci.getCallId() == callId) {
			return true;
		}
		return false;
	}

	/**
	 * Get the remote contact for this call info
	 * @return string representing the remote contact
	 */
	public String getRemoteContact() {
		return remoteContact;
	}
	

	/**
	 * @param isIncoming the isIncoming to set
	 */
	public void setIncoming(boolean isIncoming) {
		this.isIncoming = isIncoming;
	}

	/**
	 * @return the isIncoming
	 */
	public boolean isIncoming() {
		return isIncoming;
	}
	

	/**
	 * Get duration of the call right now
	 * @return duration in seconds
	 */
	public long getConnectStart() {
		return connectStart;
	}
	
	public String dumpCallInfo() {
		return pjsua.call_dump(callId, pjsua.PJ_TRUE, " ").getPtr();
	}
	
	/**
	 * Check if the specific call info indicate it is an active
	 * call in progress.
	 */
	public boolean isActive() {
		switch (callState) {
		case PJSIP_INV_STATE_INCOMING:
		case PJSIP_INV_STATE_EARLY:
		case PJSIP_INV_STATE_CALLING:
		case PJSIP_INV_STATE_CONFIRMED:
		case PJSIP_INV_STATE_CONNECTING:
			return true;
			
		case PJSIP_INV_STATE_DISCONNECTED:
		case PJSIP_INV_STATE_NULL:
			break;
		}
		return false;
	}

	public void setMediaState(pjsua_call_media_status mediaStatus2) {
		mediaStatus = mediaStatus2;
	}
	
	public int getConfPort() {
		return confPort;
	}
	
	public int getAccId() {
		return accId;
	}
	
	public boolean isSecure() {
		return mediaSecure;
	}
}
