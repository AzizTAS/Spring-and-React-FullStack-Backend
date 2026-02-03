# Backend Architecture Documentation

## 🏗 Architecture Overview
The backend follows a layered architecture pattern with clear separation of concerns:

```
Controller → Service → Repository → Database
     ↓
   DTO/Entity Mapping
```

## 📦 Module Structure

### 1. Admin Module (`admin/`)
**Purpose**: Administrative operations for managing the system

**Files**:
- `AdminController.java`: REST endpoints for admin operations

**Key Responsibilities**:
- Statistics retrieval (user count, product count, order count)
- Order management (view all orders, paginated)
- User deletion with cascade (deletes cart, orders, payments)
- Order deletion with cascade (deletes payments, order items)

**Endpoints**:
```java
GET /api/v1/admin/stats/users       // Total user count
GET /api/v1/admin/stats/products    // Total product count
GET /api/v1/admin/stats/orders      // Total order count
GET /api/v1/admin/orders           // All orders (paginated)
DELETE /api/v1/admin/users/{id}    // Delete user + cascade
DELETE /api/v1/admin/orders/{id}   // Delete order + cascade
```

**Security**: All endpoints require ADMIN role (`@PreAuthorize("hasAnyRole('ADMIN')")`)

**Transaction Management**: Uses `@Transactional` for cascade deletes to ensure data consistency

---

### 2. Authentication Module (`auth/`)
**Purpose**: User authentication and session management

**Files**:
- `AuthController.java`: Login/logout endpoints
- `AuthService.java`: Authentication business logic
- `AuthenticationException.java`: Custom exception for auth failures
- `dto/AuthResponse.java`: Login response with token and user data
- `dto/Credentials.java`: Login credentials DTO

**Token Sub-module** (`auth/token/`):
- `Token.java`: Token entity with user relationship and expiration
- `TokenRepository.java`: Token database operations
- `TokenService.java`: Token creation, validation, cleanup

**Key Features**:
- JWT token generation and validation
- HTTP-only cookie support
- Token expiration handling
- Logout token invalidation
- Password encryption verification

**Flow**:
1. User sends credentials → `AuthController`
2. Service validates password → `AuthService`
3. Generate JWT token → `TokenService`
4. Return token + user data
5. Set HTTP-only cookie

---

### 3. Cart Module (`cart/`)
**Purpose**: Shopping cart management

**Files**:
- `Cart.java`: Cart entity (one-to-many with CartItem)
- `CartItem.java`: Individual cart items with product and quantity
- `CartController.java`: Cart REST endpoints
- `CartService.java`: Cart business logic
- `CartRepository.java`: Cart database operations
- `CartItemRepository.java`: Cart item database operations
- `dto/AddToCartRequest.java`: Request DTO for adding items
- `dto/CartDTO.java`: Response DTO with cart data

**Key Responsibilities**:
- Get user's cart with all items
- Add product to cart (create or update quantity)
- Remove item from cart
- Update item quantity
- Clear entire cart
- Calculate total price

**Business Logic**:
```java
// Add to cart logic:
1. Find user's cart (create if not exists)
2. Check if product already in cart
   - If yes: Update quantity
   - If no: Create new CartItem
3. Save cart
4. Return updated cart DTO
```

**Endpoints**:
```java
GET /api/v1/cart                    // Get user's cart
POST /api/v1/cart/add              // Add item to cart
DELETE /api/v1/cart/item/{id}      // Remove cart item
PUT /api/v1/cart/item/{id}         // Update item quantity
DELETE /api/v1/cart/clear          // Clear cart
```

---

### 4. Category Module (`category/`)
**Purpose**: Product category management

**Files**:
- `Category.java`: Category entity
- `CategoryController.java`: CRUD endpoints
- `CategoryService.java`: Business logic
- `CategoryRepository.java`: Database operations
- `dto/CategoryCreate.java`: Create/update DTO

**Features**:
- List all categories (paginated)
- Get single category by ID
- Create new category
- Update category
- Delete category

**Relationships**:
- One-to-Many with Product
- Deleting category doesn't delete products (sets category to null)

---

### 5. Order Module (`order/`)
**Purpose**: Order processing and management

**Files**:
- `Order.java`: Order entity with status, total, shipping address
- `OrderItem.java`: Products in order with quantity and price snapshot
- `OrderStatus.java`: Enum (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)
- `OrderController.java`: Order endpoints
- `OrderService.java`: Order creation and management
- `OrderRepository.java`: Order database operations
- `OrderItemRepository.java`: Order item operations
- `dto/OrderDTO.java`: Response DTO
- `dto/OrderRequest.java`: Request DTO with shipping address

**Order Creation Flow**:
```java
1. Get user's cart
2. Validate cart not empty
3. Create Order entity
4. For each cart item:
   - Create OrderItem (snapshot price)
   - Reduce product stock
5. Calculate order total
6. Clear user's cart
7. Save order
8. Return order DTO
```

**Key Features**:
- Create order from cart
- View user's orders (paginated)
- View single order details
- Update order status (admin)
- Delete order (admin only)
- Cascade delete: Order → OrderItems, Payments

