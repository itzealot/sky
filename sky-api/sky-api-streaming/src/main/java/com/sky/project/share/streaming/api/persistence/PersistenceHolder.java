package com.sky.project.share.streaming.api.persistence;

import java.util.HashMap;
import java.util.Map;

import com.sky.project.share.streaming.api.Persistent;

/**
 * PersistenceHolder
 * 
 * @author zealot
 */
public final class PersistenceHolder {

	private static final Map<PersistenceEnum, Persistent> persistences = new HashMap<>();

	static {
		// add persistences
	}

	public static Persistent get(PersistenceEnum persitence) {
		return persistences.get(persitence);
	}

	public static enum PersistenceEnum {
		BASE, ALL_TRACK, ALL_TRACK_MERGE;
	}

	private PersistenceHolder() {
	}
}
