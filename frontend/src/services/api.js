const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://127.0.0.1:8080/api';

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers
    },
    ...options
  });

  if (!response.ok) {
    throw new Error(`接口请求失败：${response.status}`);
  }

  const payload = await response.json();
  if (payload.code !== 200) {
    throw new Error(payload.message || '接口返回异常');
  }

  return payload.data;
}

export async function fetchProducts({ categoryId, keyword, page = 0, size = 60 } = {}) {
  const params = new URLSearchParams();
  params.set('page', String(page));
  params.set('size', String(size));

  if (categoryId) {
    params.set('categoryId', String(categoryId));
  }
  if (keyword) {
    params.set('keyword', keyword);
  }

  return request(`/products?${params.toString()}`);
}

export async function fetchCategories() {
  return request('/categories');
}

export async function fetchMarketSummary() {
  return request('/market/summary');
}

export async function createOrder({ productId, buyerId, buyerRemark = '' }) {
  return request('/orders', {
    method: 'POST',
    body: JSON.stringify({ productId, buyerId, buyerRemark })
  });
}

export async function fetchOrders(buyerId) {
  const params = new URLSearchParams({ buyerId: String(buyerId) });
  return request(`/orders?${params.toString()}`);
}

export async function mockPay(orderId, buyerId) {
  return request(`/orders/${orderId}/pay/mock`, {
    method: 'POST',
    body: JSON.stringify({ buyerId })
  });
}

export function alipayPageUrl(orderId, buyerId) {
  const params = new URLSearchParams({ buyerId: String(buyerId) });
  return `${API_BASE_URL}/orders/${orderId}/pay/alipay?${params.toString()}`;
}

export async function login(username, password) {
  return request('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password })
  });
}

export async function register(payload) {
  return request('/auth/register', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function sendSmsCode(phone) {
  return request('/auth/sms-code', {
    method: 'POST',
    body: JSON.stringify({ phone })
  });
}

export async function phoneLogin(phone, code) {
  return request('/auth/phone-login', {
    method: 'POST',
    body: JSON.stringify({ phone, code })
  });
}

export async function askAgent(message, userId) {
  return request('/agent/chat', {
    method: 'POST',
    body: JSON.stringify({ message, userId })
  });
}
