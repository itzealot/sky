package com.sky.project.share.streaming.api.persistence;

public class AllTrackMergePersistence extends BasePersistence {

	@Override
	public boolean isPersistAllTrack() {
		return true;
	}

	@Override
	public boolean isMergeAllTrack() {
		return true;
	}
}
