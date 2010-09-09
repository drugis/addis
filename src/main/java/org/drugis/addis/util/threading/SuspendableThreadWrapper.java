/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.util.threading;

import java.lang.Thread.State;

import org.drugis.common.threading.Suspendable;

public class SuspendableThreadWrapper {
	private Thread d_thread;
	private final Runnable d_runnable;

	SuspendableThreadWrapper(Runnable runnable) {
		d_runnable = runnable;
		//d_thread = new Thread(runnable);
	}
	
	public synchronized void start() {
		if (d_thread == null) {
			startAsNewThread();
		} else if (d_runnable instanceof Suspendable) {
			resumeThread();
		} else {
			throw new RuntimeException("Thread already running and not suspendable.");
		}
	}
	
	public synchronized boolean suspend() {
		if (d_thread == null) {
			throw new IllegalStateException("Thread not started yet");
		} else if (d_runnable instanceof Suspendable) {
			((Suspendable) d_runnable).suspend();
			return true;
		} else {
			return false;
		}
	}

	public boolean isTerminated() {
		if (d_thread == null)
			return false;
		return d_thread.getState() == State.TERMINATED ;
	}
	
	public boolean terminate() {
		if (d_thread == null)
			return true;
		else if (d_runnable instanceof Suspendable) {
			((Suspendable) d_runnable).terminate();
			return true;
		}
		
		return false;
	}
	
	private void startAsNewThread() {
		d_thread = new Thread(d_runnable);
		d_thread.start();
	}
	
	private void resumeThread() {
		Suspendable susRunnable = (Suspendable) d_runnable;
		if (susRunnable.isThreadSuspended())
			susRunnable.wakeUp();
		else {
			throw new RuntimeException("Thread already running.");
		}
	}
	
	Runnable getRunnable() {
		return d_runnable;
	}
	
	@Override
	public String toString() {
		return d_runnable.toString();
	}
}
