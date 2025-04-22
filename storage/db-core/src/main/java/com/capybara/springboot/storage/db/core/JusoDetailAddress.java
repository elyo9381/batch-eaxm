package com.capybara.springboot.storage.db.core;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;

@Entity
@Table(name = "juso_detail_address", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sigungu_code", "dong_serial_no", "floor_serial_no", "unit_serial_no", "unit_suffix_serial_no"})
})
public class JusoDetailAddress extends BaseEntity {

    @Id
    @Tsid
    private Long jusoDetailAddressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "juso_address_id", nullable = false)
    private JusoAddress jusoAddress;

    @Column(name = "sigungu_code", nullable = false, length = 5)
    private String sigunguCode;

    @Column(name = "dong_serial_no", nullable = false)
    private Long dongSerialNo;

    @Column(name = "floor_serial_no", nullable = false)
    private Long floorSerialNo = 0L;

    @Column(name = "unit_serial_no", nullable = false)
    private Long unitSerialNo = 0L;

    @Column(name = "unit_suffix_serial_no", nullable = false)
    private Long unitSuffixSerialNo = 0L;

    @Column(name = "dong_name", length = 50)
    private String dongName;

    @Column(name = "floor_name", length = 50)
    private String floorName;

    @Column(name = "unit_name", length = 50)
    private String unitName;

    @Column(name = "unit_suffix_name", length = 10)
    private String unitSuffixName;

    @Column(name = "is_basement", nullable = false, length = 1)
    private String isBasement = "0";

    @Column(name = "building_management_no", nullable = false, length = 25)
    private String buildingManagementNo;

    @Column(name = "legal_dong_code", nullable = false, length = 10)
    private String legalDongCode;

    @Column(name = "road_name_code", nullable = false, length = 12)
    private String roadNameCode;

    @Column(name = "is_underground", nullable = false, length = 1)
    private String isUnderground = "0";

    @Column(name = "building_main_no", nullable = false)
    private Integer buildingMainNo;

    @Column(name = "building_sub_no", nullable = false)
    private Integer buildingSubNo = 0;

    @Column(name = "change_reason_code", length = 2)
    private String changeReasonCode;

}
