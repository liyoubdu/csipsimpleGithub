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
package com.csipsimple.utils;

import java.lang.reflect.Method;

import android.os.HandlerThread;

public class Threading {

	
	private static final String THIS_FILE = "Threading";

	public final static void stopHandlerThread(HandlerThread handlerThread) {
		if(handlerThread == null) {
			//Nothing to do if already null
			return;
		}
		boolean fails = true;
		
		if(Compatibility.isCompatible(5)) {
			try {
				Method method = handlerThread.getClass().getDeclaredMethod("quit");
				method.invoke(handlerThread);
				fails = false;
			} catch (Exception e) {
				Log.d(THIS_FILE, "Something is wrong with api level declared use fallback method");
			}
		}
		if (fails && handlerThread.isAlive()) {
			try {
				//This is needed for android 4 and lower
				handlerThread.join(500);
				/*
				if (handlerThread.isAlive()) {
					handlerThread.
				}
				*/
			} catch (Exception e) {
				Log.e(THIS_FILE, "Can t finish handler thread....", e);
			}
		}
	}
}
