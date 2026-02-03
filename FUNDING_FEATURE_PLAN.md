# Alumni Support Fund – Implementation Plan

This document is the **plan only** for the “Alumni Support Fund” feature. Implementation will follow this plan in phases.

---

## 1. Overview

**Feature name:** Alumni Support Fund  
**Purpose:** Allow admins to create funding campaigns (optionally tied to events), alumni to donate via Card or bKash, and the landing page to show transparency (totals, progress, graphs).  
**Rule:** All landing totals and percentages are computed only from donations where `status = PAID`.

---

## 2. Data Model

### 2.1 Reuse / Extend

- **events** – Already exists. Use as-is; campaigns will reference `event_id` (nullable for “general fund” campaigns).
- **BaseEntity** – All new entities extend it (id, version, createdDate, lastModifiedDate).

### 2.2 New Tables & Entities

#### A) `campaigns` (Funding Campaign)

| Column             | Type           | Notes |
|--------------------|----------------|-------|
| id                 | Long (PK)      | From BaseEntity |
| event_id           | Long (FK)      | Nullable – null = general fund |
| title              | String         | Campaign title |
| description        | String / TEXT  | Campaign description |
| goal_amount        | BigDecimal     | Target amount |
| start_date         | LocalDate/Date | Campaign start |
| end_date           | LocalDate/Date | Campaign end |
| status             | Enum           | DRAFT, ACTIVE, CLOSED |
| contact_phone      | String         | e.g. +8801673323434 |
| methods_enabled    | String/JSON    | e.g. "CARD,BKASH" or separate boolean columns |
| created_by_admin_id| Long (FK→User)| Admin who created |
| created_date, last_modified_date, version | From BaseEntity |

**Entity:** `FundingCampaign.java`  
**Repository:** `FundingCampaignRepository.java`  
**Enums:** `CampaignStatus` (DRAFT, ACTIVE, CLOSED).

**Design choice for payment methods:**  
- Option 1: `card_enabled` (boolean), `bkash_enabled` (boolean).  
- Option 2: Single column `methods_enabled` storing "CARD,BKASH".  
Recommendation: Two booleans for simpler querying and validation.

---

#### B) `donations`

| Column      | Type      | Notes |
|-------------|-----------|-------|
| id          | Long (PK) | From BaseEntity |
| campaign_id | Long (FK) | FundingCampaign |
| user_id     | Long (FK) | User (alumni) |
| amount      | BigDecimal| Donation amount |
| method      | Enum      | CARD, BKASH |
| status      | Enum      | PENDING, PAID, FAILED, REFUNDED |
| created_at  | DateTime  | From BaseEntity (createdDate) |

**Entity:** `Donation.java`  
**Repository:** `DonationRepository.java`  
**Enums:** `DonationStatus` (PENDING, PAID, FAILED, REFUNDED), `PaymentMethod` (CARD, BKASH).

**Indexes:**  
- `campaign_id`, `status` (for “raised per campaign” and reports).  
- `user_id` (for “My Donations”).  
- `created_date` (for time-series graph).

---

#### C) `payments` (Gateway / audit)

| Column          | Type       | Notes |
|-----------------|------------|-------|
| id              | Long (PK)  | From BaseEntity |
| donation_id     | Long (FK)  | Donation |
| provider        | String/Enum| card_gateway, bkash |
| provider_txn_id | String     | For idempotency & reconciliation |
| status          | String     | success, failed, pending, etc. |
| webhook_payload | TEXT/JSON  | Store raw payload for audit/debug |

**Entity:** `Payment.java`  
**Repository:** `PaymentRepository.java`

**Rule:** One donation can have one or more payment attempts (e.g. one FAILED then one PAID). When a payment succeeds via webhook, set `donation.status = PAID` and link `payment.donation_id`.

---

## 3. Backend API Design

