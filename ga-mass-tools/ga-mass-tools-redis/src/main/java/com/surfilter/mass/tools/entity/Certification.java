package com.surfilter.mass.tools.entity;

import com.surfilter.mass.tools.conf.Constant;

public class Certification {

	private String id;
	private String idType;
	private String firstStartTime;
	private String firstTerminalNum;
	private String source;
	private String createTime;
	private String sysSource;
	private String companyId;
	private String createTimeP;

	public Certification(String id, String idType, String firstStartTime, String firstTerminalNum, String source,
			String createTime, String sysSource, String companyId, String createTimeP) {
		super();
		this.id = id;
		this.idType = idType;
		this.firstStartTime = firstStartTime;
		this.firstTerminalNum = firstTerminalNum;
		this.source = source;
		this.createTime = createTime;
		this.sysSource = sysSource;
		this.companyId = companyId;
		this.createTimeP = createTimeP;
	}

	public String join() {
		StringBuilder builder = new StringBuilder();

		builder.append(id).append(Constant.KAFKA_FILED_SPLITER);
		builder.append(idType).append(Constant.KAFKA_FILED_SPLITER);
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

		builder.append(id).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(idType).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(firstStartTime).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(firstTerminalNum).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(source).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(createTime).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(sysSource).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(companyId);

		return builder.toString();
	}

	public String getId() {
		return id;
	}

	public String getIdType() {
		return idType;
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
