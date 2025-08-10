"use server";
import Title from "antd/es/typography/Title";
import { message } from "antd";
import { listQuestionVoByPageUsingPost, searchQuestionVoByPageUsingPost } from "@/api/questionController";
import QuestionTable from "@/components/QuestionTable";
import "./index.css";

/**
 * 诗歌列表页面
 * @constructor
 */
export default async function QuestionsPage({ searchParams }) {
    // 获取 url 的查询参数
    const { q: searchText } = searchParams;
    // 诗歌列表和总数
    let questionList = [];
    let total = 0;

    try {
      const res = await searchQuestionVoByPageUsingPost({
        searchText,
        pageSize: 12,
        sortField: "createTime",
        sortOrder: "descend",
      });
      // @ts-ignore
        questionList = res.data.records ?? [];
        // @ts-ignore
        total = res.data.total ?? 0;
    } catch (e) {
        // @ts-ignore
        message.error("获取诗歌列表失败，" + e.message);
    }

    return (
        <div id="questionsPage" className="max-width-content">
            <Title level={3}>诗歌大全</Title>
            <QuestionTable
                defaultQuestionList={questionList}
                defaultTotal={total}
                defaultSearchParams={{
                    title: searchText,
                }}
            />
        </div>
    );
}
