package com.jiewu.mianshigo.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiewu.mianshigo.annotation.AuthCheck;
import com.jiewu.mianshigo.common.BaseResponse;
import com.jiewu.mianshigo.common.ErrorCode;
import com.jiewu.mianshigo.common.ResultUtils;
import com.jiewu.mianshigo.constant.CommonConstant;
import com.jiewu.mianshigo.constant.UserConstant;
import com.jiewu.mianshigo.exception.BusinessException;
import com.jiewu.mianshigo.exception.ThrowUtils;
import com.jiewu.mianshigo.mapper.QuestionMapper;
import com.jiewu.mianshigo.model.dto.question.QuestionEsDTO;
import com.jiewu.mianshigo.model.dto.question.QuestionQueryRequest;
import com.jiewu.mianshigo.model.entity.Question;
import com.jiewu.mianshigo.model.entity.QuestionBankQuestion;
import com.jiewu.mianshigo.model.entity.User;
import com.jiewu.mianshigo.model.vo.QuestionVO;
import com.jiewu.mianshigo.model.vo.UserVO;
import com.jiewu.mianshigo.service.QuestionBankQuestionService;
import com.jiewu.mianshigo.service.QuestionService;
import com.jiewu.mianshigo.service.UserService;
import com.jiewu.mianshigo.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 题目服务实现

 */
