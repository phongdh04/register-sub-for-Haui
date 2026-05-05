package com.example.demo.service.impl;

import com.example.demo.domain.entity.PreRegistrationLink;
import com.example.demo.domain.entity.PreRegistrationRequest;
import com.example.demo.domain.entity.HoSoSinhVien;
import com.example.demo.domain.entity.Lop;
import com.example.demo.domain.entity.NganhDaoTao;
import com.example.demo.domain.entity.SinhVien;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.enums.Role;
import com.example.demo.domain.enums.Status;
import com.example.demo.domain.enums.PreRegistrationLinkStatus;
import com.example.demo.domain.enums.PreRegistrationRequestStatus;
import com.example.demo.payload.request.AdminPreRegistrationLinkCreateRequest;
import com.example.demo.payload.request.PublicPreRegistrationSubmitRequest;
import com.example.demo.payload.request.PreRegistrationQueueMessageDto;
import com.example.demo.payload.response.AdminPreRegistrationLinkCreateResponse;
import com.example.demo.payload.response.AdminPreRegistrationLinkItemResponse;
import com.example.demo.payload.response.AdminPreRegistrationLinkStatsResponse;
import com.example.demo.payload.response.PublicPreRegistrationLinkResponse;
import com.example.demo.payload.response.PublicPreRegistrationRequestStatusResponse;
import com.example.demo.payload.response.PublicPreRegistrationSubmitResponse;
import com.example.demo.repository.PreRegistrationLinkRepository;
import com.example.demo.repository.PreRegistrationRequestRepository;
import com.example.demo.repository.LopRepository;
import com.example.demo.repository.NganhDaoTaoRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.IPublicPreRegistrationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PublicPreRegistrationServiceImpl implements IPublicPreRegistrationService {

    private final PreRegistrationLinkRepository linkRepository;
    private final PreRegistrationRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final SinhVienRepository sinhVienRepository;
    private final NganhDaoTaoRepository nganhDaoTaoRepository;
    private final LopRepository lopRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${eduport.prereg.go-ingress.base-url:http://localhost:8081}")
    private String goIngressBaseUrl;

    @Value("${eduport.prereg.go-ingress.submit-path:/api/v1/queue/pre-reg}")
    private String goIngressSubmitPath;

    @Value("${eduport.prereg.go-ingress.timeout-ms:2000}")
    private int goIngressTimeoutMs;

    @Value("${eduport.prereg.quota.buffer-minutes:120}")
    private int quotaBufferMinutes;

    private static final List<String> REQUIRED_FIELDS = List.of(
            "hoTen", "ngaySinh", "maNganhDangKy", "nienKhoa"
    );

    @Override
    @Transactional(readOnly = true)
    public PublicPreRegistrationLinkResponse getLinkInfo(String token) {
        PreRegistrationLink link = resolveActiveByToken(token);
        String linkStatus = computeLinkStatus(link, Optional.empty());
        return PublicPreRegistrationLinkResponse.builder()
                .linkStatus(linkStatus)
                .intakeCode(link.getIntakeCode())
                .requiredFields(REQUIRED_FIELDS)
                .captchaRequired(false)
                .expiresAt(link.getExpiresAt())
                .build();
    }

    @Override
    @Transactional
    public PublicPreRegistrationSubmitResponse submit(String token, PublicPreRegistrationSubmitRequest request, String sourceIp, String userAgent) {
        PreRegistrationLink link = resolveActiveByToken(token);
        String linkStatus = computeLinkStatus(link, Optional.ofNullable(sourceIp));
        if (!"ACTIVE".equals(linkStatus)) {
            throw new IllegalArgumentException("Link pre-registration không khả dụng: " + linkStatus);
        }

        String dedupeKey = buildDedupeKey(request, link.getIntakeCode());
        Optional<PreRegistrationRequest> existing = requestRepository.findByDedupeKey(dedupeKey);
        if (existing.isPresent()) {
            PreRegistrationRequest e = existing.get();
            return PublicPreRegistrationSubmitResponse.builder()
                    .requestId(e.getRequestId())
                    .status(e.getStatus().name())
                    .estimatedProcessingSeconds(60)
                    .build();
        }

        PreRegistrationRequest row = PreRegistrationRequest.builder()
                .requestId(UUID.randomUUID())
                .link(link)
                .dedupeKey(dedupeKey)
                .payloadJson(toJson(request))
                .sourceIpHash(sha256Hex(nullToBlank(sourceIp)))
                .sourceIpPrefix(computeIpPrefix(sourceIp))
                .userAgentHash(sha256Hex(nullToBlank(userAgent)))
                .status(PreRegistrationRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .traceId(UUID.randomUUID().toString())
                .build();
        row = requestRepository.save(row);

        enqueueToGoIngress(row, link);

        return PublicPreRegistrationSubmitResponse.builder()
                .requestId(row.getRequestId())
                .status(row.getStatus().name())
                .estimatedProcessingSeconds(60)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PublicPreRegistrationRequestStatusResponse getRequestStatus(UUID requestId) {
        PreRegistrationRequest row = requestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy pre-registration request: " + requestId));
        String studentCode = null;
        Boolean accountProvisioned = Boolean.FALSE;
        String nextStep = "Hệ thống đang xử lý yêu cầu. Vui lòng kiểm tra lại sau.";
        if (row.getStatus() == PreRegistrationRequestStatus.SUCCESS) {
            Map<String, String> meta = parseSuccessMeta(row.getLastErrorDetail());
            studentCode = meta.get("studentCode");
            accountProvisioned = true;
            nextStep = "Tài khoản đã được tạo. Vui lòng dùng thông tin được cấp để đăng nhập.";
        } else if (row.getStatus() == PreRegistrationRequestStatus.FAILED) {
            nextStep = "Yêu cầu chưa hoàn tất. Vui lòng liên hệ phòng đào tạo hoặc thử lại.";
        }
        return PublicPreRegistrationRequestStatusResponse.builder()
                .requestId(row.getRequestId())
                .status(row.getStatus().name())
                .errorCode(row.getErrorCode())
                .studentCode(studentCode)
                .accountProvisioned(accountProvisioned)
                .nextStep(nextStep)
                .processedAt(row.getProcessedAt())
                .build();
    }

    @Override
    @Transactional
    public AdminPreRegistrationLinkCreateResponse createLink(AdminPreRegistrationLinkCreateRequest request, String createdBy) {
        String plainToken = generatePublicToken();
        PreRegistrationLink row = PreRegistrationLink.builder()
                .tokenHash(sha256Hex(plainToken))
                .status(PreRegistrationLinkStatus.ACTIVE)
                .expiresAt(request.getExpiresAt())
                .maxSubmissions(request.getMaxSubmissions())
                .submittedCount(0)
                .intakeCode(request.getIntakeCode().trim())
                .campusCode(trimOrNull(request.getCampusCode()))
                .rateLimitProfile(trimOrNull(request.getRateLimitProfile()))
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .build();
        row = linkRepository.save(row);
        return AdminPreRegistrationLinkCreateResponse.builder()
                .id(row.getId())
                .token(plainToken)
                .intakeCode(row.getIntakeCode())
                .campusCode(row.getCampusCode())
                .expiresAt(row.getExpiresAt())
                .maxSubmissions(row.getMaxSubmissions())
                .status(row.getStatus().name())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminPreRegistrationLinkItemResponse> listLinks() {
        return linkRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(x -> AdminPreRegistrationLinkItemResponse.builder()
                        .id(x.getId())
                        .intakeCode(x.getIntakeCode())
                        .campusCode(x.getCampusCode())
                        .maxSubmissions(x.getMaxSubmissions())
                        .submittedCount(x.getSubmittedCount() == null ? 0 : x.getSubmittedCount())
                        .status(x.getStatus().name())
                        .expiresAt(x.getExpiresAt())
                        .createdAt(x.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public AdminPreRegistrationLinkItemResponse closeLink(Long linkId) {
        PreRegistrationLink link = linkRepository.findById(linkId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy pre-registration link: " + linkId));
        link.setStatus(PreRegistrationLinkStatus.CLOSED);
        linkRepository.save(link);
        return AdminPreRegistrationLinkItemResponse.builder()
                .id(link.getId())
                .intakeCode(link.getIntakeCode())
                .campusCode(link.getCampusCode())
                .maxSubmissions(link.getMaxSubmissions())
                .submittedCount(link.getSubmittedCount() == null ? 0 : link.getSubmittedCount())
                .status(link.getStatus().name())
                .expiresAt(link.getExpiresAt())
                .createdAt(link.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AdminPreRegistrationLinkStatsResponse getLinkStats(Long linkId) {
        if (!linkRepository.existsById(linkId)) {
            throw new EntityNotFoundException("Không tìm thấy pre-registration link: " + linkId);
        }
        return AdminPreRegistrationLinkStatsResponse.builder()
                .linkId(linkId)
                .totalRequests(requestRepository.countByLink_Id(linkId))
                .pendingRequests(requestRepository.countByLink_IdAndStatus(linkId, PreRegistrationRequestStatus.PENDING))
                .processingRequests(requestRepository.countByLink_IdAndStatus(linkId, PreRegistrationRequestStatus.PROCESSING))
                .successRequests(requestRepository.countByLink_IdAndStatus(linkId, PreRegistrationRequestStatus.SUCCESS))
                .failedRequests(requestRepository.countByLink_IdAndStatus(linkId, PreRegistrationRequestStatus.FAILED))
                .build();
    }

    @Transactional
    public void processQueuedRequest(PreRegistrationQueueMessageDto message, int partition, long offset) {
        if (message.getRequestId() == null || message.getRequestId().isBlank()) return;

        if (message.getDedupeKey() != null && !message.getDedupeKey().isBlank()) {
            Optional<PreRegistrationRequest> dedupeRow = requestRepository.findByDedupeKey(message.getDedupeKey());
            if (dedupeRow.isPresent()) {
                PreRegistrationRequest existing = dedupeRow.get();
                if (existing.getStatus() == PreRegistrationRequestStatus.SUCCESS
                        || existing.getStatus() == PreRegistrationRequestStatus.FAILED) {
                    return;
                }
            }
        }

        PreRegistrationRequest row = requestRepository.findByRequestId(UUID.fromString(message.getRequestId()))
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy pre-registration request: " + message.getRequestId()));

        // Idempotency check đầu tiên: nếu đã xử lý xong, bỏ qua an toàn.
        if (row.getStatus() == PreRegistrationRequestStatus.SUCCESS || row.getStatus() == PreRegistrationRequestStatus.FAILED) {
            return;
        }

        row.setStatus(PreRegistrationRequestStatus.PROCESSING);
        row.setKafkaPartition(partition);
        row.setKafkaOffset(offset);
        requestRepository.save(row);

        PublicPreRegistrationSubmitRequest payload;
        try {
            payload = objectMapper.readValue(row.getPayloadJson(), PublicPreRegistrationSubmitRequest.class);
        } catch (Exception ex) {
            markFailed(row, "PREREG_INVALID_PAYLOAD", ex.getMessage());
            return;
        }

        NganhDaoTao nganh = nganhDaoTaoRepository.findByMaNganh(payload.getMaNganhDangKy())
                .orElse(null);
        if (nganh == null) {
            markFailed(row, "PREREG_INVALID_PAYLOAD", "Không tìm thấy ngành đăng ký: " + payload.getMaNganhDangKy());
            return;
        }

        try {
            Lop lop = resolveOrCreatePreRegLop(nganh, payload.getNienKhoa());
            User user = createStudentUser(payload);
            SinhVien sv = createSinhVienAndProfile(payload, lop, user);
            sv = sinhVienRepository.save(sv);

            row.setStatus(PreRegistrationRequestStatus.SUCCESS);
            row.setProcessedAt(LocalDateTime.now());
            row.setErrorCode(null);
            row.setLastErrorDetail("account=" + user.getUsername() + ";studentCode=" + sv.getMaSinhVien());
            requestRepository.save(row);
        } catch (IllegalArgumentException ex) {
            markFailed(row, "PREREG_INVALID_PAYLOAD", ex.getMessage());
        }
    }

    private PreRegistrationLink resolveActiveByToken(String token) {
        String tokenHash = sha256Hex(nullToBlank(token));
        PreRegistrationLink link = linkRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy pre-registration link."));
        if (link.getStatus() != PreRegistrationLinkStatus.ACTIVE) {
            throw new IllegalArgumentException("Link pre-registration đã đóng.");
        }
        if (link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Link pre-registration đã hết hạn.");
        }
        return link;
    }

    private String computeLinkStatus(PreRegistrationLink link, Optional<String> sourceIpOpt) {
        if (link.getStatus() != PreRegistrationLinkStatus.ACTIVE) {
            return "CLOSED";
        }
        if (link.getExpiresAt().isBefore(LocalDateTime.now())) {
            return "EXPIRED";
        }
        String key = quotaKey(link.getId());
        Long current = stringRedisTemplate.opsForValue().get(key) == null
                ? null
                : Long.parseLong(stringRedisTemplate.opsForValue().get(key));
        if (current != null && current >= link.getMaxSubmissions()) {
            return "QUOTA_EXCEEDED";
        }
        if (current == null) {
            long fallbackCount = requestRepository.countByLink_Id(link.getId());
            if (fallbackCount >= link.getMaxSubmissions()) {
                return "QUOTA_EXCEEDED";
            }
        }
        return "ACTIVE";
    }

    private void enqueueToGoIngress(PreRegistrationRequest row, PreRegistrationLink link) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("requestId", row.getRequestId().toString());
        body.put("linkId", link.getId());
        body.put("dedupeKey", row.getDedupeKey());
        body.put("traceId", row.getTraceId());
        body.put("submittedAt", row.getCreatedAt().toString());
        body.put("payloadRefId", row.getId());
        body.put("schema_version", 1);
        try {
            RestClient.builder()
                    .baseUrl(goIngressBaseUrl)
                    .build()
                    .post()
                    .uri(goIngressSubmitPath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

            String key = quotaKey(link.getId());
            Long newCount = stringRedisTemplate.opsForValue().increment(key);
            if (newCount != null && newCount == 1L) {
                Duration ttl = Duration.between(LocalDateTime.now(), link.getExpiresAt().plusMinutes(quotaBufferMinutes));
                if (!ttl.isNegative()) {
                    stringRedisTemplate.expire(key, ttl);
                }
            }
        } catch (RestClientException ex) {
            row.setStatus(PreRegistrationRequestStatus.FAILED);
            row.setErrorCode("PREREG_SYSTEM_BUSY");
            row.setLastErrorDetail(ex.getMessage());
            row.setProcessedAt(LocalDateTime.now());
            requestRepository.save(row);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "PREREG_SYSTEM_BUSY");
        }
    }

    private String quotaKey(Long linkId) {
        return "prereg:quota:" + linkId;
    }

    private Lop resolveOrCreatePreRegLop(NganhDaoTao nganh, String nienKhoa) {
        String normalizedNienKhoa = normalizeNienKhoa(nienKhoa);
        String maLop = "PREREG-" + nganh.getMaNganh().toUpperCase(Locale.ROOT) + "-" + normalizedNienKhoa;
        return lopRepository.findByMaLop(maLop)
                .orElseGet(() -> lopRepository.save(Lop.builder()
                        .maLop(maLop)
                        .tenLop("Lop tam pre-registration " + nganh.getMaNganh() + " " + normalizedNienKhoa)
                        .namNhapHoc(extractStartYear(normalizedNienKhoa))
                        .nganhDaoTao(nganh)
                        .build()));
    }

    private User createStudentUser(PublicPreRegistrationSubmitRequest payload) {
        String usernameBase = preferredUsernameBase(payload);
        String username = ensureUniqueUsername(usernameBase);
        String tempPassword = "Eduport@" + ThreadLocalRandom.current().nextInt(100000, 999999);
        return userRepository.save(User.builder()
                .username(username)
                .password(passwordEncoder.encode(tempPassword))
                .role(Role.STUDENT)
                .status(Status.ACTIVE)
                .email(trimOrNull(payload.getEmail()))
                .mfaEnabled(false)
                .build());
    }

    private SinhVien createSinhVienAndProfile(PublicPreRegistrationSubmitRequest payload, Lop lop, User user) {
        String maSinhVien = generateUniqueStudentCode(extractStartYear(normalizeNienKhoa(payload.getNienKhoa())));
        SinhVien sv = SinhVien.builder()
                .maSinhVien(maSinhVien)
                .hoTen(payload.getHoTen().trim())
                .lop(lop)
                .taiKhoan(user)
                .build();
        HoSoSinhVien hoSo = HoSoSinhVien.builder()
                .sinhVien(sv)
                .email(trimOrNull(payload.getEmail()))
                .sdt(trimOrNull(payload.getSoDienThoai()))
                .diaChi(trimOrNull(payload.getDiaChiThuongTru()))
                .ngaySinh(parseDate(payload.getNgaySinh()))
                .gioiTinh(trimOrNull(payload.getGioiTinh()))
                .soCccd(trimOrNull(payload.getSoCCCD()))
                .build();
        sv.setHoSoSinhVien(hoSo);
        return sv;
    }

    private String generateUniqueStudentCode(int startYear) {
        int yy = Math.abs(startYear % 100);
        for (int i = 0; i < 50; i++) {
            String candidate = "SV" + String.format("%02d", yy)
                    + ThreadLocalRandom.current().nextInt(100000, 999999);
            if (!sinhVienRepository.existsByMaSinhVien(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Không thể sinh mã sinh viên duy nhất.");
    }

    private String preferredUsernameBase(PublicPreRegistrationSubmitRequest payload) {
        String phone = normalizeIdentifier(payload.getSoDienThoai());
        if (!phone.isBlank()) return "sv" + phone;
        String email = normalizeIdentifier(payload.getEmail());
        if (!email.isBlank()) return "sv" + email;
        String cccd = normalizeIdentifier(payload.getSoCCCD());
        if (!cccd.isBlank()) return "sv" + cccd;
        return "sv" + ThreadLocalRandom.current().nextInt(100000, 999999);
    }

    private String ensureUniqueUsername(String base) {
        String sanitized = base.length() > 40 ? base.substring(0, 40) : base;
        String candidate = sanitized;
        int suffix = 1;
        while (userRepository.existsByUsername(candidate)) {
            candidate = sanitized + suffix;
            if (candidate.length() > 50) {
                candidate = candidate.substring(0, 50);
            }
            suffix++;
        }
        return candidate;
    }

    private String normalizeNienKhoa(String nienKhoa) {
        if (nienKhoa == null || nienKhoa.isBlank()) {
            throw new IllegalArgumentException("Thiếu niên khóa.");
        }
        String x = nienKhoa.trim();
        if (!x.matches("^[0-9]{4}(-[0-9]{4})?$")) {
            throw new IllegalArgumentException("Niên khóa không hợp lệ: " + nienKhoa);
        }
        return x;
    }

    private String generatePublicToken() {
        byte[] bytes = new byte[24];
        ThreadLocalRandom.current().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private Map<String, String> parseSuccessMeta(String raw) {
        Map<String, String> out = new LinkedHashMap<>();
        if (raw == null || raw.isBlank()) return out;
        String[] parts = raw.split(";");
        for (String p : parts) {
            int idx = p.indexOf('=');
            if (idx > 0 && idx < p.length() - 1) {
                out.put(p.substring(0, idx), p.substring(idx + 1));
            }
        }
        return out;
    }

    private int extractStartYear(String nienKhoa) {
        String start = nienKhoa.contains("-") ? nienKhoa.substring(0, 4) : nienKhoa;
        return Integer.parseInt(start);
    }

    private LocalDate parseDate(String raw) {
        if (raw == null || raw.isBlank()) return null;
        String x = raw.trim();
        try {
            return LocalDate.parse(x);
        } catch (DateTimeParseException ignored) {
        }
        try {
            String[] parts = x.split("/");
            if (parts.length == 3) {
                return LocalDate.of(Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
            }
        } catch (Exception ignored) {
        }
        throw new IllegalArgumentException("Ngày sinh không hợp lệ: " + raw);
    }

    private String trimOrNull(String value) {
        if (value == null) return null;
        String x = value.trim();
        return x.isEmpty() ? null : x;
    }

    private void markFailed(PreRegistrationRequest row, String errorCode, String detail) {
        row.setStatus(PreRegistrationRequestStatus.FAILED);
        row.setErrorCode(errorCode);
        row.setLastErrorDetail(detail);
        row.setProcessedAt(LocalDateTime.now());
        requestRepository.save(row);
    }

    private String toJson(PublicPreRegistrationSubmitRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Payload pre-registration không hợp lệ.");
        }
    }

    private String buildDedupeKey(PublicPreRegistrationSubmitRequest request, String intakeCode) {
        String seed = String.join("|",
                normalizeIdentifier(request.getSoCCCD()),
                normalizeIdentifier(request.getEmail()),
                normalizeIdentifier(request.getSoDienThoai()));
        return sha256Hex(seed + "|" + normalizeIdentifier(intakeCode));
    }

    private String normalizeIdentifier(String raw) {
        if (raw == null) {
            return "";
        }
        String x = raw.trim().toLowerCase(Locale.ROOT);
        return x.replaceAll("[^a-z0-9]", "");
    }

    private String computeIpPrefix(String sourceIp) {
        if (sourceIp == null || sourceIp.isBlank()) {
            return null;
        }
        String ip = sourceIp.trim();
        if (ip.contains(".")) {
            String[] parts = ip.split("\\.");
            if (parts.length == 4) {
                return parts[0] + "." + parts[1] + "." + parts[2] + ".0/24";
            }
        }
        if (ip.contains(":")) {
            String[] parts = ip.split(":");
            if (parts.length >= 3) {
                return parts[0] + ":" + parts[1] + ":" + parts[2] + "::/48";
            }
        }
        return null;
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value;
    }

    private String sha256Hex(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 không khả dụng", ex);
        }
    }
}