### 3.1 Public / Landing & Alumni

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/api/funding/summary` | Overall totalRaised, totalGoal, percentage, optional top campaigns (for landing) |
| GET    | `/api/funding/campaigns` | List ACTIVE campaigns (with raised, %, progress); optional query `?eventId=` |
| GET    | `/api/funding/campaigns/{id}` | Single campaign detail + raised + % + recent donors (optional) |
| GET    | `/api/funding/donations/over-time` | Aggregated by date (daily/weekly) for line chart |
| GET    | `/api/funding/campaigns/top` | Top campaigns by raised (for bar chart) |
| POST   | `/api/funding/donations` | Create donation (PENDING) + init payment session; body: campaignId, amount, method |
| GET    | `/api/funding/my-donations` | Current user’s donation history (alumni portal) |
| GET    | `/api/funding/events/{eventId}/campaigns` | Active campaigns for an event (for Events → Funding section) |

### 3.2 Webhook (payment gateway)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST   | `/api/funding/webhook/card` | Card gateway webhook – verify, update donation to PAID, store payment record |
| POST   | `/api/funding/webhook/bkash` | bKash webhook – same flow |

**Webhook rules:**  
- Idempotent: use `provider_txn_id`; if already processed, return 200 without re-updating.  
- Never set PAID from frontend; only via verified webhook (or admin override with audit).

### 3.3 Admin

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/api/admin/funding/campaigns` | List all campaigns (filter by status/event) |
| POST   | `/api/admin/funding/campaigns` | Create campaign (DRAFT) |
| PUT    | `/api/admin/funding/campaigns/{id}` | Update campaign |
| POST   | `/api/admin/funding/campaigns/{id}/publish` | Set status ACTIVE |
| POST   | `/api/admin/funding/campaigns/{id}/unpublish` | Set back to DRAFT or similar |
| POST   | `/api/admin/funding/campaigns/{id}/close` | Set status CLOSED |
| GET    | `/api/admin/funding/donations` | List donations; query params: campaignId, status, method, dateFrom, dateTo |
| GET    | `/api/admin/funding/donations/export` | Export report (CSV/Excel) |
| GET    | `/api/admin/funding/analytics` | Aggregates, charts data (totals, by campaign, by date) |
| PUT    | `/api/admin/funding/donations/{id}/payout` | (Optional) Mark payout approved |
| PUT    | `/api/admin/funding/donations/{id}/expense` | (Optional) Mark expense usage |

---

## 4. Frontend Structure

### 4.1 Landing Page

- **Section: Funding summary**
  - Total contributions (sum of all PAID donations).
  - Overall progress bar: `(totalRaised / totalGoal) * 100`.
  - One graph (choose one or two):
    - **Line chart:** Donations over time (daily or weekly) – data from `/api/funding/donations/over-time`.
    - **Bar chart:** Top campaigns by raised amount – data from `/api/funding/campaigns/top`.
  - **Featured active campaigns:** Cards showing title, goal, raised, % progress, “Donate” button → Campaign Details Page.

### 4.2 Events → Funding Section

- On **events** listing/detail page:
  - Each event card shows **active funding campaigns** for that event.
  - “View Campaign” → Campaign Details Page (details + donate).

- API: `GET /api/funding/events/{eventId}/campaigns`.

### 4.3 Campaign Details Page (new page)

- **URL:** e.g. `/funding-campaign.html?id={campaignId}` or `/campaign-detail.html?id=`.
- **Content:**
  - Goal, raised, percentage + progress bar.
  - Donation form: select amount (preset + custom) + method (Card / bKash).
  - Contact/help: “For help call: +8801673323434” (from campaign.contact_phone).
  - Optional: recent donors list (anonymous or name, amount, date).

### 4.4 Alumni Portal

- **Menu:** “Alumni Support Fund” or “Fund”.
- **View:** List of all **active** campaigns (reuse API `GET /api/funding/campaigns`).
- **Actions:** Donate to any campaign (Card/bKash) → same flow as Campaign Details Page.
- **My Donations:** Page/section listing user’s donations (date, campaign, amount, method, status) with confirmation/receipt view.

