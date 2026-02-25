import { useMemo, useState } from "react";
import { api } from "./api";

const initialProductForm = {
  name: "",
  description: "",
  price: "",
  quantity: "",
  category: "",
  status: "ACTIVE"
};

export default function App() {
  const [auth, setAuth] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const [loginForm, setLoginForm] = useState({ email: "", password: "" });
  const [registerForm, setRegisterForm] = useState({ name: "", email: "", password: "", mobileNumber: "" });

  const [searchForm, setSearchForm] = useState({ name: "iphone", category: "Mobiles" });
  const [products, setProducts] = useState([]);
  const [cart, setCart] = useState(null);
  const [orders, setOrders] = useState([]);
  const [selectedOrderId, setSelectedOrderId] = useState("");
  const [paymentMethod, setPaymentMethod] = useState("UPI");
  const [lastPayment, setLastPayment] = useState(null);

  const [productForm, setProductForm] = useState(initialProductForm);
  const [updateForm, setUpdateForm] = useState({ productId: "", price: "", quantity: "" });
  const [readyOrders, setReadyOrders] = useState([]);

  const role = auth?.role;
  const token = auth?.token;

  const prettyRole = useMemo(() => {
    if (!role) return "";
    return role.replace("ROLE_", "");
  }, [role]);

  const resetNotice = () => {
    setError("");
    setMessage("");
  };

  const run = async (fn, successMsg) => {
    try {
      setLoading(true);
      resetNotice();
      await fn();
      if (successMsg) setMessage(successMsg);
    } catch (e) {
      setError(e.message || "Request failed");
    } finally {
      setLoading(false);
    }
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    await run(async () => {
      const response = await api.login(loginForm);
      setAuth(response);
    }, "Login successful");
  };

  const handleRegisterCustomer = async (e) => {
    e.preventDefault();
    await run(async () => {
      await api.registerCustomer(registerForm);
      setRegisterForm({ name: "", email: "", password: "", mobileNumber: "" });
    }, "Customer registration successful");
  };

  const handleSearch = async (e) => {
    e?.preventDefault();
    await run(async () => {
      const result = await api.searchProducts(token, searchForm.name, searchForm.category);
      setProducts(result || []);
    });
  };

  const handleAddToCart = async (product) => {
    await run(async () => {
      await api.addToCart(token, {
        productId: product.id,
        productName: product.name,
        price: product.price,
        quantity: 1
      });
      const latest = await api.viewCart(token);
      setCart(latest);
    }, "Added to cart");
  };

  const loadCart = async () => {
    await run(async () => {
      const data = await api.viewCart(token);
      setCart(data);
    });
  };

  const createOrderFromCart = async () => {
    await run(async () => {
      if (!cart?.items?.length) {
        throw new Error("Cart is empty. Add items before creating order.");
      }
      const items = cart.items.map((i) => ({
        productId: i.productId,
        productName: i.productName,
        price: Number(i.price),
        quantity: Number(i.quantity)
      }));
      const created = await api.createOrder(token, items);
      setSelectedOrderId(String(created.id));
      setLastPayment(null);
      const history = await api.customerOrders(token);
      setOrders(history || []);
    }, "Order created");
  };

  const payOrder = async () => {
    await run(async () => {
      if (!selectedOrderId) {
        throw new Error("Enter/select order id before payment.");
      }
      const result = await api.payOrder(token, Number(selectedOrderId), paymentMethod);
      setLastPayment(result);
      const history = await api.customerOrders(token);
      setOrders(history || []);
    }, "Payment processed");
  };

  const loadOrders = async () => {
    await run(async () => {
      const history = await api.customerOrders(token);
      setOrders(history || []);
    });
  };

  const handleAddProduct = async (e) => {
    e.preventDefault();
    await run(async () => {
      await api.addProduct(token, {
        ...productForm,
        price: Number(productForm.price),
        quantity: Number(productForm.quantity)
      });
      setProductForm(initialProductForm);
    }, "Product added");
  };

  const handleUpdatePrice = async (e) => {
    e.preventDefault();
    await run(async () => {
      await api.updatePrice(token, Number(updateForm.productId), Number(updateForm.price));
    }, "Price updated");
  };

  const handleUpdateQuantity = async (e) => {
    e.preventDefault();
    await run(async () => {
      await api.updateQuantity(token, Number(updateForm.productId), Number(updateForm.quantity));
    }, "Quantity updated");
  };

  const loadReadyOrders = async () => {
    await run(async () => {
      const data = await api.readyOrders(token);
      setReadyOrders(data || []);
    });
  };

  const logout = () => {
    setAuth(null);
    setProducts([]);
    setCart(null);
    setOrders([]);
    setSelectedOrderId("");
    setLastPayment(null);
    setReadyOrders([]);
    setMessage("Logged out");
    setError("");
  };

  return (
    <div className="app-shell">
      <header className="topbar">
        <h1>E-FlipkartLite</h1>
        {auth && (
          <div className="session-chip">
            <span>{auth.email}</span>
            <span>{prettyRole}</span>
            <button onClick={logout}>Logout</button>
          </div>
        )}
      </header>

      {error && <div className="alert error">{error}</div>}
      {message && <div className="alert success">{message}</div>}

      {!auth && (
        <div className="grid two-col">
          <section className="card">
            <h2>Login</h2>
            <form onSubmit={handleLogin}>
              <input placeholder="Email" value={loginForm.email} onChange={(e) => setLoginForm({ ...loginForm, email: e.target.value })} required />
              <input type="password" placeholder="Password" value={loginForm.password} onChange={(e) => setLoginForm({ ...loginForm, password: e.target.value })} required />
              <button disabled={loading}>Login</button>
            </form>
          </section>

          <section className="card">
            <h2>Register Customer</h2>
            <form onSubmit={handleRegisterCustomer}>
              <input placeholder="Name" value={registerForm.name} onChange={(e) => setRegisterForm({ ...registerForm, name: e.target.value })} required />
              <input placeholder="Email" value={registerForm.email} onChange={(e) => setRegisterForm({ ...registerForm, email: e.target.value })} required />
              <input type="password" placeholder="Password" value={registerForm.password} onChange={(e) => setRegisterForm({ ...registerForm, password: e.target.value })} required />
              <input placeholder="Mobile Number" value={registerForm.mobileNumber} onChange={(e) => setRegisterForm({ ...registerForm, mobileNumber: e.target.value })} required />
              <button disabled={loading}>Register</button>
            </form>
          </section>
        </div>
      )}

      {role === "CUSTOMER" && (
        <div className="grid">
          <div className="grid two-col">
            <section className="card">
              <h2>Product Search</h2>
              <form onSubmit={handleSearch} className="inline-form">
                <input placeholder="Name" value={searchForm.name} onChange={(e) => setSearchForm({ ...searchForm, name: e.target.value })} />
                <input placeholder="Category" value={searchForm.category} onChange={(e) => setSearchForm({ ...searchForm, category: e.target.value })} />
                <button disabled={loading}>Search</button>
              </form>
              <div className="list">
                {products.map((p) => (
                  <article key={p.id} className="item">
                    <div>
                      <h4>{p.name}</h4>
                      <p>{p.description}</p>
                      <small>{p.category} | Qty: {p.quantity}</small>
                    </div>
                    <div className="right">
                      <strong>Rs.{p.price}</strong>
                      <button onClick={() => handleAddToCart(p)}>Add to Cart</button>
                    </div>
                  </article>
                ))}
              </div>
            </section>

            <section className="card">
              <div className="card-head">
                <h2>Cart</h2>
                <button onClick={loadCart}>Refresh</button>
              </div>
              {!cart && <p>No cart loaded.</p>}
              {cart && (
                <>
                  <div className="list">
                    {(cart.items || []).map((i) => (
                      <article className="item" key={i.id}>
                        <div>
                          <h4>{i.productName}</h4>
                          <small>Qty: {i.quantity}</small>
                        </div>
                        <strong>Rs.{i.lineTotal}</strong>
                      </article>
                    ))}
                  </div>
                  <h3>Total: Rs.{cart.totalAmount}</h3>
                </>
              )}
              <div className="card-head">
                <h3>Create Order</h3>
                <button onClick={createOrderFromCart}>Create From Cart</button>
              </div>
              <div className="inline-form">
                <input
                  placeholder="Order ID"
                  value={selectedOrderId}
                  onChange={(e) => setSelectedOrderId(e.target.value)}
                />
                <select value={paymentMethod} onChange={(e) => setPaymentMethod(e.target.value)}>
                  <option value="UPI">UPI</option>
                  <option value="CARD">CARD</option>
                </select>
                <button onClick={payOrder}>Pay Order</button>
              </div>
              {lastPayment && (
                <p>
                  Payment: {lastPayment.paymentSuccess ? "SUCCESS" : "FAILED"} | Ref: {lastPayment.transactionReference} |
                  Status: {lastPayment.orderStatus}
                </p>
              )}
            </section>
          </div>

          <section className="card">
            <div className="card-head">
              <h2>Order History</h2>
              <button onClick={loadOrders}>Refresh Orders</button>
            </div>
            <div className="list">
              {orders.map((o) => (
                <article className="item" key={o.id}>
                  <div>
                    <h4>Order #{o.id}</h4>
                    <small>{o.createdAt}</small>
                  </div>
                  <div className="right">
                    <strong>{o.status}</strong>
                    <small>Total: Rs.{o.totalAmount}</small>
                  </div>
                </article>
              ))}
            </div>
          </section>
        </div>
      )}

      {role === "AGENT" && (
        <div className="grid two-col">
          <section className="card">
            <h2>Add Product</h2>
            <form onSubmit={handleAddProduct}>
              <input placeholder="Name" value={productForm.name} onChange={(e) => setProductForm({ ...productForm, name: e.target.value })} required />
              <input placeholder="Description" value={productForm.description} onChange={(e) => setProductForm({ ...productForm, description: e.target.value })} required />
              <input type="number" placeholder="Price" value={productForm.price} onChange={(e) => setProductForm({ ...productForm, price: e.target.value })} required />
              <input type="number" placeholder="Quantity" value={productForm.quantity} onChange={(e) => setProductForm({ ...productForm, quantity: e.target.value })} required />
              <input placeholder="Category" value={productForm.category} onChange={(e) => setProductForm({ ...productForm, category: e.target.value })} required />
              <select value={productForm.status} onChange={(e) => setProductForm({ ...productForm, status: e.target.value })}>
                <option value="ACTIVE">ACTIVE</option>
                <option value="INACTIVE">INACTIVE</option>
              </select>
              <button disabled={loading}>Add Product</button>
            </form>
          </section>

          <section className="card">
            <h2>Update Product</h2>
            <form onSubmit={handleUpdatePrice}>
              <input type="number" placeholder="Product ID" value={updateForm.productId} onChange={(e) => setUpdateForm({ ...updateForm, productId: e.target.value })} required />
              <input type="number" placeholder="New Price" value={updateForm.price} onChange={(e) => setUpdateForm({ ...updateForm, price: e.target.value })} required />
              <button disabled={loading}>Update Price</button>
            </form>
            <form onSubmit={handleUpdateQuantity}>
              <input type="number" placeholder="Product ID" value={updateForm.productId} onChange={(e) => setUpdateForm({ ...updateForm, productId: e.target.value })} required />
              <input type="number" placeholder="New Quantity" value={updateForm.quantity} onChange={(e) => setUpdateForm({ ...updateForm, quantity: e.target.value })} required />
              <button disabled={loading}>Update Quantity</button>
            </form>
            <div className="card-head">
              <h3>Orders Ready For Shipping</h3>
              <button onClick={loadReadyOrders}>Load</button>
            </div>
            <div className="list">
              {readyOrders.map((o) => (
                <article className="item" key={o.id}>
                  <div>
                    <h4>Order #{o.id}</h4>
                    <small>{o.customerEmail}</small>
                  </div>
                  <strong>{o.status}</strong>
                </article>
              ))}
            </div>
          </section>
        </div>
      )}
    </div>
  );
}
