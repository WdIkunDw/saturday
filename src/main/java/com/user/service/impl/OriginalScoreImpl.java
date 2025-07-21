package com.sgd.nyzx.achivement.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sgd.common.base.PageRes;
import com.sgd.common.utils.F;
import com.sgd.nyzx.achivement.excel.utils.ExcelAnalysisUtils;
import com.sgd.nyzx.achivement.excel.utils.FractionalSegmentIncrement;
import com.sgd.nyzx.achivement.mapper.OriginalScoreMapper;
import com.sgd.nyzx.achivement.po.ClassScore;
import com.sgd.nyzx.achivement.po.FractionalSectionPo;
import com.sgd.nyzx.achivement.po.OriginalScoreDTO;
import com.sgd.nyzx.achivement.po.OriginalScorePo;
import com.sgd.nyzx.achivement.po.req.*;
import com.sgd.nyzx.achivement.po.vo.*;
import com.sgd.nyzx.achivement.service.OriginalScoreDTOService;
import com.sgd.nyzx.achivement.service.OriginalScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.sgd.nyzx.achivement.excel.utils.ExcelExportUtils.export;

@Service
public class OriginalScoreImpl extends ServiceImpl<OriginalScoreMapper, OriginalScorePo> implements OriginalScoreService {

    @Resource
    private OriginalScoreMapper originalScoreMapper;

    @Resource
    OriginalScoreService service;

    @Resource
    FractionalSegmentIncrement fsi;

    @Autowired
    private OriginalScoreDTOService scoreDTOService;

    @Resource
    private OriginalScoreService originalScoreService;

    //查询单人
    @Override
    public PageRes<OriginalScoreDTO> selectName(OriginalScoreReq req, String name){
        Page<OriginalScoreDTO> page = scoreDTOService.lambdaQuery()
                .like(OriginalScoreDTO::getOriginalName, name)
                .page(req.toPage());
        PageRes<OriginalScoreDTO> pageRes = PageRes.toPageRes(page, OriginalScoreDTO.class);
        return pageRes;
    }

    //原成绩导入
    @Override//解析 excel 并存入数据库
    public List<OriginalScorePo> addAnalysis(InputStream excelFileName){
//        RequestTokenHolder.saveToken(token); // 存储token到RequestTokenHolder中
        List<OriginalScorePo> analysis = ExcelAnalysisUtils.analysis(excelFileName);
        Assert.isTrue(CollUtil.isNotEmpty(analysis),"请求体不能为空");
        saveBatch(analysis);//存入数据库
//        RequestTokenHolder.removeToken(); // 在导入完成后移除token
        return analysis;
    }

    //分页
    @Override
    public PageRes<OriginalScoreDTO> select(OriginalScoreReq req) {
        Page<OriginalScoreDTO> page = scoreDTOService.lambdaQuery()
                .page(req.toPage());
        PageRes<OriginalScoreDTO> pageRes = PageRes.toPageRes(page, OriginalScoreDTO.class);
        return pageRes;
    }

    //单科成绩排名查询
    @Override
    public PageRes<FractionalSectionPo> selectFractionalSection(LessonPlanQueryReq req, String subject,int fractionalSection) {
        List<OriginalScorePo> scorePos = new ArrayList<>();
        switch (subject){
            case "subject_chinese":
            case "subject_math":
            case "subject_english":
                scorePos = originalScoreMapper.getScoreStatistics
                        (fsi.generateIncrementSequence(fractionalSection,0,150),subject);
                break;
            case "subject_physics":
                scorePos= originalScoreMapper.getScoreStatistics
                        (fsi.generateIncrementSequence(fractionalSection,0,80),subject);
                break;
            case "subject_chemical":
                scorePos= originalScoreMapper.getScoreStatistics
                        (fsi.generateIncrementSequence(fractionalSection,0,70),subject);
                break;
            case "subject_politics":
            case "subject_history":
            case "subject_physical_education":
                scorePos= originalScoreMapper.getScoreStatistics
                        (fsi.generateIncrementSequence(fractionalSection,0,50),subject);
                break;
        }
        List<FractionalSectionPo> sectionPos = scorePos.stream().map(value -> {
            FractionalSectionPo po = new FractionalSectionPo();
            po.setScoreRange(value.getScoreRange());//分数段
            po.setFractionalClass(value.getOriginalClass());//班级
            po.setCount(value.getCount());//人数
            //po.setTotalNumberOfGrades();//总人数
            return po;
        }).collect(Collectors.toList());
        Page<FractionalSectionPo> page = new Page<>(req.getPageNum(), req.getPageSize());
        page.setRecords(sectionPos);
        PageRes<FractionalSectionPo> pageRes = PageRes.toPageRes(page, FractionalSectionPo.class);
        return pageRes;
    }

    //个人成绩排名
    @Override
    public PageRes<SingleSubjectVo> selectIndividual(LessonIndividualSelectReq req){
        List<SingleSubjectVo> singleSubjectVoPageRes = originalScoreMapper.selectIndividual();
        List<SingleSubjectVo> vos = singleSubjectVoPageRes.stream().map(value ->{
            SingleSubjectVo dto = new SingleSubjectVo();
            dto.setOriginalName(value.getOriginalName());//姓名
            dto.setOriginalClass(value.getOriginalClass());//班级
            dto.setClassRanking(value.getClassRanking());//班级排名
            dto.setGradeRanking(value.getGradeRanking());
            dto.setTotalScore(value.getTotalScore());//总成绩
            dto.setSubjectChinese(value.getSubjectChinese());//语文
            dto.setChineseRanking(value.getChineseRanking());//语文排名
            dto.setSubjectMath(value.getSubjectMath());
            dto.setMathRanking(value.getMathRanking());
            dto.setSubjectEnglish(value.getSubjectEnglish());
            dto.setEnglishRanking(value.getEnglishRanking());
            dto.setSubjectPhysics(value.getSubjectPhysics());
            dto.setPhysicsRanking(value.getPhysicsRanking());
            dto.setSubjectChemical(value.getSubjectChemical());
            dto.setChemicalRanking(value.getChemicalRanking());
            dto.setSubjectPolitics(value.getSubjectPolitics());
            dto.setPoliticsRanking(value.getPoliticsRanking());
            dto.setSubjectHistory(value.getSubjectHistory());
            dto.setHistoryRanking(value.getHistoryRanking());
            dto.setSubjectPhysicalEducation(value.getSubjectPhysicalEducation());
            dto.setPhysicalEducationRanking(value.getPhysicalEducationRanking());
            return dto;
        }).collect(Collectors.toList());

        // 计算起始索引和结束索引
        int startIndex = (req.getPageNum() - 1) * req.getPageSize();
        int endIndex = Math.min(startIndex + req.getPageSize(), vos.size());
        // 获取分页数据
        List<SingleSubjectVo> infoVos = vos.subList(startIndex, endIndex);

        //逻辑分页
        PageRes<SingleSubjectVo> pageRes = new PageRes<>();
        pageRes.setTotal(vos.size());
        List<SingleSubjectVo> vos1 = F.listPage(vos, req.getPageNum(), req.getPageSize());
        pageRes.setRecords(vos1);
        return pageRes;
    }

