import { readFile } from 'node:fs/promises';
import assert from 'node:assert/strict';

async function main() {
  const app = await readFile(new URL('../src/App.vue', import.meta.url), 'utf8');
  const data = await readFile(new URL('../src/data/market.js', import.meta.url), 'utf8');
  const api = await readFile(new URL('../src/services/api.js', import.meta.url), 'utf8');
  const styles = await readFile(new URL('../src/styles.css', import.meta.url), 'utf8');
  let productSeedSql = '';
  try {
    productSeedSql = await readFile(new URL('../../campus_secondhand_market_more_products.sql', import.meta.url), 'utf8');
  } catch {
    productSeedSql = '';
  }

  for (const text of ['校园集市', '搜索二手好物', '发布商品', '热门推荐', '我的交易状态', '登录', '立即下单', '支付宝支付', '模拟支付', '我的订单', 'AI助手', 'Agent模式', '注册校园集市', '密码 7-11 位', '姓名至少 3 位']) {
    assert.match(app, new RegExp(text));
  }

  for (const token of [
    'activeView',
    'navItems',
    'switchView',
    "activeView === 'home'",
    "activeView === 'categories'",
    "activeView === 'publish'",
    "activeView === 'orders'",
    "activeView === 'messages'",
    "activeView === 'agent'",
    "activeView === 'login'",
    '@click="switchView(item.key)"',
    'resetMarketFilters',
    'login-shell',
    'login-visual',
    'login-showcase',
    'login-feature-stack',
    'login-product-marquee',
    'login-night-hero',
    'login-floating-deals',
    'login-auth-options',
    'login-social-row',
    'login-rating-strip',
    'rememberCampusLogin',
    'authMode',
    'phoneLoginForm',
    'sendSmsCode',
    'loginByPhone',
    'registerForm',
    'registerUser',
    'register(',
    'socialLoginOptions',
    'loginHighlights',
    'agentMessages',
    'agentQuestion',
    'sendAgentQuestion',
    'askAgent',
    '进入校园集市',
    '先逛逛'
  ]) {
    assert.match(app, new RegExp(token.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')));
  }

  for (const category of ['教材资料', '数码产品', '生活用品', '运动器材', '服饰鞋包']) {
    assert.match(data, new RegExp(category));
  }

  const productCount = [...data.matchAll(/title:/g)].length;
  assert.ok(productCount >= 36, `expected at least 36 fallback products, got ${productCount}`);

  const unsplashImageCount = [...data.matchAll(/image: 'https:\/\/images\.unsplash\.com\//g)].length;
  assert.equal(unsplashImageCount, 0, 'product images should not use generic Unsplash scene photos');

  for (const token of [
    'covers.openlibrary.org/b/isbn/9787040406641-L.jpg',
    'covers.openlibrary.org/b/isbn/9787040396638-L.jpg',
    'covers.openlibrary.org/b/isbn/9787121411748-L.jpg',
    'covers.openlibrary.org/b/isbn/9787302023685-L.jpg',
    'store.storeimages.cdn-apple.com',
    'resource.logitech.com',
    'resource.bose.com',
    'resource.se.com',
    'images.samsung.com',
    'static.nike.com'
  ]) {
    assert.match(data, new RegExp(token.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')));
  }
  assert.match(api, /size = 60/);

  const sqlSeedRows = [...productSeedSql.matchAll(/UNION ALL SELECT/g)].length + (productSeedSql.includes('SELECT 2 AS seller_id') ? 1 : 0);
  assert.ok(sqlSeedRows >= 40, `expected at least 40 SQL seed products, got ${sqlSeedRows}`);

  for (const fn of ['fetchProducts', 'fetchCategories', 'fetchMarketSummary', 'createOrder', 'fetchOrders', 'mockPay', 'alipayPageUrl', 'login', 'register', 'sendSmsCode', 'phoneLogin', 'askAgent']) {
    assert.match(api, new RegExp(`export (async )?function ${fn}`));
    assert.match(app, new RegExp(fn));
  }

  assert.match(api, /VITE_API_BASE_URL/);
  assert.match(styles, /\.product-image-wrap img[\s\S]*object-fit: contain/);
  assert.match(styles, /\.product-image-wrap img[\s\S]*background: #ffffff/);

  console.log('UI content check passed');
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
