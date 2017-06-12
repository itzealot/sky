package com.surfilter.mass.tools.entity;

public class ZtryRevokedData extends ZtryData {

	private String caughtDate; // 抓获日期
	private String caughtWay; // 抓获方式
	private String caughtArea; // 抓获地区
	private String caughtOffice; // 抓获单位
	private String fingerprintNo; // 指纹编号
	private String dnaNo; // DNA编号
	private String revoker; // 撤销填表人
	private String revokeDate; // 撤销日期
	private String revokeAuthor; // 撤销审批人
	private String revokeProvinceDate; // 入省撤销库时间
	private String revokeDeptDate; // 入部撤销库时间
	private String revokeLastModifyDate; // 撤销最后修改时间

	public ZtryRevokedData() {
	}

	public ZtryRevokedData(String ztryId, String ztryTId, String name, String nickname, String sex, String birth,
			String certificate, String nation, String otherCert1, String otherCert2, String height, String kouyin,
			String career, String censusAddr, String address, String census, String features, String special,
			String caseNo, String caseType, String caseRemark, String awayDate, String awayDirection, String awayType,
			String lawBook, String lawDate, String tjl, String tjlType, String tjlMoney, String ownDept,
			String deptType, String receiveDept, String receiveDate, String receiver, String receiverPhone,
			String register, String registDate, String registAuthor, String blReason, String intoProvinceDate,
			String intoDeptDate, String lastModify, String photo, String caughtDate, String caughtWay,
			String caughtArea, String caughtOffice, String fingerprintNo, String dnaNo, String revoker,
			String revokeDate, String revokeAuthor, String revokeProvinceDate, String revokeDeptDate,
			String revokeLastModifyDate) {
		super(ztryId, ztryTId, name, nickname, sex, birth, certificate, nation, otherCert1, otherCert2, height, kouyin,
				career, censusAddr, address, census, features, special, caseNo, caseType, caseRemark, awayDate,
				awayDirection, awayType, lawBook, lawDate, tjl, tjlType, tjlMoney, ownDept, deptType, receiveDept,
				receiveDate, receiver, receiverPhone, register, registDate, registAuthor, blReason, intoProvinceDate,
				intoDeptDate, lastModify, photo);
		this.caughtDate = caughtDate;
		this.caughtWay = caughtWay;
		this.caughtArea = caughtArea;
		this.caughtOffice = caughtOffice;
		this.fingerprintNo = fingerprintNo;
		this.dnaNo = dnaNo;
		this.revoker = revoker;
		this.revokeDate = revokeDate;
		this.revokeAuthor = revokeAuthor;
		this.revokeProvinceDate = revokeProvinceDate;
		this.revokeDeptDate = revokeDeptDate;
		this.revokeLastModifyDate = revokeLastModifyDate;
	}

	public String getCaughtDate() {
		return caughtDate;
	}

	public void setCaughtDate(String caughtDate) {
		this.caughtDate = caughtDate;
	}

	public String getCaughtWay() {
		return caughtWay;
	}

	public void setCaughtWay(String caughtWay) {
		this.caughtWay = caughtWay;
	}

	public String getCaughtArea() {
		return caughtArea;
	}

	public void setCaughtArea(String caughtArea) {
		this.caughtArea = caughtArea;
	}

	public String getCaughtOffice() {
		return caughtOffice;
	}

	public void setCaughtOffice(String caughtOffice) {
		this.caughtOffice = caughtOffice;
	}

	public String getFingerprintNo() {
		return fingerprintNo;
	}

	public void setFingerprintNo(String fingerprintNo) {
		this.fingerprintNo = fingerprintNo;
	}

	public String getDnaNo() {
		return dnaNo;
	}

	public void setDnaNo(String dnaNo) {
		this.dnaNo = dnaNo;
	}

	public String getRevoker() {
		return revoker;
	}

	public void setRevoker(String revoker) {
		this.revoker = revoker;
	}

