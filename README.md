# Alumni Management Portal

A comprehensive Spring Boot-based web application designed to connect alumni, students, and administrators in a unified platform. This portal facilitates networking, job opportunities, event management, surveys, and community engagement.

## 🎯 Project Overview

The Alumni Management Portal is a full-stack web application that serves as a central hub for:
- **Alumni** to connect with each other, share memories, find job opportunities, and participate in surveys
- **Students** to connect with alumni, explore career opportunities, and access resources
- **Administrators** to manage users, create surveys, post jobs, manage events, and analyze engagement

## ✨ Key Features

### 👥 User Management & Authentication
- Role-based access control (Admin, Alumni, Student)
- User registration and login system
- Profile management with detailed information
- Password reset functionality
- Account locking mechanism for security

### 🔗 Networking & Connections
- **Alumni Directory**: Search and connect with alumni by name, email, graduation year, or department
- **Student Directory**: Students can search and connect with alumni
- **Connection Requests**: Send and manage friend/connection requests
- **Messaging System**: Real-time messaging between connected users via WebSocket
- **Connection Management**: View connected users and manage connections

### 💼 Job Management
- **Job Posting**: Admins can create and manage job postings
- **Job Search**: Browse available job opportunities
- **Job Applications**: Students and alumni can apply for jobs
- **Application Tracking**: Track application status

### 📊 Survey System
- **Survey Creation**: Admins can create surveys with multiple question types:
  - Short Text
  - Long Text
  - Multiple Choice (MCQ)
  - Checkbox (Multiple Selection)
  - Rating (1-5 scale)
- **Survey Publishing**: Draft and publish surveys
- **Survey Participation**: Users can participate in published surveys
- **Results & Analytics**: Visual charts and detailed results analysis
- **Duplicate Prevention**: Users cannot submit the same survey twice

### 📅 Event Management
- **Event Creation**: Admins can create and manage events
- **Event Registration**: Users can register for events
- **Event Details**: View event information and attendees
- **Public Events**: Events visible to all users without authentication

### 📰 News & Updates
- **News Publishing**: Admins can publish news articles
- **News Feed**: Browse latest news and updates
- **News Categories**: Different types of news (General, Events, Jobs, etc.)
- **Detailed View**: Full article view with comments

### 📸 Memories & Photo Library
- **Post Memories**: Users can share academic memories with photos and stories
- **Photo Library**: Public gallery of shared memories
- **Memory Management**: View and manage posted memories

### 💬 Messaging & Communication
- **Real-time Messaging**: WebSocket-based instant messaging
- **Message History**: View conversation history
- **Connection-based Messaging**: Message only connected users

### 💰 Fundings (Alumni Support Fund)
- **Campaigns**: Admins create fundraising campaigns with goals, dates, and payment options (card, bKash)
- **Donations**: Logged-in users can donate to campaigns; payments can be simulated or integrated via webhooks
- **Public Summary**: Landing page and portal show total raised, total goal, progress, and donations-over-time charts
- **Admin Analytics**: Per-campaign and overall analytics—bar charts (donations over time), donut charts (progress), campaign breakdown
- **My Donations**: Users can view their donation history
- **Export**: Admins can export donations as CSV

### 📈 Admin Dashboard
- **User Management**: View and manage all users
- **Survey Management**: Create, publish, and analyze surveys
- **Job Management**: Post and manage job opportunities
- **Event Management**: Create and manage events
- **News Management**: Publish and manage news articles
- **Funding Management**: Create campaigns, view donations, and analytics (Campaigns, Donations, Analytics tabs)
- **Analytics**: View survey results, user statistics, and engagement metrics

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 3.1.5
- **Java Version**: 17
- **Database**: MySQL 8.0
- **ORM**: JPA/Hibernate
- **Build Tool**: Gradle
- **WebSocket**: Spring WebSocket for real-time messaging
- **Security**: Spring Security Crypto for password hashing
- **API Documentation**: SpringDoc OpenAPI (Swagger)
- **Mapping**: ModelMapper for DTO conversion
- **Validation**: Jakarta Validation & Commons Validator

