package com.sky.project.share.streaming.api.support;

import java.util.Map;
import java.util.Objects;

import org.apache.spark.streaming.api.java.JavaDStream;

import com.sky.project.share.streaming.api.MessageParser;
import com.sky.project.share.streaming.api.Persistent;
import com.sky.project.share.streaming.api.Processor;
import com.sky.project.share.streaming.api.StreamKafkaContext;

/**
 * AbstractStreamProcess
 * 
 * @author zealot
 */
@SuppressWarnings("serial")
public abstract class AbstractStreamProcess implements Processor<String[]> {

	/** parameters */
	protected Map<String, String> certProtocolMap;
	protected Map<String, String> macHasCompanyMap;
	protected int reduceByKeyNum;
	protected String mysqlConn;
	protected String brokers;
	protected String groupId;
	protected String zkConn;
	protected String streamingType;
	protected Map<String, Object> params;
	protected JavaDStream<String[]> stream;

	public AbstractStreamProcess(StreamKafkaContext<String[]> context) {
		Objects.requireNonNull(context, "context can't be null");
		this.stream = context.getStream();
		Objects.requireNonNull(stream, "stream can't be null");
		initParamsBy(context);
	}

	@Override
	public final void process(MessageParser parser) {
		if (stream != null && parser != null) {
			persist(parser);

			Persistent persistent = parser.getPersistent();

			if (persistent != null) {
				// 分析关系数据并入库
				if (persistent.isPersistRelation()) {
					persistRelation(parser);
				}

				// 分析身份数据并入库
				if (persistent.isPersistCertification()) {
					persistCertification(parser);
				}

				// 分析身份数据轨迹数据并入库
				if (persistent.isPersistCertificationTrack()) {
					persistCertificationTrack(parser);
				}
			}
		}

	}

	/**
	 * 模板持久化方法(持久化相关数据，如持久化原始日志，持久化轨迹数据等)
	 * 
	 * @param stream
	 * @param parser
	 */
	protected abstract void persist(MessageParser parser);

	private void initParamsBy(StreamKafkaContext<String[]> context) {
		// TODO
	}

	/** persist common implements */
	protected final void persistRelation(MessageParser parser) {
		// TODO
	}

	protected final void persistCertification(MessageParser parser) {
		// TODO
	}

	protected final void persistCertificationTrack(MessageParser parser) {
		// TODO
	}

}
