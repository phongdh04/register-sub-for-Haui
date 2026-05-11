/**
 * Đồng bộ học kỳ đang xem giữa /student/pre-registration và /student/registration
 * (intent PRE gắn theo học kỳ — sai học kỳ thì trang đăng ký chính thức không thấy môn dự kiến).
 */

const STORAGE_KEY = 'eduport.student.semesterSelection';

/**
 * Chọn hocKyId khởi đầu: đang có state → giữ; có bản nhớ hợp lệ trong session → dùng;
 * không thì học kỳ hiện hành; cuối cùng học kỳ đầu trong danh sách.
 */
export function resolveInitialStudentHocKyFromRows(rows, existingSelection) {
  if (existingSelection) return existingSelection;
  const list = Array.isArray(rows) ? rows : [];
  if (list.length === 0) return '';
  try {
    const stored = sessionStorage.getItem(STORAGE_KEY);
    if (stored && list.some((h) => String(h.idHocKy ?? h.id) === stored)) {
      return stored;
    }
  } catch {
    /* ignore */
  }
  const current = list.find((h) => h.trangThaiHienHanh === true);
  const chosen =
    current != null
      ? String(current.idHocKy ?? current.id)
      : String(list[0].idHocKy ?? list[0].id ?? '');
  try {
    sessionStorage.setItem(STORAGE_KEY, chosen);
  } catch {
    /* ignore */
  }
  return chosen;
}

export function rememberStudentHocKy(id) {
  if (id == null || id === '') return;
  try {
    sessionStorage.setItem(STORAGE_KEY, String(id));
  } catch {
    /* ignore */
  }
}
