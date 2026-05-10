# ResQMitra API Documentation

ResQMitra is an emergency response system that helps citizens report incidents, assigns volunteers, and supports admin monitoring. This guide lists the main API endpoints in a short, practical format.

---

## Overview

**Base URL:** `{{baseURL}}`

* System supports three roles: **Admin**, **Volunteer**, **Citizen**
* Authentication uses **JWT Tokens**
* Send token using: `Authorization: Bearer <token>`

---

## Authentication

### 1. Login

**POST** `/auth/login`
No token required.

* Validates user and returns token + user details.
* Errors: 400 (Invalid data), 404 (Email not found), 403 (Wrong password)

---

## Incident APIs

### 2. Register Incident

**POST** `/incident/register`
No token required.

* Creates a new incident with location + description.

### 3. Assign Volunteer

**POST** `/incident/volunteer/register`
Role: Volunteer

* Assigns volunteer to an incident.
* Errors: User/Incident not found.

### 4. Get All Incidents

**GET** `/incident/get`
Any role

* Returns all incidents.

### 5. Get Incidents Assigned to Volunteer

**GET** `/incident/get/byvolunteer`
Role: Volunteer

* Volunteer automatically identified from token.

### 6. Get Incidents by Date

**POST** `/incident/get/bydate`
Role: Admin

* Returns incidents between selected dates.

### 7. Search Incidents (Date + Keyword)

**POST** `/incident/get/data`

* All filters optional.
* Works differently based on user role.

### 8. Resolve Incident

**PUT** `/incident/resolve/{incidentId}`
Role: Volunteer

* Marks incident as resolved.

---

## User APIs

### 9. Register User

**POST** `/user/register`
No token required.

* Creates new user.
* Errors: 409 if user already exists.

### 10. Delete User

**DELETE** `/user/delete`
Token required.

* Deactivates user.

### 11. Update User

**PUT** `/user/update`

* Send only the fields you want to update.

### 12. Update Volunteer Location

**PUT** `/user/update/location`
Role: Volunteer

* Updates volunteer latitude/longitude.

### 13. Get User by Email

**GET** `/user/get/{emailId}`

* Returns user data by email.

### 14. Get Volunteers

**GET** `/user/get/volunteer`

* Returns list of active volunteers.

### 15. Search Volunteers

**GET** `/user/get/volunteer/search/{keyword}`

* Returns volunteers matching keyword.

---

## Error Codes Summary

* **400 Bad Request:** Invalid request format or validation error
* **401 Unauthorized:** Token missing or invalid
* **403 Forbidden:** User role does not have access
* **404 Not Found:** Resource not found
* **409 Conflict:** Duplicate user registration

---

## Roles Summary

| Role      | Description                                            |
| --------- | ------------------------------------------------------ |
| Admin     | View all incidents, filter by date, manage data        |
| Volunteer | Accept assignments, update location, resolve incidents |
| Citizen   | Create incidents, register as user                     |

---

## Token Usage

All protected APIs require sending the JWT token:

```
Authorization: Bearer <your_token_here>
```

---

## Notes

* Always check 401 and 403 responses while integrating.
* Latitude and longitude values are optional for user registration.
* Filters in `/incident/get/data` are optional.

---

"# ResQMitra" 
