package com.example.demo.payment;

import com.example.demo.domain.entity.GiaoDichThanhToan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * VNPay sandbox: tạo URL thanh toán có chữ ký HMAC-SHA512 (khi đủ cấu hình).
 */
@Component
public class VnpayPaymentAdapter implements PaymentGatewayAdapter {

    @Value("${eduport.payment.vnpay.payment-url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String paymentUrl;

    @Value("${eduport.payment.vnpay.tmn-code:}")
    private String tmnCode;

    @Value("${eduport.payment.vnpay.hash-secret:}")
    private String hashSecret;

    @Value("${eduport.payment.vnpay.return-url:http://localhost:5173/student/thanhtonqrcodeopenapi}")
    private String returnUrl;

    @Override
    public boolean supports(String providerCode) {
        return "VNPAY".equalsIgnoreCase(providerCode);
    }

    @Override
    public PaymentInitResult initiate(GiaoDichThanhToan order) {
        if (tmnCode == null || tmnCode.isBlank() || hashSecret == null || hashSecret.isBlank()) {
            return new PaymentInitResult(
                    "VNPAY_CHUA_CAU_HINH",
                    null,
                    "Cấu hình eduport.payment.vnpay.tmn-code và eduport.payment.vnpay.hash-secret để tạo URL thật."
            );
        }

        long amountVndMinor = order.getSoTien().multiply(BigDecimal.valueOf(100)).longValue();
        String createDate = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        String orderInfo = order.getNoiDung() != null && !order.getNoiDung().isBlank()
                ? order.getNoiDung()
                : "Thanh toan hoc phi EduPort";

        Map<String, String> fields = new TreeMap<>();
        fields.put("vnp_Version", "2.1.0");
        fields.put("vnp_Command", "pay");
        fields.put("vnp_TmnCode", tmnCode);
        fields.put("vnp_Amount", String.valueOf(amountVndMinor));
        fields.put("vnp_CurrCode", "VND");
        fields.put("vnp_TxnRef", order.getMaDonHang());
        fields.put("vnp_OrderInfo", orderInfo);
        fields.put("vnp_OrderType", "other");
        fields.put("vnp_Locale", "vn");
        fields.put("vnp_ReturnUrl", returnUrl);
        fields.put("vnp_IpAddr", "127.0.0.1");
        fields.put("vnp_CreateDate", createDate);

        String hashData = fields.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));

        String secureHash = hmacSha512HexUpper(hashSecret, hashData);

        String query = fields.entrySet().stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        String url = paymentUrl + "?" + query + "&vnp_SecureHash=" + secureHash;
        return new PaymentInitResult(null, url, "Chuyển hướng VNPay sandbox (có chữ ký).");
    }

    private static String hmacSha512HexUpper(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec sk = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(sk);
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(raw).toUpperCase();
        } catch (Exception e) {
            throw new IllegalStateException("Không ký được HMAC-SHA512 cho VNPay", e);
        }
    }
}
