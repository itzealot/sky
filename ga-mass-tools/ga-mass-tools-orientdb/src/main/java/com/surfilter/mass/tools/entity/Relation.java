package com.surfilter.mass.tools.entity;

import java.io.Serializable;

/**
 * Created by wellben on 2016/9/28.
 */
public class Relation implements Serializable {

    private String id_from;
    private String form_type;
    private String id_to;
    private String to_type;
    private String first_start_time;
    private String first_terminal_num;
    private String source;
    private String create_time;
    private String sys_source;
    private String company_id;
    private String found_count;

    public String getId_from() {
        return id_from;
    }

    public void setId_from(String id_from) {
        this.id_from = id_from;
    }

    public String getForm_type() {
        return form_type;
    }

    public void setForm_type(String form_type) {
        this.form_type = form_type;
    }

    public String getId_to() {
        return id_to;
    }

    public void setId_to(String id_to) {
        this.id_to = id_to;
    }

    public String getTo_type() {
        return to_type;
    }

    public void setTo_type(String to_type) {
        this.to_type = to_type;
    }

    public String getFirst_start_time() {
        return first_start_time;
    }

    public void setFirst_start_time(String first_start_time) {
        this.first_start_time = first_start_time;
    }

    public String getFirst_terminal_num() {
        return first_terminal_num;
    }

    public void setFirst_terminal_num(String first_terminal_num) {
        this.first_terminal_num = first_terminal_num;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getSys_source() {
        return sys_source;
    }

    public void setSys_source(String sys_source) {
        this.sys_source = sys_source;
    }

	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public String getFound_count() {
		return found_count;
	}

	public void setFound_count(String found_count) {
		this.found_count = found_count;
	}

	


}
