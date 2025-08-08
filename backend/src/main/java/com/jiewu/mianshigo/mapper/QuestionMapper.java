package com.jiewu.mianshigo.mapper;

import com.jiewu.mianshigo.model.entity.Question;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
* @author JieWu
* @description 针对表【question(题目)】的数据库操作Mapper
* @createDate 2025-07-31 19:03:33
* @Entity com.jiewu.mianshigo.model.entity.Question
*/
public interface QuestionMapper extends BaseMapper<Question> {

    /**
     * 查询题目列表(含逻辑删除的)
     */
    @Select("SELECT * FROM question WHERE updateTime > #{minUpdateTime}")
    List<Question> listQuestionWithDelete(Date minUpdateTime );
}




