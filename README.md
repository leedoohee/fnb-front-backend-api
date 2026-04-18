# 🚀 F&B Order & Payment System

본 프로젝트는 식음료(F&B) 서비스의 주문 및 결제 흐름을 처리하는 **이벤트 기반 백엔드 시스템**입니다. 단순한 CRUD를 넘어 복잡한 할인 정책 계산과 결제 상태의 정합성을 보장하도록 설계되었습니다.

---

## 🛠 Tech Stack
* **Framework**: Spring Boot
* **Build Tool**: Gradle
* **Language**: Java 17
* **Persistence**: CRITERIA
* **Event Handling**: Spring ApplicationEvent (Transactional Boundary 관리)

---

## 🏗 Key Architecture & Design Patterns

### 1. Event-Driven Architecture (EDA)
결제 승인 이후의 부수 효과(재고 차감, 쿠폰 사용, 포인트 적립)를 핵심 주문 로직과 분리하여 시스템의 유연성을 높였습니다.
* **Decoupling**: `PaymentService`는 결제 요청에 집중하고, 후처리는 `AfterPaymentService`에서 담당합니다.
* **Reliability**: `TransactionalEventListener`를 통해 결제 프로세스의 물리적 성공 여부에 따른 데이터 무결성을 보장합니다.


서비스 레이어의 비대화를 막기 위해 순수 비즈니스 로직을 `OrderProcessor`로 캡슐화했습니다.
* **OrderProcessor**: 주문 유효성 검사, 멤버십 할인 계산, 쿠폰 적용 로직을 독립적으로 수행합니다.
* **Strategy Pattern**: `DiscountFactory`를 통해 다양한 할인 정책(쿠폰, 멤버십)을 유연하게 확장할 수 있습니다.

### 3. Fault Tolerance (Rollback Strategy)
결제 승인 후 시스템 장애가 발생할 경우를 대비한 자동 보상 트랜잭션 로직을 포함합니다.
* 후처리 과정 실패 시 `PaymentCancelEvent`를 발행하여 PG 측 승인 취소 요청을 자동으로 수행합니다.

---

## 📑 Core Logic Flow

### Order Placement (주문 생성)
1. `OrderService.create()` 호출
2. `OrderProcessor`를 통한 도메인 모델 구축 (할인, 옵션 계산)
3. 주문 및 상품 데이터 DB Persistence
4. 결제 금액 존재 시 `RequestPaymentEvent` 발행

### Payment Processing (결제 처리)
1. PG사(카카오페이 등) 승인 결과 수신
2. `PaymentApproveEvent` 발행
3. **[AfterPaymentService]** 로직 수행:
    * 재고 차감 및 쿠폰/포인트 상태 업데이트
    * 결제 이력(Payment, PaymentElement) 저장
4. 주문 상태 업데이트 (`ORDERED`)

---
```text
src/main/java/com/fnb/front/backend/
├── service/
│   ├── OrderService.java        # 주문 생성 오케스트레이션
│   ├── PaymentService.java      # 결제 요청 및 취소 제어
│   └── AfterPaymentService.java # 결제 성공/취소 후속 비즈니스 처리
├── controller/domain/
│   ├── processor/
│   │   └── OrderProcessor.java  # 할인/금액 계산 엔진
│   ├── event/                   # 시스템 내부 메시지 객체
│   └── validator/               # 주문 제약 조건 검증기
└── repository/                  # 데이터 액세스 계층
