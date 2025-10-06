"use client";
import { Avatar, Card, Col, Row, Button, Modal, Form, Input, Upload, message, Descriptions, Tag } from "antd";
import { EditOutlined, UploadOutlined, UserOutlined, CalendarOutlined, SafetyOutlined, BookOutlined, CrownOutlined, SettingOutlined } from "@ant-design/icons";
import { useSelector, useDispatch } from "react-redux";
import { RootState } from "@/stores";
import Title from "antd/es/typography/Title";
import Paragraph from "antd/es/typography/Paragraph";
import { useState, useEffect } from "react";
import CalendarChart from "@/app/user/center/components/CalendarChart";
import { updateMyUserUsingPost, getLoginUserUsingGet } from "@/api/userController";
import { uploadFileUsingPost } from "@/api/fileController";
import { setLoginUser } from "@/stores/loginUser";
import "./index.css";

/**
 * 用户中心页面
 * @constructor
 */
export default function UserCenterPage() {
  // 获取登录用户信息
  const loginUser = useSelector((state: RootState) => state.loginUser);
  const dispatch = useDispatch();
  // 便于复用，新起一个变量
  const user = loginUser;
  // 控制菜单栏的 Tab 高亮
  const [activeTabKey, setActiveTabKey] = useState<string>("record");
  // 控制编辑模态框
  const [isEditModalVisible, setIsEditModalVisible] = useState(false);
  // 表单实例
  const [form] = Form.useForm();
  // 上传加载状态
  const [uploading, setUploading] = useState(false);

  // 格式化注册时间
  const formatDate = (dateString: string) => {
    if (!dateString) return "未知";
    return new Date(dateString).toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  // 获取角色标签颜色
  const getRoleTagColor = (role: string) => {
    switch (role) {
      case 'admin':
        return 'red';
      case 'user':
        return 'blue';
      case 'ban':
        return 'default';
      default:
        return 'blue';
    }
  };

  // 获取角色中文名
  const getRoleText = (role: string) => {
    switch (role) {
      case 'admin':
        return '管理员';
      case 'user':
        return '普通用户';
      case 'ban':
        return '已封禁';
      default:
        return '普通用户';
    }
  };

  // 打开编辑模态框
  const handleEditProfile = () => {
    form.setFieldsValue({
      userName: user.userName,
      userProfile: user.userProfile,
      userAvatar: user.userAvatar,
    });
    setIsEditModalVisible(true);
  };

  // 提交表单
  const handleSubmit = async (values: any) => {
    try {
      console.log('提交的数据:', values);
      const res = await updateMyUserUsingPost(values);
      // 响应拦截器已经处理了错误情况，到这里说明成功了
      console.log('更新响应:', res);
      message.success('更新个人信息成功！');
      setIsEditModalVisible(false);
      // 重新获取用户信息
      try {
        const userRes = await getLoginUserUsingGet();
        console.log('获取用户信息响应:', userRes);
        if (userRes && userRes.data) {
          dispatch(setLoginUser(userRes.data));
        }
      } catch (getUserError) {
        console.error('重新获取用户信息失败:', getUserError);
        // 更新成功但获取用户信息失败，可以刷新页面
        window.location.reload();
      }
    } catch (error: any) {
      console.error('更新个人信息失败:', error);
      // 请求拦截器已经显示了错误消息，这里不再重复显示
    }
  };

  // 头像上传处理（使用自定义上传）
  const handleAvatarUpload = async (file: File) => {
    // 文件验证
    const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/webp';
    if (!isJpgOrPng) {
      message.error('只能上传 JPG/PNG/WebP 格式的图片！');
      return false;
    }
    
    const isLt2M = file.size / 1024 / 1024 < 2;
    if (!isLt2M) {
      message.error('图片大小不能超过 2MB！');
      return false;
    }

    setUploading(true);
    try {
      // 使用我们的API上传文件（会自动携带登录凭证）
      const response = await uploadFileUsingPost(
        { biz: 'user_avatar' },
        {},
        file
      );
      
      console.log('Upload response:', response);
      
      if (response.code === 0 && response.data) {
        form.setFieldsValue({ userAvatar: response.data });
        message.success('头像上传成功！');
      } else {
        message.error(response.message || '头像上传失败');
      }
    } catch (error: any) {
      console.error('Upload error:', error);
      message.error(error.message || '头像上传失败，请稍后重试');
    } finally {
      setUploading(false);
    }
    
    return false; // 阻止Upload组件自动上传
  };

  return (
    <div id="userCenterPage" className="max-width-content">
      <Row gutter={[16, 16]}>
        {/* 左侧用户信息卡片 */}
        <Col xs={24} md={8}>
          <Card 
            style={{ textAlign: "center" }}
            actions={[
              <Button 
                key="edit" 
                type="link" 
                icon={<EditOutlined />}
                onClick={handleEditProfile}
              >
                编辑资料
              </Button>
            ]}
          >
            <Avatar 
              src={user.userAvatar} 
              size={100} 
              icon={<UserOutlined />}
              style={{ marginBottom: 16 }}
            />
            <Title level={3} style={{ marginBottom: 8 }}>
              {user.userName || '未设置昵称'}
            </Title>
            <Tag color={getRoleTagColor(user.userRole || 'user')} style={{ marginBottom: 16 }}>
              {getRoleText(user.userRole || 'user')}
            </Tag>
            <Paragraph type="secondary" style={{ marginBottom: 16 }}>
              {user.userProfile || '这个人很懒，什么都没留下～'}
            </Paragraph>
            
            {/* 用户详细信息 */}
            <Descriptions column={1} size="small" bordered>
              <Descriptions.Item label={<><UserOutlined /> 用户ID</>}>
                {user.id}
              </Descriptions.Item>
              <Descriptions.Item label={<><CalendarOutlined /> 注册时间</>}>
                {formatDate(user.createTime || '')}
              </Descriptions.Item>
              <Descriptions.Item label={<><SafetyOutlined /> 账号状态</>}>
                <Tag color={user.userRole === 'ban' ? 'red' : 'green'}>
                  {user.userRole === 'ban' ? '已封禁' : '正常'}
                </Tag>
              </Descriptions.Item>
            </Descriptions>
          </Card>
        </Col>

        {/* 右侧内容区域 */}
        <Col xs={24} md={16}>
          <Card
            tabList={[
              {
                key: "record",
                label: (
                  <span>
                    <BookOutlined style={{ marginRight: 8, color: '#6b8e23' }} />
                    阅读记录
                  </span>
                ),
              },
              {
                key: "info",
                label: (
                  <span>
                    <UserOutlined style={{ marginRight: 8, color: '#4a7c59' }} />
                    个人信息
                  </span>
                ),
              },
              {
                key: "settings",
                label: (
                  <span>
                    <SettingOutlined style={{ marginRight: 8, color: '#2d5016' }} />
                    设置
                  </span>
                ),
              },
            ]}
            activeTabKey={activeTabKey}
            onTabChange={(key: string) => {
              setActiveTabKey(key);
            }}
          >
            {activeTabKey === "record" && (
              <div>
                <Title level={4} style={{ marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
                  <BookOutlined style={{ color: '#6b8e23' }} />
                  学习进度统计
                  <span style={{ fontSize: 12, opacity: 0.6, marginLeft: 8 }}>📈</span>
                </Title>
                <CalendarChart />
              </div>
            )}
            {activeTabKey === "info" && (
              <div>
                <Title level={4} style={{ marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
                  <UserOutlined style={{ color: '#4a7c59' }} />
                  详细信息
                  <span style={{ fontSize: 12, opacity: 0.6, marginLeft: 8 }}>📝</span>
                </Title>
                <Descriptions bordered column={2}>
                  <Descriptions.Item label="用户昵称" span={2}>
                    {user.userName || '未设置'}
                  </Descriptions.Item>
                  <Descriptions.Item label="个人简介" span={2}>
                    {user.userProfile || '未设置'}
                  </Descriptions.Item>
                  <Descriptions.Item label="用户角色">
                    <Tag color={getRoleTagColor(user.userRole || 'user')}>
                      {getRoleText(user.userRole || 'user')}
                    </Tag>
                  </Descriptions.Item>
                  <Descriptions.Item label="注册时间">
                    {formatDate(user.createTime || '')}
                  </Descriptions.Item>
                  <Descriptions.Item label="最后更新">
                    {formatDate(user.updateTime || '')}
                  </Descriptions.Item>
                  <Descriptions.Item label="用户ID">
                    {user.id}
                  </Descriptions.Item>
                </Descriptions>
              </div>
            )}
            {activeTabKey === "settings" && (
              <div>
                <Title level={4} style={{ marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
                  <SettingOutlined style={{ color: '#2d5016' }} />
                  账号设置
                  <span style={{ fontSize: 12, opacity: 0.6, marginLeft: 8 }}>🔧</span>
                </Title>
                <Card>
                  <p>🚧 更多设置功能正在开发中...</p>
                  <p>💡 如需修改个人信息，请点击左侧的&ldquo;编辑资料&rdquo;按钮</p>
                </Card>
              </div>
            )}
          </Card>
        </Col>
      </Row>

      {/* 编辑个人信息模态框 */}
      <Modal
        title={
          <span style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <EditOutlined />
            编辑个人信息
            <span style={{ fontSize: 12, opacity: 0.8, marginLeft: 8 }}>✏️</span>
          </span>
        }
        open={isEditModalVisible}
        onCancel={() => setIsEditModalVisible(false)}
        onOk={() => form.submit()}
        confirmLoading={uploading}
        width={600}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
        >
          <Form.Item
            label="头像"
            name="userAvatar"
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
              <Avatar 
                src={form.getFieldValue('userAvatar') || user.userAvatar} 
                size={64} 
                icon={<UserOutlined />}
              />
              <Upload
                name="file"
                showUploadList={false}
                beforeUpload={handleAvatarUpload}
                customRequest={() => {}} // 禁用默认上传
              >
                <Button icon={<UploadOutlined />} loading={uploading}>
                  {uploading ? '上传中...' : '更换头像'}
                </Button>
              </Upload>
            </div>
          </Form.Item>

          <Form.Item
            label="昵称"
            name="userName"
            rules={[
              { required: true, message: '请输入昵称！' },
              { max: 20, message: '昵称不能超过20个字符！' }
            ]}
          >
            <Input placeholder="请输入您的昵称" />
          </Form.Item>

          <Form.Item
            label="个人简介"
            name="userProfile"
            rules={[
              { max: 200, message: '个人简介不能超过200个字符！' }
            ]}
          >
            <Input.TextArea 
              rows={4} 
              placeholder="介绍一下自己吧～" 
              showCount 
              maxLength={200}
            />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
