'use client';

import React, { useState, useEffect } from 'react';
import { Card, List, Typography, Tag, Button, Empty, Spin, message, Modal } from 'antd';
import { BookOutlined, EyeOutlined, HistoryOutlined } from '@ant-design/icons';
import { getPoemByIdUsingGet, PoemResponse } from '@/api/poemController';
import './index.css';

const { Title, Paragraph, Text } = Typography;

// 模拟的历史数据结构
interface PoemHistoryItem {
  id: number;
  title: string;
  createTime: string;
  keywords: string[];
  poem: string;
}

const PoemHistoryPage: React.FC = () => {
  const [historyList, setHistoryList] = useState<PoemHistoryItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedPoem, setSelectedPoem] = useState<PoemResponse | null>(null);
  const [modalVisible, setModalVisible] = useState(false);
  const [detailLoading, setDetailLoading] = useState(false);

  // 模拟数据加载
  useEffect(() => {
    loadHistoryData();
  }, []);

  const loadHistoryData = async () => {
    setLoading(true);
    // 这里应该调用真实的API获取用户的诗歌历史
    // 暂时使用模拟数据
    setTimeout(() => {
      const mockData: PoemHistoryItem[] = [
        {
          id: 1,
          title: "午后的阳光",
          createTime: "2024-01-15 14:30:00",
          keywords: ["温暖", "宁静", "回忆"],
          poem: "午后的阳光\n透过百叶窗\n在桌面上\n画出条纹..."
        },
        {
          id: 2,
          title: "雨夜思绪",
          createTime: "2024-01-10 20:15:00",
          keywords: ["雨夜", "思念", "孤独"],
          poem: "雨滴敲打着窗棂\n思绪如丝\n缠绕在\n这个寂静的夜晚..."
        }
      ];
      setHistoryList(mockData);
      setLoading(false);
    }, 1000);
  };

  const handleViewDetail = async (poemId: number) => {
    setDetailLoading(true);
    try {
      const response = await getPoemByIdUsingGet({ id: poemId });
      if (response.code === 0 && response.data) {
        setSelectedPoem(response.data);
        setModalVisible(true);
      } else {
        message.error(response.message || '获取诗歌详情失败');
      }
    } catch (error: any) {
      console.error('获取诗歌详情失败:', error);
      // 由于是模拟数据，使用历史列表中的数据
      const mockPoem = historyList.find(item => item.id === poemId);
      if (mockPoem) {
        setSelectedPoem({
          title: mockPoem.title,
          poem: mockPoem.poem,
          keywords: mockPoem.keywords,
          recommendations: ["相关作品1", "相关作品2"]
        });
        setModalVisible(true);
      } else {
        message.error('获取诗歌详情失败');
      }
    } finally {
      setDetailLoading(false);
    }
  };

  const renderPoemCard = (item: PoemHistoryItem) => (
    <Card
      key={item.id}
      className="poem-history-card"
      hoverable
      actions={[
        <Button
          key="view"
          type="link"
          icon={<EyeOutlined />}
          loading={detailLoading}
          onClick={() => handleViewDetail(item.id)}
        >
          查看详情
        </Button>
      ]}
    >
      <div className="card-header">
        <Title level={4} className="poem-title">
          <BookOutlined className="title-icon" />
          {item.title}
        </Title>
        <Text type="secondary" className="create-time">
          {item.createTime}
        </Text>
      </div>
      
      <div className="poem-preview">
        <Paragraph className="poem-content" ellipsis={{ rows: 3 }}>
          {item.poem}
        </Paragraph>
      </div>
      
      <div className="poem-keywords">
        {item.keywords.map((keyword, index) => (
          <Tag key={index} color="blue" className="keyword-tag">
            {keyword}
          </Tag>
        ))}
      </div>
    </Card>
  );

  return (
    <div className="poem-history-page">
      <div className="page-header">
        <Title level={1} className="page-title">
          <HistoryOutlined className="title-icon" />
          我的诗歌作品
        </Title>
        <Paragraph className="page-description">
          回顾您创作的美好诗篇，每一首都记录着独特的心境时刻
        </Paragraph>
      </div>

      <div className="history-content">
        <Spin spinning={loading}>
          {historyList.length > 0 ? (
            <List
              grid={{
                gutter: 24,
                xs: 1,
                sm: 1,
                md: 2,
                lg: 2,
                xl: 3,
                xxl: 3,
              }}
              dataSource={historyList}
              renderItem={renderPoemCard}
            />
          ) : (
            !loading && (
              <Empty
                className="empty-state"
                image={Empty.PRESENTED_IMAGE_SIMPLE}
                description={
                  <div>
                    <Text type="secondary">还没有创作过诗歌</Text>
                    <br />
                    <Button type="link" href="/poem">
                      去创作第一首诗吧
                    </Button>
                  </div>
                }
              />
            )
          )}
        </Spin>
      </div>

      {/* 诗歌详情模态框 */}
      <Modal
        title={selectedPoem?.title}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        footer={null}
        width={800}
        className="poem-detail-modal"
      >
        {selectedPoem && (
          <div className="poem-detail-content">
            <div className="poem-text-section">
              <Paragraph className="modal-poem-text">
                {selectedPoem.poem.split('\n').map((line, index) => (
                  <div key={index} className="modal-poem-line">
                    {line}
                  </div>
                ))}
              </Paragraph>
            </div>

            <div className="poem-meta-section">
              <div className="meta-item">
                <Text strong>情感关键词：</Text>
                <div className="meta-tags">
                  {selectedPoem.keywords?.map((keyword, index) => (
                    <Tag key={index} color="blue">
                      {keyword}
                    </Tag>
                  ))}
                </div>
              </div>

              <div className="meta-item">
                <Text strong>文学推荐：</Text>
                <div className="meta-tags">
                  {selectedPoem.recommendations?.map((recommendation, index) => (
                    <Tag key={index} color="purple">
                      {recommendation}
                    </Tag>
                  ))}
                </div>
              </div>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default PoemHistoryPage;
