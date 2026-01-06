# Running Tracker 🏃‍♂️

**Running Tracker**는 사용자의 러닝을 기록하고 분석해주는 안드로이드 애플리케이션입니다. **Kotlin**과 **Jetpack Compose**를 사용하여 현대적인 UI와 견고한 아키텍처로 개발되었습니다.

## ✨ 주요 기능 (Key Features)

*   **실시간 러닝 추적**: Foreground Service를 활용하여 앱이 백그라운드에 있어도 정확하게 이동 경로와 거리를 기록합니다.
*   **지도 연동**: OpenStreetMap (osmdroid)을 사용하여 실시간 경로 표시 및 기록된 경로를 지도 위에서 다시 확인할 수 있습니다.
*   **운동 기록 관리**: 날짜, 시간, 거리, 평균 속도 등의 상세 데이터를 **Room Database**에 저장하고 관리합니다.
*   **상세 분석**: 저장된 러닝 기록을 선택하여 당시의 경로와 상세 통계를 시각적으로 확인할 수 있습니다.
*   **광고 연동**: AdMob 배너 광고가 통합되어 있습니다.

## 🛠 기술 스택 (Tech Stack)

*   **Language**: [Kotlin](https://kotlinlang.org/)
*   **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material3)
*   **Architecture**: MVVM (Model-View-ViewModel)
*   **Concurrency**: Coroutines & Flow
*   **Local Storage**: Room Database
*   **Maps**: osmdroid (OpenStreetMap)
*   **Ads**: Google Mobile Ads SDK

## 🚀 비공개 테스트 참여 방법 (Join Closed Testing)

이 앱은 현재 **Google Play Store**의 비공개 테스트 트랙을 통해 관리되고 있습니다. 아래 절차에 따라 테스트에 참여하실 수 있습니다.

1.  **테스터 그룹 참여**: 아래 Google 그룹에 가입해야 테스트 참여 권한이 부여됩니다.
    *   [Google 그룹 참여하기](https://groups.google.com/u/2/g/hustledooalarm-tester)
2.  **테스트 참여 신청 및 앱 설치**:
    *   **Android에서 참여**: [Android PlayStore에서 설치](https://play.google.com/store/apps/details?id=com.misterjerry.runningtracker)
    *   **Web에서 참여**: [Web PlayStore에서 설치](https://play.google.com/apps/testing/com.misterjerry.runningtracker)

*현재 등록된 내부 테스터를 대상으로 기능 및 안정성을 검증하고 있으며, 테스트 완료 후 정식 출시될 예정입니다.*


## 📱 스크린샷

| 홈 화면 | 러닝 기록 중 화면 | 기록 화면 |기록 삭제 화면(다이얼로그) | 상세 기록(지도) |
|:---:|:---:|:---:|:---:|:---:|
| <img width="270" height="606" alt="image" src="https://github.com/user-attachments/assets/0f111cb7-71ee-4756-a117-ccc998e2435a" /> | <img width="270" height="606" alt="image" src="https://github.com/user-attachments/assets/9ac3785a-c9c3-4aa2-85e1-ce0f2d6fed2b" /> |<img width="270" height="606" alt="image" src="https://github.com/user-attachments/assets/a094c939-4238-4fac-bfd0-d0f2d4aa1356" /> |<img width="270" height="606" alt="image" src="https://github.com/user-attachments/assets/0c279105-e342-43b5-a661-629b167ea2cf" />| <img width="270" height="606" alt="image" src="https://github.com/user-attachments/assets/643a8f02-f2c8-4aa4-b501-731bc0cd7e7d" /> |

## 📄 라이선스 (License)

Copyright © 2026 MisterJerry. All rights reserved.
이 프로젝트의 소스 코드에 대한 무단 전재 및 재배포를 금지합니다.




