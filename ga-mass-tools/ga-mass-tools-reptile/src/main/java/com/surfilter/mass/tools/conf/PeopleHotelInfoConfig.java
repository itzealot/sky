package com.surfilter.mass.tools.conf;

import static com.surfilter.mass.tools.util.ValidateUtil.validate;
import static com.surfilter.mass.tools.util.ValidateUtil.validateProperty;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.surfilter.mass.tools.util.Dates;

/**
 * People hotel rent info config
 * 
 * @author zealot
 *
 */
public class PeopleHotelInfoConfig {

	static final Logger LOG = LoggerFactory.getLogger(PeopleHotelInfoConfig.class);

	/** hotel property */
	public static final String HOTEL_CATCH_URL = "hotel.catch.url";
	public static final String HOTEL_DATE_BETWEEN = "hotel.date.between";
	public static final String HOTEL_DAY_MAX_COUNTS = "hotel.day.max.counts";
	public static final String HOTEL_RESULTS_DIR = "hotel.results.dir";
	public static final String HOTEL_QUERY_PAGE_SIZE = "hotel.query.pageSize";
	public static final String HOTEL_QUERY_RECORD_START = "hotel.query.recordStart";

	public static final int HOTEL_DAY_MAX_COUNTS_DEFAULT = 25500;
	public static final int HOTEL_QUERY_PAGE_SIZE_DEFAULT = 10;
	public static final int HOTEL_QUERY_RECORD_START_DEFAULT = 1;

	private String catchUrl;
	private Date startDate;
	private Date endDate;
	private int dayMaxCounts;
	private String resultsDir;
	private int pageSize;
	private int recordStart;

	public PeopleHotelInfoConfig(MassConfiguration conf) {
		init(conf);
	}

	private void init(MassConfiguration conf) {
		this.catchUrl = validateProperty(conf.get(HOTEL_CATCH_URL), HOTEL_CATCH_URL);
		String[] dateArrays = validateProperty(conf.get(HOTEL_DATE_BETWEEN), HOTEL_DATE_BETWEEN).split(":");
		this.startDate = validate(Dates.str2Year(dateArrays[0]), HOTEL_DATE_BETWEEN);

		if (dateArrays.length >= 2) {
			this.endDate = validate(Dates.str2Year(dateArrays[1]), HOTEL_DATE_BETWEEN);
		} else {
			this.endDate = new Date();
		}

		this.resultsDir = validateProperty(conf.get(HOTEL_RESULTS_DIR), HOTEL_RESULTS_DIR);
		this.dayMaxCounts = conf.getInt(HOTEL_DAY_MAX_COUNTS, HOTEL_DAY_MAX_COUNTS_DEFAULT);
		this.pageSize = conf.getInt(HOTEL_QUERY_PAGE_SIZE, HOTEL_QUERY_PAGE_SIZE_DEFAULT);
		this.recordStart = conf.getInt(HOTEL_QUERY_RECORD_START, HOTEL_QUERY_RECORD_START_DEFAULT);

		LOG.debug("settings for app:{}", this);
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public int getDayMaxCounts() {
		return dayMaxCounts;
	}

	public String getResultsDir() {
		return resultsDir;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getRecordStart() {
		return recordStart;
	}

	public String getCatchUrl() {
		return catchUrl;
	}

	@Override
	public String toString() {
		return "PeopleHotelInfoConfig [startDate=" + startDate + ",catchUrl=" + catchUrl + ",endDate=" + endDate
				+ ",dayMaxCounts=" + dayMaxCounts + ",resultsDir=" + resultsDir + ",pageSize=" + pageSize
				+ ",recordStart=" + recordStart + "]";
	}

}
