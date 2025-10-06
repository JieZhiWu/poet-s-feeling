'use client';

import React, { useState } from 'react';
import { Card, Tag, Button, Typography, Divider, message, Spin } from 'antd';
import { BookOutlined, HeartOutlined, BulbOutlined, ExperimentOutlined } from '@ant-design/icons';
import { PoemWithId, PoemAnalysisResponse, generateAnalysisUsingPost } from '@/api/poemController';
import './index.css';

const { Title, Paragraph, Text } = Typography;

interface PoemDisplayProps {
  poem: PoemWithId;
}

const PoemDisplay: React.FC<PoemDisplayProps> = ({ poem }) => {
  const [analysis, setAnalysis] = useState<PoemAnalysisResponse | null>(null);
  const [loadingAnalysis, setLoadingAnalysis] = useState(false);

  const handleGenerateAnalysis = async () => {
    setLoadingAnalysis(true);
    try {
      const response = await generateAnalysisUsingPost({
        id: poem.id,
      });

      if (response.code === 0 && response.data) {
        setAnalysis(response.data);
        message.success('解读报告生成成功！');
      } else {
        message.error(response.message || '解读报告生成失败');
      }
    } catch (error: any) {
      console.error('生成解读报告失败:', error);
      message.error(error.message || '解读报告生成失败，请稍后重试');
    } finally {
      setLoadingAnalysis(false);
    }
  };

  return (
    <div className="poem-display-container">
      {/* 诗歌主体 */}
      <Card 
        className="poem-card"
        bordered={false}
      >
        <div className="poem-header">
          <Title level={2} className="poem-title">
            <BookOutlined className="poem-icon" />
            {poem.poemResponse.title}
          </Title>
        </div>

        <div className="poem-content">
          <Paragraph className="poem-text">
            {poem.poemResponse.poem.split('\n').map((line, index) => (
              <div key={index} className="poem-line">
                {line}
              </div>
            ))}
          </Paragraph>
        </div>

        <Divider />

        <div className="poem-metadata">
          <div className="keywords-section">
            <Text strong className="section-label">
              <HeartOutlined className="section-icon" />
              情感关键词
            </Text>
            <div className="keywords-container">
              {poem.poemResponse.keywords?.map((keyword, index) => (
                <Tag key={index} color="blue" className="keyword-tag">
                  {keyword}
                </Tag>
              ))}
            </div>
          </div>

          <div className="recommendations-section">
            <Text strong className="section-label">
              <BulbOutlined className="section-icon" />
              文学推荐
            </Text>
            <div className="recommendations-container">
              {poem.poemResponse.recommendations?.map((recommendation, index) => (
                <Tag key={index} color="purple" className="recommendation-tag">
                  {recommendation}
                </Tag>
              ))}
            </div>
          </div>
        </div>

        <div className="action-section">
          <Button
            type="primary"
            icon={<ExperimentOutlined />}
            onClick={handleGenerateAnalysis}
            loading={loadingAnalysis}
            size="large"
            className="analysis-button"
          >
            {loadingAnalysis ? '正在解读中...' : '生成解读报告'}
          </Button>
        </div>
      </Card>

      {/* 解读报告 */}
      {analysis && (
        <Card 
          className="analysis-card"
          title={
            <div className="analysis-title">
              <ExperimentOutlined className="title-icon" />
              诗歌解读报告
            </div>
          }
          bordered={false}
        >
          <div className="analysis-content">
            <div className="analysis-section">
              <Title level={4} className="analysis-section-title">
                💭 深度解读
              </Title>
              <Paragraph className="analysis-text">
                {analysis.analysis}
              </Paragraph>
            </div>

            <div className="analysis-section">
              <Title level={4} className="analysis-section-title">
                🎨 风格特点
              </Title>
              <Paragraph className="analysis-text">
                {analysis.style}
              </Paragraph>
            </div>

            <div className="analysis-section">
              <Title level={4} className="analysis-section-title">
                ✨ 创作灵感
              </Title>
              <Paragraph className="analysis-text">
                {analysis.inspiration}
              </Paragraph>
            </div>

            <div className="analysis-section">
              <Title level={4} className="analysis-section-title">
                🔗 相关主题
              </Title>
              <div className="themes-container">
                {analysis.relatedThemes?.map((theme, index) => (
                  <Tag key={index} color="gold" className="theme-tag">
                    {theme}
                  </Tag>
                ))}
              </div>
            </div>
          </div>
        </Card>
      )}
    </div>
  );
};

export default PoemDisplay;