    //分数段
    @Override
    public PageRes<DataInfoVo> selectFractionalSectionService(LessonSingleSubjectSegmentationReq req, String subject, int fractionalSection) {
        List<Integer> list = null;
        String subjectS = null;
        switch (subject){
            case "语文":
                subjectS ="subject_chinese";
                break;
            case "数学":
                subjectS ="subject_math";
                break;
            case "英语":
                subjectS ="subject_english";
                break;
            case "物理":
                subjectS ="subject_physics";
                break;
            case "化学":
                subjectS ="subject_chemical";
                break;
            case "政治":
                subjectS ="subject_politics";
                break;
            case "历史":
                subjectS ="subject_history";
                break;
            case "体育":
                subjectS ="subject_physical_education";
        }
        switch (subjectS){
            case "subject_chinese":
            case "subject_math":
            case "subject_english":
                list = fsi.generateIncrementSequence(fractionalSection, 0, 150);// 分数段
                break;
            case "subject_physics":
                list = fsi.generateIncrementSequence(fractionalSection, 0, 80);// 分数段
                break;
            case "subject_chemical":
                list = fsi.generateIncrementSequence(fractionalSection, 0, 70);// 分数段
                break;
            case "subject_politics":
            case "subject_history":
            case "subject_physical_education":
                list = fsi.generateIncrementSequence(fractionalSection, 0, 50);// 分数段
                break;
        }
        Map<String, List<Double>> allClassScore = new HashMap<>(); //全部班级学生的分数
        Map<String, Integer> actualTestNumber = new HashMap<>();//实考数
        Map<String, Double> highestScore = new HashMap<>();//最高分
        Map<String, Double> lowestScore = new HashMap<>();//最低分
        Map<String, List<Double>> allClassStandardDeviation = new HashMap<>(); //某个班级的全部标准差分数
        Map<String,List<Double>> variationNumber = new HashMap<>();//变异数
        //总均分
        Collections.sort(list, Collections.reverseOrder());
        //分数段
        List<StudentInfoVo> vos = list.stream().map(value -> {
            StudentInfoVo vo = new StudentInfoVo();
            vo.setScore(value);
            return vo;
        }).collect(Collectors.toList());
        //获取某单科全部成绩
        List<OriginalScorePo> chineseList = originalScoreMapper.selectChinese(subjectS);

        StudentInfoVo vo1 = new StudentInfoVo();

        //循环分数段
        for (StudentInfoVo vo : vos) {
            //循环某单科的全部成绩
            for (OriginalScorePo student : chineseList) {
                //当前学生的某科目成绩
                Double score = Double.valueOf(student.getSubjectChinese());
                //判断allClassScore是否有这个班级
                if (allClassScore.containsKey(student.getOriginalClass())){
                    List<Double> doubles = allClassScore.get(student.getOriginalClass());
                    doubles.add(score);
                    allClassScore.put(student.getOriginalClass(), doubles);
                }else{
                    List<Double> value = new ArrayList<>();
                    value.add(score);
                    allClassScore.put(student.getOriginalClass(), value);
                }
                //判断当前学生的分数是否归纳与这个分数段
                if (score >= vo.getScore()) {
                    //班级人数
                    Map<String, Integer> data = vo.getData();

                    //判断map集合中是否有学生的班级
                    if (data.containsKey(student.getOriginalClass())) {
                        //有学生的班级直接人数+1
                        Integer count = data.get(student.getOriginalClass());
                        data.put(student.getOriginalClass(), ++count);
                        //这个班的人数
                        actualTestNumber.put(student.getOriginalClass(), (actualTestNumber.get(student.getOriginalClass()) + 1));

                        /** (求某班级的最高分)判断这个人是否大于集合中的分数 */
                        Double max = highestScore.get(student.getOriginalClass());
                        if (score > max){
                            highestScore.put(student.getOriginalClass(), score);
                        }
                        /** (求某班级的最低分)判断这个人是否小于于集合中的分数 */
                        Double min = lowestScore.get(student.getOriginalClass());
                        if (score < min){
                            lowestScore.put(student.getOriginalClass(), score);
                        }

                        //这个班的标准差分数
                        List<Double> doubles = allClassStandardDeviation.get(student.getOriginalClass());
                        doubles.add(score);
                        allClassStandardDeviation.put(student.getOriginalClass(), doubles);
                        //这个班变异数
                        List<Double> doublesV = variationNumber.get(student.getOriginalClass());
                        doublesV.add(score);
                        variationNumber.put(student.getOriginalClass(), doublesV);
                    }else{
                        data.put(student.getOriginalClass(), 1);
                        //初始化班级人数
                        actualTestNumber.put(student.getOriginalClass(), 1);
                        //这个班的最高分
                        highestScore.put(student.getOriginalClass(), score);
                        //这个班的最低分
                        lowestScore.put(student.getOriginalClass(), score);
                        //这个班的标准差分数
                        List<Double> scoreDifference = new ArrayList<>();
                        scoreDifference.add(score);
                        allClassStandardDeviation.put(student.getOriginalClass(), scoreDifference);
                        //变异数
                        List<Double> VariationNumber = new ArrayList<>();
                        VariationNumber.add(score);
                        variationNumber.put(student.getOriginalClass(), VariationNumber);
                    }
                    vo.setData(data);
                }
            }
        }
        //总均分
        Map<String, Double> totalAverageScore = new HashMap<>();

        for (StudentInfoVo vo : vos) {
            Map<String, Integer> data = vo.getData();
            int sum = 0;
            for (String classNo : data.keySet()) {
                vo.setSum(sum+=data.get(classNo));
            }
        }
        Integer studentSum = 0;
        for (String countKey : actualTestNumber.keySet()) {
            studentSum += actualTestNumber.get(countKey);
        }

        //每个班总人数
        actualTestNumber.put("studentSum", studentSum);
        Double asDouble = highestScore.values().stream().mapToDouble(Double::doubleValue).max().getAsDouble();
        Double roundedNumber = Math.round(asDouble * 100) / 100.0;
        highestScore.put("maxAvg", roundedNumber);
        Double min= lowestScore.values().stream().mapToDouble(Double::doubleValue).min().getAsDouble();
        Double minNumber = Math.round(min * 100) / 100.0;
        lowestScore.put("minAvg", minNumber);


        List<DataInfoVo> voList = new ArrayList<>();

        DataInfoVo dataInfoVo = new DataInfoVo();

        dataInfoVo.setActualTestNumber(actualTestNumber);
        dataInfoVo.setHighestScore(highestScore);
        dataInfoVo.setLowestScore(lowestScore);

        Set<String> strings = allClassScore.keySet();
        for (String classNo : strings) {
            //班级的总分
            Double scoreSum = 0d;
            List<Double> doubles = allClassScore.get(classNo);
            for (Double score : doubles) {
                scoreSum += score;
            }
            //得到平均分（总分 / 人数 得到平均分）
            Double avg = scoreSum / doubles.size();
            // 创建一个DecimalFormat对象来格式化数字，保留小数点后两位
            DecimalFormat df = new DecimalFormat("0.00");
            // 使用format方法格式化avg
            String avgFormatted = df.format(avg);
            // 如果需要，可以将格式化后的字符串转换回Double类型
            Double avgRounded = Double.parseDouble(avgFormatted);
            totalAverageScore.put(classNo, avgRounded);
        }

        Double aDoubles =0d;//单科年级的总分
        for (OriginalScorePo scorePo : chineseList) {aDoubles+= scorePo.getSubjectChinese();}
        Double totalAverage = aDoubles / studentSum;//这里就是获取到年级总均分
        double truncatedAverage = (double) Math.round(totalAverage * 100) / 100;
        totalAverageScore.put("allClassAvg",truncatedAverage);
        dataInfoVo.setTotalAverageScore(totalAverageScore);
        for (String classNo : allClassStandardDeviation.keySet()) {
            Double[] doubles = allClassStandardDeviation.get(classNo).toArray(new Double[0]);
            double bzc = Sample_STD_dev(doubles);
            Map<String, Double> deviation = dataInfoVo.getStandardDeviation();
            if (Double.isNaN(bzc)){
                bzc = 0.0;
                deviation.put(classNo, bzc);
            } else {
                // 使用 DecimalFormat 格式化保留两位小数
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                String formattedBzc = decimalFormat.format(bzc);
                double roundedBzc = Double.parseDouble(formattedBzc);
                deviation.put(classNo, roundedBzc);
            }
            dataInfoVo.setStandardDeviation(deviation);
        }
        //每个班的变异数
        for (String classNo : variationNumber.keySet()) {
            Double[] doubles = variationNumber.get(classNo).toArray(new Double[0]);
            double bzc = calculateVariance(doubles);
            Map<String, Double> deviation = dataInfoVo.getVariationNumber();
            if (Double.isNaN(bzc)){
                deviation.put(classNo, bzc);
            }else {
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                String formattedBzc = decimalFormat.format(bzc);
                double roundedBzc = Double.parseDouble(formattedBzc);
                deviation.put(classNo, roundedBzc);
            }
            dataInfoVo.setVariationNumber(deviation);
        }
        voList.add(dataInfoVo);

        // 计算起始索引和结束索引
        int startIndex = (req.getPageNum() - 1) * req.getPageSize();
        int endIndex = Math.min(startIndex + req.getPageSize(), vos.size());

        PageRes<DataInfoVo> pageRes=new  PageRes<>();
        pageRes.setTotal(vos.size());

        List<DataInfoVo> vos1 = F.listPage(voList, req.getPageNum(), req.getPageSize());
        pageRes.setRecords(vos1);

        // 获取分页数据
        List<StudentInfoVo> infoVos = vos.subList(startIndex, endIndex);
        dataInfoVo.setVos(infoVos);
        return pageRes;

    }
    //个人排名查询
    @Override
    public PageRes<SingleSubjectVo> individualPerformanceSelect(LessonIndividualSelectReq req,String name) {
        List<SingleSubjectVo> singleSubjectVos = originalScoreMapper.individualPerformanceSelect(name);
        List<SingleSubjectVo> vos = singleSubjectVos.stream().map(value ->{
            SingleSubjectVo dto = new SingleSubjectVo();
            dto.setOriginalName(value.getOriginalName());//姓名
            dto.setOriginalClass(value.getOriginalClass());//班级
            dto.setClassRanking(value.getClassRanking());//班级排名
            dto.setGradeRanking(value.getGradeRanking());
            dto.setTotalScore(value.getTotalScore());//总成绩
            dto.setSubjectChinese(value.getSubjectChinese());//语文
            dto.setChineseRanking(value.getChineseRanking());//语文排名
            dto.setSubjectMath(value.getSubjectMath());
            dto.setMathRanking(value.getMathRanking());
            dto.setSubjectEnglish(value.getSubjectEnglish());
            dto.setEnglishRanking(value.getEnglishRanking());
            dto.setSubjectPhysics(value.getSubjectPhysics());
            dto.setPhysicsRanking(value.getPhysicsRanking());
            dto.setSubjectChemical(value.getSubjectChemical());
            dto.setChemicalRanking(value.getChemicalRanking());
            dto.setSubjectPolitics(value.getSubjectPolitics());
            dto.setPoliticsRanking(value.getPoliticsRanking());
            dto.setSubjectHistory(value.getSubjectHistory());
            dto.setHistoryRanking(value.getHistoryRanking());
            dto.setSubjectPhysicalEducation(value.getSubjectPhysicalEducation());
            dto.setPhysicalEducationRanking(value.getPhysicalEducationRanking());
            return dto;
        }).collect(Collectors.toList());

        // 计算起始索引和结束索引
        int startIndex = (req.getPageNum() - 1) * req.getPageSize();
        int endIndex = Math.min(startIndex + req.getPageSize(), vos.size());
        // 获取分页数据
        List<SingleSubjectVo> infoVos = vos.subList(startIndex, endIndex);

        //逻辑分页
        PageRes<SingleSubjectVo> pageRes=new  PageRes<>();
        pageRes.setTotal(vos.size());
        List<SingleSubjectVo> vos1 = F.listPage(vos, req.getPageNum(), req.getPageSize());
        pageRes.setRecords(vos1);
        return pageRes;
    }
    //
    @Override
    public AllScoreAmountToVo selectAllScoreAmountTo(Integer number) {
        //全部学生
        /** 全年级的学生 全部 分数 */
        List<LessonRankingReq> allStudent = originalScoreMapper.selectRanking();//获取num排名

        //总分 语文 数学 ... 全部分数集合
        /** 提取出每科分数的集合 （语文，数学，英语） */
        List<ClassScore> totalScore = allStudent.stream().map(value -> new ClassScore(value.getOriginalClass(), value.getTotalScore())).collect(Collectors.toList());
        List<ClassScore> chinese = allStudent.stream().map(value -> new ClassScore(value.getOriginalClass(),value.getSubjectChinese())).collect(Collectors.toList());
        List<ClassScore> math = allStudent.stream().map(value -> new ClassScore(value.getOriginalClass(), value.getSubjectMath())).collect(Collectors.toList());
        List<ClassScore> english = allStudent.stream().map(value -> new ClassScore(value.getOriginalClass(), value.getSubjectEnglish())).collect(Collectors.toList());
        List<ClassScore> physics = allStudent.stream().map(value -> new ClassScore(value.getOriginalClass(), value.getSubjectPhysics())).collect(Collectors.toList());
        List<ClassScore> chemical = allStudent.stream().map(value -> new ClassScore(value.getOriginalClass(), value.getSubjectChemical())).collect(Collectors.toList());
        List<ClassScore> politics = allStudent.stream().map(value -> new ClassScore(value.getOriginalClass(), value.getSubjectPolitics())).collect(Collectors.toList());
        List<ClassScore> history = allStudent.stream().map(value -> new ClassScore(value.getOriginalClass(), value.getSubjectHistory())).collect(Collectors.toList());
        List<ClassScore> physicalEducation = allStudent.stream().map(value -> new ClassScore(value.getOriginalClass(), value.getSubjectPhysicalEducation())).collect(Collectors.toList());

        /** 全年级的学生 全部 分数（根据分数降序排序） */
        Collections.sort(totalScore, (score1, score2) -> Double.compare(score2.getScore(), score1.getScore()));
        Collections.sort(chinese, (score1, score2) -> Double.compare(score2.getScore(), score1.getScore()));
        Collections.sort(math, (score1, score2) -> Double.compare(score2.getScore(), score1.getScore()));
        Collections.sort(english, (score1, score2) -> Double.compare(score2.getScore(), score1.getScore()));
        Collections.sort(physics, (score1, score2) -> Double.compare(score2.getScore(), score1.getScore()));
        Collections.sort(chemical, (score1, score2) -> Double.compare(score2.getScore(), score1.getScore()));
        Collections.sort(politics, (score1, score2) -> Double.compare(score2.getScore(), score1.getScore()));
        Collections.sort(history, (score1, score2) -> Double.compare(score2.getScore(), score1.getScore()));
        Collections.sort(physicalEducation, (score1, score2) -> Double.compare(score2.getScore(), score1.getScore()));

        /** 截取集合，只保留前 number 条数据 */
        List<ClassScore> totalScoreWhere = totalScore.subList(0, number);
        List<ClassScore> chineseWhere = chinese.subList(0, number);
        List<ClassScore> mathWhere = math.subList(0, number);
        List<ClassScore> englishWhere = english.subList(0, number);
        List<ClassScore> physicsWhere = physics.subList(0, number);
        List<ClassScore> chemicalWhere = chemical.subList(0, number);
        List<ClassScore> politicsWhere = politics.subList(0, number);
        List<ClassScore> historyWhere = history.subList(0, number);
        List<ClassScore> educationWhere = physicalEducation.subList(0, number);

        /** 得到前 number 条数据的最后一条（最后一条代表最低分） */
        ClassScore totalScoreMin = totalScoreWhere.get(totalScoreWhere.size() - 1);
        ClassScore chineseMin = chineseWhere.get(chineseWhere.size() - 1);
        ClassScore mathMin = mathWhere.get(mathWhere.size() - 1);
        ClassScore englishMin = englishWhere.get(englishWhere.size() - 1);
        ClassScore physicsMin = physicsWhere.get(physicsWhere.size() - 1);
        ClassScore chemicalMin = chemicalWhere.get(chemicalWhere.size() - 1);
        ClassScore politicsMin = politicsWhere.get(politicsWhere.size() - 1);
        ClassScore historyMin = historyWhere.get(historyWhere.size() - 1);
        ClassScore educationMin = educationWhere.get(educationWhere.size() - 1);

        Map<String, Double> minScore = new HashMap<>();
        minScore.put("totalScore", totalScoreMin.getScore());
        minScore.put("chinese", chineseMin.getScore());
        minScore.put("math", mathMin.getScore());
        minScore.put("english", englishMin.getScore());
        minScore.put("physics", physicsMin.getScore());
        minScore.put("chemical", chemicalMin.getScore());
        minScore.put("politics", politicsMin.getScore());
        minScore.put("history", historyMin.getScore());
        minScore.put("education", educationMin.getScore());

        /** 统计 */
        // key是科目，key是班级， 人数
        Map<String, Map<String, Integer>> statistics = new HashMap<>();
        //初始化
        statistics.put("totalScore", new HashMap<>());
        statistics.put("chinese", new HashMap<>());
        statistics.put("math", new HashMap<>());
        statistics.put("english", new HashMap<>());
        statistics.put("physics", new HashMap<>());
        statistics.put("chemical", new HashMap<>());
        statistics.put("politics", new HashMap<>());
        statistics.put("history", new HashMap<>());
        statistics.put("education", new HashMap<>());

        /** 合计 */
        // key是科目，value是合格的人数
        Map<String, Integer> amountTo = new HashMap<>();
        //初始化每课的人数（key是科目，value是人数）
        amountTo.put("totalScore", 0);//总分
        amountTo.put("chinese", 0);//语文
        amountTo.put("math", 0);//数学
        amountTo.put("english", 0);//英语
        amountTo.put("physics", 0);
        amountTo.put("chemical", 0);
        amountTo.put("politics", 0);
        amountTo.put("history", 0);
        amountTo.put("education", 0);
        //初始化班级人数
        for (LessonRankingReq student : allStudent) {
            String originalClass = student.getOriginalClass();
            if (!statistics.get("totalScore").containsKey(originalClass)) {
                statistics.get("totalScore").put(originalClass, 0);
            }
            if (!statistics.get("chinese").containsKey(originalClass)) {
                statistics.get("chinese").put(originalClass, 0);
            }
            if (!statistics.get("math").containsKey(originalClass)) {
                statistics.get("math").put(originalClass, 0);
            }
            if (!statistics.get("english").containsKey(originalClass)) {
                statistics.get("english").put(originalClass, 0);
            }
            if (!statistics.get("physics").containsKey(originalClass)) {
                statistics.get("physics").put(originalClass, 0);
            }
            if (!statistics.get("chemical").containsKey(originalClass)) {
                statistics.get("chemical").put(originalClass, 0);
            }
            if (!statistics.get("politics").containsKey(originalClass)) {
                statistics.get("politics").put(originalClass, 0);
            }
            if (!statistics.get("history").containsKey(originalClass)) {
                statistics.get("history").put(originalClass, 0);
            }
            if (!statistics.get("education").containsKey(originalClass)) {
                statistics.get("education").put(originalClass, 0);
            }
        }

        for (LessonRankingReq student : allStudent) {
            //判断当前学生的分数是否达到最低分（分数段）
            if (student.getTotalScore() >= totalScoreMin.getScore()){
                Map<String, Integer> map = statistics.get("totalScore");
                //判断是否存在这班级
                if (map.containsKey(student.getOriginalClass())) {
                    map.put(student.getOriginalClass(), map.get(student.getOriginalClass()) + 1);
                }else {
                    map.put(student.getOriginalClass(), 1);
                }
                amountTo.put("totalScore", (amountTo.get("totalScore") + 1));
                statistics.put("totalScore", map);
            }

            //判断当前学生的分数是否达到最低分（分数段）
            if (student.getSubjectChinese() >= chineseMin.getScore()){
                Map<String, Integer> map = statistics.get("chinese");
                //判断是否存在这班级
                if (map.containsKey(student.getOriginalClass())) {
                    map.put(student.getOriginalClass(), map.get(student.getOriginalClass()) + 1);
                }else {
                    map.put(student.getOriginalClass(), 1);
                }
                amountTo.put("chinese", (amountTo.get("chinese") + 1));
                statistics.put("chinese", map);
            }
            //判断当前学生的分数是否达到最低分（分数段）
            if (student.getSubjectMath() >= mathMin.getScore()){
                Map<String, Integer> map = statistics.get("math");
                //判断是否存在这班级
                if (map.containsKey(student.getOriginalClass())) {
                    map.put(student.getOriginalClass(), map.get(student.getOriginalClass()) + 1);
                }else {
                    map.put(student.getOriginalClass(), 1);
                }
                amountTo.put("math", (amountTo.get("math") + 1));
                statistics.put("math", map);
            }
            //判断当前学生的分数是否达到最低分（分数段）
            if (student.getSubjectEnglish() >= englishMin.getScore()){
                Map<String, Integer> map = statistics.get("english");
                //判断是否存在这班级
                if (map.containsKey(student.getOriginalClass())) {
                    map.put(student.getOriginalClass(), map.get(student.getOriginalClass()) + 1);
                }else {
                    map.put(student.getOriginalClass(), 1);
                }
                amountTo.put("english", (amountTo.get("english") + 1));
                statistics.put("english", map);
            }
            //判断当前学生的分数是否达到最低分（分数段）
            if (student.getSubjectPhysics() >= physicsMin.getScore()){
                Map<String, Integer> map = statistics.get("physics");
                //判断是否存在这班级
                if (map.containsKey(student.getOriginalClass())) {
                    map.put(student.getOriginalClass(), map.get(student.getOriginalClass()) + 1);
                }else {
                    map.put(student.getOriginalClass(), 1);
                }
                amountTo.put("physics", (amountTo.get("physics") + 1));
                statistics.put("physics", map);
            }
            //判断当前学生的分数是否达到最低分（分数段）
            if (student.getSubjectChemical() >= chemicalMin.getScore()){
                Map<String, Integer> map = statistics.get("chemical");
                //判断是否存在这班级
                if (map.containsKey(student.getOriginalClass())) {
                    map.put(student.getOriginalClass(), map.get(student.getOriginalClass()) + 1);
                }else {
                    map.put(student.getOriginalClass(), 1);
                }
                amountTo.put("chemical", (amountTo.get("chemical") + 1));
                statistics.put("chemical", map);
            }
            //判断当前学生的分数是否达到最低分（分数段）
            if (student.getSubjectPolitics() >= politicsMin.getScore()){
                Map<String, Integer> map = statistics.get("politics");
                //判断是否存在这班级
                if (map.containsKey(student.getOriginalClass())) {
                    map.put(student.getOriginalClass(), map.get(student.getOriginalClass()) + 1);
                }else {
                    map.put(student.getOriginalClass(), 1);
                }
                amountTo.put("politics", (amountTo.get("politics") + 1));
                statistics.put("politics", map);
            }
            //判断当前学生的分数是否达到最低分（分数段）
            if (student.getSubjectHistory() >= historyMin.getScore()){
                Map<String, Integer> map = statistics.get("history");
                //判断是否存在这班级
                if (map.containsKey(student.getOriginalClass())) {
                    map.put(student.getOriginalClass(), map.get(student.getOriginalClass()) + 1);
                }else {
                    map.put(student.getOriginalClass(), 1);
                }
                amountTo.put("history", (amountTo.get("history") + 1));
                statistics.put("history", map);
            }
            //判断当前学生的分数是否达到最低分（分数段）
            if (student.getSubjectPhysicalEducation() >= educationMin.getScore()){
                Map<String, Integer> map = statistics.get("education");
                //判断是否存在这班级
                if (map.containsKey(student.getOriginalClass())) {
                    map.put(student.getOriginalClass(), map.get(student.getOriginalClass()) + 1);
                }else {
                    map.put(student.getOriginalClass(), 1);
                }
                amountTo.put("education", (amountTo.get("education") + 1));

                statistics.put("education", map);

            }
        }

        AllScoreAmountToVo vo = new AllScoreAmountToVo();
        vo.setAmountTo(amountTo);
        vo.setStatistics(statistics);
        vo.setMinScore(minScore);
        return vo;
    }

