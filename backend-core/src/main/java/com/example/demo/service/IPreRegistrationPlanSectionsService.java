package com.example.demo.service;

import com.example.demo.payload.request.PreRegistrationPlanSectionsRequest;
import com.example.demo.payload.response.PreRegistrationPlanSectionsResponse;

public interface IPreRegistrationPlanSectionsService {

    /**
     * Tạo (hoặc replay) các {@code lop_hoc_phan} shell {@code SHELL} theo nhu cầu PRE hoặc {@code sectionCount}.
     *
     * @param idempotencyKey optional header — đưa vào digest mã lớp để tránh va chạm giữa hai kế hoạch cùng tham số.
     */
    PreRegistrationPlanSectionsResponse planSections(PreRegistrationPlanSectionsRequest request, String idempotencyKey);
}
