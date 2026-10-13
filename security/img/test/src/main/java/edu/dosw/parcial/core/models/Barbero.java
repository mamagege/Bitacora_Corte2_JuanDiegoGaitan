package edu.dosw.parcial.core.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Barbero {
    private String name;
    private List<String> specialties;
    private List<String> days;
    private int startHour;
    private int endHour;

    public boolean isAvailableFor(String specialty, String day, int hour24) {
        if (specialties == null || days == null) {
            return false;
        }
        return specialties.contains(specialty)
                && days.contains(day)
                && hour24 >= startHour
                && hour24 < endHour;
    }
}
