package com.capybara.springboot.storage.db.core;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;

@Entity
@Table(name = "juso_address", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"management_no"})
})
public class JusoAddress extends BaseEntity {

    @Id
    @Tsid
    private Long jusoAddressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "juso_road_code_id", nullable = false)
    private JusoRoadCode jusoRoadCode;

    @Column(name = "management_no", nullable = false, length = 25)
    private String managementNo;

    @Column(name = "road_name_code", nullable = false, length = 12)
    private String roadNameCode;

    @Column(name = "emdong_serial_no", nullable = false, length = 2)
    private String emdongSerialNo;

    @Column(name = "is_underground", nullable = false, length = 1)
    private String isUnderground = "0";

    @Column(name = "building_main_no", nullable = false)
    private Integer buildingMainNo;

    @Column(name = "building_sub_no", nullable = false)
    private Integer buildingSubNo = 0;

    @Column(name = "zipcode", length = 5)
    private String zipcode;

    @Column(name = "change_reason_code", length = 2)
    private String changeReasonCode;

    @Column(name = "announcement_date", length = 8)
    private String announcementDate;

    @Column(name = "previous_address", length = 25)
    private String previousAddress;

    @Column(name = "has_detail_address", nullable = false, length = 1)
    private String hasDetailAddress = "0";
}