'use client';

import { Avatar, Button, Spin, Alert } from "antd";
import { getQuestionBankVoByIdUsingGet } from "@/api/questionBankController";
import Paragraph from "antd/es/typography/Paragraph";
import Title from "antd/es/typography/Title";
import QuestionList from "@/components/QuestionList";
import { useEffect, useState, useMemo } from "react";
import { useParams } from 'next/navigation';
import "./index.css";

export default function BankPage() {
  const params = useParams();
  const questionBankId = params.questionBankId as string;
  
  const [bank, setBank] = useState<API.QuestionBankVO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  // 使用 useMemo 计算第一个问题的ID，避免无限循环
  const firstQuestionId = useMemo(() => {
    return bank?.questionPage?.records?.[0]?.id || null;
  }, [bank?.questionPage?.records]);

  useEffect(() => {
    const fetchBankData = async () => {
      if (!questionBankId) return;
      
      setLoading(true);
      setError(null);
      
      try {
        console.log('正在获取诗人数据:', questionBankId);
        const res = await getQuestionBankVoByIdUsingGet({
          id: parseInt(questionBankId),
          needQueryQuestionList: true,
          pageSize: 200,
        });
        
        console.log('API响应:', res);
        
        if (res.code === 0 && res.data) {
          setBank(res.data);
        } else {
          setError(res.message || '获取诗人信息失败');
        }
      } catch (e: any) {
        console.error("获取诗人失败:", e);
        setError(e.message || '网络请求失败');
      } finally {
        setLoading(false);
      }
    };

    fetchBankData();
  }, [questionBankId]);

  if (loading) {
    return (
      <div className="poet-detail-page">
        <div className="loading-container">
          <Spin size="large" />
          <div style={{ marginTop: 16, color: 'white' }}>正在加载诗人信息...</div>
        </div>
      </div>
    );
  }

  if (error || !bank) {
    return (
      <div className="poet-detail-page">
        <div className="error-container">
          <Alert
            message="获取诗人信息失败"
            description={error || '请检查网络连接或稍后重试'}
            type="error"
            showIcon
          />
        </div>
      </div>
    );
  }


  return (
    <div id="bankPage" className="poet-detail-page">
      <div className="page-hero">
        <div className="hero-content">
          <Avatar src={bank.picture} size={120} className="poet-main-avatar" />
          <Title level={1} className="poet-main-title">
            {bank.title}
          </Title>
          <Paragraph className="poet-main-description">
            {bank.description}
          </Paragraph>
          <div className="poet-stats">
            <div className="stat-item">
              <span className="stat-number">{bank.questionPage?.total || 0}</span>
              <span className="stat-label">首诗歌</span>
            </div>
            <div className="stat-item">
              <span className="stat-number">{bank.reviewCount || 0}</span>
              <span className="stat-label">次学习</span>
            </div>
          </div>
          {firstQuestionId && (
            <Button
              type="primary"
              size="large"
              className="start-study-btn"
              href={`/bank/${questionBankId}/question/${firstQuestionId}`}
              target="_blank"
            >
              🎭 开始学习诗歌
            </Button>
          )}
        </div>
      </div>

      <div className="content-section">
        <div className="section-header">
          <Title level={2} className="section-title">
            📚 {bank.title} 的诗歌作品
          </Title>
          <div className="total-count">
            共 {bank.questionPage?.total || 0} 首
          </div>
        </div>
        
        {bank.questionPage?.records && bank.questionPage.records.length > 0 ? (
          <QuestionList
            questionBankId={questionBankId}
            questionList={bank.questionPage.records}
            cardTitle=""
          />
        ) : (
          <div className="empty-state">
            <div className="empty-icon">📝</div>
            <div className="empty-text">暂无诗歌作品</div>
            <div className="empty-desc">该诗人的作品正在整理中，敬请期待</div>
          </div>
        )}
      </div>
    </div>
  );
}