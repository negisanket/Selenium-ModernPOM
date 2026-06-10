package com.modern.automation.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Enhanced Utility to read and write Excel files.
 */
public class ExcelUtils {

    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    /**
     * Reads data from an Excel sheet and returns it as a 2D Object array.
     */
    public static Object[][] getTestData(String filePath, String sheetName) {
        Object[][] data = null;
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                logger.error("Sheet '{}' not found in file: {}", sheetName, filePath);
                return new Object[0][0];
            }

            int rowCount = sheet.getLastRowNum();
            int colCount = sheet.getRow(0).getLastCellNum();

            data = new Object[rowCount][colCount];

            for (int i = 0; i < rowCount; i++) {
                Row row = sheet.getRow(i + 1);
                for (int j = 0; j < colCount; j++) {
                    Cell cell = (row == null) ? null : row.getCell(j);
                    data[i][j] = (cell == null) ? "" : getCellValue(cell);
                }
            }
        } catch (IOException e) {
            logger.error("Error reading Excel file: {}", filePath, e);
        }
        return data;
    }

    /**
     * Writes a 2D String array to an Excel file.
     * Automatically creates the file and parent directories if they don't exist.
     */
    public static void writeToExcel(String filePath, String sheetName, String[][] data) {
        logger.info("Writing data to Excel file: {}", filePath);
        
        try {
            Files.createDirectories(Paths.get(filePath).getParent());
        } catch (IOException e) {
            logger.error("Failed to create directories for: {}", filePath, e);
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            for (int i = 0; i < data.length; i++) {
                Row row = sheet.createRow(i);
                for (int j = 0; j < data[i].length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(data[i][j]);
                }
            }

            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
                logger.info("Successfully wrote Excel file to: {}", filePath);
            }
        } catch (IOException e) {
            logger.error("Error writing Excel file: {}", filePath, e);
        }
    }

    private static Object getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return cell.getBooleanCellValue();
            default:
                return "";
        }
    }
}
