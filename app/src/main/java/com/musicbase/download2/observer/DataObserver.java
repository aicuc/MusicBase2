package com.musicbase.download2.observer;


import com.musicbase.download2.entity.DocInfo;

public abstract class DataObserver {

	public void onChanged(DocInfo info) {
		// Do nothing
	}

	public void onInvalidated(DocInfo info) {
		// Do nothing
	}
}