### Frontend
- **HTML5/CSS3**: Modern, responsive design
- **JavaScript**: Vanilla JS for interactivity
- **WebSocket Client**: Real-time messaging
- **Chart.js**: Data visualization for survey results
- **Font Awesome**: Icons
- **Google Fonts**: Inter font family

### Database
- **MySQL 8.0** on port 3308
- **Database Name**: `ea_lab`
- **JPA/Hibernate**: Automatic schema updates

## 📋 Prerequisites

Before running the application, ensure you have:

- **Java 17** or higher
- **MySQL 8.0** installed and running
- **Gradle** (or use Gradle Wrapper)
- **MySQL Database** created:
  ```sql
  CREATE DATABASE ea_lab;
  ```

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
cd AlumniManagementPortal
```

### 2. Configure Database

Update `src/main/resources/application.properties` with your MySQL credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3308/ea_lab?useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_password
```

### 3. Build the Project
```bash
./gradlew build
```

### 4. Run the Application
```bash
./gradlew bootRun
```

Or run from your IDE by executing the `AlumniManagementPortalApplication` class.

### 5. Access the Application
- **Local**: http://localhost:8081
- **Network**: http://[your-ip]:8081 (accessible from other devices on the same network)

## 🌐 Accessing from Another Device

The application is configured to accept connections from other devices on your network:

1. **Find your IP address**:
   - **Windows**: `ipconfig` (look for IPv4 Address)
   - **Mac/Linux**: `ifconfig` or `ip addr`
   
2. **Access from another device**:
   - Open browser on the other device
   - Navigate to: `http://[your-ip]:8081`
   - Example: `http://192.168.1.100:8081`

3. **Firewall**: Ensure port 8081 is open in your firewall settings

## 📁 Project Structure

```
AlumniManagementPortal/
├── src/
│   ├── main/
│   │   ├── java/com/miu/alumnimanagementportal/
│   │   │   ├── controllers/          # REST API controllers
│   │   │   ├── services/             # Business logic
│   │   │   ├── repositories/         # Data access layer
│   │   │   ├── entities/             # JPA entities
│   │   │   ├── dtos/                 # Data Transfer Objects
│   │   │   ├── surveys/              # Survey module
│   │   │   ├── funding/               # Fundings (campaigns, donations, analytics)
│   │   │   ├── config/                # Configuration classes
│   │   │   ├── exceptions/           # Custom exceptions
│   │   │   └── common/                # Common utilities
│   │   └── resources/
│   │       ├── static/                # Frontend files (HTML, CSS, JS)
│   │       └── application.properties # Configuration
│   └── test/                          # Test files
├── uploads/                           # Uploaded files (images, PDFs)
├── build.gradle                       # Build configuration
└── README.md                          # This file
```

## 🔑 User Roles

### Admin
- Full system access
- User management
- Survey creation and management
- Job posting and management
- Event creation and management
- News publishing
- **Funding campaigns**: Create, publish, close campaigns; view donations and analytics; export CSV
- Analytics and reports

### Alumni
- Profile management
- Connect with other alumni and students
- Search alumni directory
- Apply for jobs
- Participate in surveys
- Post memories
- View events and news
- **Donate to campaigns** (Alumni Support Fund); view my donations
- Messaging with connections

### Student
- Profile management
- Connect with alumni
- Search alumni directory
- Apply for jobs
- Participate in surveys
- Post memories
- View events and news
- **Donate to campaigns** (Alumni Support Fund); view my donations
- Messaging with connections

## 📡 API Endpoints

### Authentication & Users
- `POST /users/register` - User registration
- `POST /users/login` - User login
- `GET /users` - Get user information
- `GET /api/profile` - Get user profile