### 4.5 Admin Dashboard

- **New tab/section:** “Funding” or “Alumni Support Fund”.
- **Tabs:**
  1. **Campaigns** – CRUD table: event (dropdown), title, goal, dates, contact phone, Card/bKash toggles, status, Publish/Unpublish/Close.
  2. **Donations** – Table with filters: campaign, status, method, date range; export report button.
  3. **Analytics** – Graphs (totals, by campaign, by date), export.

- **Optional:** Approve payouts / mark expense usage (buttons or status dropdown on donation row).

---

## 5. Payment Flow (Card + bKash)

1. **Alumni** clicks “Donate” and selects amount + method (Card or bKash).
2. **Frontend** calls `POST /api/funding/donations` with campaignId, amount, method.
3. **Backend** creates:
   - `Donation` (status = PENDING).
   - `Payment` (provider, status pending).
   - Returns payment session URL or token (for redirect to gateway).
4. **User** completes payment on gateway (Card or bKash).
5. **Gateway** sends webhook to backend (`/api/funding/webhook/card` or `/api/funding/webhook/bkash`).
6. **Backend** in webhook handler:
   - Verifies signature/payload.
   - Idempotency: if `provider_txn_id` already exists and donation already PAID → return 200.
   - Updates `Donation.status` to PAID.
   - Updates `Payment` record (status, provider_txn_id, webhook_payload).
7. **Frontend:** Success page shows confirmation + receipt (can poll donation status or redirect with token).

**Maintenance points:**
- Webhook must be **idempotent** (same webhook twice → no duplicate PAID).
- **Never** mark donation as PAID from frontend alone.
- Always store `provider_txn_id` for reconciliation.
- Store `webhook_payload` for audit/debug.

---

## 6. Calculations (Landing & Campaigns)

- **Overall (landing):**
  - `overallGoal` = sum of `goal_amount` for campaigns you choose to include (e.g. ACTIVE only, or ACTIVE + CLOSED).
  - `overallRaised` = sum of `donation.amount` where `donation.status = PAID`.
  - `overallPercent` = `(overallRaised / overallGoal) * 100` (cap at 100 if needed).

- **Per campaign:**
  - `campaignRaised` = sum of `donation.amount` where `donation.campaign_id = id` and `donation.status = PAID`.
  - `campaignPercent` = `(campaignRaised / campaign.goal_amount) * 100`.

- **Graphs:**
  - **Donations over time:** Group PAID donations by date (day or week).
  - **Top campaigns:** Rank campaigns by `campaignRaised` (bar chart).
  - **Campaign share (pie/donut):** Each campaign’s share of total PAID amount.

---

## 7. Implementation Phases (Order)

### Phase 1 – Data & core backend
1. Create enums: `CampaignStatus`, `DonationStatus`, `PaymentMethod`.
2. Create entities: `FundingCampaign`, `Donation`, `Payment`.
3. Create repositories and ensure `Event` is used as-is (campaign.event_id → Event.id).
4. Implement service layer: campaign CRUD, donation create, donation status update (for webhook).
5. Implement **admin** campaign APIs (create, update, publish, unpublish, close).
6. Implement **public** read APIs: summary, list campaigns, campaign by id, events/{id}/campaigns.

### Phase 2 – Donations & webhook stub
1. Implement `POST /api/funding/donations` (create PENDING donation + payment record).
2. Implement `GET /api/funding/my-donations` (for logged-in user).
3. Add webhook endpoints with **mock/sandbox** behavior first (e.g. simulate success for testing).
4. Implement idempotent webhook logic: check `provider_txn_id`, then update donation to PAID.

### Phase 3 – Admin UI
1. Admin dashboard: Funding tab with Campaigns sub-tab (CRUD, event dropdown, methods, Publish/Unpublish/Close).
2. Donations sub-tab: list with filters (campaign, status, method, date), export report.
3. Analytics sub-tab: totals, simple charts (by campaign, by date), export.

