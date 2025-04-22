package com.capybara.springboot.storage.db.core;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;

@Entity
@Table(name = "juso_road_code", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"road_name_code", "emdong_serial_no"})
})
public class JusoRoadCode extends BaseEntity {

    @Id
    @Tsid
    private Long jusoRoadCodeId;

    @Column(name = "road_name_code", nullable = false, length = 12)
    private String roadNameCode;

    @Column(name = "road_name", nullable = false, length = 80)
    private String roadName;

    @Column(name = "road_name_roman", length = 80)
    private String roadNameRoman;

    @Column(name = "emdong_serial_no", nullable = false, length = 2)
    private String emdongSerialNo;

    @Column(name = "sido", nullable = false, length = 20)
    private String sido;

    @Column(name = "sido_roman", length = 40)
    private String sidoRoman;

    @Column(name = "sigungu", nullable = false, length = 20)
    private String sigungu;

    @Column(name = "sigungu_roman", length = 40)
    private String sigunguRoman;

    @Column(name = "emdong_name", length = 20)
    private String emdongName;

    @Column(name = "emdong_roman", length = 40)
    private String emdongRoman;

    @Column(name = "emdong_type", nullable = false, length = 1)
    private String emdongType;

    @Column(name = "emdong_code", length = 3)
    private String emdongCode;

    @Column(name = "is_used", nullable = false, length = 1)
    private String isUsed = "0";

    @Column(name = "change_reason", length = 1)
    private String changeReason;

    @Column(name = "change_history_info", length = 14)
    private String changeHistoryInfo;

    @Column(name = "announcement_date", length = 8)
    private String announcementDate;

    @Column(name = "deletion_date", length = 8)
    private String deletionDate;

    public Long getJusoRoadCodeId() {
        return jusoRoadCodeId;
    }

    public void setJusoRoadCodeId(Long jusoRoadCodeId) {
        this.jusoRoadCodeId = jusoRoadCodeId;
    }

    public String getRoadNameCode() {
        return roadNameCode;
    }

    public void setRoadNameCode(String roadNameCode) {
        this.roadNameCode = roadNameCode;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public String getRoadNameRoman() {
        return roadNameRoman;
    }

    public void setRoadNameRoman(String roadNameRoman) {
        this.roadNameRoman = roadNameRoman;
    }

    public String getEmdongSerialNo() {
        return emdongSerialNo;
    }

    public void setEmdongSerialNo(String emdongSerialNo) {
        this.emdongSerialNo = emdongSerialNo;
    }

    public String getSido() {
        return sido;
    }

    public void setSido(String sido) {
        this.sido = sido;
    }

    public String getSidoRoman() {
        return sidoRoman;
    }

    public void setSidoRoman(String sidoRoman) {
        this.sidoRoman = sidoRoman;
    }

    public String getSigungu() {
        return sigungu;
    }

    public void setSigungu(String sigungu) {
        this.sigungu = sigungu;
    }

    public String getSigunguRoman() {
        return sigunguRoman;
    }

    public void setSigunguRoman(String sigunguRoman) {
        this.sigunguRoman = sigunguRoman;
    }

    public String getEmdongName() {
        return emdongName;
    }

    public void setEmdongName(String emdongName) {
        this.emdongName = emdongName;
    }

    public String getEmdongRoman() {
        return emdongRoman;
    }

    public void setEmdongRoman(String emdongRoman) {
        this.emdongRoman = emdongRoman;
    }

    public String getEmdongType() {
        return emdongType;
    }

    public void setEmdongType(String emdongType) {
        this.emdongType = emdongType;
    }

    public String getEmdongCode() {
        return emdongCode;
    }

    public void setEmdongCode(String emdongCode) {
        this.emdongCode = emdongCode;
    }

    public String getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(String isUsed) {
        this.isUsed = isUsed;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

    public String getChangeHistoryInfo() {
        return changeHistoryInfo;
    }

    public void setChangeHistoryInfo(String changeHistoryInfo) {
        this.changeHistoryInfo = changeHistoryInfo;
    }

    public String getAnnouncementDate() {
        return announcementDate;
    }

    public void setAnnouncementDate(String announcementDate) {
        this.announcementDate = announcementDate;
    }

    public String getDeletionDate() {
        return deletionDate;
    }

    public void setDeletionDate(String deletionDate) {
        this.deletionDate = deletionDate;
    }
}