### Surveys
- `GET /api/surveys` - List published surveys
- `GET /api/surveys/{id}` - Get survey details
- `POST /api/surveys/{id}/responses` - Submit survey response
- `GET /api/admin/surveys` - Admin: List all surveys
- `POST /api/admin/surveys` - Admin: Create survey
- `POST /api/admin/surveys/{id}/publish` - Admin: Publish survey
- `POST /api/admin/surveys/publish-all-draft` - Admin: Publish all draft surveys
- `GET /api/admin/surveys/{id}/results` - Admin: Get survey results

### Jobs
- `GET /jobs` - List available jobs
- `GET /jobs/{id}` - Get job details
- `POST /job-applications` - Apply for a job
- `GET /api/admin/jobs` - Admin: List all jobs
- `POST /api/admin/jobs` - Admin: Create job

### Events
- `GET /events` - List events
- `GET /events/{id}` - Get event details
- `POST /events/{id}/register` - Register for event
- `GET /api/admin/events` - Admin: List all events
- `POST /api/admin/events` - Admin: Create event

### Connections & Messaging
- `GET /api/alumni-directory` - Search alumni
- `GET /api/connections` - Get user connections
- `POST /api/connections/request` - Send connection request
- `POST /api/connections/accept` - Accept connection request
- `GET /messages` - Get messages
- `POST /messages` - Send message
- WebSocket: `/ws/messages` - Real-time messaging

### Memories
- `GET /api/memories` - List memories
- `POST /api/memories` - Post memory
- `GET /photo-library.html` - View photo library

### News
- `GET /news` - List news articles
- `GET /news/{id}` - Get news details
- `GET /api/admin/news` - Admin: List all news
- `POST /api/admin/news` - Admin: Create news

### Fundings (Alumni Support Fund)
- **Public**
  - `GET /api/funding/summary` - Get summary (total raised, goal, percent, featured campaigns, donations over time)
  - `GET /api/funding/campaigns` - List active campaigns (optional `eventId`)
  - `GET /api/funding/campaigns/{id}` - Get campaign details
  - `GET /api/funding/campaigns/top?limit=10` - Top campaigns by amount raised
  - `GET /api/funding/donations/over-time?groupBy=day` - Donations over time
  - `POST /api/funding/donations` - Create donation (requires `userEmail`, body: campaignId, amount, method, etc.)
  - `GET /api/funding/my-donations?userEmail=...` - Current user's donations
  - `GET /api/funding/donations/{id}` - Get donation by ID
  - `GET /api/funding/events/{eventId}/campaigns` - Campaigns for an event
- **Admin** (require admin email)
  - `GET /api/admin/funding/campaigns?adminEmail=...` - List all campaigns
  - `POST /api/admin/funding/campaigns` - Create campaign
  - `PUT /api/admin/funding/campaigns/{id}` - Update campaign
  - `DELETE /api/admin/funding/campaigns/{id}` - Delete campaign
  - `POST /api/admin/funding/campaigns/{id}/publish` - Publish campaign
  - `POST /api/admin/funding/campaigns/{id}/unpublish` - Unpublish campaign
  - `POST /api/admin/funding/campaigns/{id}/close` - Close campaign
  - `GET /api/admin/funding/donations` - List donations (optional campaignId, status, method)
  - `GET /api/admin/funding/donations/export` - Export donations CSV
  - `GET /api/admin/funding/analytics?adminEmail=...` - Summary, donations over time, top campaigns, per-campaign analytics
- **Webhooks**
  - `POST /api/funding/webhook/card` - Card gateway webhook (mark donation paid)
  - `POST /api/funding/webhook/bkash` - bKash webhook (mark donation paid)

## 🎨 Frontend Pages

