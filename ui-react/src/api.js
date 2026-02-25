const jsonHeaders = (token) => ({
  "Content-Type": "application/json",
  ...(token ? { Authorization: `Bearer ${token}` } : {})
});

async function request(url, options = {}) {
  const response = await fetch(url, options);
  const text = await response.text();
  const data = text ? JSON.parse(text) : null;

  if (!response.ok) {
    const message = data?.message || data?.error || `HTTP ${response.status}`;
    throw new Error(message);
  }

  return data;
}

export const api = {
  login: (payload) => request("/auth/login", {
    method: "POST",
    headers: jsonHeaders(),
    body: JSON.stringify(payload)
  }),

  registerCustomer: (payload) => request("/auth/register", {
    method: "POST",
    headers: jsonHeaders(),
    body: JSON.stringify(payload)
  }),

  searchProducts: (token, name, category) => request(`/customer/products/search?name=${encodeURIComponent(name)}&category=${encodeURIComponent(category)}`, {
    headers: jsonHeaders(token)
  }),

  addToCart: (token, payload) => request("/customer/cart/items", {
    method: "POST",
    headers: jsonHeaders(token),
    body: JSON.stringify(payload)
  }),

  viewCart: (token) => request("/customer/cart", {
    headers: jsonHeaders(token)
  }),

  createOrder: (token, items) => request("/customer/orders", {
    method: "POST",
    headers: jsonHeaders(token),
    body: JSON.stringify({ items })
  }),

  payOrder: (token, orderId, method) => request(`/customer/orders/${orderId}/pay`, {
    method: "POST",
    headers: jsonHeaders(token),
    body: JSON.stringify({ method })
  }),

  customerOrders: (token) => request("/customer/orders", {
    headers: jsonHeaders(token)
  }),

  addProduct: (token, payload) => request("/agent/products", {
    method: "POST",
    headers: jsonHeaders(token),
    body: JSON.stringify(payload)
  }),

  updatePrice: (token, productId, price) => request(`/agent/products/${productId}/price`, {
    method: "PUT",
    headers: jsonHeaders(token),
    body: JSON.stringify({ price })
  }),

  updateQuantity: (token, productId, quantity) => request(`/agent/products/${productId}/quantity`, {
    method: "PUT",
    headers: jsonHeaders(token),
    body: JSON.stringify({ quantity })
  }),

  readyOrders: (token) => request("/agent/orders/ready-for-shipping", {
    headers: jsonHeaders(token)
  })
};
