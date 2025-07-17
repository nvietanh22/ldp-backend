package vn.lottefinance.landingpage.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.lottefinance.landingpage.utils.ExcelColumn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExcelService {

    public <T> ByteArrayInputStream exportToExcel(List<T> dataList, Class<T> clazz) throws IOException, IllegalAccessException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sheet1");

        // Header
        Row headerRow = sheet.createRow(0);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Cell cell = headerRow.createCell(i);
            fields[i].setAccessible(true);
            cell.setCellValue(fields[i].getName());
        }

        // Data rows
        for (int i = 0; i < dataList.size(); i++) {
            Row dataRow = sheet.createRow(i + 1);
            T item = dataList.get(i);
            for (int j = 0; j < fields.length; j++) {
                Cell cell = dataRow.createCell(j);
                Object value = fields[j].get(item);
                cell.setCellValue(value != null ? value.toString() : "");
            }
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return new ByteArrayInputStream(out.toByteArray());
    }

    public <T> List<T> importFromExcel(MultipartFile file, Class<T> clazz) throws Exception {
        List<T> resultList = new ArrayList<>();
        InputStream inputStream = file.getInputStream();
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        // Lấy header từ dòng đầu tiên
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) throw new RuntimeException("Không tìm thấy dòng header");

        // Map header name → column index
        Map<String, Integer> headerIndexMap = new HashMap<>();
        for (Cell cell : headerRow) {
            String headerName = cell.getStringCellValue().trim();
            headerIndexMap.put(headerName, cell.getColumnIndex());
        }

        // Map tên header → field tương ứng
        Map<String, Field> fieldMap = new HashMap<>();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
            String headerName = (annotation != null) ? annotation.value() : field.getName();
            fieldMap.put(headerName, field);
        }

        // Duyệt từng dòng và mapping field
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
                String header = entry.getKey();
                Field field = entry.getValue();

                Integer colIndex = headerIndexMap.get(header);
                if (colIndex == null) continue;

                Cell cell = row.getCell(colIndex);
                if (cell == null) continue;

                String cellValue = getCellValueAsString(cell);
                Object convertedValue = convertValue(cellValue, field.getType());
                field.set(instance, convertedValue);
            }

            resultList.add(instance);
        }

        workbook.close();
        return resultList;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                String value = cell.getStringCellValue().trim();
                // Nếu là ngày dạng "yyyy-MM-dd" thì thêm "00:00:00" (tuỳ ý)
                if (value.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                    return value + " 00:00:00";
                }
                return value;

            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Format luôn theo yyyy-MM-dd HH:mm:ss
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    return sdf.format(cell.getDateCellValue());
                } else {
                    double valueNumeric = cell.getNumericCellValue();
                    return (valueNumeric == (long) valueNumeric)
                            ? String.valueOf((long) valueNumeric)
                            : String.valueOf(valueNumeric);
                }

            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());

            case FORMULA:
                try {
                    return cell.getStringCellValue().trim();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }

            case BLANK:
                return "";

            default:
                return cell.toString().trim();
        }
    }



    private Object convertValue(String value, Class<?> targetType) {
        if (targetType == String.class) return value;
        if (targetType == Integer.class || targetType == int.class) return Integer.parseInt(value.split("\\.")[0]);
        if (targetType == Long.class || targetType == long.class) return Long.parseLong(value.split("\\.")[0]);
        if (targetType == Double.class || targetType == double.class) return Double.parseDouble(value);
        if (targetType == Boolean.class || targetType == boolean.class) return Boolean.parseBoolean(value);
        return null;
    }
}
