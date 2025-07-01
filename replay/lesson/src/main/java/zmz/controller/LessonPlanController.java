package zmz.controller;

import zmz.entity.LessonPlan;
import zmz.service.LessonPlanService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/lessonPlan")
public class LessonPlanController extends HttpServlet {
    private LessonPlanService service = new LessonPlanService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String topicName = req.getParameter("topicName");
        if (id != null) {
            LessonPlan plan = service.getById(Long.valueOf(id));
            req.setAttribute("lessonPlan", plan);
            req.getRequestDispatcher("/lessonPlanDetail.jsp").forward(req, resp);
        } else {
            List<LessonPlan> list = service.list(topicName);
            req.setAttribute("lessonPlanList", list);
            req.getRequestDispatcher("/lessonPlanList.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 这里只做简单示例，实际应做参数校验和异常处理
        LessonPlan plan = new LessonPlan();
        // ... 从req获取参数并set到plan ...
        service.save(plan);
        resp.sendRedirect("/lessonPlan");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // ... 获取参数并更新 ...
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        if (id != null) {
            service.delete(Long.valueOf(id));
        }
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
} 