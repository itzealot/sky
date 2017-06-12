package com.surfilter.mass.tools.entity;

import com.surfilter.mass.tools.util.ZtryCatchUtil;

/**
 * 在逃人员
 * 
 * @author zealot
 *
 */
public class ZtryData {

	protected static final String SPLITER = ZtryCatchUtil.SPLITER;
	protected String ztryId; // 编号(跳转url上的Id)
	protected String ztryTId; // 编号(详情上的编号)
	protected String name; // 姓名
	protected String nickname; // 别名绰号
	protected String sex; // 性别
	protected String birth; // 出生日期
	protected String certificate; // 身份证号
	protected String nation; // 民族
	protected String otherCert1; // 其他证件(1)
	protected String otherCert2; // 其他证件(2)
	protected String height; // 身高
	protected String kouyin; // 口音
	protected String career; // 职业
	protected String censusAddr; // 户籍地址
	protected String address; // 现住地址
	protected String census; // 籍贯
	protected String features; // 体貌特征
	protected String special; // 特殊标记
	protected String caseNo; // 案件编号
	protected String caseType; // 案件类别
	protected String caseRemark; // 简要案情及附加信息
	protected String awayDate; // 逃跑日期
	protected String awayDirection; // 逃跑方向
	protected String awayType; // 在逃类型
	protected String lawBook; // 法律文书
	protected String lawDate; // 签发日期
	protected String tjl; // 通缉令
	protected String tjlType; // 督捕级别
	protected String tjlMoney; // 奖金
	protected String ownDept; // 立案单位
	protected String deptType; // 单位分类
	protected String receiveDept; // 主办单位
	protected String receiveDate; // 立案日期
	protected String receiver; // 主办人
	protected String receiverPhone; // 联系方式
	protected String register; // 登记填表人
	protected String registDate; // 登记日期
	protected String registAuthor; // 登记审批人
	protected String blReason; // 补录原因
	protected String intoProvinceDate; // 入省登记库时间
	protected String intoDeptDate; // 入部登记库时间
	protected String lastModify; // 最后修改时间
	protected String photo; // 图片地址

	public ZtryData() {
	}

	public ZtryData(String ztryId, String ztryTId, String name, String nickname, String sex, String birth,
			String certificate, String nation, String otherCert1, String otherCert2, String height, String kouyin,
			String career, String censusAddr, String address, String census, String features, String special,
			String caseNo, String caseType, String caseRemark, String awayDate, String awayDirection, String awayType,
			String lawBook, String lawDate, String tjl, String tjlType, String tjlMoney, String ownDept,
			String deptType, String receiveDept, String receiveDate, String receiver, String receiverPhone,
			String register, String registDate, String registAuthor, String blReason, String intoProvinceDate,
			String intoDeptDate, String lastModify, String photo) {
		super();
		this.ztryId = ztryId;
		this.ztryTId = ztryTId;
		this.name = name;
		this.nickname = nickname;
		this.sex = sex;
		this.birth = birth;
		this.certificate = certificate;
		this.nation = nation;
		this.otherCert1 = otherCert1;
		this.otherCert2 = otherCert2;
		this.height = height;
		this.kouyin = kouyin;
		this.career = career;
		this.censusAddr = censusAddr;
		this.address = address;
		this.census = census;
		this.features = features;
		this.special = special;
		this.caseNo = caseNo;
		this.caseType = caseType;
		this.caseRemark = caseRemark;
		this.awayDate = awayDate;
		this.awayDirection = awayDirection;
		this.awayType = awayType;
		this.lawBook = lawBook;
		this.lawDate = lawDate;
		this.tjl = tjl;
		this.tjlType = tjlType;
		this.tjlMoney = tjlMoney;
		this.ownDept = ownDept;
		this.deptType = deptType;
		this.receiveDept = receiveDept;
		this.receiveDate = receiveDate;
		this.receiver = receiver;
		this.receiverPhone = receiverPhone;
		this.register = register;
		this.registDate = registDate;
		this.registAuthor = registAuthor;
		this.blReason = blReason;
		this.intoProvinceDate = intoProvinceDate;
		this.intoDeptDate = intoDeptDate;
		this.lastModify = lastModify;
		this.photo = photo;
	}

