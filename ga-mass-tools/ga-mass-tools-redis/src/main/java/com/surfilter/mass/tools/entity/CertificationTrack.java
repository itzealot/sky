package com.surfilter.mass.tools.entity;

import com.surfilter.mass.tools.conf.Constant;

public class CertificationTrack {

	private String id_no;
	private String id_type;
	private String cert_type;
	private String devicenum;
	private String province_code;
	private String city_code;
	private String area_code;
	private String service_code;
	private String start_time;
	private String times;
	private String source;
	private String company_id;
	private String center_code;
	private String create_time;
	private String sys_source;
	private String create_time_p;

	public CertificationTrack(String id_no, String id_type, String cert_type, String devicenum, String province_code,
			String city_code, String area_code, String service_code, String start_time, String times, String source,
			String company_id, String center_code, String create_time, String sys_source, String create_time_p) {
		super();
		this.id_no = id_no;
		this.id_type = id_type;
		this.cert_type = cert_type;
		this.devicenum = devicenum;
		this.province_code = province_code;
		this.city_code = city_code;
		this.area_code = area_code;
		this.service_code = service_code;
		this.start_time = start_time;
		this.times = times;
		this.source = source;
		this.company_id = company_id;
		this.center_code = center_code;
		this.create_time = create_time;
		this.sys_source = sys_source;
		this.create_time_p = create_time_p;
	}

	public String join() {
		StringBuilder builder = new StringBuilder();

		builder.append(id_no).append(Constant.KAFKA_FILED_SPLITER);
		builder.append(id_type).append(Constant.KAFKA_FILED_SPLITER);
		builder.append(cert_type).append(Constant.KAFKA_FILED_SPLITER);
		builder.append(devicenum).append(Constant.KAFKA_FILED_SPLITER);
		builder.append(province_code).append(Constant.KAFKA_FILED_SPLITER);
		builder.append(city_code).append(Constant.KAFKA_FILED_SPLITER);
		builder.append(area_code).append(Constant.KAFKA_FILED_SPLITER);
		builder.append(service_code).append(Constant.KAFKA_FILED_SPLITER);
		builder.append(start_time).append(Constant.KAFKA_FILED_SPLITER);
		builder.append(times).append(Constant.KAFKA_FILED_SPLITER);
		builder.append(source).append(Constant.KAFKA_FILED_SPLITER);
		builder.append(company_id).append(Constant.KAFKA_FILED_SPLITER);
		builder.append(center_code).append(Constant.KAFKA_FILED_SPLITER);
		builder.append(create_time).append(Constant.KAFKA_FILED_SPLITER);
		builder.append(sys_source);

		return builder.append(Constant.KAFKA_MSG_SPLITER).toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(id_no).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(id_type).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(cert_type).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(devicenum).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(province_code).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(city_code).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(area_code).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(service_code).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(start_time).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(times).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(source).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(company_id).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(center_code).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(create_time).append(Constant.DEFAULT_FILE_WRITE_SPLITER);
		builder.append(sys_source);

		return builder.toString();
	}

	public String getId_no() {
		return id_no;
	}

	public String getId_type() {
		return id_type;
	}

	public String getCert_type() {
		return cert_type;
	}

	public String getDevicenum() {
		return devicenum;
	}

	public String getProvince_code() {
		return province_code;
	}

	public String getCity_code() {
		return city_code;
	}

	public String getArea_code() {
		return area_code;
	}

	public String getService_code() {
		return service_code;
	}

	public String getStart_time() {
		return start_time;
	}

	public String getTimes() {
		return times;
	}

	public String getSource() {
		return source;
	}

	public String getCompany_id() {
		return company_id;
	}

	public String getCenter_code() {
		return center_code;
	}

	public String getCreate_time() {
		return create_time;
	}

	public String getSys_source() {
		return sys_source;
	}

	public String getCreate_time_p() {
		return create_time_p;
	}

}
