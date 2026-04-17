import React, { useCallback, useEffect, useState } from 'react';
import { API_BASE_URL, authHeaders } from '../config/api';

const XcThcaYuTMfa2FaVChKS = () => {
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [msg, setMsg] = useState('');
  const [err, setErr] = useState('');
  const [mfaEnabled, setMfaEnabled] = useState(false);
  const [emailMasked, setEmailMasked] = useState('');
  const [hasEmail, setHasEmail] = useState(false);
  const [emailInput, setEmailInput] = useState('');
  const [wantEnable, setWantEnable] = useState(false);

  const loadStatus = useCallback(async () => {
    setErr('');
    setLoading(true);
    try {
      const res = await fetch(`${API_BASE_URL}/api/v1/admin/mfa/status`, { headers: authHeaders() });
      if (!res.ok) {
        setErr(res.status === 401 ? 'Phiên hết hạn — đăng nhập lại với tài khoản Admin.' : 'Không tải được cấu hình MFA.');
        return;
      }
      const data = await res.json();
      setMfaEnabled(!!data.mfaEnabled);
      setWantEnable(!!data.mfaEnabled);
      setEmailMasked(data.emailMasked || '');
      setHasEmail(!!data.hasEmail);
    } catch (e) {
      setErr('Lỗi kết nối API.');
      console.error(e);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadStatus();
  }, [loadStatus]);

  const saveSettings = async (e) => {
    e.preventDefault();
    setMsg('');
    setErr('');
    setSaving(true);
    try {
      const body = { enabled: wantEnable };
      if (wantEnable && emailInput.trim()) {
        body.email = emailInput.trim();
      }
      const res = await fetch(`${API_BASE_URL}/api/v1/admin/mfa/settings`, {
        method: 'PUT',
        headers: authHeaders(),
        body: JSON.stringify(body)
      });
      const text = await res.text();
      if (!res.ok) {
        setErr(text || 'Cập nhật thất bại.');
        return;
      }
      let data;
      try {
        data = JSON.parse(text);
      } catch {
        setErr('Phản hồi không hợp lệ.');
        return;
      }
      setMfaEnabled(!!data.mfaEnabled);
      setEmailMasked(data.emailMasked || '');
      setHasEmail(!!data.hasEmail);
      setMsg('Đã lưu cấu hình MFA (Task 22).');
    } catch (e) {
      setErr('Lỗi kết nối API.');
      console.error(e);
    } finally {
      setSaving(false);
    }
  };

  return (
    <main className="flex-1 p-8 min-h-screen">
      <div className="max-w-3xl mx-auto space-y-8">
        <div>
          <h1 className="text-2xl font-bold text-on-surface flex items-center gap-3">
            <span className="material-symbols-outlined text-primary">mail_lock</span>
            Xác thực đa yếu tố (Email OTP)
          </h1>
          <p className="text-on-surface-variant text-sm mt-2">
            Chỉ áp dụng cho tài khoản <strong>Admin</strong>: sau khi nhập mật khẩu đúng, hệ thống gửi mã 6 số tới email đã khai báo.
            Môi trường demo: mã xuất hiện trong log backend (WARN) nếu chưa cấu hình SMTP.
          </p>
        </div>

        <div className="bg-surface-container-lowest p-8 rounded-xl shadow-sm border border-outline-variant/20">
          {loading ? (
            <p className="text-on-surface-variant">Đang tải…</p>
          ) : (
            <form onSubmit={saveSettings} className="space-y-6">
              {err && (
                <div className="p-4 rounded-xl bg-error/10 border border-error/20 text-error text-sm">{err}</div>
              )}
              {msg && (
                <div className="p-4 rounded-xl bg-primary/10 border border-primary/20 text-primary text-sm font-medium">{msg}</div>
              )}

              <div className="flex items-center justify-between p-4 bg-surface-container-low rounded-xl">
                <div>
                  <p className="font-bold text-on-surface">Bật MFA khi đăng nhập</p>
                  <p className="text-sm text-on-surface-variant mt-1">
                    Trạng thái hiện tại: {mfaEnabled ? 'Đang bật' : 'Đang tắt'}
                    {emailMasked ? ` — ${emailMasked}` : ''}
                  </p>
                </div>
                <label className="relative inline-flex items-center cursor-pointer">
                  <input
                    type="checkbox"
                    className="sr-only peer"
                    checked={wantEnable}
                    onChange={(e) => setWantEnable(e.target.checked)}
                  />
                  <div className="w-11 h-6 bg-outline-variant peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary" />
                </label>
              </div>

              {wantEnable && (
                <div className="space-y-2">
                  <label className="text-label-md font-bold text-on-surface-variant uppercase" htmlFor="mfa-email">
                    Email nhận mã OTP {hasEmail ? '(đổi nếu cần)' : '(bắt buộc)'}
                  </label>
                  <input
                    id="mfa-email"
                    type="email"
                    className="w-full px-4 py-3 rounded-xl bg-surface-container-low border-none focus:ring-2 focus:ring-primary/20"
                    placeholder="admin@ten-truong.edu.vn"
                    value={emailInput}
                    onChange={(e) => setEmailInput(e.target.value)}
                  />
                </div>
              )}

              <div className="flex gap-3 flex-wrap">
                <button
                  type="submit"
                  disabled={saving || (wantEnable && !emailInput.trim() && !hasEmail)}
                  className="px-6 py-3 rounded-full bg-primary text-on-primary font-bold text-sm disabled:opacity-50"
                >
                  {saving ? 'Đang lưu…' : 'Lưu cấu hình'}
                </button>
                <button type="button" onClick={loadStatus} className="px-6 py-3 rounded-full border border-outline-variant font-semibold text-sm">
                  Tải lại
                </button>
              </div>
            </form>
          )}
        </div>

        <p className="text-xs text-on-surface-variant">
          API: <code className="bg-surface-container-high px-1 rounded">GET /api/v1/admin/mfa/status</code>,{' '}
          <code className="bg-surface-container-high px-1 rounded">PUT /api/v1/admin/mfa/settings</code>; đăng nhập:{' '}
          <code className="bg-surface-container-high px-1 rounded">POST /api/auth/mfa/verify</code>
        </p>
      </div>
    </main>
  );
};

export default XcThcaYuTMfa2FaVChKS;