	public String getZtryId() {
		return ztryId;
	}

	public void setZtryId(String ztryId) {
		this.ztryId = ztryId;
	}

	public String getZtryTId() {
		return ztryTId;
	}

	public void setZtryTId(String ztryTId) {
		this.ztryTId = ztryTId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getCertificate() {
		return certificate;
	}

	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	public String getNation() {
		return nation;
	}

	public void setNation(String nation) {
		this.nation = nation;
	}

	public String getOtherCert1() {
		return otherCert1;
	}

	public void setOtherCert1(String otherCert1) {
		this.otherCert1 = otherCert1;
	}

	public String getOtherCert2() {
		return otherCert2;
	}

	public void setOtherCert2(String otherCert2) {
		this.otherCert2 = otherCert2;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getKouyin() {
		return kouyin;
	}

	public void setKouyin(String kouyin) {
		this.kouyin = kouyin;
	}

	public String getCareer() {
		return career;
	}

	public void setCareer(String career) {
		this.career = career;
	}

	public String getCensusAddr() {
		return censusAddr;
	}

	public void setCensusAddr(String censusAddr) {
		this.censusAddr = censusAddr;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCensus() {
		return census;
	}

	public void setCensus(String census) {
		this.census = census;
	}

	public String getFeatures() {
		return features;
	}

	public void setFeatures(String features) {
		this.features = features;
	}

	public String getSpecial() {
		return special;
	}

	public void setSpecial(String special) {
		this.special = special;
	}

	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getCaseType() {
		return caseType;
	}

	public void setCaseType(String caseType) {
		this.caseType = caseType;
	}

	public String getCaseRemark() {
		return caseRemark;
	}

	public void setCaseRemark(String caseRemark) {
		this.caseRemark = caseRemark;
	}

	public String getAwayDate() {
		return awayDate;
	}

	public void setAwayDate(String awayDate) {
		this.awayDate = awayDate;
	}

	public String getAwayDirection() {
		return awayDirection;
	}

	public void setAwayDirection(String awayDirection) {
		this.awayDirection = awayDirection;
	}

	public String getAwayType() {
		return awayType;
	}

	public void setAwayType(String awayType) {
		this.awayType = awayType;
	}

	public String getLawBook() {
		return lawBook;
	}

	public void setLawBook(String lawBook) {
		this.lawBook = lawBook;
	}

	public String getLawDate() {
		return lawDate;
	}

	public void setLawDate(String lawDate) {
		this.lawDate = lawDate;
	}

	public String getTjl() {
		return tjl;
	}

	public void setTjl(String tjl) {
		this.tjl = tjl;
	}

	public String getTjlType() {
		return tjlType;
	}

	public void setTjlType(String tjlType) {
		this.tjlType = tjlType;
	}

	public String getTjlMoney() {
		return tjlMoney;
	}

	public void setTjlMoney(String tjlMoney) {
		this.tjlMoney = tjlMoney;
	}

	public String getOwnDept() {
		return ownDept;
	}

	public void setOwnDept(String ownDept) {
		this.ownDept = ownDept;
	}

	public String getDeptType() {
		return deptType;
	}

	public void setDeptType(String deptType) {
		this.deptType = deptType;
	}

	public String getReceiveDept() {
		return receiveDept;
	}

	public void setReceiveDept(String receiveDept) {
		this.receiveDept = receiveDept;
	}

	public String getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(String receiveDate) {
		this.receiveDate = receiveDate;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getReceiverPhone() {
		return receiverPhone;
	}

	public void setReceiverPhone(String receiverPhone) {
		this.receiverPhone = receiverPhone;
	}

	public String getRegister() {
		return register;
	}

	public void setRegister(String register) {
		this.register = register;
	}

	public String getRegistDate() {
		return registDate;
	}

	public void setRegistDate(String registDate) {
		this.registDate = registDate;
	}

	public String getRegistAuthor() {
		return registAuthor;
	}

	public void setRegistAuthor(String registAuthor) {
		this.registAuthor = registAuthor;
	}

	public String getBlReason() {
		return blReason;
	}

	public void setBlReason(String blReason) {
		this.blReason = blReason;
	}

	public String getIntoProvinceDate() {
		return intoProvinceDate;
	}

	public void setIntoProvinceDate(String intoProvinceDate) {
		this.intoProvinceDate = intoProvinceDate;
	}

	public String getIntoDeptDate() {
		return intoDeptDate;
	}

	public void setIntoDeptDate(String intoDeptDate) {
		this.intoDeptDate = intoDeptDate;
	}

	public String getLastModify() {
		return lastModify;
	}

	public void setLastModify(String lastModify) {
		this.lastModify = lastModify;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String map2Str(StringBuffer buffer) {
		buffer.append(ztryId).append(SPLITER).append(ztryTId).append(SPLITER).append(name).append(SPLITER)
				.append(nickname).append(SPLITER).append(sex).append(SPLITER).append(birth).append(SPLITER)
				.append(certificate).append(SPLITER).append(nation).append(SPLITER).append(otherCert1).append(SPLITER)
				.append(otherCert2).append(SPLITER).append(height).append(SPLITER).append(kouyin).append(SPLITER)
				.append(career).append(SPLITER).append(censusAddr).append(SPLITER).append(address).append(SPLITER)
				.append(census).append(SPLITER).append(features).append(SPLITER).append(special).append(SPLITER)
				.append(caseNo).append(SPLITER).append(caseType).append(SPLITER).append(caseRemark).append(SPLITER)
				.append(awayDate).append(SPLITER).append(awayDirection).append(SPLITER).append(awayType).append(SPLITER)
				.append(lawBook).append(SPLITER).append(lawDate).append(SPLITER).append(tjl).append(SPLITER)
				.append(tjlType).append(SPLITER).append(tjlMoney).append(SPLITER).append(ownDept).append(SPLITER)
				.append(deptType).append(SPLITER).append(receiveDept).append(SPLITER).append(receiveDate)
				.append(SPLITER).append(receiver).append(SPLITER).append(receiverPhone).append(SPLITER).append(register)
				.append(SPLITER).append(registDate).append(SPLITER).append(registAuthor).append(SPLITER)
				.append(blReason).append(SPLITER).append(intoProvinceDate).append(SPLITER).append(intoDeptDate)
				.append(SPLITER).append(lastModify).append(SPLITER).append(photo);

		String result = buffer.toString();
		buffer.setLength(0);
		return result;
	}

	@Override
	public String toString() {
		return "ZTRYData [ztryId=" + ztryId + ", ztryTId=" + ztryTId + ", name=" + name + ", nickname=" + nickname
				+ ", sex=" + sex + ", birth=" + birth + ", certificate=" + certificate + ", nation=" + nation
				+ ", otherCert1=" + otherCert1 + ", otherCert2=" + otherCert2 + ", height=" + height + ", kouyin="
				+ kouyin + ", career=" + career + ", censusAddr=" + censusAddr + ", address=" + address + ", census="
				+ census + ", features=" + features + ", special=" + special + ", caseNo=" + caseNo + ", caseType="
				+ caseType + ", caseRemark=" + caseRemark + ", awayDate=" + awayDate + ", awayDirection="
				+ awayDirection + ", awayType=" + awayType + ", lawBook=" + lawBook + ", lawDate=" + lawDate + ", tjl="
				+ tjl + ", tjlType=" + tjlType + ", tjlMoney=" + tjlMoney + ", ownDept=" + ownDept + ", deptType="
				+ deptType + ", receiveDept=" + receiveDept + ", receiveDate=" + receiveDate + ", receiver=" + receiver
				+ ", receiverPhone=" + receiverPhone + ", register=" + register + ", registDate=" + registDate
				+ ", registAuthor=" + registAuthor + ", blReason=" + blReason + ", intoProvinceDate=" + intoProvinceDate
				+ ", intoDeptDate=" + intoDeptDate + ", lastModify=" + lastModify + ", photo=" + photo + "]";
	}

}
