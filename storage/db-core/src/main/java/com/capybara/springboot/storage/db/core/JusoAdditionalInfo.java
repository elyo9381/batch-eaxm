package com.capybara.springboot.storage.db.core;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;

@Entity
@Table(name = "juso_additional_info", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"management_no"})
})
public class JusoAdditionalInfo extends BaseEntity {

    @Id
    @Tsid
    private Long jusoAdditionalInfoId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "juso_address_id", nullable = false)
    private JusoAddress jusoAddress;

    @Column(name = "management_no", nullable = false, length = 25)
    private String managementNo;

    @Column(name = "admin_dong_code", length = 10)
    private String adminDongCode;

    @Column(name = "admin_dong", length = 20)
    private String adminDong;

    @Column(name = "zipcode", length = 5)
    private String zipcode;

    @Column(name = "zipcode_serial_no", length = 3)
    private String zipcodeSerialNo;

    @Column(name = "bulk_delivery_name", length = 40)
    private String bulkDeliveryName;

    @Column(name = "bdong_arch_building_name", length = 40)
    private String bdongArchBuildingName;

    @Column(name = "sigungu_building_name", length = 40)
    private String sigunguBuildingName;

    @Column(name = "is_apartment", nullable = false, length = 1)
    private String isApartment = "0";


}