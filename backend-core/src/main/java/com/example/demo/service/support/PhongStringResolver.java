package com.example.demo.service.support;

import com.example.demo.domain.entity.PhongHoc;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Chuẩn hóa chuỗi phòng từ JSON TKB và khớp với {@link PhongHoc#maPhong}
 * (exact không phân biệt hoa thường, fallback loose-key alphanumeric).
 */
@Component
public class PhongStringResolver {

    public enum Kind {
        UNIQUE,
        AMBIGUOUS,
        MISSING,
        NO_MATCH
    }

    public record Match(Kind kind, List<PhongHoc> candidates) {
        public static Match unique(PhongHoc p) {
            return new Match(Kind.UNIQUE, List.of(p));
        }

        public static Match ambiguous(List<PhongHoc> list) {
            return new Match(Kind.AMBIGUOUS, List.copyOf(list));
        }

        public static Match missing() {
            return new Match(Kind.MISSING, List.of());
        }

        public static Match noMatch() {
            return new Match(Kind.NO_MATCH, List.of());
        }

        public Optional<Long> uniqueId() {
            return kind == Kind.UNIQUE && !candidates.isEmpty()
                    ? Optional.of(candidates.getFirst().getIdPhong())
                    : Optional.empty();
        }
    }

    public Index buildIndex(List<PhongHoc> allRooms) {
        Map<String, List<PhongHoc>> loose = new HashMap<>();
        for (PhongHoc p : allRooms) {
            String k = looseKey(p.getMaPhong());
            if (k.isEmpty()) {
                continue;
            }
            loose.computeIfAbsent(k, x -> new ArrayList<>()).add(p);
        }
        return new Index(allRooms, loose);
    }

    public Match resolve(Index index, Object phongRaw) {
        if (phongRaw == null) {
            return Match.missing();
        }
        String s = String.valueOf(phongRaw).trim();
        if (s.isEmpty()) {
            return Match.missing();
        }
        for (PhongHoc p : index.allRooms()) {
            if (p.getMaPhong() != null && p.getMaPhong().trim().equalsIgnoreCase(s)) {
                return Match.unique(p);
            }
        }
        String lk = looseKey(s);
        if (lk.isEmpty()) {
            return Match.noMatch();
        }
        List<PhongHoc> hit = index.looseIndex().getOrDefault(lk, List.of());
        if (hit.isEmpty()) {
            return Match.noMatch();
        }
        if (hit.size() == 1) {
            return Match.unique(hit.getFirst());
        }
        return Match.ambiguous(hit);
    }

    /** Chỉ giữ A–Z 0–9 sau khi uppercase (gộp A.101 ↔ A101). */
    public static String looseKey(String raw) {
        if (raw == null) {
            return "";
        }
        String u = raw.trim().toUpperCase(Locale.ROOT);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < u.length(); i++) {
            char c = u.charAt(i);
            if ((c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public record Index(List<PhongHoc> allRooms, Map<String, List<PhongHoc>> looseIndex) {}
}
