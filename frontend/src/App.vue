<script setup>
import { computed, onMounted, ref, watch } from 'vue';
import { categories as fallbackCategories, hotDeals, products as fallbackProducts } from './data/market';
import {
  alipayPageUrl,
  createOrder,
  fetchCategories,
  fetchMarketSummary,
  fetchOrders,
  fetchProducts,
  askAgent,
  login,
  phoneLogin,
  register,
  sendSmsCode,
  mockPay
} from './services/api';

const activeCategory = ref('全部');
const keyword = ref('');
const sortMode = ref('最新');
const activeView = ref('login');
const categories = ref(fallbackCategories);
const products = ref(fallbackProducts);
const orders = ref([]);
const isLoading = ref(false);
const actionLoading = ref(false);
const loadError = ref('');
const actionMessage = ref('');
const backendConnected = ref(false);
const currentUser = ref(null);
const authMode = ref('login');
const loginForm = ref({ username: 'buyer01', password: '123456' });
const phoneLoginForm = ref({ phone: '', code: '' });
const registerForm = ref({
  username: '',
  password: '',
  realName: '',
  studentNo: '',
  phone: '',
  email: ''
});
const rememberCampusLogin = ref(false);
const smsCountdown = ref(0);
const agentQuestion = ref('');
const agentLoading = ref(false);
const agentMessages = ref([
  {
    role: 'assistant',
    text: '我是校园集市 AI 助手，可以帮你找商品、说明下单流程、提醒交易安全，也能回答发布和支付问题。'
  }
]);
const publishForm = ref({
  title: '',
  category: '教材资料',
  price: '',
  tradePlace: '',
  condition: 'GOOD',
  description: ''
});
const marketSummary = ref({
  pendingChats: 3,
  activeOrders: 2,
  finishedOrders: 9,
  todayNewProducts: 36,
  marketValue: null
});

const navItems = [
  { key: 'home', label: '首页' },
  { key: 'categories', label: '分类' },
  { key: 'publish', label: '发布商品' },
  { key: 'orders', label: '我的订单' },
  { key: 'messages', label: '消息' },
  { key: 'agent', label: 'AI助手' }
];
const loginHighlights = [
  { value: '6000+', label: '累计闲置' },
  { value: '2800+', label: '校内用户' },
  { value: '4.9/5', label: '用户评分' }
];
const loginFeatureStack = [
  { badge: '人', title: '真实校内', text: '校内实名认证' },
  { badge: '包', title: '安全交易', text: '平台担保交易' },
  { badge: '盾', title: '快捷支付', text: '支持多种支付' }
];
const socialLoginOptions = [
  { label: '微信登录', icon: '微' },
  { label: 'QQ登录', icon: 'Q' },
  { label: '手机号登录', icon: '机' }
];
const filterTabs = ['全部', '最新', '价格低到高', '成色良好'];
const categoryPalette = ['#16a34a', '#0891b2', '#f97316', '#4f46e5', '#db2777', '#0f766e'];
const categoryAccents = {
  教材资料: '#16a34a',
  数码产品: '#0891b2',
  生活用品: '#f97316',
  运动器材: '#4f46e5',
  服饰鞋包: '#db2777'
};
const conditionLabels = {
  NEW: '全新',
  LIKE_NEW: '九成新',
  GOOD: '成色良好',
  FAIR: '轻微使用'
};
const orderStatusLabels = {
  PENDING: '待支付',
  PAID: '已支付',
  FINISHED: '已完成',
  CANCELLED: '已取消'
};
const viewMeta = {
  home: ['二手交易 · 校内自提 · 安全沟通', '发现同学正在出手的好物'],
  categories: ['分类浏览 · 快速筛选 · 数据统计', '按商品分类查看校园好物'],
  publish: ['发布商品 · 表单演示 · 后续可接数据库', '填写商品信息并预览发布流程'],
  orders: ['订单中心 · 支付状态 · 交易追踪', '查看我的订单和支付入口'],
  messages: ['消息沟通 · 校内面交 · 安全提醒', '查看商品咨询与系统提醒'],
  agent: ['Agent模式 · DeepSeek · LangChain4j', '校园集市 AI 小助手']
};
const demoMessages = [
  { title: '系统提醒', text: '下单后请优先使用平台订单记录沟通交易地点。', time: '刚刚' },
  { title: '商品咨询', text: '数据库教材是否还可以在图书馆门口交易？', time: '12 分钟前' },
  { title: '支付提醒', text: '支付宝真实支付需要配置开放平台应用参数和公网回调。', time: '今天' }
];
const agentQuickPrompts = [
  '帮我找便宜的教材',
  '怎么发布二手商品？',
  '下单后怎么支付？',
  '校园面交要注意什么？'
];

