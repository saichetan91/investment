# 🚀 Investment Platform API Test Automation

<div align="center">

[![Java](https://img.shields.io/badge/Java-23-orange?style=for-the-badge&logo=java)](https://www.oracle.com/java/)
[![TestNG](https://img.shields.io/badge/TestNG-7.10.2-blue?style=for-the-badge&logo=testng)](https://testng.org/)
[![RestAssured](https://img.shields.io/badge/RestAssured-5.4.0-green?style=for-the-badge)](https://rest-assured.io/)
[![Maven](https://img.shields.io/badge/Maven-3.x-red?style=for-the-badge&logo=apache-maven)](https://maven.apache.org/)

*Comprehensive API test automation framework for investment platform with KYC flow and separate investment flow*

[🎯 Features](#-features) • [🛠️ Prerequisites](#️-prerequisites) • [⚡ Quick Start](#-quick-start) • [📊 Test Flows](#-test-flows) • [📖 API Documentation](#-api-documentation)

</div>

---

## 📋 Overview

This project is a robust **API test automation framework** built with **Java**, **TestNG**, and **RestAssured** to test a comprehensive investment platform. The framework covers complete user journeys from signup to investment, including KYC verification processes, bank integration, and compliance checks.

### 🎯 Key Highlights

- **🔐 Complete User Signup Flow** - Phone OTP verification and user registration
- **📝 Comprehensive KYC Process** - Profile updates, email verification, PAN/Aadhaar validation
- **🏦 Bank Integration** - Bank details submission and verification
- **💰 Investment Flow** - End-to-end investment process with deal selection
- **📊 ExtentReports Integration** - Beautiful HTML test reports
- **🔧 Database Utilities** - PAN update scripts with MySQL integration

---

## ✨ Features

### 🧪 Test Automation Framework
- **TestNG Integration**: Parallel test execution with proper test orchestration
- **RestAssured**: Powerful API testing with request/response validation
- **ExtentReports**: Rich HTML reports with detailed test execution logs
- **POJO Support**: Strongly typed request/response models using Lombok
- **Data-Driven Testing**: Dynamic test data generation for mobile numbers, bank accounts

### 🔐 Authentication & Security Testing
- **OTP Verification**: Phone and email OTP validation flows
- **Session Management**: Complete session handling with tokens and cookies
- **Multi-Header Support**: Custom headers (X-Tap-Auth, X-Tap-Session, X-Device-Id)
- **Device Tracking**: UUID-based device identification

### 📊 Comprehensive Test Coverage
- **Signup Flow**: Complete user registration with phone verification
- **KYC Flow**: Identity verification (PAN, Aadhaar, email, bank details)
- **Investment Flow**: Deal selection, terms validation, investment execution
- **Compliance Checks**: Regulatory compliance and accreditation testing

---

## 🛠️ Prerequisites

Before running the tests, ensure you have:

- **Java 17+** (currently configured for Java 23)
- **Maven 3.6+** for dependency management
- **Node.js** (for database utility scripts)
- **MySQL/MariaDB access** (for PAN update operations)
- **IDE** (IntelliJ IDEA recommended)

### System Requirements
- **OS**: Windows 10+, macOS 10.15+, or Linux
- **RAM**: Minimum 4GB (8GB recommended)
- **Network**: Access to staging environment (`kraken-stage.tapinvest.in`)

---

## ⚡ Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/saichetan91/investment.git
cd investment
```

### 2. Install Dependencies
```bash
# Install Java dependencies
mvn clean install

# Install Node.js dependencies for scripts
cd scripts
npm install
cd ..
```

### 3. Configuration Setup
Update the configuration file:
```bash
# Edit test configuration
nano src/test/resources/config.properties
```

**Config Properties:**
```properties
base.url=https://kraken-stage.tapinvest.in
```

### 4. Database Configuration (for PAN updates)
Edit the database configuration in `scripts/updatePan.js`:
```javascript
const dbConfig = {
    host: 'your-database-host',
    user: 'your-username', 
    password: 'your-password',
    database: 'user',
    port: 3306
};
```

### 5. Run Tests

**All Flows:**
```bash
mvn test -DsuiteXmlFile=AllFlows.xml
```

**Signup + KYC Only:**
```bash
mvn test -DsuiteXmlFile=SignupKYC.xml
```

**Individual Test Classes:**
```bash
# Run specific flow
mvn test -Dtest=flows.SignupFlow
mvn test -Dtest=flows.KycFlow  
mvn test -Dtest=flows.Investment
```

---

## 📁 Project Structure

```
investment/
├── 📂 src/
│   ├── 📂 main/java/
│   │   └── 📂 org/example/         # Main application code
│   └── 📂 test/java/
│       ├── 📂 common/              # Test utilities and base classes
│       │   ├── 📄 ApiBaseTest.java # Base test configuration
│       │   ├── 📄 Endpoints.java   # API endpoint definitions
│       │   ├── 📄 OtpHelper.java   # OTP handling utilities
│       │   ├── 📄 RequestHelper.java # HTTP request builders
│       │   ├── 📄 TestDataHelper.java # Test data generators
│       │   └── 📂 pojo/            # Request/Response POJOs
│       ├── 📂 flows/               # Test flow implementations
│       │   ├── 📄 SignupFlow.java  # User registration tests
│       │   ├── 📄 KycFlow.java     # KYC verification tests
│       │   └── 📄 Investment.java  # Investment flow tests
│       ├── 📂 manager/             # Request management
│       └── 📂 Report/              # ExtentReports configuration
├── 📂 scripts/                     # Database utility scripts
│   ├── 📄 updatePan.js            # PAN number update script
│   └── 📄 package.json            # Node.js dependencies
├── 📂 reports/                     # Generated test reports
├── 📄 AllFlows.xml                # Complete test suite
├── 📄 SignupKYC.xml               # Signup + KYC test suite
└── 📄 pom.xml                     # Maven configuration
```

---

## 📊 Test Flows

### 🔐 1. Signup Flow (`SignupFlow.java`)
- **Phone Number Generation**: Creates random valid mobile numbers
- **OTP Request**: Initiates phone verification process
- **OTP Verification**: Validates user with fixed OTP (1001)
- **Session Establishment**: Captures auth tokens and session data

**Test Method:**
```java
@Test(priority = 1, groups = {"signup"})
public void testCompleteSignup()
```

### 📝 2. KYC Flow (`KycFlow.java`)
Comprehensive identity verification process:

#### **Profile Update**
- Updates user profile with first name and gender
- **Test Method**: `testUpdateUserProfile()`

#### **Email Verification** 
- Generates dynamic email addresses
- Handles email OTP verification (Fixed OTP: 1001)
- **Test Method**: `testEmailVerification()`

#### **PAN Verification**
- Validates PAN number: `TESTS0001T`
- Verifies PAN holder name: `KOVURU SAI CHETAN`
- **Test Method**: `testPanVerification()`

#### **Aadhaar Verification**
- Processes Aadhaar number: `1111111115196`
- Validates with fixed Aadhaar OTP: `123456`
- **Test Method**: `testAadhaarVerification()`

#### **Bank & Accreditation**
- Bank details submission with generated account numbers
- IFSC code validation: `SBIN0001234`
- Accreditation questionnaire completion
- Compliance acknowledgments
- **Test Method**: `testBankAndAccreditation()`

#### **Final PAN Update**
- Executes Node.js script for database PAN updates
- **Test Method**: `testFinalPanUpdate()`

### 💰 3. Investment Flow (`Investment.java`)
Complete investment process testing:

#### **Deal Discovery**
- Fetches available investment deals
- Validates deal availability and investment limits

#### **Investment Execution**
- **Default Deal ID**: 4487
- **Default Investment Amount**: (your choice of investment)
- Validates minimum/maximum investment limits
- Checks deal sold-out status

#### **Transaction Processing**
- Payment order creation with success/failure paths
- Wallet balance utilization
- Partial payment support

#### **Post-Investment Verification**
- Transaction history validation  
- Investment metrics tracking
- User investment status confirmation

---

## 🔧 Key Components

### 📡 API Endpoints (`Endpoints.java`)
```java
// Authentication
public static final String AUTH_PHONE_REQUEST = "/v2/auth/phone/otp/request";
public static final String AUTH_PHONE_VERIFY = "/v2/auth/phone/otp/verify";

// KYC Process
public static final String USER_UPDATE_PROFILE = "/v2/user/update-profile";
public static final String USER_PAN_VERIFY = "/v2/user/pan/verify";
public static final String USER_AADHAAR_REQUEST = "/v2/user/aadhaar/verify";

// Investment
public static final String DEALS = "/v2/deals";
public static final String INVESTMENTS_START_ID_FLOW = "/v2/investments/start-invoice-discounting-investment-flow";
```

### 🎲 Test Data Generation (`TestDataHelper.java`)
- **Random Mobile Numbers**: Generates valid Indian mobile numbers (6-9 prefix)
- **Bank Account Numbers**: Creates even-digit bank account numbers
- **Device IDs**: UUID-based unique device identification

### 🍪 Session Management (`RequestHelper.java`)
Handles complex authentication with multiple headers:
```java
X-Tap-Auth: Authentication token
X-Tap-Session: Session identifier  
X-Device-Id: Unique device ID
X-Tap-Dshan: Security header
Cookie: _session: Session cookie
```

### 🗄️ Database Utilities (`scripts/updatePan.js`)
- **MySQL Integration**: Direct database operations
- **PAN Generation**: Creates random lowercase PAN numbers
- **User-Specific Updates**: Targeted PAN updates by user ID

---

## 📈 Reports & Logging

### ExtentReports Integration
- **Location**: `reports/extent.html`
- **Features**: Timeline view, test categorization, detailed logs
- **Configuration**: `ExtentReportListener.java`

### Sample Report Metrics
- **Total Tests**: 7 (as shown in recent run)
- **Pass Rate**: 100%
- **Categories**: SignupandKYCFlows
- **Environment Info**: Java 23, TestNG + RestAssured

---

## 🚀 Running Tests

### Test Suites

**1. Complete Test Suite** (`AllFlows.xml`):
```xml
<test name="ALL Flows">
    <classes>
        <class name="flows.SignupFlow"/>
        <class name="flows.KycFlow"/>
        <class name="flows.Investment"/>
    </classes>
</test>
```

**2. KYC-Only Suite** (`SignupKYC.xml`):
```xml
<test name="Signup and KYC Flows">
    <classes>
        <class name="flows.SignupFlow"/>
        <class name="flows.KycFlow"/>
    </classes>
</test>
```

### Execution Commands
```bash
# Run all flows
mvn clean test -DsuiteXmlFile=AllFlows.xml

# Run specific test class
mvn clean test -Dtest=flows.Investment

# Run with specific Java version
mvn clean test -Djava.version=23
```

---

## 🛡️ Security & Configuration

### Environment Configuration
- **Base URL**: `https://kraken-stage.tapinvest.in`
- **Fixed OTPs**: Phone (1001), Aadhaar (123456) for testing
- **Database**: MariaDB staging environment

### Headers Configuration
```java
X-Client-Type: Web Investor App
X-Device-Type: Desktop  
X-OS-Type: Mac OS
X-Platform-Request: WEB_READ/WEB_WRITE
X-App-Id: ULTRA
```

---

## 🔧 Troubleshooting

### Common Issues

**1. Maven Dependency Issues:**
```bash
mvn clean install -U
```

**2. Test Failures:**
- Check network connectivity to staging environment
- Verify database credentials in `updatePan.js`
- Ensure Node.js is installed for script execution

**3. Report Generation:**
- Reports are generated in `reports/extent.html`
- Check write permissions in the project directory

### Debug Mode
```bash
# Run with debug logging
mvn test -DsuiteXmlFile=AllFlows.xml -Dlog4j.debug=true
```
- **Framework**: TestNG + RestAssured + ExtentReports

---

## 📄 Dependencies

### Core Testing Framework
```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.4.0</version>
</dependency>

<dependency>
    <groupId>org.testng</groupId>
    <artifactId>testng</artifactId>
    <version>7.10.2</version>
</dependency>

<dependency>
    <groupId>com.aventstack</groupId>
    <artifactId>extentreports</artifactId>
    <version>5.1.1</version>
</dependency>
```

### Utility Libraries
- **Jackson**: JSON processing and POJO serialization
- **Lombok**: Reduces boilerplate code with annotations  
- **MySQL2**: Database connectivity for utility scripts

---

<div align="center">

**🌟 Built for comprehensive API testing of investment platforms 🌟**

*Automated testing for KYC flows, investment processes, and regulatory compliance*

</div>
