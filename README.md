# mdx

<!-- Plugin description -->

Markdown X

<!-- Plugin description end -->

## 功能清单

- [x] 在 Markdown 中编写 `@java-class FQN`，会显示一个行标记图标，点击就能跳转到该 FQN 对应的 Java 类
- [x] 从 Java 类跳转到引用它的 Markdown 文档
- [x] 重命名 Java 类时，同时重命名引用它的 Markdown 文档里的 `@java-class` 标记中的类名
- [ ] 使 `@java-class FQN` 变为可点击，支持 `Ctrl + 左击` 直接跳转
- [ ] 鼠标光标悬浮 `@java-class FQN` 时，显示一个 Popup，直接显示引用的代码
- [ ] Markdown 预览模式下，解析 `@java-class FQN` 为代码块并展示
- [ ] 保存 Markdown 时自动在中英数字间插入空格
- [ ] 支持 Wiki 形式的双中括号链接
- [ ] 更多标记支持：`@java-method`

## 问题

* 自定义 Markdown PsiReferenceContributor 不生效
