        package com.propertypilot.web.controller;

        import com.propertypilot.application.dto.SubscriptionNotificationResponse;
        import com.propertypilot.application.service.SubscriptionNotificationService;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.*;

        import java.util.List;
        import java.util.UUID;

        @RestController
        @RequestMapping("/api/subscription-notifications")
        public class SubscriptionNotificationController {

        private final SubscriptionNotificationService
                notificationService;

        public SubscriptionNotificationController(
                SubscriptionNotificationService notificationService) {

                this.notificationService =
                        notificationService;
        }

        @GetMapping("/{customerSubscriptionId}")
        public ResponseEntity<
                List<SubscriptionNotificationResponse>>
        getNotifications(
                @PathVariable
                UUID customerSubscriptionId) {

                return ResponseEntity.ok(
                        notificationService.getNotifications(
                                customerSubscriptionId));
        }
        }