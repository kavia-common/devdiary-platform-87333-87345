package com.example.devdiarybackend.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "user_settings")
public class UserSettings {
    @Id
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 30)
    private String timezone;

    @Column(length = 10)
    private String locale;

    @Column(length = 30)
    private String theme;

    @Column(nullable = false)
    private boolean dailySummaryEmail = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    public String getLocale() { return locale; }
    public void setLocale(String locale) { this.locale = locale; }
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
    public boolean isDailySummaryEmail() { return dailySummaryEmail; }
    public void setDailySummaryEmail(boolean dailySummaryEmail) { this.dailySummaryEmail = dailySummaryEmail; }
}
