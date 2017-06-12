package com.surfilter.mass.tools.utils;

/**
 * 手机号码准确性校验.
 * Created by wellben on 2016/10/24.
 */
public class DataValid {

    private static final String PHONE_REG = "^1[34578]\\d{9}";
    public static final String IP_REGEXP = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
	public static final String MAC_REGEXP = "([0-9A-F]{2}-){5}[0-9A-F]{2}";
	public static final String MAC_REGEXP_EQ = "^[A-F0-9]{1}[048C]{1}(-[A-F0-9]{2}){5}$";
	public static final String MOBILE_REGEXP = "^(0|86|17951|086|0086|12593||\\+86)?(13[0-9]|15[0123456789]|17[0-9]|18[0-9]|14[57])[0-9]{8}$";
	public static final String NUMBER_REGEXP = "^\\d+$";
	public static final String FLOAT_REGEXP = "^[+-]?(\\d*\\.)?\\d+$";//"^[+-]?([0-9]*\\.?[0-9]+|[0-9]+\\.?[0-9]*)([eE][+-]?[0-9]+)?$";//"^(\\d*\\.)?\\d+$";
	public static final String LONGITUDE_REGEXP = "^[-]?(\\d|([1-9]\\d)|(1[0-7]\\d)|(180))(\\.\\d*)?$";
	public static final String LATITUDE_REGEXP = "^[-]?(\\d|([1-8]\\d)|(90))(\\.\\d*)?$";
	public static final String INTERER_REGEXP = "^-?[0-9]\\d*$";
	public static final String IMSI_REGEXP = "^460[0-2]([0-7]|9)\\d{10}$";
	public static final String IMEI_REGEXP = "^(\\d{15}|\\d{17})$";
	public static final String CERTIFICATE15_REGEXP = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
	public static final String CERTIFICATE18_REGEXP = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$";
	public static final String CERTIFICATE_REGEXP = "^\\d{6}(18|19|20)?\\d{2}(0[1-9]|1[12])(0[1-9]|[12]\\d|3[01])\\d{3}(\\d|X)$/i";
	public static final String NUMBER_LETTER_REGEXP = "^[0-9a-zA-Z]+$";
	//public static final String TELEPHONE_REGEXP = "^(0|86|17951|086|0086|12593||\\+86)?(13[0-9]|15[0123456789]|17[0-9]|18[0-9]|14[57])[0-9]{8}$|0\\d{2,3}-\\d{7,8}|0\\d{2,3}\\d{7,8}";
	//public static final String TELE_REGEXP = "([0]?[0-9]{2,3}-?)?[1-9]{7,8}";
	public static final String IRREGULAR_REGEXP = "^(^.*[(/)|(\\\\)|(:)|(\\*)|(\\?)|(\")|(<)|(>)|(+)|(,)|(，)].*$)";
	public static final String SERVICECODE_REGEXP = "^(\\d{6}[123]\\d{7}|\\d{6}[123][a-zA-Z]\\d{6})|(00000000000000)|(00000000000001)";

    public static String validPhone(String str) {
        try {
            if(str != null && !str.trim().equals("")) {
                str = str.trim().replaceAll(" ", "").replaceAll("[,|;|\\-|\\=|\\.|\\?|\\*|\\+|\\(|\\)|（|）|\\']*]", "");
                int strlen = str.length();
                if(strlen < 11) {
                    return "";
                }

                try {
                    long l = Long.parseLong(str);
                    if (l < 13000000000l) {
                        return "";
                    }
                } catch (NumberFormatException nfe) {
                    return "";
                }

                String str1 = "";
                String str2 = "";

                if(strlen > 11) {
                    str1 = str.substring(strlen - 11, strlen);
                    str2 = str.substring(0, 11);
                }

                if(strlen == 11 && str.matches(PHONE_REG)) {
                    return str;
                } else if(!str1.equals("") && str1.matches(PHONE_REG)) {
                    return str1;
                } else if(!str2.equals("") && str2.matches(PHONE_REG)) {
                    return str2;
                }
            }
        } catch (Exception ignore) {
        }
        return "";
    }
    
    //mac校验
	public static String validMac(String mac, int flag) {
		try {

			if (mac != null && !mac.trim().equals("")) {
				if (mac.length() != 17) {
					return "";
				}

				// flag用于区分是围栏还是非围栏，围栏是1，非围栏是0，对于围栏的，只有048C的才是合法的MAC
				if (flag == 1) {
					if (mac.matches(MAC_REGEXP_EQ)) {
						return mac;
					}
				} else {
					if (mac.matches(MAC_REGEXP)) {
						return mac;
					}
				}

			}
		} catch (Exception ignore) {

		}

		return "";
	}

	
	// imei校验
	public static String validImei(String imei) {
		try {
			if (imei != null && !imei.trim().equals("")) {

				if (imei.matches(IMEI_REGEXP)) {
					return imei;
				}

			}
		} catch (Exception ignore) {

		}

		return "";
	}
	
	// imsi校验
	public static String validImsi(String imsi) {
		try {
			if (imsi != null && !imsi.trim().equals("")) {

				if (imsi.matches(IMSI_REGEXP)) {
					return imsi;
				}

			}
		} catch (Exception ignore) {

		}

		return "";
	}
	
	// qqwx校验
	public static String validQqwx(String qqwx) {
		try {
			if (qqwx != null && !qqwx.trim().equals("")) {
				if (qqwx.length()>3 && qqwx.length()<15 && qqwx.matches(NUMBER_REGEXP)) {
					return qqwx;
				}

			}
		} catch (Exception ignore) {

		}

		return "";
	}

	// 身份证校验
	public static String validCert(String cert) {
		try {
			if (cert != null && !cert.trim().equals("")) {
				if (cert.length() == 18 && cert.matches(CERTIFICATE18_REGEXP)) {
					return cert;
				}

			}
		} catch (Exception ignore) {

		}

		return "";
	}
	
	// IP校验，这个对IP校验正好相反，若是IP,则返回空，否则返回原来的值
	public static String validIp(String ip) {
		try {
			if (ip != null && !ip.trim().equals("")) {
				if (ip.matches(IP_REGEXP)) {
					return "";
				}

			}
		} catch (Exception ignore) {

		}

		return ip;
	}	
}
