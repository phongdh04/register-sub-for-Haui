import React, { useEffect, useMemo, useState } from 'react';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
const WEEK_DAYS = [2, 3, 4, 5, 6, 7, 8];

const formatWeekDay = (thu) => {
  if (thu === 8) return 'CN';
  return `Thứ ${thu}`;
};

const extractPeriodStart = (tiet) => {
  if (!tiet) return 999;
  const first = String(tiet).split('-')[0];
  const value = Number(first);
  return Number.isNaN(value) ? 999 : value;
};

const DchVThiKhaBiuThngMinh = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const controller = new AbortController();

    const loadTimetable = async () => {
      const token = localStorage.getItem('jwt_token');
      if (!token) {
        setError('Bạn chưa đăng nhập. Vui lòng đăng nhập để xem thời khóa biểu.');
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        setError('');

        const response = await fetch(`${API_BASE_URL}/api/v1/timetable/me`, {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json'
          },
          signal: controller.signal
        });

        if (!response.ok) {
          const fallback = 'Không tải được thời khóa biểu từ hệ thống.';
          const body = await response.json().catch(() => ({}));
          throw new Error(body.message || fallback);
        }

        const payload = await response.json();
        setData(payload);
      } catch (err) {
        if (err.name !== 'AbortError') {
          setError(err.message || 'Có lỗi xảy ra khi tải dữ liệu.');
        }
      } finally {
        setLoading(false);
      }
    };

    loadTimetable();
    return () => controller.abort();
  }, []);

  const sessionRows = useMemo(() => {
    if (!data?.courses?.length) return [];

    const rows = [];
    data.courses.forEach((course) => {
      (course.sessions || []).forEach((session) => {
        rows.push({
          courseCode: course.maHocPhan,
          courseName: course.tenHocPhan,
          credit: course.soTinChi,
          lecturer: course.tenGiangVien,
          thu: session.thu,
          tiet: session.tiet,
          phong: session.phong,
          ngayBatDau: session.ngayBatDau,
          ngayKetThuc: session.ngayKetThuc
        });
      });
    });

    rows.sort((a, b) => {
      if (a.thu !== b.thu) return (a.thu || 99) - (b.thu || 99);
      return extractPeriodStart(a.tiet) - extractPeriodStart(b.tiet);
    });

    return rows;
  }, [data]);

  const sessionsByDay = useMemo(() => {
    const map = new Map();
    WEEK_DAYS.forEach((day) => map.set(day, []));
    sessionRows.forEach((row) => {
      if (!map.has(row.thu)) return;
      map.get(row.thu).push(row);
    });
    return map;
  }, [sessionRows]);

  return (
    <main className="min-h-screen p-8 bg-surface">
      <div className="max-w-7xl mx-auto space-y-6">
        <div className="flex flex-col gap-2">
          <h2 className="text-3xl font-black tracking-tight text-on-surface">Dịch vụ Thời khóa biểu thông minh</h2>
          <p className="text-on-surface-variant">
            Theo dõi lịch học theo học kỳ từ dữ liệu đăng ký học phần đã xác nhận.
          </p>
        </div>

        {loading && (
          <div className="bg-surface-container-lowest rounded-xl p-6 text-sm font-medium text-on-surface-variant">
            Đang tải thời khóa biểu...
          </div>
        )}

        {!loading && error && (
          <div className="bg-error-container/40 border border-error/30 rounded-xl p-6 text-sm font-medium text-error">
            {error}
          </div>
        )}

        {!loading && !error && data && (
          <>
            <section className="grid grid-cols-1 md:grid-cols-4 gap-4">
              <div className="bg-surface-container-lowest rounded-xl p-4">
                <p className="text-xs uppercase text-on-surface-variant font-bold">Sinh viên</p>
                <p className="text-lg font-bold text-on-surface mt-1">{data.hoTenSinhVien}</p>
                <p className="text-sm text-on-surface-variant">{data.maSinhVien}</p>
              </div>
              <div className="bg-surface-container-lowest rounded-xl p-4">
                <p className="text-xs uppercase text-on-surface-variant font-bold">Học kỳ</p>
                <p className="text-lg font-bold text-primary mt-1">{data.tenHocKy || 'Chưa xác định'}</p>
              </div>
              <div className="bg-surface-container-lowest rounded-xl p-4">
                <p className="text-xs uppercase text-on-surface-variant font-bold">Môn đã đăng ký</p>
                <p className="text-2xl font-black text-on-surface mt-1">{data.tongMonDangKy}</p>
              </div>
              <div className="bg-surface-container-lowest rounded-xl p-4">
                <p className="text-xs uppercase text-on-surface-variant font-bold">Tổng tín chỉ</p>
                <p className="text-2xl font-black text-on-surface mt-1">{data.tongTinChi}</p>
              </div>
            </section>

            <section className="bg-surface-container-lowest rounded-xl p-6">
              <h3 className="text-lg font-bold text-on-surface mb-4">Lịch theo ngày trong tuần</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
                {WEEK_DAYS.map((day) => (
                  <div key={day} className="border border-outline-variant/20 rounded-lg p-3 bg-surface-container-low">
                    <p className="font-bold text-sm text-primary mb-2">{formatWeekDay(day)}</p>
                    {(sessionsByDay.get(day) || []).length === 0 ? (
                      <p className="text-xs text-on-surface-variant">Không có lịch.</p>
                    ) : (
                      <div className="space-y-2">
                        {(sessionsByDay.get(day) || []).map((item, idx) => (
                          <div key={`${item.courseCode}-${item.tiet}-${idx}`} className="bg-white rounded-md p-2">
                            <p className="text-xs font-bold text-on-surface">{item.courseName}</p>
                            <p className="text-xs text-on-surface-variant">{item.tiet} - {item.phong || 'Chưa có phòng'}</p>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </section>

            <section className="bg-surface-container-lowest rounded-xl p-6 overflow-x-auto">
              <h3 className="text-lg font-bold text-on-surface mb-4">Danh sách buổi học chi tiết</h3>
              <table className="w-full text-left border-collapse min-w-[900px]">
                <thead>
                  <tr className="border-b border-outline-variant/20 text-xs uppercase text-on-surface-variant">
                    <th className="py-3 pr-4">Mã HP</th>
                    <th className="py-3 pr-4">Tên môn</th>
                    <th className="py-3 pr-4">Tín chỉ</th>
                    <th className="py-3 pr-4">Giảng viên</th>
                    <th className="py-3 pr-4">Thứ</th>
                    <th className="py-3 pr-4">Tiết</th>
                    <th className="py-3 pr-4">Phòng</th>
                    <th className="py-3 pr-4">Bắt đầu</th>
                    <th className="py-3 pr-0">Kết thúc</th>
                  </tr>
                </thead>
                <tbody>
                  {sessionRows.length === 0 ? (
                    <tr>
                      <td className="py-4 text-sm text-on-surface-variant" colSpan={9}>
                        Chưa có dữ liệu lịch học cho học kỳ hiện tại.
                      </td>
                    </tr>
                  ) : (
                    sessionRows.map((row, index) => (
                      <tr key={`${row.courseCode}-${row.tiet}-${index}`} className="border-b border-outline-variant/10 text-sm">
                        <td className="py-3 pr-4 font-mono text-primary">{row.courseCode}</td>
                        <td className="py-3 pr-4 font-medium">{row.courseName}</td>
                        <td className="py-3 pr-4">{row.credit || '-'}</td>
                        <td className="py-3 pr-4">{row.lecturer || 'Chưa phân công'}</td>
                        <td className="py-3 pr-4">{formatWeekDay(row.thu)}</td>
                        <td className="py-3 pr-4">{row.tiet || '-'}</td>
                        <td className="py-3 pr-4">{row.phong || '-'}</td>
                        <td className="py-3 pr-4">{row.ngayBatDau || '-'}</td>
                        <td className="py-3 pr-0">{row.ngayKetThuc || '-'}</td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </section>
          </>
        )}
      </div>
    </main>
  );
};

export default DchVThiKhaBiuThngMinh;
