"use server";
import Title from "antd/es/typography/Title";
import {Flex, message} from "antd";
import {listQuestionBankVoByPageUsingPost} from "@/api/questionBankController";
import QuestionBankList from "@/components/QuestionBankList";
import "./index.css";

/**
 * 主页
 * @constructor
 */
export default async function BanksPage() {
  let questionBankList: API.QuestionBankVO[] = [];

  try {
    const res = await listQuestionBankVoByPageUsingPost({
      pageSize: 200,
      sortField: "createTime",
      sortOrder: "descend",
    });
    questionBankList = res.data.records ?? [];
  } catch (e) {
    // @ts-ignore
    message.error("获取题库列表失败，" + e.message);
  }

  return (
    <div id="banksPage" className="max-width-content">
        <Title level={3}>题库大全</Title>
      <QuestionBankList questionBankList={questionBankList} />
    </div>
  );
}
