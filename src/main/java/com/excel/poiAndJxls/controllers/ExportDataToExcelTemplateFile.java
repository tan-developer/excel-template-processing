package com.excel.poiAndJxls.controllers;

import com.excel.poiAndJxls.dto.ExportParams;
import com.excel.poiAndJxls.intergration.dto.SuperSetRest;
import com.excel.poiAndJxls.services.ExportDataToExcelTemplateService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
public class ExportDataToExcelTemplateFile {

    @Autowired
    private ExportDataToExcelTemplateService exportDataToExcelTemplateService;

    @Autowired
    private SuperSetRest superSetRest;

    @GetMapping
    public void exportDataToExcelTemplateFile(HttpServletResponse response , @RequestParam String reportFileName) {
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=exportDataToExcelTemplate.xlsx");
            this.exportDataToExcelTemplateService.exportDataToExcelTemplate(response.getOutputStream() , reportFileName);
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/try")
    public ResponseEntity<?> tryCall(HttpServletResponse response) {
        superSetRest.getCharById("1");

        return ResponseEntity.ok(null);
    }
}
