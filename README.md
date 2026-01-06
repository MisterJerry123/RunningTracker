# Running Tracker 🏃‍♂️

**Running Tracker**는 사용자의 러닝을 기록하고 분석해주는 안드로이드 애플리케이션입니다. **Kotlin**과 **Jetpack Compose**를 사용하여 현대적인 UI와 견고한 아키텍처로 개발되었습니다.

## ✨ 주요 기능 (Key Features)

*   **실시간 러닝 추적**: Foreground Service를 활용하여 앱이 백그라운드에 있어도 정확하게 이동 경로와 거리를 기록합니다.
*   **지도 연동**: **OpenStreetMap (osmdroid)**을 사용하여 실시간 경로 표시 및 기록된 경로를 지도 위에서 다시 확인할 수 있습니다.
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

## ⚙️ 설정 및 빌드 (Setup)

이 프로젝트를 빌드하고 실행하기 위해서는 `local.properties` 설정이 권장됩니다.

1.  **프로젝트 클론**
    ```bash
    git clone https://github.com/MisterJerry123/RunningTracker.git
    ```

2.  **`local.properties` 설정**
    AdMob ID를 안전하게 관리하기 위해 프로젝트 루트의 `local.properties` 파일에 다음 변수들을 추가해주세요. 설정하지 않을 경우 테스트 ID가 기본값으로 사용됩니다.

    ```properties
    # Google AdMob App ID
    admob_app_id=ca-app-pub-YOUR_APP_ID~YOUR_ID
    
    # Run Detail Screen Banner Ad Unit ID
    admob_run_detail_screen_top_banner_id=ca-app-pub-YOUR_ID/YOUR_UNIT_ID
    ```

3.  **빌드 및 실행**
    Android Studio에서 프로젝트를 열고 `Run` 버튼을 눌러 빌드하세요.

## 📱 스크린샷

| 홈 화면 | 러닝 기록 중 | 상세 기록(지도) |
|:---:|:---:|:---:|
| <img src="docs/home.png" width="200" /> | <img src="docs/tracking.png" width="200" /> | <img src="docs/detail.png" width="200" /> |

*(스크린샷 이미지는 `docs` 폴더에 추가 후 경로를 수정해주세요)*

## 📄 라이선스 (License)

Copyright © 2026 MisterJerry. All rights reserved.
이 프로젝트의 소스 코드에 대한 무단 전재 및 재배포를 금지합니다.
