package com.excel.poiAndJxls.services.impl;


import com.excel.poiAndJxls.dto.ChartQueryResponse;
import com.excel.poiAndJxls.exception.NotFoundException;
import com.excel.poiAndJxls.intergration.dto.SuperSetRest;
import com.excel.poiAndJxls.services.ExportDataToExcelTemplateService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ExportDataToExcelTemplateServiceImpl implements ExportDataToExcelTemplateService {

    @Autowired
    private SuperSetRest superSetRest;

    @Override
    public void exportDataToExcelTemplate(OutputStream outputStream, String reportFileName) throws IOException {
        String fileName = String.format("/template_exports/%s", reportFileName);


        try (InputStream inputStream = this.getClass().getResourceAsStream(fileName)) {
            Workbook workbook = new XSSFWorkbook(inputStream);

            // Hard code sheet index number 2 is config sheet
            Sheet sheetConfig = workbook.getSheetAt(1);

            if (sheetConfig != null) {
                Row headerRow = sheetConfig.getRow(0);
                Cell chartIdCell = headerRow.getCell(1);
                Long chartId = (long) chartIdCell.getNumericCellValue();

                ChartQueryResponse res = superSetRest.getCharById(String.valueOf(chartId));
                Map<String, Object> servers = new HashMap<>();

                if (!res.result.isEmpty()) {
                    servers.put("data", res.getResult().get(0).data);
                }

                Map<String, Object> params = getParams();
                servers.putAll(params);

                InputStream imageInputStream = this.getClass().getResourceAsStream("/static/1550.png");

                assert imageInputStream != null;
                byte[] imageBytes = imageInputStream.readAllBytes();
                servers.put("image", imageBytes);

                this.exportToExcel(outputStream, servers, fileName);
            } else {
                throw new NotFoundException("Can not found config sheet");
            }
        } catch (NullPointerException e) {
            throw new NotFoundException("Template not found in resources/templates_exports");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public void exportToExcel(OutputStream outputStream, Map<String, Object> servers, String fileName) throws IOException {
        // Create a temporary file to hold the workbook
        File tempFile = File.createTempFile("modified_excel", ".xlsx");
        try (InputStream templateInputStream = this.getClass().getResourceAsStream(fileName)) {
            // First, create the workbook and modify it with the data
            Workbook workbook = new XSSFWorkbook(templateInputStream); // Read template
            Sheet newSheet = workbook.createSheet("Raw data");
            Row labelRow = newSheet.createRow(0);
            List<Map<String, Object>> data = (List<Map<String, Object>>) servers.get("data");

            if (!data.isEmpty()) {
                Map<String, Object> obj = data.get(0);
                AtomicInteger i = new AtomicInteger(0);
                obj.forEach((key, val) -> {
                    Cell cell = labelRow.createCell(i.get());
                    cell.setCellValue(key);
                    i.getAndIncrement();
                });

                AtomicInteger y = new AtomicInteger(1);
                i.set(0);

                data.forEach(e -> {
                    Row newRow = newSheet.createRow(y.get());
                    e.forEach((key, val) -> {
                        Cell cell = newRow.createCell(i.get()); // Use newRow, not labelRow
                        cell.setCellValue((String) val);
                        i.getAndIncrement();
                    });
                    i.set(0);
                    y.incrementAndGet();
                });
            }

            // Write the modified workbook to the temporary file
            try (FileOutputStream tempOutputStream = new FileOutputStream(tempFile)) {
                workbook.write(tempOutputStream);
                workbook.close();
            }

            // Now, process the template with JxlsHelper using the temporary file
            try (InputStream freshTemplateInputStream = new FileInputStream(tempFile)) {
                Context context = new Context();
                context.toMap().putAll(servers);
                JxlsHelper.getInstance().processTemplate(freshTemplateInputStream, outputStream, context);
            } catch (Exception e) {
                throw new RuntimeException("Error processing template with JxlsHelper", e);
            }
        } catch (NullPointerException e) {
            throw new NotFoundException("Template not found in resources/templates_exports");
        } catch (Exception e) {
            throw new RuntimeException("Error exporting to Excel", e);
        } finally {
            // Clean up the temporary file
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }


    private Map<String, Object> getParams() {
        String fileName = "/template_exports/dataParams.json";

        try (InputStream file = this.getClass().getResourceAsStream(fileName)) {
            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readValue(file, new TypeReference<>() {
            });
        } catch (IOException e) {
            return Map.of();
        }
    }

}
