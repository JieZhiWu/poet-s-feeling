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
    console.error("获取诗人列表失败，" + e.message);
  }

  return (
    <div id="banksPage" className="banks-page-container">
      <div className="page-hero">
        <div className="hero-content">
          <Title level={1} className="page-title">
            👑 诗人大全
          </Title>
          <div className="page-subtitle">
            走进文学大师的世界，感受不同时代的诗意风采
          </div>
        </div>
      </div>
      
      <div className="content-wrapper">
        <div className="stats-section">
          <div className="stat-card">
            <div className="stat-number">{questionBankList.length}</div>
            <div className="stat-label">份诗人图谱</div>
          </div>
          <div className="stat-card">
            <div className="stat-number">
              {questionBankList.reduce((total, bank) => total + (bank.questionNum || 0), 0)}
            </div>
            <div className="stat-label">首经典诗歌</div>
          </div>
        </div>
        
        <QuestionBankList questionBankList={questionBankList} />
      </div>
    </div>
  );
}
