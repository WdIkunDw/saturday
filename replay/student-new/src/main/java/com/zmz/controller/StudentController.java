package com.zmz.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zmz.entity.Student;
import com.zmz.service.StudentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 学生管理控制器
 */
@RestController
@RequestMapping("/student")
public class StudentController {

    @Resource
    private StudentService studentService;

    /**
     * 新增学生
     * @param student 学生信息
     * @return 是否成功
     */
    @PostMapping
    public boolean save(@RequestBody Student student) {
        return studentService.save(student);
    }

    /**
     * 删除学生
     * @param id 学生ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return studentService.removeById(id);
    }

    /**
     * 更新学生信息
     * @param student 学生信息
     * @return 是否成功
     */
    @PutMapping
    public boolean update(@RequestBody Student student) {
        return studentService.updateById(student);
    }

    /**
     * 根据ID查询学生
     * @param id 学生ID
     * @return 学生信息
     */
    @GetMapping("/{id}")
    public Student getById(@PathVariable Long id) {
        return studentService.getById(id);
    }

    /**
     * 分页查询学生列表
     * @param current 当前页
     * @param size 每页大小
     * @param name 学生姓名（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Page<Student> page(@RequestParam(defaultValue = "1") Integer current,
                            @RequestParam(defaultValue = "10") Integer size,
                            @RequestParam(required = false) String name) {
        Page<Student> page = new Page<>(current, size);
        LambdaQueryWrapper<Student> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null, Student::getName, name);
        return studentService.page(page, wrapper);
    }
} 