- `index.html` - Landing page
- `login.html` - Login page
- `signup.html` - Registration page
- `dashboard.html` - User dashboard
- `admin-dashboard.html` - Admin dashboard
- `alumni-portal.html` - Alumni portal
- `student-view.html` - Student portal
- `alumni-directory.html` - Alumni directory
- `student-directory.html` - Student directory
- `jobs.html` - Job listings
- `job-detail.html` - Job details
- `events.html` - Event listings
- `surveys.html` - Survey participation
- `news.html` - News feed
- `messages.html` - Messaging interface
- `photo-library.html` - Photo library
- `connection-requests.html` - Connection requests
- `campaign-detail.html` - Campaign list and donation (Alumni Support Fund)
- `my-donations.html` - User's donation history
- **Landing (index.html)**: Alumni Support Fund card with donations-over-time chart, progress donut, campaign names, and "Want to donate? Log in"
- **Admin dashboard**: Funding modal (Campaigns, Donations, Analytics) with per-campaign analytics

## 🔧 Configuration

### Application Properties
- **Server Port**: 8081
- **Server Address**: 0.0.0.0 (allows external connections)
- **Database**: MySQL on localhost:3308
- **File Upload**: Max 10MB
- **JPA**: Auto-update schema

### Database Configuration
The application uses MySQL with the following settings:
- **URL**: `jdbc:mysql://localhost:3308/ea_lab`
- **Username**: `root` (configurable)
- **Password**: (configurable)
- **DDL Mode**: `update` (auto-creates/updates tables)

## 📝 Key Features Explained

### Survey System
- Surveys can be created as drafts and published later
- Multiple question types supported
- Visual analytics with pie charts
- Prevents duplicate submissions
- Results show individual responses and aggregated data

### Connection System
- Users can search and send connection requests
- Connection requests must be accepted before messaging
- Separate directories for alumni and students
- Connection status visible in search results

### Job Application System
- Job postings with detailed descriptions
- Application tracking
- Status management
- Filtering and search capabilities

### Real-time Messaging
- WebSocket-based messaging
- Instant message delivery
- Message history
- Connection-based messaging (only connected users)

### Fundings (Alumni Support Fund)
- **Campaigns** have a goal amount, start/end dates, and optional event link; admins can publish, unpublish, or close them.
- **Donations** are tied to a campaign and user; status flows to PAID via webhook or simulate endpoint.
- **Landing page** shows a summary card with bar chart (donations over time), donut (progress to goal), campaign name(s), and a login CTA to donate.
- **Admin analytics** include overall and per-campaign charts (bar and donut) and CSV export of donations.

## 🐛 Troubleshooting

### Surveys Not Showing on Another PC
1. **Check if surveys are published**: Surveys must be published to be visible
2. **Use "Publish All Draft Surveys"**: Click the button in admin dashboard to publish all existing surveys
3. **Verify backend is running**: Ensure Spring Boot is running on the server
4. **Check network connectivity**: Verify both devices are on the same network
5. **Check firewall**: Ensure port 8081 is open

### Database Connection Issues
- Verify MySQL is running
- Check database credentials in `application.properties`
- Ensure database `ea_lab` exists
- Check MySQL port (default: 3308)

### File Upload Issues
- Check `uploads/` directory exists
- Verify file size limits (max 10MB)
- Check file permissions

## 📚 Development Notes

### Adding New Features
1. Create entity in `entities/` package
2. Create repository in `repositories/` package
3. Create service interface and implementation
4. Create DTOs in `dtos/` package
5. Create controller with REST endpoints
6. Add frontend pages in `static/` directory

### Code Style
- Follow Java naming conventions
- Use Lombok for boilerplate code
- Use DTOs for API communication
- Implement proper error handling
- Add logging for important operations

## 📄 License

This project is developed for educational purposes.

## 👥 Contributors

Developed as part of the Alumni Management Portal project.

## 📞 Support

For issues or questions, please refer to the project documentation or contact the development team.

---

**Note**: This application is configured to run on port 8081 and accepts connections from all network interfaces (0.0.0.0), making it accessible from other devices on your local network.
