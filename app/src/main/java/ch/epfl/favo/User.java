package ch.epfl.favo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


public class User {

    private String name;
    private String email;
    private String deviceId;
    private LocalDate birthDate;
    private int activeAcceptingFavors;
    private int activeRequestingFavors;

    public User() {}

    public User(
            String name,
            String email,
            String deviceId,
            LocalDate birthDate,
            int activeAcceptingFavors,
            int activeRequestingFavors
    ) {
        this.name = name;
        this.email = email;
        this.deviceId = deviceId;
        this.birthDate = birthDate;
        this.activeAcceptingFavors = activeAcceptingFavors;
        this.activeRequestingFavors = activeRequestingFavors;
    }

    public String getName() { return name; }

    public String getEmail() { return email; }

    public String getDeviceId() { return deviceId; }

    public LocalDate getBirthDate() { return birthDate; }

    public int getActiveAcceptingFavors() { return activeAcceptingFavors; }

    public int getActiveRequestingFavors() { return activeRequestingFavors; }

    void setActiveAcceptingFavors(int activeAcceptingFavors) {
        this.activeAcceptingFavors = activeAcceptingFavors;
    }

    void setActiveRequestingFavors(int activeRequestingFavors) {
        this.activeRequestingFavors = activeRequestingFavors;
    }

    boolean canAccept() {
        return activeAcceptingFavors + activeRequestingFavors <= 1;
    }

    boolean canRequest() {
        return activeAcceptingFavors + activeRequestingFavors <= 1;
    }
}
