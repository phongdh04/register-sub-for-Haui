package com.example.demo.scheduling.snapshot;

/** Một chiếm chỗ (lớp đã có sẵn trong snapshot) trong không gian thứ + tiết. */
public record SchedulingOccupancyOccurrence(long idLopHp, int tietBd, int tietKt) {

    public boolean overlaps(int bd, int kt) {
        return tietBd <= kt && bd <= tietKt;
    }
}
