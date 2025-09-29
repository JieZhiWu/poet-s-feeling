"use client";
import { Avatar, Card, List, Typography } from "antd";
import Link from "next/link";
import "./index.css";

interface Props {
  questionBankList: API.QuestionBankVO[];
}

/**
 * 诗人列表组件
 * @param props
 * @constructor
 */
const QuestionBankList = (props: Props) => {
  const { questionBankList = [] } = props;

  const questionBankView = (questionBank: API.QuestionBankVO) => {
    return (
      <Card className="poet-card" hoverable>
        <Link href={`/bank/${questionBank.id}`} className="poet-link">
          <div className="poet-header">
            <Avatar 
              src={questionBank.picture} 
              size={64}
              className="poet-avatar"
            />
            <div className="poet-badge">
              {questionBank.questionNum || 0} 首
            </div>
          </div>
          <div className="poet-content">
            <div className="poet-title">{questionBank.title}</div>
            <Typography.Paragraph
              className="poet-description"
              ellipsis={{ rows: 2 }}
            >
              {questionBank.description}
            </Typography.Paragraph>
          </div>
          <div className="poet-footer">
            <span className="view-works">查看作品 →</span>
          </div>
        </Link>
      </Card>
    );
  };

  return (
    <div className="question-bank-list">
      <List
        grid={{
          gutter: 16,
          column: 4,
          xs: 1,
          sm: 2,
          md: 3,
          lg: 3,
        }}
        dataSource={questionBankList}
        renderItem={(item) => <List.Item>{questionBankView(item)}</List.Item>}
      />
    </div>
  );
};

export default QuestionBankList;
