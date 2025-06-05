# Project Management System - Documentation

## Overview
A Spring Boot-based project management system supporting users, projects, issues, comments, subscriptions, and payments.

---

## Main Entities
- **User**: Represents a system user.
- **Project**: Contains project details, owned by a user.
- **Issue**: Task or bug within a project, can be assigned to a user.
- **Comments**: User comments on issues.
- **Subscription**: User's subscription plan.
- **Payment**: Payment details for subscriptions.

---

## Entity Relationships
- A **User** can own multiple **Projects**.
- A **Project** contains multiple **Issues**.
- An **Issue** can be assigned to a **User**.
- A **User** can add **Comments** to **Issues**.
- A **User** can have a **Subscription** plan.
- A **Subscription** is linked to **Payments** made by the **User**.

---

## Features
- User authentication & role-based access control.
- Project creation & management.
- Issue tracking & assignment.
- Commenting system for issues.
- Subscription plans & payment processing.
- Real-time notifications & reporting.

---

## Technologies Used
- **Backend**: Spring Boot, Hibernate, MySQL
- **Frontend**: React.js, Bootstrap
- **Authentication**: Spring Security, JWT
- **Payments**: Stripe API
- **Version Control**: Git & GitHub

---

## API Endpoints

### UserController (`/api/users`)
- `POST /api/users/register` — Register a new user
- `POST /api/users/login` — User login (returns JWT)
- `GET /api/users/{id}` — Get user by ID (requires Authorization)
- `PUT /api/users/{id}` — Update user profile (requires Authorization)
- `DELETE /api/users/{id}` — Delete user (requires Authorization)

### ProjectController (`/api/projects`)
- `GET /api/projects` — Get all projects (requires Authorization)
- `GET /api/projects/{projectId}` — Get project by ID (requires Authorization)
- `POST /api/projects` — Create a new project (requires Authorization)
- `PUT /api/projects/{projectId}` — Update project (requires Authorization)
- `DELETE /api/projects/{projectId}` — Delete project (requires Authorization)

### IssueController (`/api/issues`)
- `GET /api/issues/{issueId}` — Get issue by ID (requires Authorization)
- `GET /api/issues/project/{projectId}` — Get all issues for a project (requires Authorization)
- `POST /api/issues` — Create a new issue (requires Authorization)
- `DELETE /api/issues/{issueId}` — Delete an issue (requires Authorization)
- `PUT /api/issues/{issueId}/assignee/{userId}` — Assign a user to an issue (requires Authorization)
- `PUT /api/issues/{issueId}/status/{status}` — Update issue status (requires Authorization)

### CommentController (`/api/comments`)
- `POST /api/comments` — Create a comment on an issue (requires Authorization)
- `DELETE /api/comments/{commentId}` — Delete a comment (requires Authorization)
- `GET /api/comments/{issueId}` — Get all comments for an issue (requires Authorization)

### SubscriptionController (`/api/subscriptions`)
- `GET /api/subscriptions/{userId}` — Get subscription for a user (requires Authorization)
- `POST /api/subscriptions` — Create or update subscription (requires Authorization)

### PaymentController (`/api/payments`)
- `POST /api/payments` — Initiate a payment (requires Authorization)
- `GET /api/payments/{userId}` — Get payment history for a user (requires Authorization)

> All `/api/**` endpoints require a valid JWT in the `Authorization` header.
