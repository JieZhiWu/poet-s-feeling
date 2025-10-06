"use client";
import { Card, List } from "antd";
import TagList from "@/components/TagList";
import Link from "next/link";
import "./index.css";

interface Props {
  questionBankId?: number;
  questionList: API.QuestionVO[];
  cardTitle?: string;
}

/**
 * 诗歌列表组件
 * @param props
 * @constructor
 */
const QuestionList = (props: Props) => {
  const { questionList = [], cardTitle, questionBankId } = props;

  return (
    <div className="question-list-container">
      {cardTitle && (
        <div className="list-title">{cardTitle}</div>
      )}
      <div className="question-grid">
        {questionList.map((item, index) => (
          <Card key={item.id} className="question-card" hoverable>
            <Link
              href={
                questionBankId
                  ? `/bank/${questionBankId}/question/${item.id}`
                  : `/question/${item.id}`
              }
              className="question-link"
            >
              <div className="question-header">
                <div className="question-number">#{index + 1}</div>
                <div className="question-title">{item.title}</div>
              </div>
              <div className="question-content">
                {item.content && (
                  <div className="question-preview">
                    {item.content.substring(0, 100)}
                    {item.content.length > 100 && '...'}
                  </div>
                )}
              </div>
              <div className="question-footer">
                <TagList tagList={item.tagList} />
                <span className="view-detail">查看详情 →</span>
              </div>
            </Link>
          </Card>
        ))}
      </div>
      
      {questionList.length === 0 && (
        <div className="empty-questions">
          <div className="empty-icon">📝</div>
          <div className="empty-text">暂无诗歌</div>
        </div>
      )}
    </div>
  );
};

export default QuestionList;