    @Override
    public Boolean deleteGrade() {
        return originalScoreMapper.deleteGrade();
    }

    @Override
    public void exportExcel(HttpServletResponse response, SingleSubjectReq req) {
        List<List<DataInfoVo>> records1 = new ArrayList<>();
        List<List<StudentInfoVo>> studentInfoVo1 = new ArrayList<>();
        for (String string : req.getDiscipline()) {
//            PageRes<DataInfoVo> voPageRes = originalScoreService.selectFractionalSectionService(req, string, req.getFractionalSection());
//            List<DataInfoVo> records = voPageRes.getRecords();
            List<DataInfoVo> infoVos = originalScoreService.singleSubject(req, string, req.getFractionalSection());
            records1.add(infoVos);
            List<StudentInfoVo> studentInfoVo = new ArrayList<>();
            for (DataInfoVo record : infoVos) {
                List<StudentInfoVo> vos = record.getVos();
                if (vos != null){
                    studentInfoVo.addAll(vos);
                }
            }
            studentInfoVo1.add(studentInfoVo);
        }
        exportExcel(response,records1,studentInfoVo1,req.getDiscipline());
    }


    //导出数据使用
    @Override
    public List<DataInfoVo> singleSubject(SingleSubjectReq req, String subject, int fractionalSection) {
        List<Integer> list = null;
        String subjectS = null;
        switch (subject){
            case "语文":
                subjectS ="subject_chinese";
                break;
            case "数学":
                subjectS ="subject_math";
                break;
            case "英语":
                subjectS ="subject_english";
                break;
            case "物理":
                subjectS ="subject_physics";
                break;
            case "化学":
                subjectS ="subject_chemical";
                break;
            case "政治":
                subjectS ="subject_politics";
                break;
            case "历史":
                subjectS ="subject_history";
                break;
            case "体育":
                subjectS ="subject_physical_education";
        }
        switch (subjectS){
            case "subject_chinese":
            case "subject_math":
            case "subject_english":
                list = fsi.generateIncrementSequence(fractionalSection, 0, 150);// 分数段
                break;
            case "subject_physics":
                list = fsi.generateIncrementSequence(fractionalSection, 0, 80);// 分数段
                break;
            case "subject_chemical":
                list = fsi.generateIncrementSequence(fractionalSection, 0, 70);// 分数段
                break;
            case "subject_politics":
            case "subject_history":
            case "subject_physical_education":
                list = fsi.generateIncrementSequence(fractionalSection, 0, 50);// 分数段
                break;
        }
        Map<String, List<Double>> allClassScore = new HashMap<>(); //全部班级学生的分数
        Map<String, Integer> actualTestNumber = new HashMap<>();//实考数
        Map<String, Double> highestScore = new HashMap<>();//最高分
        Map<String, Double> lowestScore = new HashMap<>();//最低分
        Map<String, List<Double>> allClassStandardDeviation = new HashMap<>(); //某个班级的全部标准差分数
        Map<String,List<Double>> variationNumber = new HashMap<>();//变异数
        //总均分
        Collections.sort(list, Collections.reverseOrder());
        //分数段
        List<StudentInfoVo> vos = list.stream().map(value -> {
            StudentInfoVo vo = new StudentInfoVo();
            vo.setScore(value);
            return vo;
        }).collect(Collectors.toList());
        //获取某单科全部成绩
        List<OriginalScorePo> chineseList = originalScoreMapper.selectChinese(subjectS);

        StudentInfoVo vo1 = new StudentInfoVo();

        //循环分数段
        for (StudentInfoVo vo : vos) {
            //循环某单科的全部成绩
            for (OriginalScorePo student : chineseList) {
                //当前学生的某科目成绩
                Double score = Double.valueOf(student.getSubjectChinese());
                //判断allClassScore是否有这个班级
                if (allClassScore.containsKey(student.getOriginalClass())){
                    List<Double> doubles = allClassScore.get(student.getOriginalClass());
                    doubles.add(score);
                    allClassScore.put(student.getOriginalClass(), doubles);
                }else{
                    List<Double> value = new ArrayList<>();
                    value.add(score);
                    allClassScore.put(student.getOriginalClass(), value);
                }
                //判断当前学生的分数是否归纳与这个分数段
                if (score >= vo.getScore()) {
                    //班级人数
                    Map<String, Integer> data = vo.getData();

                    //判断map集合中是否有学生的班级
                    if (data.containsKey(student.getOriginalClass())) {
                        //有学生的班级直接人数+1
                        Integer count = data.get(student.getOriginalClass());
                        data.put(student.getOriginalClass(), ++count);
                        //这个班的人数
                        actualTestNumber.put(student.getOriginalClass(), (actualTestNumber.get(student.getOriginalClass()) + 1));

                        /** (求某班级的最高分)判断这个人是否大于集合中的分数 */
                        Double max = highestScore.get(student.getOriginalClass());
                        if (score > max){
                            highestScore.put(student.getOriginalClass(), score);
                        }
                        /** (求某班级的最低分)判断这个人是否小于于集合中的分数 */
                        Double min = lowestScore.get(student.getOriginalClass());
                        if (score < min){
                            lowestScore.put(student.getOriginalClass(), score);
                        }

                        //这个班的标准差分数
                        List<Double> doubles = allClassStandardDeviation.get(student.getOriginalClass());
                        doubles.add(score);
                        allClassStandardDeviation.put(student.getOriginalClass(), doubles);
                        //这个班变异数
                        List<Double> doublesV = variationNumber.get(student.getOriginalClass());
                        doublesV.add(score);
                        variationNumber.put(student.getOriginalClass(), doublesV);
                    }else{
                        data.put(student.getOriginalClass(), 1);
                        //初始化班级人数
                        actualTestNumber.put(student.getOriginalClass(), 1);
                        //这个班的最高分
                        highestScore.put(student.getOriginalClass(), score);
                        //这个班的最低分
                        lowestScore.put(student.getOriginalClass(), score);
                        //这个班的标准差分数
                        List<Double> scoreDifference = new ArrayList<>();
                        scoreDifference.add(score);
                        allClassStandardDeviation.put(student.getOriginalClass(), scoreDifference);
                        //变异数
                        List<Double> VariationNumber = new ArrayList<>();
                        VariationNumber.add(score);
                        variationNumber.put(student.getOriginalClass(), VariationNumber);
                    }
                    vo.setData(data);
                }
            }
        }
        //总均分
        Map<String, Double> totalAverageScore = new HashMap<>();
        for (StudentInfoVo vo : vos) {
            Map<String, Integer> data = vo.getData();
            int sum = 0;
            for (String classNo : data.keySet()) {
                vo.setSum(sum+=data.get(classNo));
            }
        }
        Integer studentSum = 0;
        for (String countKey : actualTestNumber.keySet()) {
            studentSum += actualTestNumber.get(countKey);
        }
        //每个班总人数
        actualTestNumber.put("studentSum", studentSum);

        Double asDouble = highestScore.values().stream().mapToDouble(Double::doubleValue)
                .max().getAsDouble();
        Double roundedNumber = Math.round(asDouble * 100) / 100.0;
        //年级最高分
        highestScore.put("maxAvg", roundedNumber);

        Double asDouble1 = lowestScore.values().stream().mapToDouble(Double::doubleValue).min().getAsDouble();
        Double minNumber = Math.round(asDouble1 * 100) / 100.0;
        //年级最低分
        lowestScore.put("minAvg", minNumber);

        List<DataInfoVo> voList = new ArrayList<>();//全部成绩

        DataInfoVo dataInfoVo = new DataInfoVo();
        dataInfoVo.setVos(vos);

        dataInfoVo.setActualTestNumber(actualTestNumber);
        dataInfoVo.setHighestScore(highestScore);
        dataInfoVo.setLowestScore(lowestScore);

        Set<String> strings = allClassScore.keySet();
        for (String classNo : strings) {
            //班级的总分
            Double scoreSum = 0d;
            List<Double> doubles = allClassScore.get(classNo);
            for (Double score : doubles) {
                scoreSum += score;
            }
            //得到平均分（总分 / 人数 得到平均分）
            Double avg = scoreSum / doubles.size();
            // 创建一个DecimalFormat对象来格式化数字，保留小数点后两位
            DecimalFormat df = new DecimalFormat("0.00");
            // 使用format方法格式化avg
            String avgFormatted = df.format(avg);
            // 如果需要，可以将格式化后的字符串转换回Double类型
            Double avgRounded = Double.parseDouble(avgFormatted);
            totalAverageScore.put(classNo, avgRounded);
        }
        Double aDoubles =0d;//单科年级的总分
        for (OriginalScorePo scorePo : chineseList) {
            aDoubles+= scorePo.getSubjectChinese();}
        Double totalAverage = aDoubles / studentSum;//这里就是获取到年级总均分
        double truncatedAverage = (double) Math.round(totalAverage * 100) / 100;
        totalAverageScore.put("allClassAvg",truncatedAverage);

        dataInfoVo.setTotalAverageScore(totalAverageScore);
        for (String classNo : allClassStandardDeviation.keySet()) {
            Double[] doubles = allClassStandardDeviation.get(classNo).toArray(new Double[0]);
            double bzc = Sample_STD_dev(doubles);
            Map<String, Double> deviation = dataInfoVo.getStandardDeviation();
            if (Double.isNaN(bzc)){
                bzc = 0.0;
                deviation.put(classNo, bzc);
            } else {
                // 使用 DecimalFormat 格式化保留两位小数
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                String formattedBzc = decimalFormat.format(bzc);
                double roundedBzc = Double.parseDouble(formattedBzc);
                deviation.put(classNo, roundedBzc);
            }
            dataInfoVo.setStandardDeviation(deviation);
        }
        //每个班的变异数
        for (String classNo : variationNumber.keySet()) {
            Double[] doubles = variationNumber.get(classNo).toArray(new Double[0]);
            double bzc = calculateVariance(doubles);
            Map<String, Double> deviation = dataInfoVo.getVariationNumber();
            if (Double.isNaN(bzc)){
                deviation.put(classNo, bzc);
            }else {
                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                String formattedBzc = decimalFormat.format(bzc);
                double roundedBzc = Double.parseDouble(formattedBzc);
                deviation.put(classNo, roundedBzc);
            }
            dataInfoVo.setVariationNumber(deviation);
        }
        voList.add(dataInfoVo);
        return voList;
    }

