package zmz.service;

import zmz.dao.LessonPlanDao;
import zmz.entity.LessonPlan;
import java.util.List;

public class LessonPlanService {
    private LessonPlanDao lessonPlanDao = new LessonPlanDao();

    public boolean save(LessonPlan plan) {
        return lessonPlanDao.save(plan);
    }

    public LessonPlan getById(Long id) {
        return lessonPlanDao.getById(id);
    }

    public boolean update(LessonPlan plan) {
        return lessonPlanDao.update(plan);
    }

    public boolean delete(Long id) {
        return lessonPlanDao.delete(id);
    }

    public List<LessonPlan> list(String topicName) {
        return lessonPlanDao.list(topicName);
    }
} 