'use client';

import React, { useState } from 'react';
import { Button, Input, Card, message, Spin } from 'antd';
import { EditOutlined, SendOutlined } from '@ant-design/icons';
import { generatePoemUsingPost, PoemWithId } from '@/api/poemController';
import './index.css';

const { TextArea } = Input;

interface PoemInputProps {
  onPoemGenerated?: (poem: PoemWithId) => void;
}

const PoemInput: React.FC<PoemInputProps> = ({ onPoemGenerated }) => {
  const [inputText, setInputText] = useState('');
  const [loading, setLoading] = useState(false);

  const handleGenerate = async () => {
    if (!inputText.trim()) {
      message.warning('请输入您的心情文字');
      return;
    }

    if (inputText.trim().length < 10) {
      message.warning('请输入至少10个字符，这样AI才能更好地理解您的情感');
      return;
    }

    setLoading(true);
    try {
      const response = await generatePoemUsingPost({
        inputText: inputText.trim(),
      });

      if (response.code === 0 && response.data) {
        message.success('诗歌生成成功！');
        onPoemGenerated?.(response.data);
        setInputText(''); // 清空输入框
      } else {
        message.error(response.message || '诗歌生成失败');
      }
    } catch (error: any) {
      console.error('生成诗歌失败:', error);
      message.error(error.message || '诗歌生成失败，请稍后重试');
    } finally {
      setLoading(false);
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && (e.ctrlKey || e.metaKey)) {
      handleGenerate();
    }
  };

  return (
    <Card 
      className="poem-input-card"
      title={
        <div className="card-title">
          <EditOutlined className="title-icon" />
          风之札记 - 记录您的心境
        </div>
      }
      bordered={false}
    >
      <div className="input-container">
        <TextArea
          value={inputText}
          onChange={(e) => setInputText(e.target.value)}
          onKeyDown={handleKeyPress}
          placeholder="在这里写下您的心情、日记或者零散的想法...&#10;&#10;比如：今天的阳光很温暖，让我想起了童年的午后...&#10;&#10;按 Ctrl+Enter 快速生成"
          className="poem-textarea"
          autoSize={{ minRows: 6, maxRows: 12 }}
          maxLength={1000}
          showCount
        />
        <div className="action-bar">
          <div className="tips">
            💡 提示：描述得越详细，生成的诗歌越贴合您的心境
          </div>
          <Button
            type="primary"
            icon={<SendOutlined />}
            loading={loading}
            onClick={handleGenerate}
            size="large"
            className="generate-button"
            disabled={!inputText.trim()}
          >
            {loading ? '正在创作中...' : '生成诗歌'}
          </Button>
        </div>
      </div>
    </Card>
  );
};

export default PoemInput;
