"use client";
import Title from "antd/es/typography/Title";
import { Spin, Alert } from "antd";
import { searchQuestionVoByPageUsingPost } from "@/api/questionController";
import QuestionTable from "@/components/QuestionTable";
import { useEffect, useState } from "react";
import { useSearchParams } from "next/navigation";
import "./index.css";

/**
 * 诗歌列表页面
 * @constructor
 */
export default function QuestionsPage() {
    const searchParams = useSearchParams();
    const searchText = searchParams.get('q') || '';
    
    const [questionList, setQuestionList] = useState([]);
    const [total, setTotal] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            setError(null);
            
            try {
                console.log('正在获取诗歌列表...', { searchText });
                const res = await searchQuestionVoByPageUsingPost({
                    searchText: searchText || undefined,
                    pageSize: 12,
                    sortField: "createTime",
                    sortOrder: "descend",
                });
                
                console.log('API响应:', res);
                
                if (res.code === 0 && res.data) {
                    setQuestionList(res.data.records ?? []);
                    setTotal(res.data.total ?? 0);
                } else {
                    setError(res.message || '获取诗歌列表失败');
                }
            } catch (e: any) {
                console.error("获取诗歌列表失败:", e);
                setError(e.message || '网络请求失败');
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [searchText]);

    if (loading) {
        return (
            <div className="questions-page-container">
                <div className="loading-container">
                    <Spin size="large" />
                    <div style={{ marginTop: 16, color: 'white' }}>正在加载诗歌列表...</div>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="questions-page-container">
                <div className="error-container">
                    <Alert
                        message="获取诗歌列表失败"
                        description={error}
                        type="error"
                        showIcon
                        action={
                            <button 
                                onClick={() => window.location.reload()} 
                                style={{ 
                                    background: '#4a7c59', 
                                    color: 'white', 
                                    border: 'none', 
                                    padding: '8px 16px', 
                                    borderRadius: '4px',
                                    cursor: 'pointer'
                                }}
                            >
                                重试
                            </button>
                        }
                    />
                </div>
            </div>
        );
    }

    return (
        <div id="questionsPage" className="questions-page-container">
            <div className="page-hero">
                <div className="hero-content">
                    <Title level={1} className="page-title">
                        📚 诗歌大全
                    </Title>
                    <div className="page-subtitle">
                        探索千年诗歌瑰宝，品味文学经典之美
                    </div>
                </div>
            </div>
            
            <div className="content-wrapper">
                <div className="stats-section">
                    <div className="stat-card">
                        <div className="stat-number">{total}</div>
                        <div className="stat-label">首诗歌</div>
                    </div>
                    <div className="stat-card">
                        <div className="stat-number">{questionList.length}</div>
                        <div className="stat-label">当前显示</div>
                    </div>
                </div>

                <QuestionTable
                    defaultQuestionList={questionList}
                    defaultTotal={total}
                    defaultSearchParams={{
                        title: searchText,
                    }}
                />
            </div>
        </div>
    );
}
