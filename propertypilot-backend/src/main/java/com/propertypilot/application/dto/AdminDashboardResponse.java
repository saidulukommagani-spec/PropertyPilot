package com.propertypilot.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDashboardResponse {

    private long totalRequests;

    private long newRequests;

    private long pendingPayment;

    private long paymentCompleted;

    private long pendingAssignment;

    private long assigned;

    private long accepted;

    private long inProgress;

    private long reportSubmitted;

    private long underReview;

    private long completed;

    private long cancelled;
}