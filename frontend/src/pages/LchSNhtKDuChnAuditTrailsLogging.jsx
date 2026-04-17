import React, { useCallback, useEffect, useState } from 'react';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const formatDt = (iso) => {
  if (!iso) return '—';
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return String(iso);
  return d.toLocaleString('vi-VN');
};

const ACTIONS = [
  { value: '', label: 'Tất cả mã hành động' },
  { value: 'GRADING_DRAFT_SAVE', label: 'GRADING_DRAFT_SAVE' },
  { value: 'GRADING_PUBLISH', label: 'GRADING_PUBLISH' },
  { value: 'RETAKE_SUBMIT', label: 'RETAKE_SUBMIT' },
  { value: 'RETAKE_APPROVE', label: 'RETAKE_APPROVE' },
  { value: 'RETAKE_REJECT', label: 'RETAKE_REJECT' }
];

const LchSNhtKDuChnAuditTrailsLogging = () => {
  const token = typeof localStorage !== 'undefined' ? localStorage.getItem('jwt_token') : null;
  const [page, setPage] = useState(0);
  const [size] = useState(15);
  const [maHanhDong, setMaHanhDong] = useState('');
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    if (!token) {
      setError('Chưa đăng nhập. Dùng tài khoản admin từ All Portal.');
      setData(null);
      setLoading(false);
      return;
    }
    setLoading(true);
    setError('');
    try {
      const q = new URLSearchParams({ page: String(page), size: String(size) });
      if (maHanhDong) q.set('maHanhDong', maHanhDong);
      const res = await fetch(`${API_BASE_URL}/api/v1/admin/audit-logs?${q}`, {
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' }
      });
      const body = await res.json().catch(() => ({}));
      if (!res.ok) throw new Error(body.message || 'Không tải nhật ký.');
      setData(body);
    } catch (e) {
      setError(e.message || 'Lỗi tải dữ liệu.');
      setData(null);
    } finally {
      setLoading(false);
    }
  }, [token, page, size, maHanhDong]);

  useEffect(() => {
    load();
  }, [load]);

  const rows = data?.content || [];
  const total = data?.totalElements ?? 0;
  const totalPages = data?.totalPages ?? 0;

  return (
    <main className="flex-1 flex flex-col min-h-screen">
      <section className="p-8 space-y-6 max-w-7xl mx-auto w-full">
        <div className="flex flex-col md:flex-row md:items-end justify-between gap-4">
          <div>
            <h2 className="text-3xl md:text-4xl font-extrabold text-blue-900 dark:text-blue-100 tracking-tight font-headline">
              Nhật ký dấu chân
            </h2>
            <p className="text-on-surface-variant mt-2 text-sm max-w-xl">
              Ghi nhận tối thiểu các thao tác nhập điểm (nháp / công bố) và xử lý phúc khảo (Task 23).
            </p>
          </div>
          <button
            type="button"
            onClick={load}
            className="flex items-center gap-2 px-5 py-2.5 bg-primary text-on-primary rounded-full font-semibold text-sm hover:opacity-90"
          >
            Làm mới
          </button>
        </div>

        <div className="flex flex-wrap gap-4 items-end bg-surface-container-lowest p-4 rounded-xl shadow-sm">
          <div>
            <label className="block text-xs font-bold text-on-surface-variant uppercase mb-1">Lọc mã hành động</label>
            <select
              className="min-w-[220px] px-4 py-2 rounded-lg bg-surface-container-low border-none text-sm font-medium"
              value={maHanhDong}
              onChange={(e) => {
                setMaHanhDong(e.target.value);
                setPage(0);
              }}
            >
              {ACTIONS.map((a) => (
                <option key={a.value || 'all'} value={a.value}>
                  {a.label}
                </option>
              ))}
            </select>
          </div>
          <p className="text-sm text-on-surface-variant pb-1">
            Tổng bản ghi: <strong className="text-on-surface">{total}</strong>
          </p>
        </div>

        {error && (
          <div className="rounded-xl bg-error-container text-on-error-container px-4 py-3 text-sm font-medium">{error}</div>
        )}

        <div className="bg-surface-container-lowest rounded-xl shadow-sm overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm min-w-[720px]">
              <thead className="bg-surface-container-low/80">
                <tr>
                  <th className="px-4 py-3 font-bold text-on-surface-variant text-xs uppercase">Thời gian</th>
                  <th className="px-4 py-3 font-bold text-on-surface-variant text-xs uppercase">User</th>
                  <th className="px-4 py-3 font-bold text-on-surface-variant text-xs uppercase">Vai trò</th>
                  <th className="px-4 py-3 font-bold text-on-surface-variant text-xs uppercase">Mã</th>
                  <th className="px-4 py-3 font-bold text-on-surface-variant text-xs uppercase">Mô tả</th>
                  <th className="px-4 py-3 font-bold text-on-surface-variant text-xs uppercase">Chi tiết (JSON)</th>
                </tr>
              </thead>
              <tbody>
                {loading && (
                  <tr>
                    <td colSpan={6} className="px-4 py-8 text-center text-on-surface-variant">
                      Đang tải…
                    </td>
                  </tr>
                )}
                {!loading && rows.length === 0 && (
                  <tr>
                    <td colSpan={6} className="px-4 py-8 text-center text-on-surface-variant">
                      Chưa có bản ghi. Thử nhập/sửa điểm bằng gv01 hoặc nộp phúc khảo bằng sv01 rồi làm mới.
                    </td>
                  </tr>
                )}
                {!loading &&
                  rows.map((r) => (
                    <tr key={r.idNhatKy} className="border-t border-outline-variant/20 hover:bg-surface-container-low/40">
                      <td className="px-4 py-3 whitespace-nowrap text-xs">{formatDt(r.thoiGian)}</td>
                      <td className="px-4 py-3 font-mono text-xs">{r.tenDangNhap}</td>
                      <td className="px-4 py-3 text-xs">{r.vaiTro || '—'}</td>
                      <td className="px-4 py-3">
                        <span className="px-2 py-0.5 rounded-full bg-primary/10 text-primary text-[10px] font-bold">{r.maHanhDong}</span>
                      </td>
                      <td className="px-4 py-3 text-on-surface-variant max-w-xs">{r.moTaNgan}</td>
                      <td className="px-4 py-3 font-mono text-[10px] text-on-surface-variant max-w-md truncate" title={r.chiTietJson}>
                        {r.chiTietJson || '—'}
                      </td>
                    </tr>
                  ))}
              </tbody>
            </table>
          </div>
          {totalPages > 1 && (
            <div className="flex justify-between items-center px-4 py-3 border-t border-outline-variant/20">
              <button
                type="button"
                disabled={page <= 0}
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                className="text-sm font-bold text-primary disabled:opacity-40"
              >
                ← Trước
              </button>
              <span className="text-xs text-on-surface-variant">
                Trang {page + 1} / {totalPages}
              </span>
              <button
                type="button"
                disabled={page >= totalPages - 1}
                onClick={() => setPage((p) => p + 1)}
                className="text-sm font-bold text-primary disabled:opacity-40"
              >
                Sau →
              </button>
            </div>
          )}
        </div>
      </section>
    </main>
  );
};

export default LchSNhtKDuChnAuditTrailsLogging;