	public String getRevokeDate() {
		return revokeDate;
	}

	public void setRevokeDate(String revokeDate) {
		this.revokeDate = revokeDate;
	}

	public String getRevokeAuthor() {
		return revokeAuthor;
	}

	public void setRevokeAuthor(String revokeAuthor) {
		this.revokeAuthor = revokeAuthor;
	}

	public String getRevokeProvinceDate() {
		return revokeProvinceDate;
	}

	public void setRevokeProvinceDate(String revokeProvinceDate) {
		this.revokeProvinceDate = revokeProvinceDate;
	}

	public String getRevokeDeptDate() {
		return revokeDeptDate;
	}

	public void setRevokeDeptDate(String revokeDeptDate) {
		this.revokeDeptDate = revokeDeptDate;
	}

	public String getRevokeLastModifyDate() {
		return revokeLastModifyDate;
	}

	public void setRevokeLastModifyDate(String revokeLastModifyDate) {
		this.revokeLastModifyDate = revokeLastModifyDate;
	}

	@Override
	public String map2Str(StringBuffer buffer) {
		buffer.append(super.map2Str(buffer)).append(SPLITER);

		buffer.append(caughtDate).append(SPLITER).append(caughtWay).append(SPLITER).append(caughtArea).append(SPLITER)
				.append(caughtOffice).append(SPLITER).append(fingerprintNo).append(SPLITER).append(dnaNo)
				.append(SPLITER).append(revoker).append(SPLITER).append(revokeDate).append(SPLITER).append(revokeAuthor)
				.append(SPLITER).append(revokeProvinceDate).append(SPLITER).append(revokeDeptDate).append(SPLITER)
				.append(revokeLastModifyDate);

		String result = buffer.toString();
		buffer.setLength(0);
		return result;
	}

	@Override
	public String toString() {
		return "ZTRYRevokedData [ztryId=" + ztryId + ", ztryTId=" + ztryTId + ", name=" + name + ", nickname="
				+ nickname + ", sex=" + sex + ", birth=" + birth + ", certificate=" + certificate + ", nation=" + nation
				+ ", otherCert1=" + otherCert1 + ", otherCert2=" + otherCert2 + ", height=" + height + ", kouyin="
				+ kouyin + ", career=" + career + ", censusAddr=" + censusAddr + ", address=" + address + ", census="
				+ census + ", features=" + features + ", special=" + special + ", caseNo=" + caseNo + ", caseType="
				+ caseType + ", caseRemark=" + caseRemark + ", awayDate=" + awayDate + ", awayDirection="
				+ awayDirection + ", awayType=" + awayType + ", lawBook=" + lawBook + ", lawDate=" + lawDate + ", tjl="
				+ tjl + ", tjlType=" + tjlType + ", tjlMoney=" + tjlMoney + ", ownDept=" + ownDept + ", deptType="
				+ deptType + ", receiveDept=" + receiveDept + ", receiveDate=" + receiveDate + ", receiver=" + receiver
				+ ", receiverPhone=" + receiverPhone + ", register=" + register + ", registDate=" + registDate
				+ ", registAuthor=" + registAuthor + ", blReason=" + blReason + ", intoProvinceDate=" + intoProvinceDate
				+ ", intoDeptDate=" + intoDeptDate + ", lastModify=" + lastModify + ", photo=" + photo + ",caughtDate="
				+ caughtDate + ", caughtWay=" + caughtWay + ", caughtArea=" + caughtArea + ", caughtOffice="
				+ caughtOffice + ", fingerprintNo=" + fingerprintNo + ", dnaNo=" + dnaNo + ", revoker=" + revoker
				+ ", revokeDate=" + revokeDate + ", revokeAuthor=" + revokeAuthor + ", revokeProvinceDate="
				+ revokeProvinceDate + ", revokeDeptDate=" + revokeDeptDate + ", revokeLastModifyDate="
				+ revokeLastModifyDate + "]";
	}

}
