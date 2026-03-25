package com.chirayu.ecom.helper;

import com.chirayu.ecom.entity.Product;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Helper {

    public static boolean checkFileContentType(MultipartFile file) {

        String contentType = file.getContentType();

        return contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public static List<Product> convertExcelToListOfProduct(InputStream is) {

        List<Product> list = new ArrayList<>();

        try {

            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet sheet = workbook.getSheet("data");
            int rowNumber = 0;
            Iterator<Row> iterator = sheet.iterator();
            while (iterator.hasNext()) {
                Row row = iterator.next();

                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }
                Iterator<Cell> cells = row.iterator();
                int cid = 0;

                Product product = new Product();
                while (cells.hasNext()) {
                    Cell cell = cells.next();

                    switch (cid) {
                        case 0:
                            product.setName(cell.getStringCellValue());
                            break;
                        case 1:
                            product.setDescription(cell.getStringCellValue());
                            break;
                        case 2:
                            product.setPrice(
                                    BigDecimal.valueOf(cell.getNumericCellValue()));
                            break;
                        case 3:
                            product.setStockQuantity(
                                    (int) cell.getNumericCellValue());
                            break;
                        case 4:
                            product.setCategory(cell.getStringCellValue());
                            break;
                        case 5:
                            product.setImageUrl(cell.getStringCellValue());
                            break;
                        default:
                            break;
                    }
                    cid++;
                }
                list.add(product);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
