package core.acc.api.service;

import core.acc.api.common.Util1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;

@Slf4j
@Service
@Transactional
public class ConvertServiceImpl implements ConverterService {
    @Autowired
    private ReportService reportService;

    @Override
    public void convertToUnicode() {
        convertCOA();
        convertTrader();
        convertGl();
    }

    private void convertCOA() {
        String sql = "select *\n" +
                "from chart_of_account\n";
        try {
            ResultSet rs = reportService.getResult(sql);
            if (rs != null) {
                while (rs.next()) {
                    String description = rs.getString("coa_name_eng");
                    if (!Util1.isNullOrEmpty(description)) {
                        if (Util1.isZGText(description)) {
                            rs.updateString("coa_name_eng", Util1.convertToUniCode(description));
                            rs.updateRow();
                        }
                    }
                }
                log.info("converted coa.");
            }
        } catch (Exception e) {
            log.error("convertCOA : " + e.getMessage());
        }
    }

    private void convertTrader() {
        String sql = "select *\n" +
                "from trader\n";
        try {
            ResultSet rs = reportService.getResult(sql);
            if (rs != null) {
                while (rs.next()) {
                    String typeName = rs.getString("trader_name");
                    if (Util1.isZGText(typeName)) {
                        rs.updateString("trader_name", Util1.convertToUniCode(typeName));
                        rs.updateRow();
                    }
                }
                log.info("converted trader.");
            }
        } catch (Exception e) {
            log.error("convertTrader : " + e.getMessage());
        }
    }

    private void convertGl() {
        String sql = "select *\n" +
                "from gl\n";
        try {
            ResultSet rs = reportService.getResult(sql);
            if (rs != null) {
                while (rs.next()) {
                    String description = Util1.isNull(rs.getString("description"), "");
                    if (Util1.isZGText(description)) {
                        rs.updateString("description", Util1.convertToUniCode(description));
                        rs.updateRow();
                    }
                    String reference = Util1.isNull(rs.getString("reference"), "");
                    if (Util1.isZGText(reference)) {
                        rs.updateString("reference", Util1.convertToUniCode(reference));
                        rs.updateRow();
                    }
                }
                log.info("converted gl.");
            }
        } catch (Exception e) {
            log.error("convertGL : " + e.getMessage());
        }
    }

}
