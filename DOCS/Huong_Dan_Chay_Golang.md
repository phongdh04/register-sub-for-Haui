# Hướng Dẫn Chạy Dự Án Golang (Queue Service)

Dịch vụ Golang `backend-queue` (Task 12) đóng vai trò là Cổng tiếp nhận Giờ Vàng, chịu trách nhiệm xử lý cực nhanh các yêu cầu đăng ký học phần, trừ slot trên Redis, và đẩy tin nhắn vào Kafka.

Dưới đây là các cách để chạy dịch vụ này.

## Yêu Cầu Tiền Quyết (Prerequisites)

Dù chạy bằng phương pháp nào, bạn hệ thống cũng dựa trên **Redis** và **Kafka**. Hãy đảm bảo bạn đã chạy Docker Compose lên trước cho các dịch vụ nền tảng:

```bash
# Đứng ở thư mục gốc của dự án, chạy:
docker-compose up -d redis kafka postgres
```

---

## Cách 1: Chạy trực tiếp qua Docker Compose (Khuyên dùng)

Do chúng ta đã cấu hình sẵn Go Queue Service trong file `docker-compose.yml`, đây là cách nhanh chóng và chính xác nhất để chạy cùng với toàn bộ hệ thống mạng của Docker.

```bash
# 1. Đảm bảo bạn đang đứng ở thư mục gốc dự án (có chứa docker-compose.yml)
# 2. Build image và khởi động luôn Go Service cùng các service khác:
docker-compose up -d --build go-queue

# 3. Để theo dõi Log xem Queue đã khởi động và kết nối Redis/Kafka chưa:
docker-compose logs -f go-queue
```

---

## Cách 2: Chạy Local trực tiếp trên máy (Dành cho Dev/Debug)

Nếu bạn muốn chỉnh sửa code Go và test trực tiếp, bạn cần thiết lập môi trường Cài đặt Go:

### Bước 1: Cài đặt Golang
- Tải file thiết lập Golang cho Windows tại trang chủ: [https://go.dev/dl/](https://go.dev/dl/)
- Chạy file `.msi` vừa tải và cứ cài đặt "Next" tới cùng.
- Mở Terminal/PowerShell **MỚI** và kiếm tra cài đặt:
  ```bash
  go version
  ```

### Bước 2: Thiết lập Biến Môi Trường (Environment Variable)
Dự án có sẵn file `.env.example`. Bạn tiến hành tạo `.env`:

```bash
# 1. Di chuyển vào thư mục backend-queue
cd backend-queue

# 2. Copy file env (Trên Windows có thể copy paste file trong Explorer và đổi tên thành .env)
# Hoặc trên terminal:
copy .env.example .env
```
*(Nếu bạn để Redis/Kafka port nguyên bản của docker-compose thì không cần sửa nội dung trong file `.env`)*.

### Bước 3: Cài đặt thư viện dependencies (Tải go.sum)
Trước khi chạy, bạn cần phải cài đặt tất cả các thư viện của bên thứ 3 (như Fiber, Redis client, Kafka Sarama):

```bash
# Đứng trong thư mục backend-queue chạy:
go mod tidy
```
*(Lệnh này sẽ quét code và sinh lại một file `go.sum` chứa hash các package).*

### Bước 4: Chạy Service
Dùng lệnh Run mặc định của Golang:

```bash
go run main.go
```

Khi chạy thành công, Terminal sẽ hiển thị:
```text
✅ Kết nối Redis thành công! Addr=localhost:6379 DB=0
✅ Kết nối Kafka thành công! Brokers=[localhost:9092] Topic=eduport.dang-ky-hoc-phan
🚀 EduPort Go Queue Service đang chạy trên port :3000
```

---

## API Testing & Endpoints

Sau khi Go Service chạy ở cổng `3000`, bạn có thể dùng Postman để test:

### 1. Nạp Slot (Admin trước Giờ Vàng)
- **POST** `http://localhost:3000/api/v1/admin/khoi-tao-slot`
- **Body (JSON):**
  ```json
  {
      "id_lop_hp": 1,
      "si_so_toi_da": 80
  }
  ```

### 2. Sinh viên Đăng ký (Giờ Vàng)
- **POST** `http://localhost:3000/api/v1/queue/dang-ky`
- **Body (JSON):**
  ```json
  {
      "id_sinh_vien": 1001,
      "id_lop_hp": 1,
      "id_hoc_ky": 2
  }
  ```

### 3. Xem Slot Thời Gian Thực
- **GET** `http://localhost:3000/api/v1/queue/slot/1`