const currentViewEyebrow = computed(() => viewMeta[activeView.value]?.[0] ?? viewMeta.home[0]);
const currentViewTitle = computed(() => viewMeta[activeView.value]?.[1] ?? viewMeta.home[1]);
const campusPreviewProducts = computed(() => products.value.slice(0, 3));
const loginMarqueeProducts = computed(() => products.value.slice(0, 3));

const filteredProducts = computed(() => {
  let list = products.value.filter((item) => {
    const matchCategory = activeCategory.value === '全部' || item.category === activeCategory.value;
    const matchKeyword = !keyword.value || item.title.includes(keyword.value) || item.category.includes(keyword.value);
    return matchCategory && matchKeyword;
  });

  if (sortMode.value === '价格低到高') {
    list = [...list].sort((a, b) => a.price - b.price);
  }

  if (sortMode.value === '成色良好') {
    list = list.filter((item) => item.condition.includes('良好') || item.condition.includes('新'));
  }

  return list;
});

const totalValue = computed(() => products.value.reduce((sum, item) => sum + item.price, 0));
const marketValue = computed(() => Number(marketSummary.value.marketValue ?? totalValue.value));
const pendingOrderCount = computed(() => orders.value.filter((item) => item.status === 'PENDING').length);
const paidOrderCount = computed(() => orders.value.filter((item) => item.status === 'PAID').length);

function formatMoney(value) {
  return Number(value || 0).toLocaleString('zh-CN', { maximumFractionDigits: 2 });
}

function toDisplayCategory(item, index) {
  return {
    id: item.id,
    name: item.name,
    count: item.productCount ?? item.count ?? 0,
    accent: categoryAccents[item.name] || categoryPalette[index % categoryPalette.length]
  };
}

function toDisplayProduct(item, index = 0) {
  const price = Number(item.price ?? 0);
  return {
    id: item.productId ?? item.id ?? index + 1,
    title: item.title,
    category: item.categoryName ?? item.category,
    price,
    originalPrice: Number(item.originalPrice ?? Math.ceil(price * 2.36)),
    condition: conditionLabels[item.conditionLevel] ?? item.condition ?? '成色良好',
    location: item.tradePlace ?? item.location ?? '校内自提',
    seller: item.sellerUsername ?? item.seller ?? '同学',
    views: item.viewCount ?? item.views ?? 0,
    image: item.coverImage ?? item.image,
    createdAt: item.createdAt
  };
}

function toDisplayOrder(item) {
  return {
    ...item,
    amount: Number(item.amount ?? 0),
    statusText: orderStatusLabels[item.status] ?? item.status
  };
}

function selectedCategoryId() {
  if (activeCategory.value === '全部') {
    return undefined;
  }
  return categories.value.find((item) => item.name === activeCategory.value)?.id;
}

function resetMarketFilters() {
  activeCategory.value = filterTabs[0];
  keyword.value = '';
  sortMode.value = filterTabs[1];
}

function switchView(view) {
  activeView.value = view;
  if (view === 'login') {
    return;
  }
  if (view === 'home') {
    resetMarketFilters();
  }
  if (view === 'home' || view === 'categories') {
    bootstrapMarket();
  }
  if (view === 'orders') {
    loadOrders();
  }
}

function openCategory(category) {
  activeCategory.value = category.name;
  activeView.value = 'home';
  loadProducts();
}

async function loadProducts() {
  if (!backendConnected.value) {
    return;
  }

  isLoading.value = true;
  try {
    const page = await fetchProducts({
      categoryId: selectedCategoryId(),
      keyword: keyword.value
    });
    products.value = (page.content ?? []).map(toDisplayProduct);
    loadError.value = '';
  } catch (error) {
    loadError.value = '后端连接中断，当前显示本地演示数据';
    products.value = fallbackProducts.map(toDisplayProduct);
  } finally {
    isLoading.value = false;
  }
}

