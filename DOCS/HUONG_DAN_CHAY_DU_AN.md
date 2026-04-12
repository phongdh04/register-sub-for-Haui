# Hướng Dẫn Chạy & Khởi Tạo Dự Án (EduPort)

Tài liệu này hướng dẫn bạn cài đặt, chạy toàn bộ các thành phần của hệ thống đăng ký học phần **EduPort** trên môi trường máy tính cá nhân (localhost).

---

## 1. Yêu Cầu Môi Trường (Prerequisites)

Dự án được ứng dụng kiến trúc Microservices & Hybrid (Java + Go), yêu cầu máy tính của bạn cài đặt sẵn các phần mềm sau:

- **Docker Desktop** (Dùng để chạy Postgres, Redis, Kafka bằng container).
- **Java 21** & **Maven** (Spring Boot 3.4.x).
- **Go 1.20+** (Go Queue Service).
- **Node.js 18+** & **npm** (Frontend ReactJS/Vite).

---

## 2. Khởi Động Infrastructure (Hạ Tầng Lõi)

Hệ thống cung cấp sẵn file `docker-compose.yml` để cấu hình toàn bộ database và message broker cùng một lúc.

**Mở terminal ở thư mục gốc của dự án (`d:\docs do an`) và chạy:**

```bash
docker-compose up -d
```

**Các dịch vụ sẽ được khởi chạy tại background (`-d`):**
- **PostgreSQL:** Port `5432` (Username: `eduport_user`, Password: `eduport_password`)
- **Redis:** Port `6379` (Password: `eduport_redis`)
- **Apache Kafka:** Port `9092`
- **Go Queue API:** Port `3000` (Nếu bạn thấy lỗi Go thì có thể chạy Go thủ công ở bước 4).

> 💡 **Tip:** Nếu muốn dừng hệ thống, chạy `docker-compose down`.

---

## 3. Khởi Động Backend Core (Spring Boot)

Backend Core chịu trách nhiệm Quản trị dữ liệu (Master Data), Xử lý đồng bộ Kafka cập nhật SQL và Expose API tổng.

**Mở một terminal mới:**

```bash
cd backend-core
# Trên Windows
.\mvnw spring-boot:run
# Trên Mac/Linux
./mvnw spring-boot:run
```

**✅ Dấu hiệu thành công:**
- Spring Boot chạy ở port `8080`.
- Chế độ `ddl-auto=update` sẽ tự sinh các bảng tự động.
- **DataSeeder** sẽ tự động chèn dữ liệu mẫu, bao gồm Khoa CNTT và các tài khoản test mặc định.

**Tài khoản Test được tạo sẵn (Mật khẩu chung `123456` cho tất cả):**
| Role | Username | Password | Quyền Hạn |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin` | `123456` | Toàn quyền thêm, sửa, xóa, phát hành lớp |
| **Sinh Viên** | `sv01` | `123456` | Đăng ký môn (Qua Go), tra cứu TKB môn học bắt buộc |
| **Giảng Viên**| `gv01` | `123456` | Tra cứu Danh mục hệ thống |

---

## 4. Khởi Động Backend Queue (Go) - (Tuỳ Chọn Nếu Không Dùng Docker)

Mặc định file Docker Compose đã bao gồm một container `go-queue`. Nếu bạn muốn debug/chạy Go thủ công ở máy host cục bộ:

```bash
cd backend-queue
go auto build / run
go run main.go
```

**Lưu ý:** Go Service chạy ở port `3000`.

---

## 5. Khởi Động Frontend (UI ReactJS/Vite)

Bảng điều khiển giao diện UI. 

**Mở một terminal mới:**

```bash
cd frontend

# Cài đặt thư viện NodeJS (NPM) nếu chưa có
npm install

# Khởi động máy chủ dev Vite
npm run dev
```

Giao diện sẽ chạy ở đường dẫn: **http://localhost:5173** (Hoặc tuỳ console hiển thị).

---

## 6. Luồng Test Thử Chức Năng Hiện Tại (Giai Đoạn 1)

Nếu bạn chạy ứng dụng thành công, hãy test chức năng theo các bước dưới đây trên Frontend hoặc [Postman](https://www.postman.com/):

1. **Test Đăng nhập thành công:**
   - Truy cập trang Login. 
   - Đăng nhập với Username: `admin` / Password: `123456`.
   - Nếu đăng nhập thành công, bạn sẽ nhận được JwtToken -> RBAC Proxy hoạt động ở Backend-Core.
   
2. **Test chức năng Tra Cứu (Public):**
   - Đăng nhập vào account Sinh Viên (`sv01`), trải nghiệm route API `GET /api/hoc-phan` hoặc `GET /api/lop-hoc-phan/hoc-ky/{id}` để tận hưởng JSONB schema.

3. **Thêm dữ liệu bằng Admin:**
   - Dùng tài khoản Admin (`admin`), truy cập quản lý môn học, nhập API POST vào `/api/hoc-phan` hay `/api/lop-hoc-phan`.

Chúc bạn thành công! Nếu có lỗi liên quan đến DB, hãy kiểm tra lại trạng thái Docker Container bằng lệnh `docker ps`.
