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
package com.csipsimple.utils.contacts;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ArrayAdapter;

import com.csipsimple.R;
import com.csipsimple.utils.Compatibility;

public abstract class ContactsWrapper {
	private static ContactsWrapper instance;
	
	public static ContactsWrapper getInstance() {
		if(instance == null) {
			String className = "com.csipsimple.utils.contacts.ContactsUtils";
			if(Compatibility.isCompatible(5)) {
				className += "5";
			}else {
				className += "3";
			}
			try {
                Class<? extends ContactsWrapper> wrappedClass = Class.forName(className).asSubclass(ContactsWrapper.class);
                instance = wrappedClass.newInstance();
	        } catch (Exception e) {
	        	throw new IllegalStateException(e);
	        }
		}
		
		return instance;
	}
	
	protected ContactsWrapper() {}
	
	public abstract Bitmap getContactPhoto(Context ctxt, Uri uri, Integer defaultResource);
	public abstract ArrayList<Phone> getPhoneNumbers(Context ctxt, String id);
	
	public class Phone {
		private String number;
		private String type;

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Phone(String n, String t) {
			this.number = n;
			this.type = t;
		}
	}
	
	public void treatContactPickerPositiveResult(final Context ctxt, final Intent data, final OnPhoneNumberSelected l) {
		Uri contactUri = data.getData();
        List<String> list = contactUri.getPathSegments();
        String contactId = list.get(list.size() - 1);
        ArrayList<Phone> phones = getPhoneNumbers(ctxt, contactId);
        
        if(phones.size() == 0) {
	        final AlertDialog.Builder builder = new AlertDialog.Builder(ctxt);
        	builder.setPositiveButton(R.string.ok, null);
        	builder.setTitle(R.string.choose_phone);
        	builder.setMessage(R.string.no_phone_found);
        	AlertDialog dialog = builder.create();
        	dialog.show();
        }else if(phones.size() == 1) {
        	if(l != null) {
        		l.onTrigger(phones.get(0).getNumber());
        	}
        }else {
	        final AlertDialog.Builder builder = new AlertDialog.Builder(ctxt);
			
			ArrayList<String> entries = new ArrayList<String>();
			for (Phone phone : phones) {
				entries.add(phone.getNumber());
			}
			
			final ArrayAdapter<String> phoneChoiceAdapter = new ArrayAdapter<String>(ctxt, android.R.layout.simple_dropdown_item_1line, entries );
			
			builder.setTitle(R.string.choose_phone);
	        builder.setAdapter(phoneChoiceAdapter, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(l != null) {
						l.onTrigger(phoneChoiceAdapter.getItem(which));
					}
				}
			});
	        builder.setCancelable(true);
	        builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Nothing to do
					dialog.dismiss();
				}
			});
	        AlertDialog dialog = builder.create();
	        dialog.show();
        }
	}
	
	public interface OnPhoneNumberSelected {
		void onTrigger(String number);
	}
}
