"use client";
import { Card } from "antd";
import Title from "antd/es/typography/Title";
import TagList from "@/components/TagList";
import MdViewer from "@/components/MdViewer";
import "./index.css";
import useAddUserSignInRecord from "@/hooks/useAddUserSignInRecord";

interface Props {
  question: API.QuestionVO;
}

/**
 * 诗歌卡片
 * @param props
 * @constructor
 */
const QuestionCard = (props: Props) => {
  const { question } = props;

  // 签到
  useAddUserSignInRecord();

  return (
    <div className="question-card">
      <Card>
        <Title level={1} style={{ fontSize: 24 }}>
          {question.title}
        </Title>
        <TagList tagList={question.tagList} />
        <div style={{ marginBottom: 16 }} />
        <MdViewer value={question.content} />
      </Card>
      <div style={{ marginBottom: 16 }} />
      <Card title="诗歌内容">
        <div style={{ whiteSpace: "pre-wrap", wordBreak: "break-word" }}>
          <MdViewer value={question.answer} />
        </div>
      </Card>
    </div>
  );
};

export default QuestionCard;