@Service
@Slf4j
public  class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Resource
    private UserService userService;

    @Resource
    private QuestionBankQuestionService questionBankQuestionService;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    // 标志位：ES 是否可用（避免频繁尝试）
    // volatile 是一个关键字，用于修饰变量，主要作用是保证变量的可见性和禁止指令重排序
    private volatile boolean esAvailable = true;

    /**
     * 校验数据
     *
     * @param question
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validQuestion(Question question, boolean add) {
        ThrowUtils.throwIf(question == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        String title = question.getTitle();
        String content = question.getContent();
        // 创建数据时，参数不能为空
        if (add) {
            // todo 补充校验规则
            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        // todo 补充校验规则
        if (StringUtils.isNotBlank(title)) {
            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content)){
            ThrowUtils.throwIf(content.length() > 10240, ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = questionQueryRequest.getId();
        Long notId = questionQueryRequest.getNotId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        String searchText = questionQueryRequest.getSearchText();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();
        List<String> tagList = questionQueryRequest.getTags();
        Long userId = questionQueryRequest.getUserId();
        String answer = questionQueryRequest.getAnswer();
        // todo 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.like(StringUtils.isNotBlank(answer), "answer", answer);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取题目封装
     *
     * @param question
     * @param request
     * @return
     */
    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        // 对象转封装类
        QuestionVO questionVO = QuestionVO.objToVo(question);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = question.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionVO.setUser(userVO);
        // endregion

        return questionVO;
    }

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param request
     * @return
     */
    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollUtil.isEmpty(questionList)) {
            return questionVOPage;
        }
        // 对象列表 => 封装对象列表
        List<QuestionVO> questionVOList = questionList.stream().map(question -> {
            return QuestionVO.objToVo(question);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionList.stream().map(Question::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        questionVOList.forEach(questionVO -> {
            Long userId = questionVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionVO.setUser(userService.getUserVO(user));
         });
        // endregion

        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

    /**
     * 分页获取题目列表（仅管理员可用）
     *
     * @param questionQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Page<Question> listQuestionByPage(QuestionQueryRequest questionQueryRequest) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 题目查询条件
        QueryWrapper<Question> queryWrapper = this.getQueryWrapper(questionQueryRequest);
        // 根据题库查询题目列表接口
        Long questionBankId = questionQueryRequest.getQuestionBankId();
        if (questionBankId != null) {
            // 查询题库内题目id
            LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                    .select(QuestionBankQuestion::getQuestionId)
                    .eq(QuestionBankQuestion::getQuestionBankId, questionBankId);
            List<QuestionBankQuestion> questionList = questionBankQuestionService.list(lambdaQueryWrapper);
            if (CollUtil.isNotEmpty(questionList)) {
                // 取出题目 id 集合
                Set<Long> questionIdSet = questionList.stream()
                        .map(QuestionBankQuestion::getQuestionId)
                        .collect(Collectors.toSet());
                queryWrapper.in("id", questionIdSet);
            }else {
                return new Page<>(current, size, 0);
            }
        }
        // 查询数据库
        Page<Question> questionPage = this.page(new Page<>(current, size),queryWrapper);
        return questionPage;
    }

    /**
     * 从数据库查询题目（供 ES 降级使用）
     * @param questionQueryRequest 查询条件
     * @return 分页结果
     */
    private Page<Question> searchFromDb(QuestionQueryRequest questionQueryRequest) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();

        // 构造查询条件
        QueryWrapper<Question> queryWrapper = getQueryWrapper(questionQueryRequest);

        // 处理题库 ID 查询（如果需要）
        Long questionBankId = questionQueryRequest.getQuestionBankId();
        if (questionBankId != null) {
            LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                    .select(QuestionBankQuestion::getQuestionId)
                    .eq(QuestionBankQuestion::getQuestionBankId, questionBankId);
            List<QuestionBankQuestion> relationList = questionBankQuestionService.list(lambdaQueryWrapper);
            if (CollUtil.isNotEmpty(relationList)) {
                Set<Long> questionIdSet = relationList.stream()
                        .map(QuestionBankQuestion::getQuestionId)
                        .collect(Collectors.toSet());
                queryWrapper.in("id", questionIdSet);
            } else {
                return new Page<>(current, size, 0); // 没有题目
            }
        }

        // 查询数据库
        return this.page(new Page<>(current, size), queryWrapper);
    }

    @Override
    public Page<Question> searchFromEs(QuestionQueryRequest questionQueryRequest) {
        // 如果 ES 不可用，直接降级
        if (!esAvailable) {
            log.warn("ES 已标记为不可用，直接降级到数据库查询");
            return searchFromDb(questionQueryRequest);
        }

        try {
            // 获取参数
            Long id = questionQueryRequest.getId();
            Long notId = questionQueryRequest.getNotId();
            String searchText = questionQueryRequest.getSearchText();
            List<String> tags = questionQueryRequest.getTags();
            Long questionBankId = questionQueryRequest.getQuestionBankId();
            Long userId = questionQueryRequest.getUserId();
            // 注意，ES 的起始页为 0
            int current = questionQueryRequest.getCurrent() - 1;
            int pageSize = questionQueryRequest.getPageSize();
            String sortField = questionQueryRequest.getSortField();
            String sortOrder = questionQueryRequest.getSortOrder();

            // 构造查询条件
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            // 过滤
            boolQueryBuilder.filter(QueryBuilders.termQuery("isDelete", 0));
            if (id != null) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("id", id));
            }
            if (notId != null) {
                boolQueryBuilder.mustNot(QueryBuilders.termQuery("id", notId));
            }
            if (userId != null) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("userId", userId));
            }
            if (questionBankId != null) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("questionBankId", questionBankId));
            }
            // 必须包含所有标签
            if (CollUtil.isNotEmpty(tags)) {
                for (String tag : tags) {
                    boolQueryBuilder.filter(QueryBuilders.termQuery("tags", tag));
                }
            }
            // 按关键词检索
            if (StringUtils.isNotBlank(searchText)) {
                boolQueryBuilder.should(QueryBuilders.matchQuery("title", searchText));
                boolQueryBuilder.should(QueryBuilders.matchQuery("content", searchText));
                boolQueryBuilder.should(QueryBuilders.matchQuery("answer", searchText));
                boolQueryBuilder.minimumShouldMatch(1);
            }
            // 排序
            SortBuilder<?> sortBuilder = SortBuilders.scoreSort();
            if (StringUtils.isNotBlank(sortField)) {
                sortBuilder = SortBuilders.fieldSort(sortField);
                sortBuilder.order(CommonConstant.SORT_ORDER_ASC.equals(sortOrder) ? SortOrder.ASC : SortOrder.DESC);
            }
            // 分页
            PageRequest pageRequest = PageRequest.of(current, pageSize);
            // 构造查询
            NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(boolQueryBuilder)
                    .withPageable(pageRequest)
                    .withSorts(sortBuilder)
                    .build();
            SearchHits<QuestionEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, QuestionEsDTO.class);
            // 复用 MySQL 的分页对象，封装返回结果
            Page<Question> page = new Page<>();
            page.setTotal(searchHits.getTotalHits());
            List<Question> resourceList = new ArrayList<>();
            if (searchHits.hasSearchHits()) {
                List<SearchHit<QuestionEsDTO>> searchHitList = searchHits.getSearchHits();
                for (SearchHit<QuestionEsDTO> questionEsDTOSearchHit : searchHitList) {
                    resourceList.add(QuestionEsDTO.dtoToObj(questionEsDTOSearchHit.getContent()));
                }
            }
            page.setRecords(resourceList);
            return page;
        } catch (Exception e) {
            // 记录详细错误日志
            log.error("Elasticsearch 查询失败，将降级到数据库。请求参数: {}", questionQueryRequest, e);
            esAvailable = false; // 标记 ES 不可用，避免后续请求反复尝试

            // 降级到数据库
            return searchFromDb(questionQueryRequest);
        }
    }

    /**
     * 提供一个恢复机制，定时检查 ES 是否恢复
     */
    public void resetEsAvailable() {
        esAvailable = true;
        log.info("已重置 ES 可用状态，下次请求将尝试使用 ES");
    }


    /**
     * 批量删除题目
     *
     * @param questionIdList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteQuestions(List<Long> questionIdList) {
        ThrowUtils.throwIf(CollUtil.isEmpty(questionIdList), ErrorCode.PARAMS_ERROR, "题目列表为空");
        for (Long questionId : questionIdList) {
            boolean result = this.removeById(questionId);
            ThrowUtils.throwIf(!result, ErrorCode.NOT_FOUND_ERROR, "删除题目失败");
            // 从题库删除题目(移除关联)
            // 构造查询
            LambdaQueryWrapper<QuestionBankQuestion> lambdaQueryWrapper = Wrappers.lambdaQuery(QuestionBankQuestion.class)
                    .eq(QuestionBankQuestion::getQuestionId, questionId);
            result = questionBankQuestionService.remove(lambdaQueryWrapper);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "从题库删除题目失败");

        }
    }


}
