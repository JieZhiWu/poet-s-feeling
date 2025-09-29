import { Result, Button, Space } from "antd";
import { LockOutlined, HomeOutlined, UserOutlined } from "@ant-design/icons";
import React from "react";
import './forbidden.css';

/**
 * 无权限访问
 * @constructor
 */
const Forbidden = () => {
  return (
    <div className="forbidden-container">
      <div className="forbidden-content">
        <div className="forbidden-icon">
          <LockOutlined />
        </div>
        <Result
          status="403"
          title={
            <div className="forbidden-title">
              <span>访问受限</span>
            </div>
          }
          subTitle={
            <div className="forbidden-subtitle">
              <p>抱歉，您当前的权限无法访问此页面</p>
              <p>此页面需要 <span className="admin-badge">管理员</span> 权限才能访问</p>
            </div>
          }
          extra={
            <Space direction="vertical" size="large">
              <div className="forbidden-suggestions">
                <h4>您可以尝试：</h4>
                <ul>
                  <li>联系系统管理员获取相应权限</li>
                  <li>确认您已使用正确的管理员账户登录</li>
                  <li>返回主页继续使用其他功能</li>
                </ul>
              </div>
              <Space>
                <Button type="primary" icon={<HomeOutlined />} href="/">
                  返回主页
                </Button>
                <Button icon={<UserOutlined />} href="/user/login">
                  重新登录
                </Button>
              </Space>
            </Space>
          }
        />
      </div>
    </div>
  );
};

export default Forbidden;