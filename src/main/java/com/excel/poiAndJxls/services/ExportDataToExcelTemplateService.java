package com.excel.poiAndJxls.services;

import java.io.IOException;
import java.io.OutputStream;

public interface ExportDataToExcelTemplateService {
    void exportDataToExcelTemplate(OutputStream outputStream , String reportFileName) throws IOException;
}
