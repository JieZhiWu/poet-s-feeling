'use client';

import React, { useState } from 'react';
import { Layout, Typography, Space, Divider } from 'antd';
import { PoemWithId } from '@/api/poemController';
import PoemInput from '@/components/PoemInput';
import PoemDisplay from '@/components/PoemDisplay';
import './index.css';

const { Content } = Layout;
const { Title, Paragraph } = Typography;

const PoemPage: React.FC = () => {
  const [currentPoem, setCurrentPoem] = useState<PoemWithId | null>(null);

  const handlePoemGenerated = (poem: PoemWithId) => {
    setCurrentPoem(poem);
    // 滚动到诗歌展示区域
    setTimeout(() => {
      const poemDisplay = document.querySelector('.poem-display-container');
      if (poemDisplay) {
        poemDisplay.scrollIntoView({ behavior: 'smooth' });
      }
    }, 100);
  };

  return (
    <Layout className="poem-page-layout">
      <Content className="poem-page-content">
        <div className="page-container">
          {/* 页面头部 */}
          <div className="page-header">
            <div className="header-content">
              <Title level={1} className="page-title">
                🌟 风之札记
              </Title>
              <Paragraph className="page-description">
                将您的心境化作诗篇，让AI为您的情感注入诗意的灵魂
              </Paragraph>
            </div>
            <div className="header-decoration">
              <div className="floating-element element-1">✨</div>
              <div className="floating-element element-2">🍃</div>
              <div className="floating-element element-3">🌙</div>
              <div className="floating-element element-4">⭐</div>
            </div>
          </div>

          <Divider className="section-divider" />

          {/* 输入区域 */}
          <div className="input-section">
            <PoemInput onPoemGenerated={handlePoemGenerated} />
          </div>

          {/* 诗歌展示区域 */}
          {currentPoem && (
            <div className="poem-section">
              <div className="section-header">
                <Title level={2} className="section-title">
                  您的专属诗篇
                </Title>
                <Paragraph className="section-subtitle">
                  基于您的心境，AI为您创作的独特诗歌
                </Paragraph>
              </div>
              <PoemDisplay poem={currentPoem} />
            </div>
          )}

          {/* 使用说明 */}
          {!currentPoem && (
            <div className="guide-section">
              <Title level={3} className="guide-title">
                ✍️ 如何使用
              </Title>
              <div className="guide-steps">
                <div className="guide-step">
                  <div className="step-number">1</div>
                  <div className="step-content">
                    <div className="step-title">记录心境</div>
                    <div className="step-description">
                      写下您的日记、心情或者任何想法
                    </div>
                  </div>
                </div>
                <div className="guide-step">
                  <div className="step-number">2</div>
                  <div className="step-content">
                    <div className="step-title">AI创作</div>
                    <div className="step-description">
                      AI理解您的情感，创作专属诗歌
                    </div>
                  </div>
                </div>
                <div className="guide-step">
                  <div className="step-number">3</div>
                  <div className="step-content">
                    <div className="step-title">深度解读</div>
                    <div className="step-description">
                      获取诗歌的深入分析和创作建议
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      </Content>
    </Layout>
  );
};

export default PoemPage;
