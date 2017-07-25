package com.sky.project.share.streaming.analysis;

import java.util.List;
import java.util.Set;

/**
 * CertificationTrackMerger
 * 
 * @author zealot
 */
public interface CertificationTrackMerger {

	/**
	 * 合并身份+协议号+场所编码集合的数据(即合并单个身份在一个场所内的所有轨迹数据)
	 * 
	 * @param certs
	 *            CertificationTrack按时间增序排列
	 * @param seconds
	 *            数据合并允许的最大时间间隔
	 * @return certs 为null或为空则返回空集合
	 */
	Set<CertificationTrackTimeSlice> merge(List<CertificationTrack> certs, long seconds);
}
