package com.capybara.springboot.storage.db.core;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;

@Entity
@Table(name = "juso_jibun", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"management_no", "jibun_serial_no"})
})
public class JusoJibun extends BaseEntity {

    @Id
    @Tsid
    private Long jusoJibunId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "juso_address_id", nullable = false)
    private JusoAddress jusoAddress;

    @Column(name = "management_no", nullable = false, length = 25)
    private String managementNo;

    @Column(name = "jibun_serial_no", nullable = false)
    private Integer jibunSerialNo;

    @Column(name = "legal_dong_code", nullable = false, length = 10)
    private String legalDongCode;

    @Column(name = "sido", nullable = false, length = 20)
    private String sido;

    @Column(name = "sigungu", nullable = false, length = 20)
    private String sigungu;

    @Column(name = "legal_dong", nullable = false, length = 20)
    private String legalDong;

    @Column(name = "legal_ri", length = 20)
    private String legalRi;

    @Column(name = "is_mountain", nullable = false, length = 1)
    private String isMountain = "0";

    @Column(name = "jibun_main_no", nullable = false)
    private Integer jibunMainNo;

    @Column(name = "jibun_sub_no", nullable = false)
    private Integer jibunSubNo = 0;

    @Column(name = "is_representative", nullable = false, length = 1)
    private String isRepresentative = "0";

}