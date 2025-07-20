# Polling Application – REST API

This is the backend part of a full-stack Polling Application built with **Java 17**, **Spring Boot**, and **PostgreSQL**.  
It allows users to register, log in, create polls, vote, and receive email notifications.

---

## Demonstration

[Youtube Demo](https://youtu.be/il2WmRzUG6w)

[Live Demo](https://idyllic-dango-375c9d.netlify.app/)

---

## Related Repositories

[Frontend - React](https://github.com/arturlarkin/poll-ui-demo)

---

## Features

- User registration & login with **Spring Security**
- JWT-based authentication
- Create custom polls with multiple options
- Vote on active polls
- View poll results
- **Email notifications** via Spring Mail:
  - Password reset
  - Poll creation alerts
- Role-based access: user/admin
- Deployed with **Docker** & **Render**

---

## Tech Stack

- **Java 17**
- **Spring Boot**
- **Spring Security + JWT**
- **Spring Data JPA**
- **Spring Mail**
- **PostgreSQL**
- **Docker**

---

## API Overview

| Method | Endpoint                   | Description              |
| ------ | -------------------------- | ------------------------ |
| POST   | `/api/auth/signup`         | Register new user        |
| POST   | `/api/auth/login`          | Login & receive JWT      |
| PUT    | `/api/auth/reset`          | Reset password           |
| GET    | `/api/user/me`             | Get user details         |
| PUT    | `/api/user/me`             | Update user details      |
| DELETE | `/api/user/me`             | Delete user              |
| GET    | `/api/polls/public/all`    | Get list of public polls |
| GET    | `/api/polls/my-polls`      | Get list of user's polls |
| POST   | `/api/polls/poll`          | Create new poll          |
| DELETE | `/api/polls/poll/{id}`     | Delete poll              |
| POST   | `/api/vote?pollId&optionId`| Make a vote              |
| DELETE | `/api/vote?pollId`         | Cancel vote              |

