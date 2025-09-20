package com.example.devdiarybackend.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "analytics_daily", uniqueConstraints = @UniqueConstraint(name = "uniq_user_date", columnNames = {"user_id", "date"}))
public class AnalyticsDaily {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int entriesCount = 0;

    @Column(nullable = false)
    private int commits = 0;

    @Column(nullable = false)
    private int prs = 0;

    @Column(nullable = false)
    private int meetings = 0;

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public int getEntriesCount() { return entriesCount; }
    public void setEntriesCount(int entriesCount) { this.entriesCount = entriesCount; }
    public int getCommits() { return commits; }
    public void setCommits(int commits) { this.commits = commits; }
    public int getPrs() { return prs; }
    public void setPrs(int prs) { this.prs = prs; }
    public int getMeetings() { return meetings; }
    public void setMeetings(int meetings) { this.meetings = meetings; }
}
