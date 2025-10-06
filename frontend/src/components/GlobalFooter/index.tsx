"use client";
import React from "react";
import { BookOutlined, EditOutlined, HeartOutlined } from "@ant-design/icons";
import "./index.css";

/**
 * 全局底部栏组件
 */
export default function GlobalFooter() {
  const currentYear = new Date().getFullYear();
  return (
    <div className="global-footer">
      <div className="footer-content">
        <div className="footer-decorations">
          <EditOutlined className="footer-icon pen" />
          <BookOutlined className="footer-icon book" />
          <HeartOutlined className="footer-icon heart" />
        </div>
        <div className="footer-text">
          © {currentYear} 芥雾浮心 - 在线诗歌平台
        </div>
        <div className="footer-quote">
          "文章千古事，得失寸心知"
        </div>
      </div>
    </div>
  );
}