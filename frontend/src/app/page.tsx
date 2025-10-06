"use client";
import Title from "antd/es/typography/Title";
import { Divider, Spin } from "antd";
import Link from "next/link";
import { listQuestionBankVoByPageUsingPost } from "@/api/questionBankController";
import { listQuestionVoByPageUsingPost } from "@/api/questionController";
import QuestionBankList from "@/components/QuestionBankList";
import QuestionList from "@/components/QuestionList";
import { useEffect, useState } from "react";
import "./index.css";

/**
 * 主页
 * @constructor
 */
export default function HomePage() {
  const [questionBankList, setQuestionBankList] = useState<API.QuestionBankVO[]>([]);
  const [questionList, setQuestionList] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      
      try {
        // 并行获取数据，但如果失败就使用空数组
        const [banksRes, questionsRes] = await Promise.allSettled([
          listQuestionBankVoByPageUsingPost({
            pageSize: 12,
            sortField: "createTime",
            sortOrder: "descend",
          }),
          listQuestionVoByPageUsingPost({
            pageSize: 12,
            sortField: "createTime",
            sortOrder: "descend",
          })
        ]);

        // 处理诗人列表结果
        if (banksRes.status === 'fulfilled' && banksRes.value.code === 0) {
          setQuestionBankList(banksRes.value.data?.records ?? []);
        } else {
          console.warn("获取诗人列表失败，使用空列表");
          setQuestionBankList([]);
        }

        // 处理诗歌列表结果
        if (questionsRes.status === 'fulfilled' && questionsRes.value.code === 0) {
          setQuestionList(questionsRes.value.data?.records ?? []);
        } else {
          console.warn("获取诗歌列表失败，使用空列表");
          setQuestionList([]);
        }
      } catch (e) {
        console.error("获取数据失败:", e);
        // 即使失败也设置空数组，保证页面能正常显示
        setQuestionBankList([]);
        setQuestionList([]);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  if (loading) {
    return (
      <div className="homepage-container">
        <div className="loading-container" style={{ 
          display: 'flex', 
          flexDirection: 'column', 
          alignItems: 'center', 
          justifyContent: 'center', 
          minHeight: '60vh',
          color: 'white'
        }}>
          <Spin size="large" />
          <div style={{ marginTop: 16 }}>正在加载芥雾浮心...</div>
        </div>
      </div>
    );
  }

  return (
    <div id="homePage" className="homepage-container">
      {/* 主横幅 */}
      <div className="hero-section">
        <div className="hero-content">
          <Title level={1} className="hero-title">
            🍃 芥雾浮心 - 在线诗歌平台
          </Title>
          <div className="hero-subtitle">
            品味诗歌意境，感受文字之美，在诗意中静心成长
          </div>
          <div className="hero-actions">
            <Link href="/poem" className="cta-button primary">
              🖋️ 开始创作
            </Link>
            <Link href="/banks" className="cta-button secondary">
              📚 浏览诗人
            </Link>
          </div>
        </div>
        <div className="hero-decoration">
          <div className="floating-poetry">📜</div>
          <div className="floating-poetry">✨</div>
          <div className="floating-poetry">🌸</div>
        </div>
      </div>

      {/* 诗人分类区域 */}
      <div className="content-section">
        <div className="section-header">
          <Title level={2} className="section-title">
            <span className="title-icon">👑</span>
            诗人图谱
          </Title>
          <Link href="/banks" className="more-link">
            查看全部 →
          </Link>
        </div>
        <QuestionBankList questionBankList={questionBankList} />
      </div>

      <Divider className="section-divider" />

      {/* 诗歌大全区域 */}
      <div className="content-section">
        <div className="section-header">
          <Title level={2} className="section-title">
            <span className="title-icon">📝</span>
            经典诗歌
          </Title>
          <Link href="/questions" className="more-link">
            查看全部 →
          </Link>
        </div>
        <QuestionList questionList={questionList} />
      </div>

      {/* 功能导航区域 */}
      <div className="feature-section">
        <Title level={2} className="feature-title">探索更多功能</Title>
        <div className="feature-grid">
          <Link href="/poem" className="feature-card">
            <div className="feature-icon">🌟</div>
            <div className="feature-name">AI创作诗歌</div>
            <div className="feature-desc">用心境创作独特诗篇</div>
          </Link>
          <Link href="/poem/history" className="feature-card">
            <div className="feature-icon">📚</div>
            <div className="feature-name">我的作品集</div>
            <div className="feature-desc">回顾创作历程</div>
          </Link>
          <Link href="/banks" className="feature-card">
            <div className="feature-icon">👥</div>
            <div className="feature-name">诗人学堂</div>
            <div className="feature-desc">学习大师作品</div>
          </Link>
          <Link href="/questions" className="feature-card">
            <div className="feature-icon">📖</div>
            <div className="feature-name">诗歌宝库</div>
            <div className="feature-desc">海量经典收藏</div>
          </Link>
        </div>
      </div>
    </div>
  );
}