### Phase 4 – Public & alumni UI
1. Landing page: summary (total raised, total goal, %), progress bar, one or two graphs, featured campaign cards with “Donate”.
2. Campaign details page: goal/raised/%, donation form (amount + method), contact phone, optional recent donors.
3. Events page: add “Funding” section per event; “View Campaign” → campaign details.
4. Alumni portal: “Alumni Support Fund” – list active campaigns, “My Donations” history, confirmation/receipt after donate.

### Phase 5 – Real payment integration
1. Integrate Card gateway (e.g. SSLCommerz or similar): payment URL generation + webhook verification.
2. Integrate bKash: same flow (create payment, redirect, webhook).
3. Test end-to-end: donate → webhook → PAID → landing and campaign totals update.
4. (Optional) Admin: approve payout / mark expense usage.

---

## 8. File / Component Checklist

### Backend (Java)
- Enums: `CampaignStatus`, `DonationStatus`, `PaymentMethod`, (optional) `PaymentProvider`.
- Entities: `FundingCampaign`, `Donation`, `Payment`.
- Repositories: `FundingCampaignRepository`, `DonationRepository`, `PaymentRepository`.
- DTOs: e.g. `CampaignDto`, `CampaignCreateRequest`, `DonationDto`, `DonationRequest`, `FundingSummaryDto`, `DonationsOverTimeDto`.
- Services: `FundingCampaignService`, `DonationService`, `PaymentService` (or one `FundingService`).
- Controllers: `FundingPublicController` (or `FundingController`), `FundingAdminController`, `FundingWebhookController` (or under same controller with path `/webhook/...`).
- Config: ensure CORS/allowed origins for webhook if needed.

### Frontend
- Landing: update `index.html` – funding section (summary, progress bar, graph, featured campaigns).
- New: `campaign-detail.html` or `funding-campaign.html` (campaign detail + donate form).
- New: `my-donations.html` (alumni) or section inside alumni portal.
- Events: update `events.html` (and event-detail if exists) – funding block per event.
- Alumni portal: update `alumni-portal.html` – fund menu, list campaigns, my donations.
- Admin: update `admin-dashboard.html` – Funding tab with Campaigns, Donations, Analytics sub-tabs.
- JS: shared or per-page for charts (e.g. Chart.js), donation form submit, success/redirect handling.
- Optional: receipt/confirmation modal or page (query param `?donation=id`).

### Database
- Migrations: JPA `ddl-auto=update` will create `campaigns`, `donations`, `payments` from entities. Optionally add indexes on (campaign_id, status), (user_id), (created_date).

---

## 9. Optional Enhancements (Later)

- **Payout approval:** Admin marks donation as “payout approved” (extra column or status).
- **Expense usage:** Admin marks how much of a campaign’s raised amount was used for expense (for transparency).
- **Receipt PDF:** Generate PDF receipt for donor.
- **Email confirmation:** Send email on PAID donation.
- **Recurring donations:** Out of scope for first version; can be added later.

---

## 10. Summary

| Area | Deliverable |
|------|-------------|
| **Data** | `FundingCampaign`, `Donation`, `Payment`; campaigns linked to `Event` (nullable). |
| **Admin** | Create/edit campaigns (event, title, goal, dates, contact, Card/bKash); Publish/Unpublish/Close; donations list + export; analytics. |
| **Alumni** | View active campaigns; donate (Card/bKash); confirmation + receipt; “My Donations” history. |
| **Landing** | Total raised/goal, overall %, progress bar; line or bar chart; featured campaign cards; Donate → campaign detail. |
| **Events** | Funding section per event; “View Campaign” → detail + donate. |
| **Campaign detail** | Goal/raised/%, progress bar; donation form; contact; optional recent donors. |
| **Payment** | Create PENDING donation → gateway → webhook → set PAID; idempotent; store provider_txn_id and webhook payload. |

Once you confirm this plan (or request small changes), implementation can proceed phase by phase as above.
