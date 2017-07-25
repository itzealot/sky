package com.sky.project.share.streaming.api;

/**
 * 持久化方式
 * 
 * @author zealot
 */
public interface Persistent {

	boolean isPersistCertification();

	boolean isPersistCertificationTrack();

	boolean isPersistRelation();

	/**
	 * 是否持久化AllTrack
	 * 
	 * @return
	 */
	boolean isPersistAllTrack();

	/**
	 * all_track是否进行合并
	 * 
	 * @return
	 */
	boolean isMergeAllTrack();
}
