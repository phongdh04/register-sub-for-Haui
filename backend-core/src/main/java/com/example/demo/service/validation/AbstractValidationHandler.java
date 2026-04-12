package com.example.demo.service.validation;

import com.example.demo.payload.request.RegistrationMessageDto;

/**
 * Base class trừu tượng cho mọi Handler trong Chain.
 * Template Method Pattern: Định nghĩa sườn validate(), handler con override doValidate().
 *
 * SRP: Chỉ quản lý con trỏ next và điều phối chain, không chứa business logic.
 * OCP: Handler con kế thừa và thêm logic mà không sửa class này.
 */
public abstract class AbstractValidationHandler implements IRegistrationValidationHandler {

    /** Con trỏ tới handler kế tiếp trong chain. null = cuối chain. */
    private IRegistrationValidationHandler next;

    @Override
    public IRegistrationValidationHandler setNext(IRegistrationValidationHandler next) {
        this.next = next;
        return next; // Trả về next để hỗ trợ: handlerA.setNext(handlerB).setNext(handlerC)
    }

    /**
     * Template Method: Gọi doValidate() của handler hiện tại,
     * nếu không ném exception thì chuyển tiếp cho next.
     */
    @Override
    public final void validate(RegistrationMessageDto msg) throws RegistrationValidationException {
        doValidate(msg);
        if (next != null) {
            next.validate(msg);
        }
    }

    /**
     * Mỗi Handler con implement method này để thực hiện 1 rule validate duy nhất.
     * @throws DangKyValidationException nếu phát hiện vi phạm.
     */
    protected abstract void doValidate(RegistrationMessageDto msg) throws RegistrationValidationException;
}
