📘 项目开发文档：《日记生成器》
一、项目概述
项目名称：日记生成器
平台：Android 原生（Kotlin）
目标：根据用户心情、地点、表情等自动生成格式化日记文本，支持导出、保存、设置。

二、核心功能模块

功能模块	简介
主界面生成模块	支持用户选择心情、输入表情与地点，点击按钮生成格式化日记文本
设置界面	保存常用地点、自定义表情、随机表情选项
启动页	自定义渐变背景+动画展示，几秒后自动跳转至主页面
历史记录模块	（已完成，用户选择不保留）
分享与导出功能	支持将日记复制至剪贴板或分享至外部应用
UI 适配	支持深色与浅色主题自动切换，布局自适应不同分辨率
三、技术实现
1. 开发语言
   Kotlin

2. 开发工具
   Android Studio

Gradle 构建系统

3. 关键依赖库
   Material Components

CardView

AppCompat

SQLite（用于存储情绪推荐语和表情）

4. 数据库结构（简略）
   sql
   复制
   编辑
   CREATE TABLE MoodAssets (
   id INTEGER PRIMARY KEY AUTOINCREMENT,
   mood TEXT,
   type TEXT,         -- 'text' 或 'face'
   content TEXT
   );
5. 动画与 UI
   启动页使用自定义 SplashLogoView.kt 绘制向量图标，无需图片资源。

渐变背景由 bg_gradient.xml 实现。

fade_in.xml 用于 Logo 与标题的淡入效果。