**Endpoints**:
```java
GET /api/v1/orders                 // User's orders
GET /api/v1/orders/{id}           // Single order
POST /api/v1/orders               // Create order
PUT /api/v1/orders/{id}/status   // Update status
DELETE /api/v1/orders/{id}        // Delete order (admin)
```

---

### 6. Payment Module (`payment/`)
**Purpose**: Payment processing and tracking

**Files**:
- `Payment.java`: Payment entity
- `PaymentStatus.java`: Enum (PENDING, COMPLETED, FAILED, REFUNDED)
- `PaymentMethod.java`: Enum (CREDIT_CARD, DEBIT_CARD, PAYPAL, CASH)
- `PaymentController.java`: Payment endpoints
- `PaymentService.java`: Payment business logic
- `PaymentRepository.java`: Database operations

**Payment Flow**:
```java
1. Order created → Create PENDING payment
2. User submits payment → Process payment
3. Update payment status (COMPLETED/FAILED)
4. Update order status accordingly
5. Store transaction ID
```

**Key Features**:
- Create payment for order
- Process payment (simulation)
- View payment details
- Update payment status
- One-to-One relationship with Order

**Endpoints**:
```java
GET /api/v1/payments/{id}              // Get payment
GET /api/v1/payments/order/{orderId}  // Payment by order
POST /api/v1/payments/order/{orderId} // Create payment
PUT /api/v1/payments/{id}/status      // Update status
```

---

### 7. Product Module (`product/`)
**Purpose**: Product catalog management

**Files**:
- `Product.java`: Product entity
- `ProductController.java`: CRUD and search endpoints
- `ProductService.java`: Business logic
- `ProductRepository.java`: Database operations with custom queries
- `dto/ProductDTO.java`: Response DTO
- `dto/ProductCreate.java`: Create/update DTO

**Key Features**:
- List products (paginated)
- Search products by keyword
- Filter by category
- Get single product
- Create/update/delete product
- Stock management
- Image URL storage

**Custom Queries**:
```java
@Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
Page<Product> searchByKeyword(String keyword, Pageable pageable);

@Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
```

**Endpoints**:
```java
GET /api/v1/products                    // All products
GET /api/v1/products/{id}              // Single product
GET /api/v1/products/search            // Search
GET /api/v1/products/category/{id}     // By category
POST /api/v1/products                  // Create (admin)
PUT /api/v1/products/{id}              // Update (admin)
DELETE /api/v1/products/{id}           // Delete (admin)
```

---

### 8. Review Module (`review/`)
**Purpose**: Product reviews and ratings

**Files**:
- `Review.java`: Review entity with rating and comment
- `ReviewController.java`: Review endpoints
- `ReviewService.java`: Business logic
- `ReviewRepository.java`: Database operations
- `dto/ReviewDTO.java`: Response DTO
- `dto/ReviewCreate.java`: Create/update DTO

**Key Features**:
- Add review to product
- Update own review
- Delete own review
- Get product reviews (paginated)
- Calculate average rating
- Prevent duplicate reviews (one per user per product)

**Endpoints**:
```java
GET /api/v1/reviews/product/{id}           // Product reviews
GET /api/v1/reviews/product/{id}/rating    // Average rating
GET /api/v1/reviews/{id}                   // Single review
POST /api/v1/reviews/product/{id}          // Add review
PUT /api/v1/reviews/{id}                   // Update review
DELETE /api/v1/reviews/{id}                // Delete review
```

---

### 9. User Module (`user/`)
**Purpose**: User account management

**Files**:
- `User.java`: User entity with roles, profile data
- `UserController.java`: User CRUD endpoints
- `UserService.java`: User business logic
- `UserRepository.java`: Custom user queries
- `dto/UserDTO.java`: Response DTO (hides password)
- `dto/UserCreate.java`: Registration DTO
- `dto/UserUpdate.java`: Profile update DTO
- `dto/PasswordResetRequest.java`: Password reset email
- `dto/PasswordUpdate.java`: Set new password

**Key Features**:
- User registration with email verification
- Account activation via token
- Profile management
- Password reset flow
- Profile image upload
- Role management (USER/ADMIN)
- Soft delete support

**Security**:
- Password hashing (BCrypt)
- Email verification required
- Token-based password reset
- Role-based access control

**Endpoints**:
```java
POST /api/v1/users                      // Register
PATCH /api/v1/users/{token}/active     // Activate account
GET /api/v1/users                       // List users
GET /api/v1/users/{id}                 // Get user
PUT /api/v1/users/{id}                 // Update profile
DELETE /api/v1/users/{id}              // Delete account
POST /api/v1/users/password-reset     // Request reset
PATCH /api/v1/users/{token}/password  // Set new password
```

---

## 🔐 Security Configuration (`configuration/`)

### SecurityConfiguration.java
**Purpose**: Spring Security setup

