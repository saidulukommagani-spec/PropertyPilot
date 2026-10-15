# PropertyPilot Dead Code and Persistence-Only Report

This report separates **confirmed inactive implementation** from **unreferenced persistence candidates**. JPA entity/repository existence alone does not prove runtime use. References were searched across backend production and test Java sources. Database tables may already contain data, so this report does not recommend dropping tables without a data/usage review.

## Confirmed inactive or incomplete code

| Component | Evidence | Classification / action |
|---|---|---|
| `application/service/AgentAssignmentServiceImpl.java` | `acceptAssignment`, `rejectAssignment`, `startAssignment`, `completeAssignment` throw `IllegalStateException`; lifecycle work is instead in `ServiceRequestServiceImpl`/`VisitServiceImpl`. No dedicated assignment controller consumes this service. | Partially dead duplicate lifecycle implementation. Retain only create/get if consumed; otherwise consolidate behind one workflow owner. |
| `application/service/ServiceRequestWorkflowService.java` | Interface present with no matching implementation in `application/service/impl`. | Unimplemented/unused abstraction. Remove or implement only after searching external wiring and intended API. |
| `application/scheduler/SubscriptionReminderScheduler.java` | Queries expiring subscriptions and logs IDs; no notification call. | Runnable but business-inactive scheduler. Connect notification delivery or disable/remove. |
| `application/service/impl/PricingRuleEngineServiceImpl.java` | Returns `BigDecimal.ZERO` for cluster and ETA discounts; calculator never applies coupon/subscription discount in its calculation path. | Partial feature/dead paths relative to persisted rule entities. Implement or clearly disable unsupported features. |
| `application/service/impl/DocumentServiceImpl.java` | Two helper paths at about lines 834/840 return `null`; follow call sites before treating as dead, since callers may use null as optional mapping. | Suspicious dead/null behavior requiring call-chain cleanup. |
| `propertypilot-frontend/src/**` | 143 of 154 files are ≤150 bytes; sampled `App.tsx`, `AppRoutes.tsx`, API client and feature pages contain directory/setup prose rather than executable UI/API code; the 11 larger files are mostly generic implementation roadmaps. `package.json`, `vite.config.ts`, and tsconfig are prose. | Scaffold/documentation mistaken for app code; rebuild before treating frontend as launchable. |
| `mobile_app/lib/main.dart` | Default Flutter counter app, no PropertyPilot API/domain references. | Demo app, not a PropertyPilot client. |
| `backend/` and `admin-portal/` | Backend contains README plus empty planned service dirs; admin portal has README and empty src dirs. | Planning scaffolds, no runtime code. |

## Strong unreferenced repository candidates

The following repository interfaces were found only at their declarations by class-name search in backend production and test Java. Spring may still instantiate repository beans, but no application code currently consumes them; corresponding entities may remain mapped or be referenced by relationships.

`AgentSkillRepository`, `AuthOtpChallengeRepository`, `AuthSessionRepository`, `ClusterRepository`, `ClusterRuleRepository`, `ComplaintCommentRepository`, `ComplaintRepository`, `ContractRepository`, `CountryRepository`, `CouponRedemptionRepository`, `CouponRepository`, `CoverageZoneRepository`, `DistrictRepository`, `EtaRuleRepository`, `KnowledgeBaseArticleRepository`, `LeadJpaRepository`, `LocationMasterRepository`, `MarketplaceCommissionRepository`, `MarketplaceContactDisclosureRepository`, `MarketplaceInquiryRepository`, `MarketplaceListingRepository`, `MonitoringAlertRepository`, `MonitoringScheduleRepository`, `NotificationRepository`, `NotificationTemplateRepository`, `NriRelationshipAssignmentRepository`, `NriRelationshipMessageRepository`, `OfflineSyncOperationRepository`, `PartnerRepository`, `PartnerServiceRepository`, `PricingDiscountApplicationRepository`, `PricingEstimateRepository`, `PricingProfileRepository`, `PricingQuoteLineItemRepository`, `PricingRuleApprovalRepository`, `QuotationRepository`, `ReferenceDataRepository`, `RefundRepository`, `ReportReviewRepository`, `RoleJpaRepository`, `ServicePriceRuleRepository`, `ServiceRatingRepository`, `ServiceSlaPolicyRepository`, `StateRepository`, `SubscriptionLifecycleEventRepository`, `SubscriptionPauseRepository`, `UserRoleJpaRepository`, `VendorAssignmentRepository`, `VendorRepository`, `VendorServiceMappingRepository`.

`LeadJpaRepository`, `RoleJpaRepository`, and `UserRoleJpaRepository` duplicate active alternatives (`LeadRepository`, `RoleRepository`, `UserRoleRepository`). Customer also has `CustomerJpaRepository` and `CustomerRepository`, both actively referenced in distinct services and should be consolidated only after query-method review.

## Entity candidates without direct Java references

Static class-name search found `DealActivityEntity`, `DealMilestoneEntity`, and `DealServiceRequestLinkEntity` referenced only by their declarations. Verify their JPA table mappings, inverse relationships, migration use and intended deal workflows before deciding whether to wire or retire them.

Many repositories above correspond to entity families for OTP/session, monitoring, coupons/discounts, marketplace/vendor/partner, contracts/refunds, notifications, offline sync, NRI relationship, reports and reference geography. These are persistence-only or nearly so in the current application. Entity relationship annotations can create indirect references, so these are not all classified as wholly dead entities.

## Obsolete/misaligned modules

- **Marketplace:** marketplace listing/inquiry/commission/disclosure persistence and React vendor/quotation pages are not aligned with the business statement “PropertyPilot is NOT a marketplace.” Backend route policy mentions `/api/v1/marketplace-listings`, but no matching controller appears in the controller package inventory. Preserve internal partner fulfillment only if used; remove customer-facing marketplace concept.
- **Monitoring:** tables and repositories exist but no controller/service workflow. Clarify whether this means customer property monitoring or technical platform monitoring.
- **Auth challenges/sessions:** OTP/session entities and repositories are not integrated into login service; do not claim MFA/session revocation capability from schema alone.
- **Coupons/discounts/price approvals:** rules/tables exist without active end-to-end calculation/persistence paths; these are unfinished rather than safely removable until pricing scope is decided.
- **Parallel migration/documentation roots:** `my-database-migration-project/migrations` and `docs/08_Implementation/database` are not runtime migrations. Mark historical/planning or reconcile to Flyway; do not apply blindly.

