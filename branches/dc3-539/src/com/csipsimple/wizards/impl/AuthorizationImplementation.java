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
package com.csipsimple.wizards.impl;

import java.util.HashMap;

import android.preference.EditTextPreference;

import com.csipsimple.R;
import com.csipsimple.api.SipProfile;

public abstract class AuthorizationImplementation extends BaseImplementation {
	protected EditTextPreference accountDisplayName;
	protected EditTextPreference accountUsername;
	protected EditTextPreference accountAuthorization;
	protected EditTextPreference accountPassword;
	protected EditTextPreference accountServer;
	
	protected static String DISPLAY_NAME = "display_name";
	protected static String USER_NAME = "phone_number";
	protected static String AUTH_NAME = "auth_name";
	protected static String PASSWORD = "password";
	protected static String SERVER = "server";

	private void bindFields() {
		accountDisplayName = (EditTextPreference) parent.findPreference(DISPLAY_NAME);
		accountUsername = (EditTextPreference) parent.findPreference(USER_NAME);
		accountAuthorization = (EditTextPreference) parent.findPreference(AUTH_NAME);
		accountPassword = (EditTextPreference) parent.findPreference(PASSWORD);
		accountServer = (EditTextPreference) parent.findPreference(SERVER);
	}
	
	
	
	public void fillLayout(final SipProfile account) {
		bindFields();
		
		accountDisplayName.setText(account.getDisplayName());
		
		accountUsername.setText(account.getUserName());
		accountServer.setText(account.getSipDomain());
		
		accountPassword.setText(account.data);
		accountAuthorization.setText(account.username);
	}

	public void updateDescriptions() {
		setStringFieldSummary(DISPLAY_NAME);
		setStringFieldSummary(USER_NAME);
		setPasswordFieldSummary(PASSWORD);
		setStringFieldSummary(AUTH_NAME);
		setStringFieldSummary(SERVER);
		
	}
	
	private static HashMap<String, Integer>SUMMARIES = new  HashMap<String, Integer>(){/**
		 * 
		 */
		private static final long serialVersionUID = -5743705263738203615L;

	{
		put(DISPLAY_NAME, R.string.w_common_display_name_desc);
		put(USER_NAME, R.string.w_authorization_phone_number_desc);
		put(AUTH_NAME, R.string.w_authorization_auth_name_desc);
		put(PASSWORD, R.string.w_common_password_desc);
		put(SERVER, R.string.w_common_server_desc);
	}};
	
	@Override
	public String getDefaultFieldSummary(String fieldName) {
		Integer res = SUMMARIES.get(fieldName);
		if(res != null) {
			return parent.getString( res );
		}
		return "";
	}

	public boolean canSave() {
		boolean isValid = true;
		
		isValid &= checkField(accountDisplayName, isEmpty(accountDisplayName));
		isValid &= checkField(accountUsername, isEmpty(accountUsername));
		isValid &= checkField(accountAuthorization, isEmpty(accountAuthorization));
		isValid &= checkField(accountPassword, isEmpty(accountPassword));
		isValid &= checkField(accountServer, isEmpty(accountServer));

		return isValid;
	}

	public SipProfile buildAccount(SipProfile account) {
		account.display_name = accountDisplayName.getText();
		// TODO add an user display name
		account.acc_id = "<sip:" + accountUsername.getText() + "@" + getDomain() + ">";
		
		String regUri = "sip:" + getDomain();
		account.reg_uri = regUri;
		account.proxies = new String[] { regUri } ;

		account.realm = "*";
		account.username = getText(accountAuthorization);
		account.data = getText(accountPassword);
		account.scheme = "Digest";
		account.datatype = SipProfile.CRED_DATA_PLAIN_PASSWD;
		account.reg_timeout = 1800;
		
		return account;
	}

	protected String getDomain() {
		return accountServer.getText();
	}
	
	
	protected abstract String getDefaultName();

	@Override
	public int getBasePreferenceResource() {
		return R.xml.w_authorization_preferences;
	}

	@Override
	public boolean needRestart() {
		return false;
	}

}