async function loadOrders() {
  if (!currentUser.value) {
    orders.value = [];
    return;
  }

  try {
    const data = await fetchOrders(currentUser.value.userId);
    orders.value = data.map(toDisplayOrder);
  } catch (error) {
    actionMessage.value = error.message;
  }
}

async function bootstrapMarket() {
  isLoading.value = true;
  try {
    const [categoryData, productPage, summaryData] = await Promise.all([
      fetchCategories(),
      fetchProducts(),
      fetchMarketSummary()
    ]);
    categories.value = categoryData.map(toDisplayCategory);
    products.value = (productPage.content ?? []).map(toDisplayProduct);
    marketSummary.value = { ...marketSummary.value, ...summaryData };
    backendConnected.value = true;
    loadError.value = '';
  } catch (error) {
    backendConnected.value = false;
    categories.value = fallbackCategories;
    products.value = fallbackProducts.map(toDisplayProduct);
    loadError.value = '后端未启动，当前显示本地演示数据';
  } finally {
    isLoading.value = false;
  }
}

async function loginUser() {
  actionLoading.value = true;
  try {
    currentUser.value = await login(loginForm.value.username, loginForm.value.password);
    actionMessage.value = `${currentUser.value.realName || currentUser.value.username} 登录成功`;
    activeView.value = 'home';
    await loadOrders();
  } catch (error) {
    actionMessage.value = error.message;
  } finally {
    actionLoading.value = false;
  }
}

function validatePhoneLoginForm(requireCode = false) {
  if (!/^1[3-9]\d{9}$/.test(phoneLoginForm.value.phone)) {
    return '请输入有效的中国大陆手机号';
  }
  if (requireCode && !/^\d{6}$/.test(phoneLoginForm.value.code)) {
    return '请输入短信里的 6 位验证码';
  }
  return '';
}

async function requestSmsCode() {
  const error = validatePhoneLoginForm(false);
  if (error) {
    actionMessage.value = error;
    return;
  }

  actionLoading.value = true;
  try {
    const message = await sendSmsCode(phoneLoginForm.value.phone);
    actionMessage.value = `${message}，请查看手机短信`;
    smsCountdown.value = 60;
    const timer = window.setInterval(() => {
      smsCountdown.value -= 1;
      if (smsCountdown.value <= 0) {
        window.clearInterval(timer);
      }
    }, 1000);
  } catch (error) {
    actionMessage.value = error.message;
  } finally {
    actionLoading.value = false;
  }
}

async function loginByPhone() {
  const error = validatePhoneLoginForm(true);
  if (error) {
    actionMessage.value = error;
    return;
  }

  actionLoading.value = true;
  try {
    currentUser.value = await phoneLogin(phoneLoginForm.value.phone, phoneLoginForm.value.code);
    actionMessage.value = `${currentUser.value.realName || currentUser.value.username} 手机号登录成功`;
    activeView.value = 'home';
    await loadOrders();
  } catch (error) {
    actionMessage.value = error.message;
  } finally {
    actionLoading.value = false;
  }
}

function validateRegisterForm() {
  if (registerForm.value.password.length <= 6 || registerForm.value.password.length >= 12) {
    return '密码必须大于6位小于12位，也就是 7-11 位';
  }
  if (registerForm.value.realName.length <= 2) {
    return '姓名至少 3 位，中文英文都可以';
  }
  if (!/^[\u4e00-\u9fa5A-Za-z]+$/.test(registerForm.value.realName)) {
    return '姓名只能填写中文或英文';
  }
  if (!registerForm.value.username || !registerForm.value.studentNo) {
    return '请填写账号和学号';
  }
  return '';
}

async function registerUser() {
  const error = validateRegisterForm();
  if (error) {
    actionMessage.value = error;
    return;
  }

  actionLoading.value = true;
  try {
    const user = await register(registerForm.value);
    actionMessage.value = `${user.realName} 注册成功，请直接登录`;
    loginForm.value.username = registerForm.value.username;
    loginForm.value.password = registerForm.value.password;
    authMode.value = 'login';
  } catch (error) {
    actionMessage.value = error.message;
  } finally {
    actionLoading.value = false;
  }
}

function enterMarketAsGuest() {
  activeView.value = 'home';
  actionMessage.value = '';
}

