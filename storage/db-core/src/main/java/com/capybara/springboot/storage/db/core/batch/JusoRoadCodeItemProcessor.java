package com.capybara.springboot.storage.db.core.batch;

import com.capybara.springboot.storage.db.core.JusoRoadCode;
import org.springframework.batch.item.ItemProcessor;
import java.util.concurrent.atomic.AtomicLong;

public class JusoRoadCodeItemProcessor implements ItemProcessor<String[], JusoRoadCode> {

    // 고유 ID 생성을 위한 스레드 안전한 원자적 카운터
    private static final AtomicLong idCounter = new AtomicLong(1);
    
    @Override
    public JusoRoadCode process(String[] fields) throws Exception {
        if (fields.length < 16) {
            return null; // 필드 수가 부족한 경우 처리하지 않음
        }

        JusoRoadCode roadCode = new JusoRoadCode();

        // 고유한 ID 할당 (원자적 연산으로 스레드 안전)
        roadCode.setJusoRoadCodeId(idCounter.getAndIncrement());
        roadCode.setRoadNameCode(fields[0]);
        roadCode.setRoadName(fields[1]);
        roadCode.setRoadNameRoman(fields[2]);
        roadCode.setEmdongSerialNo(fields[3]);
        roadCode.setSido(fields[4]);
        roadCode.setSidoRoman(fields[5]);
        roadCode.setSigungu(fields[6]);
        roadCode.setSigunguRoman(fields[7]);
        roadCode.setEmdongName(fields[8]);
        roadCode.setEmdongRoman(fields[9]);
        roadCode.setEmdongType(fields[10]);
        roadCode.setEmdongCode(fields[11]);
        roadCode.setIsUsed(fields[12]);
        roadCode.setChangeReason(fields.length > 13 ? fields[13] : null);
        roadCode.setChangeHistoryInfo(fields.length > 14 ? fields[14] : null);
        roadCode.setAnnouncementDate(fields.length > 15 ? fields[15] : null);
        roadCode.setDeletionDate(fields.length > 16 ? fields[16] : null);

        return roadCode;
    }
}