**Key Configurations**:
```java
// Public endpoints (no auth required)
- POST /api/v1/users (registration)
- POST /api/v1/auth (login)
- GET /api/v1/products/** (browse products)
- GET /api/v1/categories (browse categories)

// Authenticated endpoints
- /api/v1/cart/** (requires USER or ADMIN)
- /api/v1/orders/** (requires authentication)

// Admin endpoints
- /api/v1/admin/** (requires ADMIN role)
- POST/PUT/DELETE /api/v1/products (requires ADMIN)

// CORS configuration
- Allowed origins: configured dynamically
- Allowed methods: GET, POST, PUT, DELETE
- Credentials: true (cookies allowed)

// JWT Token Filter
- Validates token on each request
- Extracts user from token
- Sets SecurityContext
```

### CurrentUser.java
**Purpose**: Security context holder for authenticated user

**Usage**:
```java
@GetMapping("/profile")
public UserDTO getProfile(@AuthenticationPrincipal CurrentUser currentUser) {
    return userService.getUser(currentUser.getId());
}
```

---

## 📧 Email Module (`email/`)

### EmailService.java
**Purpose**: Send transactional emails via SendGrid

**Email Types**:
1. **Activation Email**
   ```java
   Subject: Please activate your account
   Content: HTML email with activation link
   Link format: /activation/{token}
   ```

2. **Password Reset Email**
   ```java
   Subject: Reset your password
   Content: HTML email with reset link
   Link format: /password-reset/set?tk={token}
   ```

**SendGrid Integration**:
- Uses SendGrid API v3
- Dynamic template support
- HTML email formatting
- Error handling and retries

---

## 🗄 Database Design

### Entity Relationships
```
User (1) → (1) Cart → (*) CartItem → (*) Product
User (1) → (*) Order → (*) OrderItem → (*) Product
User (1) → (*) Review → (*) Product
Order (1) → (1) Payment
Category (1) → (*) Product
User (1) → (*) Token
```

### Cascade Delete Rules
```java
// User deleted → Cascades to:
- Cart (via CartRepository.deleteByUserId)
- Orders (via OrderRepository.deleteByUserId)
- Reviews (automatic via @OneToMany cascade)
- Tokens (automatic via @OneToMany cascade)

// Order deleted → Cascades to:
- Payment (via PaymentRepository.deleteByOrderId)
- OrderItems (automatic via @OneToMany cascade = ALL)

// Cart deleted → Cascades to:
- CartItems (automatic via @OneToMany cascade = ALL)
```

---

## 🔄 Transaction Management

### @Transactional Usage
```java
// AdminController - User deletion
@Transactional
public void deleteUserById(Long id) {
    cartRepository.deleteByUserId(id);      // Step 1
    orderRepository.deleteByUserId(id);     // Step 2
    userRepository.deleteById(id);          // Step 3
    // All or nothing - if any fails, rollback all
}

// AdminController - Order deletion
@Transactional
public void deleteOrderById(Long id) {
    paymentRepository.deleteByOrderId(id);  // Step 1
    orderRepository.deleteById(id);         // Step 2 (cascades to OrderItems)
}
```

---

## 🛡 Error Handling (`error/`)

### ErrorHandler.java
**Purpose**: Global exception handling

**Handles**:
- `MethodArgumentNotValidException` → 400 Bad Request
- `AccessDeniedException` → 403 Forbidden
- `AuthenticationException` → 401 Unauthorized
- `DataIntegrityViolationException` → 409 Conflict
- `NotFoundException` → 404 Not Found
- Generic exceptions → 500 Internal Server Error

**Response Format**:
```json
{
  "path": "/api/v1/users",
  "message": "Validation failed",
  "status": 400,
  "timestamp": 1706659200000,
  "validationErrors": {
    "username": "must not be blank"
  }
}
```

---

## 📝 DTOs vs Entities

### Why DTOs?
- **Security**: Hide sensitive data (passwords, tokens)
- **Decoupling**: Frontend doesn't depend on entity structure
- **Performance**: Send only needed data
- **Versioning**: Easy API version management

### Example:
```java
// Entity (database)
public class User {
    private Long id;
    private String username;
    private String password;  // Never send to frontend!
    private String email;
    // ...
}

// DTO (API response)
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String image;
    // No password field!
}
```

---

## 🚀 Performance Optimizations

1. **Pagination**: All list endpoints use `Pageable`
2. **Eager/Lazy Loading**: Strategic use of `FetchType`
3. **Caching**: Consider adding Redis for frequently accessed data
4. **Indexing**: Database indexes on foreign keys
5. **Query Optimization**: Custom JPQL queries for complex operations

---

## 📊 Best Practices Implemented

1. ✅ **Layered Architecture**: Clear separation of concerns
2. ✅ **DTO Pattern**: Security and decoupling
3. ✅ **Transaction Management**: Data consistency
4. ✅ **Exception Handling**: Centralized error responses
5. ✅ **Security**: JWT + Role-based access
6. ✅ **Validation**: Bean Validation annotations
7. ✅ **Documentation**: JavaDoc for complex logic
8. ✅ **RESTful Design**: Proper HTTP methods and status codes