    //原成绩导出
    @Override
    public void originalScoreExcel(HttpServletResponse response) {
        //设置表头
        List<List<String>> head = new ArrayList<>();
        List<String> uniqueValues = Arrays.asList("班级", "考号", "姓名", "语文成绩", "数学成绩", "英语成绩", "物理成绩", "化学成绩", "政治成绩", "历史", "体育", "总分成绩");
        for (String value : uniqueValues) {
            head.add(Arrays.asList(value));
        }
        //内容
        List<List<Object>> data = new ArrayList<>();
        OriginalScoreReq req = new  OriginalScoreReq();
        req.setPageSize(99999);
        req.setPageNum(1);

        PageRes<OriginalScoreDTO> select = originalScoreService.select(req);
        for (OriginalScoreDTO record : select.getRecords()) {
            List<Object> row = new ArrayList<>();
            row.add(record.getOriginalClass());
            row.add(record.getCandidateNumber());
            row.add(record.getOriginalName());
            row.add(record.getSubjectChinese());
            row.add(record.getSubjectMath());
            row.add(record.getSubjectEnglish());
            row.add(record.getSubjectPhysics());
            row.add(record.getSubjectChemical());
            row.add(record.getSubjectPolitics());
            row.add(record.getSubjectHistory());
            row.add(record.getSubjectPhysicalEducation());
            row.add(record.getTotalScore());
            data.add(row);
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);//通常用于文件下载
        try {
            String encode = URLEncoder.encode("原成绩.xlsx", "UTF-8");
            headers.setContentDispositionFormData("attachment", encode);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        export(response, "单科成绩", "原成绩", head, data);
        System.out.println("导出成功");
    }


    //变异数
   private double calculateVariance(Double[] data) {
        int n = data.length;
        // Step 1: Calculate the mean (average) of the data
        double sum = 0;
        for (double value : data) {
            sum += value;
        }
        double mean = sum / n;
        // Step 2: Calculate the sum of squared differences from the mean
        double squaredDiffSum = 0;
        for (double value : data) {
            double diff = value - mean;
            squaredDiffSum += diff * diff;
        }
        // Step 3: Calculate the variance (average of squared differences)
        double variance = squaredDiffSum / n;
        return variance;
    }
    // 标准差
    private double Sample_STD_dev(Double[] data) {
        double std_dev;
        std_dev = Math.sqrt(Sample_Variance(data));
        return std_dev;
    }
    //sample variance 样本方差
    private double Sample_Variance(Double[] data) {
        double variance = 0;
        for (int i = 0; i < data.length; i++) {
            variance = variance + (Math.pow((data[i] - Mean(data)), 2));
        }
        variance = variance / (data.length-1);
        return variance;
    }
    private double Mean(Double[] data) {
        double mean = 0;
        mean = Sum(data) / data.length;
        return mean;
    }
    private double Sum(Double[] data) {
        double sum = 0;
        for (int i = 0; i < data.length; i++)
            sum = sum + data[i];
        return sum;
    }

    /***
     * 单科成绩导出
     */
    public void  exportExcel(HttpServletResponse response,List<List<DataInfoVo>> dataInfoVo, List<List<StudentInfoVo>> studentInfoVo,List<String> subjects){
        List<StudentInfoVo> collect = studentInfoVo.stream().flatMap(List::stream).collect(Collectors.toList());
        List<List<String>> hand = hand(collect);
        List<String> classz = getClassz(collect);
        List<List<Object>> data = new ArrayList<>();
        for (int i=0; i < dataInfoVo.size(); i++ ) {
            List<Object> kong = Arrays.asList(" ");//添加一行空白
            data.add(kong);
            List<Object> classSize = Arrays.asList(subjects.get(i));//添加科目表示
            data.add(classSize);
            data(dataInfoVo.get(i), classz, data);
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);//通常用于文件下载
        try {
            String encode = URLEncoder.encode("单科成绩.xlsx", "UTF-8");
            headers.setContentDispositionFormData("attachment", encode);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("导出成功");
        export(response, "单科成绩", "成绩", hand, data);
    }

    //拼接excel表头
    public List<List<String>>hand(List<StudentInfoVo> studentInfoVo){
        // excel表头
        List<String> fractionalRange = Arrays.asList("分数段");
        //获取全部班级
        List<String> uniqueValues = getClassz(studentInfoVo);
        //年级头
        List<String> grade = Arrays.asList("年级");
        //封装头
        List<List<String>> head = new ArrayList<>();
        head.add(fractionalRange);
        uniqueValues.stream().forEach(v -> head.add(new ArrayList<String>(){{add(v);}}));
        head.add(grade);
        return head;
    }

    //拼接excel数据
    public void data(List<DataInfoVo> dataInfoVo,List<String> classz, List<List<Object>> data){
        for (DataInfoVo infoVo : dataInfoVo) {
            //遍历班级人数
            for (StudentInfoVo vo : infoVo.getVos()) {
                Map<String, Integer> dataMap = vo.getData();
                List<Object>classSize = new ArrayList<>();

                classSize.add(vo.getScore());//添加分数段

                for (String qclassz :classz) {//
                    if (!vo.getData().containsKey(qclassz)){
                        dataMap.put(qclassz, 0);//
                        classSize.add(0);
                    }else {
                        Integer aDefault = dataMap.getOrDefault(qclassz, 0);
                        classSize.add(aDefault);
                    }
                }
                Integer Sum = vo.getSum();
                if (ObjectUtil.isNull(vo.getSum())){
                    Sum = 0;
                }
                classSize.add(Sum);
                data.add(classSize);
            }
            //总均分
            List<Object> totalAverageScore = new ArrayList<>();
            totalAverageScore.add("总均分");
            for (String s : classz) {
                if (!infoVo.getTotalAverageScore().containsKey(s)){
                    totalAverageScore.add(0);
                }else {
                    Double orDefault = infoVo.getTotalAverageScore().getOrDefault(s, 0.0);
                    totalAverageScore.add(orDefault);
                }
            }

            for (String s : infoVo.getTotalAverageScore().keySet()) {
                if (s.equals("allClassAvg")){
                    Double allClassAvg = infoVo.getTotalAverageScore().getOrDefault("allClassAvg", 0.0);
                    totalAverageScore.add(allClassAvg);
                }
            }
            data.add(totalAverageScore);

            List<Object> highestScore = new ArrayList<>();
            highestScore.add("最高分");
            for (String s : classz) {
                if (!infoVo.getHighestScore().containsKey(s)){
                    highestScore.add(0);
                }else {
                    Double orDefault = infoVo.getHighestScore().getOrDefault(s, 0.0);
                    highestScore.add(orDefault);
                }
            }
            for (String s : infoVo.getHighestScore().keySet()) {
                if (s.equals("maxAvg")){
                    Double allClassAvg = infoVo.getHighestScore().getOrDefault("maxAvg", 0.0);
                    highestScore.add(allClassAvg);
                }
            }
            data.add(highestScore);

            List<Object> lowestScore = new ArrayList<>();
            lowestScore.add("最低分");
            for (String s : classz) {
                if (!infoVo.getLowestScore().containsKey(s)){
                    lowestScore.add(0);
                }else {
                    Double orDefault = infoVo.getLowestScore().getOrDefault(s, 0.0);
                    lowestScore.add(orDefault);
                }
            }
            for (String s : infoVo.getLowestScore().keySet()) {
                if (s.equals("minAvg")){
                    Double allClassAvg = infoVo.getLowestScore().getOrDefault("minAvg", 0.0);
                    lowestScore.add(allClassAvg);
                }
            }
            data.add(lowestScore);

        }
    }

    //获取全部班级
    public List<String> getClassz(List<StudentInfoVo> studentInfoVo){
        //获取全部班级
        List<String> classz = new ArrayList<>();
        for (StudentInfoVo infoVo : studentInfoVo) {
            List<String> collect = infoVo.getData().keySet().stream().distinct().collect(Collectors.toList());
            classz.addAll(collect);
        }
        //获取到全部班级
        List<String> uniqueValues = classz.stream()
                .distinct() // 进行去重操作
                .sorted(Comparator.comparingInt(s -> Integer.parseInt(s)))//对班级进行排序
                .collect(Collectors.toList());
        return uniqueValues;
    }

}
