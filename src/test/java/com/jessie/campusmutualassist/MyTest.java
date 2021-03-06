package com.jessie.campusmutualassist;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.jessie.campusmutualassist.entity.StuPointsDetail;
import com.jessie.campusmutualassist.entity.StuPointsWithRealName;
import com.jessie.campusmutualassist.mapper.UserMapper;
import com.jessie.campusmutualassist.service.PermissionService;
import com.jessie.campusmutualassist.service.StuPointsDetailService;
import com.jessie.campusmutualassist.service.StudentPointsService;
import com.jessie.campusmutualassist.service.UserService;
import com.jessie.campusmutualassist.service.impl.AliyunGreenService;
import com.jessie.campusmutualassist.utils.RedisUtil;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import com.aliyuncs.IAcsClient;
//import com.aliyuncs.green.model.v20180509.TextScanRequest;
//import com.aliyuncs.http.FormatType;
//import com.aliyuncs.http.HttpResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CampusMutualAssistApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MyTest {
    @Autowired
    UserService userService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    PermissionService permissionService;
    @Autowired
    StudentPointsService studentPointsService;
    @Autowired
    StuPointsDetailService stuPointsDetailService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    AliyunGreenService aliyunGreenService;
    //    @Autowired
//    IAcsClient iAcsClient;
    private static Logger logger = LoggerFactory.getLogger(MyTest.class);

    @Test
    public void testQueryUserPermissions() {
        List<String> myList = permissionService.getAllUserPermissions("Jessie");
        for (String x : myList) {
            System.out.println(x);
        }
        logger.info("??????????????????");
    }

    @Test
    public void testReadExcel() throws Exception {
        File file2 = new File("D:/test.xlsx");
        String suffix = file2.getName();
        suffix = suffix.substring(suffix.lastIndexOf(".") + 1);
        Workbook workbook;
        if ("xlsx".equals(suffix)) {
            workbook = new XSSFWorkbook(file2);
        } else if ("xls".equals(suffix)) {
            FileInputStream fis = new FileInputStream(file2);
            workbook = new HSSFWorkbook(fis);
        } else {
            return;
        }
        Sheet sheet = workbook.getSheetAt(0);     //??????sheet 0

        int firstRowIndex = sheet.getFirstRowNum();   //?????????????????????????????????
        int lastRowIndex = sheet.getLastRowNum();
        System.out.println("firstRowIndex: " + firstRowIndex);
        System.out.println("lastRowIndex: " + lastRowIndex);
        Row row0 = sheet.getRow(firstRowIndex);
        int theNoIndex = 0;
        if (row0 != null) {
            int firstCellIndex = row0.getFirstCellNum();
            int lastCellIndex = row0.getLastCellNum();
            for (int cIndex = firstCellIndex; cIndex < lastCellIndex; cIndex++) {   //?????????
                Cell cell = row0.getCell(cIndex);
                if (cell != null && cell.toString().contains("??????")) {
                    theNoIndex = cIndex;
                    System.out.println("????????????" + theNoIndex);
                    break;
                }
            }
        }
        for (int rIndex = firstRowIndex + 1; rIndex <= lastRowIndex; rIndex++) {   //?????????
            System.out.println("rIndex: " + rIndex);
            Row row = sheet.getRow(rIndex);
            if (row != null) {
                Cell cell = row.getCell(theNoIndex);

                if (cell != null) {
                    cell.setCellType(CellType.STRING);//??????String?????????????????????????????????????????????????????????
                    System.out.println(cell.toString());
                    //System.out.println(cell.getCellTypeEnum());
                    if (cell.toString().contains(".")) {
                        System.out.println("???????????????????????????????????????????????????????????????????????????????????????");
                    }
                    //????????????????????????????????????(??????toString???????????????????????????????????????????????????????????????????????????0?????????????????????????????????
                    //??????????????????????????????????????????
                }
            }
        }

        file2.delete();
        return;
    }

    @Test
    public void testXMlQuery() {
        Set<String> mySet = new HashSet<>();
        mySet.add("student1");
        mySet.add("student2");
        List<Map<String, String>> myMap = userMapper.getRealNameWithUsername(mySet);
        System.out.println(myMap);
        //??????????????????
    }

    @Test
    public void testJWCHPost() {
        String No = "66233";
        String Password = "123456";
        int code = 200;
        String result;
        try {
            String postURL = "http://59.77.226.32/logincheck.asp";
            PostMethod postMethod = null;
            postMethod = new PostMethod(postURL);
            postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            postMethod.setRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            postMethod.setRequestHeader("Accept-Encoding", "gzip, deflate");
            postMethod.setRequestHeader("Referer", "http://jwch.fzu.edu.cn/");
            postMethod.setRequestHeader("Origin", "http://jwch.fzu.edu.cn");
            postMethod.setRequestHeader("Upgrade-Insecure-Requests", "1");
            postMethod.setRequestHeader("DNT", "1");
            postMethod.setRequestHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
            NameValuePair[] data = {
                    new NameValuePair("muser", No),
                    new NameValuePair("passwd", Password)

            };
            //???????????????????????????????????????????????????NULL?????????????????????
            postMethod.setRequestBody(data);

            org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
            int response = httpClient.executeMethod(postMethod); // ??????POST??????
            result = postMethod.getResponseBodyAsString();
            System.out.println(result);
            System.out.println(postMethod.getStatusCode());
            code = postMethod.getStatusCode();
            postMethod.abort();
        } catch (Exception e) {
            System.out.println("????????????" + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        if (code == 302) {
            System.out.println("Not");
        }
        System.out.println("OK");
    }

    @Test
    public void ProjectDir() {
        String projectName = System.getProperty("user.dir");
        String path = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
        System.out.println(projectName);
        System.out.println(path);
    }

    @Test
    public void writeToXlsx() throws Exception {
        String classID = "CIRD9F";
        String path = "D:\\camFiles\\";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String fileTime= LocalDate.now().toString();
        System.out.println(fileTime);

        File file2 = new File(path, "?????????????????????"+fileTime+".xlsx");

        if(!file2.exists()){
        file2.createNewFile();}
        else{
            file2.delete();
            file2.createNewFile();
        }
        System.out.println(file2.getName());
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("???????????????");     //??????sheet 0


//        System.out.println("firstRowIndex: " + firstRowIndex);
//        System.out.println("lastRowIndex: " + lastRowIndex);
        Row firstRow = sheet.createRow(0);
        Cell cell1 = firstRow.createCell(0);
        cell1.setCellValue("??????");
        Cell cell2 = firstRow.createCell(1);
        cell2.setCellValue("??????");
        Cell cell3 = firstRow.createCell(2);
        cell3.setCellValue("?????????");
        List<StuPointsWithRealName> list = studentPointsService.StusPoints(classID);
        System.out.println(list);
        for (int rIndex = 1; rIndex <= list.size(); rIndex++) {   //?????????
            System.out.println("rIndex: " + rIndex);
            Row row = sheet.createRow(rIndex);
            Cell cellNo = row.createCell(0);
            cellNo.setCellType(CellType.STRING);
            cellNo.setCellValue("031902000");
            Cell cellName = row.createCell(1);
            cellName.setCellValue(list.get(rIndex - 1).getRealName());
            Cell cellPoints = row.createCell(2);
            cellPoints.setCellValue(list.get(rIndex - 1).getPoints());
            //????????????????????????????????????(??????toString???????????????????????????????????????????????????????????????????????????0?????????????????????????????????
        }       //??????????????????????????????????????????
        sheet.setColumnWidth(0,10*256);
        Sheet sheet2 = workbook.createSheet("??????????????????");
        int firstRowIndex = 4;   //??????0-4?????????????????????
        Row infoRow=sheet2.createRow(0);
        Cell infoCell=infoRow.createCell(0);
        Font font = workbook.createFont();
        font.setColor(Font.COLOR_RED);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setFont(font);
        infoCell.setCellValue("????????????????????????????????????????????????????????????-1~1??????????????????????????????????????????????????????3???????????????????????????????????????????????????????????????????????????");
        infoCell.setCellStyle(cellStyle);
        CellRangeAddress region = new CellRangeAddress(0, 3, 0, 4);
        sheet2.addMergedRegion(region);
        String[] titles = {"??????", "??????", "????????????", "??????", "?????????"};
        List<StuPointsDetail> DetailList = stuPointsDetailService.classDetails(classID);
        Row firstRow2 = sheet2.createRow(firstRowIndex);
        for (int i = 0; i < titles.length; i++) {
            Cell cell = firstRow2.createCell(i);
            cell.setCellValue(titles[i]);
        }
        for (int rIndex = 1; rIndex <= DetailList.size(); rIndex++) {
            Row row = sheet2.createRow(firstRowIndex+rIndex);
            Cell cellNo = row.createCell(0);
            cellNo.setCellType(CellType.STRING);
            cellNo.setCellValue(DetailList.get(rIndex - 1).getTarget());
            Cell cellType = row.createCell(1);
            cellType.setCellValue(DetailList.get(rIndex - 1).getReason());
            Cell cellPoints = row.createCell(2);
            cellPoints.setCellValue(DetailList.get(rIndex - 1).getPoints());
            Cell cellTime = row.createCell(3);
            cellTime.setCellValue(DetailList.get(rIndex - 1).getTime().toString());
            Cell cellOperator = row.createCell(4);
            cellOperator.setCellValue(DetailList.get(rIndex - 1).getOperator());
            //????????????????????????????????????(??????toString???????????????????????????????????????????????????????????????????????????0?????????????????????????????????
        }       //??????????????????????????????????????????
//        sheet2.setColumnWidth(1,20*256);//?????????????????????excel????????????????????????
//        sheet2.setColumnWidth(3,20*256);
       // sheet2.autoSizeColumn(1);
        sheet2.setColumnWidth(1,15*256);
        sheet2.autoSizeColumn(3);
        workbook.write(new FileOutputStream(file2));
        workbook.close();
    }

    @Test
    public void testPageInfo() {
        String json = "{\n" +
                "    \"endRow\": 2,\n" +
                "    \"hasNextPage\": true,\n" +
                "    \"hasPreviousPage\": true,\n" +
                "    \"isFirstPage\": false,\n" +
                "    \"isLastPage\": false,\n" +
                "    \"list\": [\n" +
                "        {\n" +
                "            \"body\": \"???????????????????????????\",\n" +
                "            \"classID\": \"CIRD9F\",\n" +
                "            \"confirm\": false,\n" +
                "            \"deadLine\": null,\n" +
                "            \"nid\": 2,\n" +
                "            \"publishedTime\": \"2021-08-05 01:39:58\",\n" +
                "            \"publisher\": \"teacher1\",\n" +
                "            \"title\": \"??????????????????????????????\",\n" +
                "            \"type\": \"\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"navigateFirstPage\": 1,\n" +
                "    \"navigateLastPage\": 6,\n" +
                "    \"navigatePages\": 8,\n" +
                "    \"navigatepageNums\": [\n" +
                "        1,\n" +
                "        2,\n" +
                "        3,\n" +
                "        4,\n" +
                "        5,\n" +
                "        6\n" +
                "    ],\n" +
                "    \"nextPage\": 3,\n" +
                "    \"pageNum\": 2,\n" +
                "    \"pageSize\": 1,\n" +
                "    \"pages\": 6,\n" +
                "    \"prePage\": 1,\n" +
                "    \"size\": 1,\n" +
                "    \"startRow\": 2,\n" +
                "    \"total\": 6\n" +
                "}";
        PageInfo pageInfo = JSON.parseObject(json, PageInfo.class);
        System.out.println(pageInfo);
    }

    @Test
    public void testSimpleStu() {
        String classID = "CIRD9F";
//        List<SimpleStu> list =.getSimpleStuList(classID);
//        System.out.println(list);
    }

    @Test
    public void testClassMemQuery() {
        JSONObject MyObject = permissionService.getClassListSortByPermission("CIRD9F");
        System.out.println(MyObject);
    }

    @Test
    public void testRedisError() {
        redisUtil.zAdd("class:" + "CIRD9F" + ":" + "type:" + "VoteSelections" + ":" + "vid:" + 11,
                "??????1", 0);
    }


    public void test() throws Exception {
////        TextScanRequest textScanRequest = new TextScanRequest();
////        textScanRequest.setAcceptFormat(FormatType.JSON);//??????API???????????????textScanRequest.setHttpContentType (FormatType.JSON) ;
////        textScanRequest.setMethod(com.aliyuncs. http.MethodType.POST);//?????????????????????textScanRequest.setEncoding("UTF-8") ;
////        textScanRequest.setRegionId(" cn-shanghai");
//        List<Map<String, Object>> tasks = new ArrayList<>();
//        Map<String, Object> task1 = new LinkedHashMap<String, Object>();
//        task1.put("dataId", UUID.randomUUID().toString());
///**
// ?????????????????????,???????????????10000????????????*/
//        task1.put("content", "woc???????????????");
//        tasks.add(task1);
//        JSONObject data = new JSONObject();
//
//        /*??????????????????????????????????????????antispam ???**/
//        data.put("scenes", Arrays.asList("antispam"));
//        data.put("tasks", tasks);
//        System.out.println(JSON.toJSONString(data, true));
//        textScanRequest.setHttpContent(data.toJSONString().getBytes("UTF-8"), "UTF-8", FormatType.JSON);
//        //??????????????????????????????
////        textScanRequest.setConnectTimeout (3000);
////        textScanRequest.setReadTimeout (6000) ;
//        try {
//            HttpResponse httpResponse = iAcsClient.doAction(textScanRequest);
//            if (httpResponse.isSuccess()) {
//                JSONObject scrResponse = JSON.parseObject(new String(httpResponse.getHttpContent(), "UTF-8"));
//                System.out.println(JSON.toJSONString(scrResponse, true));
//
//                if (200 == scrResponse.getInteger("code")) {
//                    JSONArray taskResults = scrResponse.getJSONArray("data");
//                    for (Object taskResult : taskResults) {
//                        if (200 == ((JSONObject) taskResult).getInteger("code")) {
//                            JSONArray sceneResults = ((JSONObject) taskResult).getJSONArray("results");
//                            for (Object sceneResult :
//                                    sceneResults) {
//                                String scene = ((JSONObject) sceneResult).getString("scene");
//                                String suggestion = ((JSONObject) sceneResult).getString("suggestion");
//                                // ??????scene???suggetion??????????????????
//                                // suggestion == pass????????????????????????suggestion == block????????????????????????????????????1abe1??????:
//                                System.out.println("args = [" + scene + "]");
//                                System.out.println("args = [" + suggestion + "]");
//                            }
//                        } else {
//                            System.out.println("task process fail:" + ((JSONObject) taskResult).getInteger(" code"));
//                        }
//                    }
//                } else {
//                    System.out.println("detect not success. code:" + scrResponse.getInteger(" code"));
//                }
//            } else {
//                System.out.println("response not success. status:" + httpResponse.getStatus());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
