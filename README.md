# Ethylene

Mod Fabric (client) tối ưu FPS cho Minecraft **26.3**, yêu cầu **Java 25**.

## Tính năng

### 1. Chunk LOD (giảm chi tiết chunk ở xa)
- Giữ chi tiết đầy đủ cho các vòng chunk gần người chơi (mặc định 2 vòng).
- Các vòng ngoài cùng được giảm pixel mỗi mặt block, từ 32 px xuống tối thiểu 1 px.
- Mặc định mỗi chunk render tăng thêm sẽ thêm 1 vòng bị giảm chi tiết:
  - render 4 → 2 vòng ngoài giảm chi tiết
  - render 5 → 3 vòng ngoài giảm chi tiết
- Có thể chỉnh bước thêm vòng (`lodChunkStep`) thành 0.5, 0.3, 0.2... và mức giảm pixel (`lodReduction`).

### 2. Giảm tải mob
- Đếm số mob trong bán kính `mobDensityRadius` quanh người chơi.
- Khi vượt `mobDensityThreshold`, mob được làm mờ tăng dần (tối đa `mobBlurPercent`%). Bối cảnh xung quanh không bị ảnh hưởng.
- Khi vượt ngưỡng, có thể giảm chuyển động của mob (`reduceMobMotion`).

## Trạng thái hiện tại

| Hạng mục | Trạng thái |
|---|---|
| Khung project Fabric (Gradle, `fabric.mod.json`, entrypoint client) | Đã viết |
| Cấu hình đọc/ghi `config/ethylene.json`, có kiểm tra giá trị | Đã viết, đã biên dịch |
| Logic LOD (số vòng, bậc pixel) và logic mật độ mob | Đã viết, **đã chạy test** (`EthyleneLogicTest`) |
| Áp dụng LOD vào pipeline render chunk của Minecraft | **Chưa làm** |
| Áp dụng làm mờ / giảm chuyển động vào render mob | **Chưa làm** |
| Menu cấu hình trong game | **Chưa làm** (chỉnh qua file JSON) |

Hiện tại mod chỉ nạp cấu hình và ghi log, chưa thay đổi gì trong game. Hai phần render còn lại cần viết mixin/render hook theo API của 26.3 và kiểm tra trong game.

## Build

Yêu cầu JDK 25.

```bash
# Tạo Gradle wrapper (theo hướng dẫn của Fabric)
gradle wrapper --gradle-version latest
./gradlew build
```

File jar nằm trong `build/libs/`.

**Lưu ý:** `loom_version` trong `gradle.properties` (hiện là `1.16-SNAPSHOT`) chưa được xác minh. Nếu Gradle báo không tìm thấy plugin, hãy thay bằng phiên bản mới nhất ở https://docs.fabricmc.net/develop/.

## Kiểm tra logic (không cần Minecraft)

```bash
javac -d out src/main/java/com/knmusic/ethylene/lod/LodPolicy.java \
      src/main/java/com/knmusic/ethylene/mob/MobDensityPolicy.java \
      src/test/java/com/knmusic/ethylene/EthyleneLogicTest.java
java -ea -cp out com.knmusic.ethylene.EthyleneLogicTest
```

## Cấu hình (`config/ethylene.json`)

| Khóa | Mặc định | Ý nghĩa |
|---|---|---|
| `lodEnabled` | `true` | Bật/tắt chunk LOD |
| `fullDetailChunks` | `2` | Số vòng gần người chơi giữ chi tiết đầy đủ |
| `lodChunkStep` | `1.0` | Số vòng LOD thêm cho mỗi chunk render tăng thêm (0.0–4.0) |
| `lodReduction` | `1.0` | Mức giảm pixel: 0 = không giảm, 1 = vòng xa nhất còn 1 px |
| `mobBlurEnabled` | `true` | Bật/tắt làm mờ mob |
| `mobBlurPercent` | `50` | Mức làm mờ tối đa (0–100%) |
| `mobDensityThreshold` | `40` | Số mob trong bán kính trên mức này thì bắt đầu giảm tải |
| `mobDensityRadius` | `32` | Bán kính (block) để đếm mật độ mob |
| `reduceMobMotion` | `true` | Bật/tắt giảm chuyển động mob khi quá đông |

Giá trị ngoài khoảng hợp lệ sẽ được đưa về giới hạn gần nhất khi tải.

## Cấu trúc

```
src/main/java/com/knmusic/ethylene/
├── EthyleneClient.java          # Entrypoint client, nạp cấu hình
├── config/EthyleneConfig.java   # Cấu hình JSON
├── lod/LodPolicy.java           # Logic chunk LOD (thuần Java)
└── mob/MobDensityPolicy.java    # Logic mật độ mob (thuần Java)
src/test/java/com/knmusic/ethylene/
└── EthyleneLogicTest.java       # Kiểm tra logic LOD và mob
```
