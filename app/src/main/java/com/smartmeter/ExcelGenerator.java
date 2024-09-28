package com.smartmeter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.util.Log;

import com.smartmeter.models.CounterInfo;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExcelGenerator {
    private Context context;
    private HSSFWorkbook hssfWorkbook;
    private HSSFSheet hssfSheet;
    private HSSFRow hssfRow;
    private HSSFCell[] hssfCell;

    public ExcelGenerator(Context context, String name) {
        this.context = context;
        hssfWorkbook = new HSSFWorkbook();
        hssfSheet = hssfWorkbook.createSheet(name);
        hssfRow = hssfSheet.createRow(0);
        hssfCell = new HSSFCell[13];
    }

    public void createHeader(String id, String company, String room, String floor, String multiplier,
                             String counter, String date, String currentValue, String previousValue,
                             String difference, String total) {
        for (int j = 0; j < 13; j++) {
            hssfCell[j] = hssfRow.createCell(j);
        }

        hssfCell[0].setCellValue(id);
        hssfCell[1].setCellValue(company);
        hssfCell[2].setCellValue(room);
        hssfCell[3].setCellValue(floor);
        hssfCell[4].setCellValue(multiplier);
        hssfCell[5].setCellValue(counter);
        hssfCell[6].setCellValue(date);
        hssfCell[7].setCellValue(currentValue);
        hssfCell[8].setCellValue(previousValue);
        hssfCell[9].setCellValue(difference);
        hssfCell[11].setCellValue(company);
        hssfCell[12].setCellValue(total);
    }

    public void fill(ArrayList<CounterInfo> values) {
        List<CounterInfo> rows = new ArrayList<>();
        Map<String, Integer> total = new HashMap<>();
        Collections.sort(values);

        for (int i = 0; i < values.size(); i++) {
            hssfRow = hssfSheet.createRow(i + 1);

            for (int j = 0; j < 13; j++) {
                hssfCell[j] = hssfRow.createCell(j);
            }

            hssfCell[0].setCellValue(values.get(i).id);
            hssfCell[1].setCellValue(values.get(i).company);
            hssfCell[2].setCellValue(values.get(i).room);
            hssfCell[3].setCellValue(values.get(i).floor);
            hssfCell[4].setCellValue(values.get(i).multiplier);
            hssfCell[5].setCellValue(values.get(i).counter);
            hssfCell[6].setCellValue(values.get(i).lastDate);
            hssfCell[7].setCellValue(values.get(i).currentValue);
            hssfCell[8].setCellValue(values.get(i).previousValue);
            hssfCell[9].setCellValue(values.get(i).difference);
            total.put(
                    values.get(i).company,
                    total.getOrDefault(values.get(i).company, 0) + values.get(i).difference
            );
        }

        int index = 0;
        for (Map.Entry<String, Integer> item : total.entrySet()) {
            hssfRow = hssfSheet.getRow(index + 1);
            if (hssfRow == null) {
                hssfRow = hssfSheet.createRow(index + 1);
                for (int j = 0; j < 13; j++) {
                    hssfCell[j] = hssfRow.createCell(j);
                }
            }
            hssfRow.getCell(11).setCellValue(item.getKey());
            hssfRow.getCell(12).setCellValue(item.getValue());
            index++;
        }
    }

    public void save(String fileName) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, MODE_PRIVATE);
            hssfWorkbook.write(fileOutputStream);
        } catch (Exception e) {
            Log.e("Error", Objects.requireNonNull(e.getMessage()));
        }
    }
}