function showRegisterForm() {
  authMode.value = 'register';
  actionMessage.value = '';
}

function showLoginForm() {
  authMode.value = 'login';
  actionMessage.value = '';
}

function showPhoneLoginForm() {
  authMode.value = 'phone';
  actionMessage.value = '';
}

function handleSocialLogin(option) {
  if (option.label === '手机号登录') {
    showPhoneLoginForm();
    return;
  }
  actionMessage.value = `${option.label} 暂未接入，当前请使用账号或手机号登录。`;
}

function forgotPassword() {
  actionMessage.value = '可以切换到手机号登录，通过短信验证码进入系统。';
}

function submitPublishDemo() {
  if (!publishForm.value.title || !publishForm.value.price) {
    actionMessage.value = '请填写商品名称和价格';
    return;
  }
  actionMessage.value = `已生成发布演示：${publishForm.value.title}，后续可接入商品发布接口`;
  activeView.value = 'home';
}

async function placeOrder(product) {
  if (!currentUser.value) {
    actionMessage.value = '请先登录 buyer01 / 123456';
    activeView.value = 'login';
    return;
  }

  actionLoading.value = true;
  try {
    const order = await createOrder({
      productId: product.id,
      buyerId: currentUser.value.userId,
      buyerRemark: `${product.location} 面交`
    });
    actionMessage.value = `订单 ${order.orderNo} 创建成功，请继续支付`;
    await Promise.all([loadProducts(), loadOrders(), refreshSummary()]);
  } catch (error) {
    actionMessage.value = error.message;
  } finally {
    actionLoading.value = false;
  }
}

async function payByMock(order) {
  actionLoading.value = true;
  try {
    await mockPay(order.orderId, currentUser.value.userId);
    actionMessage.value = '模拟支付成功，订单已更新为已支付';
    await Promise.all([loadOrders(), refreshSummary()]);
  } catch (error) {
    actionMessage.value = error.message;
  } finally {
    actionLoading.value = false;
  }
}

function payByAlipay(order) {
  if (!currentUser.value) {
    actionMessage.value = '请先登录';
    return;
  }
  window.open(alipayPageUrl(order.orderId, currentUser.value.userId), '_blank', 'noopener,noreferrer');
  actionMessage.value = '已打开支付宝支付页；若未配置支付宝参数，请使用模拟支付完成课程演示';
}

async function sendAgentQuestion(prompt = agentQuestion.value) {
  const message = prompt.trim();
  if (!message) {
    return;
  }

  agentMessages.value.push({ role: 'user', text: message });
  agentQuestion.value = '';
  agentLoading.value = true;

  try {
    const data = await askAgent(message, currentUser.value?.userId);
    agentMessages.value.push({
      role: 'assistant',
      text: data.answer,
      meta: `${data.provider} · ${data.modelName}`
    });
  } catch (error) {
    agentMessages.value.push({
      role: 'assistant',
      text: `暂时连接不到 AI 助手：${error.message}`
    });
  } finally {
    agentLoading.value = false;
  }
}

async function refreshSummary() {
  try {
    marketSummary.value = { ...marketSummary.value, ...(await fetchMarketSummary()) };
  } catch (error) {
    // 统计不是核心交易链路，失败时保留当前展示数据。
  }
}

watch([activeCategory, keyword], () => {
  loadProducts();
});

onMounted(() => {
  products.value = fallbackProducts.map(toDisplayProduct);
  bootstrapMarket();
});
</script>

