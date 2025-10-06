"use server";
import { message } from "antd";
import { getQuestionVoByIdUsingGet } from "@/api/questionController";
import QuestionCard from "@/components/QuestionCard";
import "./index.css";

/**
 * 诗歌详情页
 * @constructor
 */
export default async function QuestionPage({ params }) {
  const { questionId } = params;

  // 获取诗歌详情
  let question = undefined;
  try {
    const res = await getQuestionVoByIdUsingGet({
      id: questionId,
    });
    question = res.data;
  } catch (e) {
    message.error("获取诗歌详情失败，" + e.message);
  }
  // 错误处理
  if (!question) {
    return <div>获取诗歌详情失败，请刷新重试</div>;
  }

  return (
    <div id="questionPage">
      <QuestionCard question={question} />
    </div>
  );
}
