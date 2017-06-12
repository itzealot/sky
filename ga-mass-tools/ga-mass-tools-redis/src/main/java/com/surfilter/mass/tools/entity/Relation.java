package com.surfilter.mass.tools.entity;

import com.surfilter.mass.tools.conf.Constant;

public class Relation {

	private String idFrom;
	private String fromType;
	private String idTo;
	private String toType;
	private String firstStartTime;
	private String firstTerminalNum;
	private String source;
	private String createTime;
	private String sysSource;
	private String companyId;
	private String createTimeP;

	public Relation(String idFrom, String fromType, String idTo, String toType, String firstStartTime,
			String firstTerminalNum, String source, String createTime, String sysSource, String companyId,
			String createTimeP) {
		this(idFrom, fromType, idTo, toType, firstStartTime, firstTerminalNum, source, createTime, sysSource,
				createTimeP);
		this.companyId = companyId;
	}

	private Relation(String idFrom, String fromType, String idTo, String toType, String firstStartTime,
			String firstTerminalNum, String source, String createTime, String sysSource, String createTimeP) {
		super();
		this.idFrom = idFrom;
		this.fromType = fromType;
		this.idTo = idTo;
		this.toType = toType;
		this.firstStartTime = firstStartTime;
		this.firstTerminalNum = firstTerminalNum;
		this.source = source;
		this.createTime = createTime;
		this.sysSource = sysSource;
		this.createTimeP = createTimeP;
	}

	public String join() {
		StringBuilder builder = new StringBuilder();

		builder.append(idFrom).append(Constant.KAFKA_FILED_SPLITER);
		builder.append(fromType).append(Constant.KAFKA_FILED_SPLITER);
		builder.append(idTo).append(Constant.KAFKA_FILED_SPLITER);
		builder.append(toType).append(Constant.KAFKA_FILED_SPLITER);
		builder.append(firstStartTime).append(Constant.KAFKA_FILED_SPLITER);
		builder.append(firstTerminalNum).append(Constant.KAFKA_FILED_SPLITER);
		builder.append(source).append(Constant.KAFKA_FILED_SPLITER);
		builder.append(sysSource).append(Constant.KAFKA_FILED_SPLITER);
		builder.append(companyId);

		return builder.append(Constant.KAFKA_MSG_SPLITER).toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(idFrom).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(fromType).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(idTo).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(toType).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(firstStartTime).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(firstTerminalNum).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(source).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(createTime).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(sysSource).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(companyId);

		return builder.toString();
	}

	public String getIdFrom() {
		return idFrom;
	}

	public String getFromType() {
		return fromType;
	}

	public String getIdTo() {
		return idTo;
	}

	public String getToType() {
		return toType;
	}

	public String getFirstStartTime() {
		return firstStartTime;
	}

	public String getFirstTerminalNum() {
		return firstTerminalNum;
	}

	public String getSource() {
		return source;
	}

	public String getCreateTime() {
		return createTime;
	}

	public String getSysSource() {
		return sysSource;
	}

	public String getCompanyId() {
		return companyId;
	}

	public String getCreateTimeP() {
		return createTimeP;
	}

}