<template>
  <main v-if="activeView === 'login'" class="login-shell login-showcase login-night-hero">
    <section class="login-visual">
      <div class="login-brand">
        <div class="brand-mark">集</div>
        <div>
          <p>Campus Market</p>
          <h1>校园集市</h1>
        </div>
      </div>

      <div class="login-hero-copy">
        <h2>校园集市</h2>
        <p>把闲置变价值，让资源更循环</p>
      </div>

      <div class="login-feature-stack" aria-label="登录页亮点">
        <article v-for="item in loginFeatureStack" :key="item.title">
          <span>{{ item.badge }}</span>
          <div>
            <strong>{{ item.title }}</strong>
            <p>{{ item.text }}</p>
          </div>
        </article>
      </div>

      <div class="login-floating-deals login-product-marquee" aria-label="热门商品预览">
        <article v-for="product in loginMarqueeProducts" :key="`login-${product.id}`">
          <img :src="product.image" :alt="product.title" />
          <div>
            <strong>{{ product.title }}</strong>
            <span>{{ product.condition }}</span>
            <em>¥{{ formatMoney(product.price) }}</em>
          </div>
        </article>
      </div>

      <div class="login-rating-strip">
        <article v-for="item in loginHighlights" :key="item.label">
          <strong>{{ item.value }}</strong>
          <span>{{ item.label }}</span>
        </article>
      </div>

      <p class="login-quote">在校园集市，发现身边的好物，成就更多可能。</p>
    </section>

    <section class="login-card-panel">
      <div>
        <p class="eyebrow">{{ authMode === 'register' ? '创建账号' : authMode === 'phone' ? '短信验证码' : '欢迎回来' }}</p>
        <h2>{{ authMode === 'register' ? '注册校园集市' : authMode === 'phone' ? '手机号登录' : '登录校园集市' }}</h2>
        <span>{{ authMode === 'register' ? '密码 7-11 位，姓名至少 3 位' : authMode === 'phone' ? '没有账号会自动注册' : '演示账号 buyer01 / 123456' }}</span>
      </div>
      <form v-if="authMode === 'login'" class="login-form" @submit.prevent="loginUser">
        <label>
          <span>账号</span>
          <input v-model.trim="loginForm.username" type="text" autocomplete="username" placeholder="buyer01" />
        </label>
        <label>
          <span>密码</span>
          <input v-model.trim="loginForm.password" type="password" autocomplete="current-password" placeholder="123456" />
        </label>
        <div class="login-auth-options">
          <label>
            <input v-model="rememberCampusLogin" type="checkbox" />
            <span>记住我</span>
          </label>
          <button type="button" @click="forgotPassword">忘记密码？</button>
        </div>
        <button class="primary-button full" type="submit" :disabled="actionLoading">
          {{ actionLoading ? '登录中...' : '进入校园集市' }}
        </button>
      </form>
      <form v-else-if="authMode === 'phone'" class="login-form phone-login-form" @submit.prevent="loginByPhone">
        <label>
          <span>手机号</span>
          <input v-model.trim="phoneLoginForm.phone" type="tel" autocomplete="tel" placeholder="请输入真实手机号" />
        </label>
        <label>
          <span>短信验证码</span>
          <div class="sms-code-row">
            <input v-model.trim="phoneLoginForm.code" type="text" inputmode="numeric" maxlength="6" placeholder="6 位验证码" />
            <button type="button" :disabled="actionLoading || smsCountdown > 0" @click="requestSmsCode">
              {{ smsCountdown > 0 ? `${smsCountdown}s` : '获取验证码' }}
            </button>
          </div>
        </label>
        <p class="register-rule">第一次手机号登录会自动注册并存入数据库。</p>
        <button class="primary-button full" type="submit" :disabled="actionLoading">
          {{ actionLoading ? '登录中...' : '手机号登录/自动注册' }}
        </button>
      </form>
      <form v-else class="login-form register-form" @submit.prevent="registerUser">
        <label>
          <span>账号</span>
          <input v-model.trim="registerForm.username" type="text" autocomplete="username" placeholder="例如 buyer02" />
        </label>
        <label>
          <span>密码</span>
          <input v-model.trim="registerForm.password" type="password" autocomplete="new-password" placeholder="7-11 位密码" />
        </label>
        <label>
          <span>姓名</span>
          <input v-model.trim="registerForm.realName" type="text" placeholder="中文英文均可，至少 3 位" />
        </label>
        <label>
          <span>学号</span>
          <input v-model.trim="registerForm.studentNo" type="text" placeholder="例如 2415304999" />
        </label>
        <label>
          <span>手机号</span>
          <input v-model.trim="registerForm.phone" type="tel" placeholder="可选" />
        </label>
        <label>
          <span>邮箱</span>
          <input v-model.trim="registerForm.email" type="email" placeholder="可选" />
        </label>
        <p class="register-rule">密码 7-11 位；姓名至少 3 位，只允许中文或英文。</p>
        <button class="primary-button full" type="submit" :disabled="actionLoading">
          {{ actionLoading ? '注册中...' : '创建账号并存入数据库' }}
        </button>
      </form>
      <div class="login-divider"><span>其他登录方式</span></div>
      <div class="login-social-row">
        <button v-for="item in socialLoginOptions" :key="item.label" type="button" @click="handleSocialLogin(item)">
          <span>{{ item.icon }}</span>
          {{ item.label }}
        </button>
      </div>
      <button
        v-if="authMode !== 'register'"
        class="login-register-link"
        type="button"
        @click="showRegisterForm"
      >
        还没有账号？立即注册
      </button>
      <button v-if="authMode !== 'phone'" class="login-register-link" type="button" @click="showPhoneLoginForm">使用手机号短信登录</button>
      <button v-if="authMode !== 'login'" class="login-register-link" type="button" @click="showLoginForm">已有账号？返回登录</button>
      <button class="login-register-link" type="button" aria-label="先逛逛" @click="enterMarketAsGuest">先逛逛</button>
      <p v-if="actionMessage" class="action-message">{{ actionMessage }}</p>
    </section>
  </main>

  <main v-else class="market-shell">
    <aside class="side-nav">
      <div class="brand-block">
        <div class="brand-mark">集</div>
        <div>
          <h1>校园集市</h1>
          <p>Campus Market</p>
        </div>
      </div>

      <nav class="nav-list" aria-label="主导航">
        <button
          v-for="item in navItems"
          :key="item.key"
          class="nav-item"
          :class="{ active: activeView === item.key }"
          type="button"
          @click="switchView(item.key)"
        >
          {{ item.label }}
        </button>
      </nav>

      <section class="category-panel">
        <div class="panel-title">
          <span>商品分类</span>
          <button type="button" @click="switchView('home')">全部</button>
        </div>
        <button
          v-for="category in categories"
          :key="category.name"
          class="category-item"
          :class="{ selected: activeCategory === category.name }"
          type="button"
          @click="openCategory(category)"
        >
          <span class="category-dot" :style="{ backgroundColor: category.accent }"></span>
          <span>{{ category.name }}</span>
          <strong>{{ category.count }}</strong>
        </button>
      </section>
    </aside>

    <section class="content">
      <header class="topbar">
        <div>
          <p class="eyebrow">{{ currentViewEyebrow }}</p>
          <h2>{{ currentViewTitle }}</h2>
        </div>
        <div class="topbar-actions">
          <button class="ghost-button" type="button" @click="currentUser ? switchView('orders') : switchView('login')" :disabled="actionLoading">
            {{ currentUser ? currentUser.realName : '登录' }}
          </button>
          <button class="primary-button" type="button" @click="switchView('publish')">发布商品</button>
        </div>
      </header>

      <section v-if="activeView === 'home'" class="hero-search">
        <div class="hero-copy">
          <span class="hero-pill">今日新增 {{ marketSummary.todayNewProducts }} 件</span>
          <h3>搜索二手好物</h3>
          <p>教材、数码、宿舍用品和运动装备都在这里，支持登录、下单、订单查看和支付宝支付演示。</p>
          <p v-if="loadError" class="sync-note">{{ loadError }}</p>
          <p v-else-if="backendConnected" class="sync-note">已连接后端接口：商品、分类、订单、支付数据实时读取</p>
        </div>
        <label class="search-box">
          <span>搜索</span>
          <input v-model.trim="keyword" type="search" placeholder="输入商品名或分类，例如 数据库、鼠标" />
        </label>
      </section>

      <div v-if="activeView === 'home'" class="filter-row">
        <button
          v-for="tab in filterTabs"
          :key="tab"
          class="filter-chip"
          :class="{ active: sortMode === tab || (tab === '全部' && activeCategory === '全部' && sortMode === '最新') }"
          type="button"
          @click="tab === '全部' ? (activeCategory = '全部', sortMode = '最新') : (sortMode = tab)"
        >
          {{ tab }}
        </button>
      </div>

      <section v-if="activeView === 'home'" class="product-grid" aria-label="商品列表">
        <article v-for="product in filteredProducts" :key="product.id" class="product-card">
          <div class="product-image-wrap">
            <img :src="product.image" :alt="product.title" />
            <span>{{ product.condition }}</span>
          </div>
          <div class="product-body">
            <div>
              <p class="product-category">{{ product.category }}</p>
              <h4>{{ product.title }}</h4>
            </div>
            <div class="price-row">
              <strong>￥{{ formatMoney(product.price) }}</strong>
              <del>￥{{ formatMoney(product.originalPrice) }}</del>
            </div>
            <div class="meta-row">
              <span>{{ product.location }}</span>
              <span>{{ product.views }} 浏览</span>
            </div>
            <div class="product-actions">
              <button class="primary-button compact" type="button" :disabled="actionLoading" @click="placeOrder(product)">
                立即下单
              </button>
            </div>
          </div>
        </article>
        <div v-if="!isLoading && filteredProducts.length === 0" class="empty-state">
          没有找到匹配商品
        </div>
      </section>

      <section v-if="activeView === 'categories'" class="workspace-panel">
        <div class="section-heading">
          <h3>分类总览</h3>
          <span>点击分类进入商品列表</span>
        </div>
        <div class="category-grid">
          <button
            v-for="category in categories"
            :key="category.name"
            class="category-card"
            type="button"
            @click="openCategory(category)"
          >
            <span class="category-dot" :style="{ backgroundColor: category.accent }"></span>
            <strong>{{ category.name }}</strong>
            <p>{{ category.count }} 件在售商品</p>
          </button>
        </div>
      </section>

      <section v-if="activeView === 'publish'" class="workspace-panel">
        <div class="section-heading">
          <h3>发布商品</h3>
          <span>课程演示表单</span>
        </div>
        <form class="publish-form" @submit.prevent="submitPublishDemo">
          <label>
            <span>商品名称</span>
            <input v-model.trim="publishForm.title" type="text" placeholder="例如 数据库教材" />
          </label>
          <label>
            <span>分类</span>
            <select v-model="publishForm.category">
              <option v-for="category in categories" :key="category.name" :value="category.name">
                {{ category.name }}
              </option>
            </select>
          </label>
          <label>
            <span>价格</span>
            <input v-model.trim="publishForm.price" type="number" min="0" step="0.01" placeholder="25.00" />
          </label>
          <label>
            <span>交易地点</span>
            <input v-model.trim="publishForm.tradePlace" type="text" placeholder="图书馆门口" />
          </label>
          <label class="form-wide">
            <span>商品描述</span>
            <textarea v-model.trim="publishForm.description" rows="4" placeholder="描述成色、配件、交易方式"></textarea>
          </label>
          <button class="primary-button form-wide" type="submit">提交发布演示</button>
        </form>
      </section>

      <section v-if="activeView === 'orders'" class="workspace-panel">
        <div class="section-heading">
          <h3>我的订单</h3>
          <span>{{ orders.length }} 笔</span>
        </div>
        <div class="order-list-large">
          <article v-for="order in orders" :key="order.orderId" class="order-row">
            <img :src="order.coverImage" :alt="order.productTitle" />
            <div>
              <strong>{{ order.productTitle }}</strong>
              <p>{{ order.orderNo }} · {{ order.statusText }} · ￥{{ formatMoney(order.amount) }}</p>
              <p>{{ order.tradePlace }} · 卖家 {{ order.sellerUsername }}</p>
            </div>
            <div v-if="order.status === 'PENDING'" class="pay-actions">
              <button type="button" @click="payByAlipay(order)">支付宝支付</button>
              <button type="button" @click="payByMock(order)">模拟支付</button>
            </div>
          </article>
          <div v-if="currentUser && orders.length === 0" class="empty-state">暂无订单，先去首页下单。</div>
          <div v-if="!currentUser" class="empty-state">请先登录 buyer01 / 123456。</div>
        </div>
      </section>

      <section v-if="activeView === 'messages'" class="workspace-panel">
        <div class="section-heading">
          <h3>消息</h3>
          <span>沟通记录</span>
        </div>
        <article v-for="message in demoMessages" :key="message.title" class="message-item">
          <div>
            <strong>{{ message.title }}</strong>
            <p>{{ message.text }}</p>
          </div>
          <span>{{ message.time }}</span>
        </article>
      </section>

      <section v-if="activeView === 'agent'" class="workspace-panel agent-workspace">
        <div class="section-heading">
          <div>
            <h3>AI助手</h3>
            <span>Agent模式已接入后端 DeepSeek / LangChain4j</span>
          </div>
          <span>{{ agentLoading ? '思考中' : '在线问答' }}</span>
        </div>

        <div class="agent-chat-window" aria-label="AI助手问答记录">
          <article
            v-for="(message, index) in agentMessages"
            :key="`${message.role}-${index}`"
            class="agent-bubble"
            :class="message.role"
          >
            <strong>{{ message.role === 'assistant' ? '校园小助手' : '我' }}</strong>
            <p>{{ message.text }}</p>
            <span v-if="message.meta">{{ message.meta }}</span>
          </article>
        </div>

        <div class="agent-quick-row">
          <button
            v-for="prompt in agentQuickPrompts"
            :key="prompt"
            type="button"
            @click="sendAgentQuestion(prompt)"
          >
            {{ prompt }}
          </button>
        </div>

        <form class="agent-input-row" @submit.prevent="sendAgentQuestion()">
          <input v-model.trim="agentQuestion" type="text" placeholder="问问校园集市小助手，例如：怎么安全交易？" />
          <button class="primary-button" type="submit" :disabled="agentLoading">
            {{ agentLoading ? '生成中...' : '发送' }}
          </button>
        </form>
      </section>
    </section>

    <aside class="insight-panel">
      <section class="user-card">
        <div class="avatar">{{ currentUser?.realName?.slice(0, 1) || '黄' }}</div>
        <div>
          <h3>{{ currentUser?.realName || '黄同学' }}</h3>
          <p>{{ currentUser ? `${currentUser.role} / ${currentUser.username}` : '买家 / 卖家双身份' }}</p>
        </div>
      </section>

      <section class="login-card">
        <div class="section-heading">
          <h3>账户状态</h3>
          <span>{{ currentUser ? '已登录' : '未登录' }}</span>
        </div>
        <p class="action-message">
          {{ currentUser ? `${currentUser.realName || currentUser.username} 已登录，可以下单和支付。` : '请先进入独立登录页，使用 buyer01 / 123456。' }}
        </p>
        <button v-if="!currentUser" class="primary-button full" type="button" @click="switchView('login')">
          去登录
        </button>
        <p v-if="actionMessage" class="action-message">{{ actionMessage }}</p>
      </section>

      <section class="stats-card">
        <h3>我的交易状态</h3>
        <div class="stat-grid">
          <div>
            <strong>{{ pendingOrderCount }}</strong>
            <span>待支付</span>
          </div>
          <div>
            <strong>{{ paidOrderCount }}</strong>
            <span>已支付</span>
          </div>
          <div>
            <strong>{{ marketSummary.finishedOrders }}</strong>
            <span>已完成</span>
          </div>
        </div>
      </section>

      <section class="hot-card">
        <div class="section-heading">
          <h3>我的订单</h3>
          <span>{{ orders.length }} 笔</span>
        </div>
        <article v-for="order in orders" :key="order.orderId" class="order-item">
          <img :src="order.coverImage" :alt="order.productTitle" />
          <div>
            <strong>{{ order.productTitle }}</strong>
            <p>{{ order.orderNo }} · {{ order.statusText }}</p>
            <p>￥{{ formatMoney(order.amount) }} · {{ order.tradePlace }}</p>
            <div v-if="order.status === 'PENDING'" class="pay-actions">
              <button type="button" @click="payByAlipay(order)">支付宝支付</button>
              <button type="button" @click="payByMock(order)">模拟支付</button>
            </div>
          </div>
        </article>
        <p v-if="currentUser && orders.length === 0" class="empty-copy">还没有订单，先在左侧商品卡点击立即下单。</p>
        <p v-if="!currentUser" class="empty-copy">请先登录 buyer01 / 123456 查看订单。</p>
      </section>

      <section class="hot-card">
        <div class="section-heading">
          <h3>热门推荐</h3>
          <span>实时</span>
        </div>
        <article v-for="deal in hotDeals" :key="deal.text" class="deal-item">
          <strong>{{ deal.label }}</strong>
          <p>{{ deal.text }}</p>
        </article>
      </section>

      <section class="summary-card">
        <span>今日市场估值</span>
        <strong>￥{{ formatMoney(marketValue) }}</strong>
        <p>真实支付宝支付入口已接入；未配置支付宝应用时，可使用模拟支付完成课程演示。</p>
      </section>
    </aside>

    <button class="floating-agent-button" type="button" @click="switchView('agent')">
      <span>AI</span>
      Agent模式
    </button>
  </main>
</template>
